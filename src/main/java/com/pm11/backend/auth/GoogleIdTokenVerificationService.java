package com.pm11.backend.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.pm11.backend.ApiException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleIdTokenVerificationService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleIdTokenVerificationService(
            @Value("${pm11.auth.google.web-client-id:}") String webClientId) {
        if (webClientId == null || webClientId.isBlank()) {
            this.verifier = null;
        } else {
            this.verifier =
                    new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                            .setAudience(Collections.singletonList(webClientId.trim()))
                            .build();
        }
    }

    /**
     * Android Credential Manager 등에서 받은 Google ID 토큰을 검증하고 {@code sub}, 이메일을 반환한다.
     */
    public Verified verify(String idTokenString) {
        if (verifier == null) {
            throw ApiException.badRequest(
                    "Google 로그인용 서버 설정(GOOGLE_CLIENT_ID / pm11.auth.google.web-client-id)이 비어 있습니다.");
        }
        if (idTokenString == null || idTokenString.isBlank()) {
            throw ApiException.badRequest("id_token 이 필요합니다.");
        }
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw ApiException.unauthorized("유효하지 않은 Google ID 토큰입니다.");
            }
            GoogleIdToken.Payload payload = idToken.getPayload();
            String sub = payload.getSubject();
            if (sub == null || sub.isBlank()) {
                throw ApiException.unauthorized("Google ID 토큰에 subject 가 없습니다.");
            }
            String email = payload.getEmail();
            return new Verified(sub, email);
        } catch (GeneralSecurityException | IOException e) {
            throw ApiException.unauthorized("Google ID 토큰 검증에 실패했습니다.");
        }
    }

    public record Verified(String subject, String email) {}
}
