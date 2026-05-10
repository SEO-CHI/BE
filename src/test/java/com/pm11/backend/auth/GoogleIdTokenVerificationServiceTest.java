package com.pm11.backend.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pm11.backend.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

class GoogleIdTokenVerificationServiceTest {

    @Test
    void verify_throwsWhenWebClientIdNotConfigured() {
        GoogleIdTokenVerificationService service = new GoogleIdTokenVerificationService("");

        ApiException ex = assertThrows(ApiException.class, () -> service.verify("any.jwt.token"));
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void verify_throwsWhenIdTokenMissing(String idToken) {
        GoogleIdTokenVerificationService service =
                new GoogleIdTokenVerificationService("123456789-abc.apps.googleusercontent.com");

        ApiException ex = assertThrows(ApiException.class, () -> service.verify(idToken));
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}
