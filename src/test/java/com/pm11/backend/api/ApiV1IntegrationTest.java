package com.pm11.backend.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.pm11.backend.auth.TokenStore;
import com.pm11.backend.emotion.Emotion;
import com.pm11.backend.emotion.EmotionRepository;
import com.pm11.backend.user.AuthProvider;
import com.pm11.backend.user.User;
import com.pm11.backend.user.UserRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * API 명세서(private-docs/API명세서.md) v1 엔드포인트 스모크/계약 검증.
 * 인메모리 H2 + {@code test} 프로파일로 로컬 MySQL 없이 동작한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiV1IntegrationTest {

    private static final String EMOTION_ID = "em_01HZX2B7C3D4E5F6A7B8C9D0";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmotionRepository emotionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String userId;
    private String accessToken;

    @BeforeEach
    void setUp() {
        userId = "u_" + UUID.randomUUID().toString().replace("-", "");
        userRepository.save(User.builder()
                .id(userId)
                .provider(AuthProvider.kakao)
                .providerId("kakao-" + userId)
                .email(null)
                .createdAt(Instant.now())
                .build());
        accessToken = tokenStore.issue(userId).accessToken();

        List<Emotion> emotions = List.of(
                new Emotion("em_01HZX2B7C3D4E5F6A7B8C9D0", "번아웃"),
                new Emotion("em_01HZX2B7C3D4E5F6A7B8C9D1", "외로움"),
                new Emotion("em_01HZX2B7C3D4E5F6A7B8C9D2", "무기력"),
                new Emotion("em_01HZX2B7C3D4E5F6A7B8C9D3", "불안"),
                new Emotion("em_01HZX2B7C3D4E5F6A7B8C9D4", "답답함"));
        emotionRepository.saveAll(emotions);

        jdbcTemplate.update(
                """
                INSERT INTO place (id, name, category, created_at, latitude, longitude, is_indoor, is_free, needs_reservation)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                167,
                "테스트장소",
                "갤러리",
                Timestamp.from(Instant.now()),
                37.5665,
                126.978,
                true,
                false,
                false);
    }

    @Test
    void getEmotions_returnsFive() throws Exception {
        mockMvc.perform(get("/v1/emotions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emotions.length()").value(5))
                .andExpect(jsonPath("$.emotions[0].emotion_id").exists())
                .andExpect(jsonPath("$.emotions[0].name").exists());
    }

    @Test
    void postCheckIn_returns201WithCheckInId() throws Exception {
        mockMvc.perform(post("/v1/check-ins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "emotion_id": "%s",
                                  "latitude": 37.5665,
                                  "longitude": 126.978
                                }
                                """
                                        .formatted(EMOTION_ID)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.check_in_id").isString());
    }

    @Test
    void getRecommendations_returns200WithExpectedShape() throws Exception {
        String body = mockMvc.perform(post("/v1/check-ins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "emotion_id": "%s",
                                  "latitude": 37.5665,
                                  "longitude": 126.978
                                }
                                """
                                        .formatted(EMOTION_ID)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String checkInId = objectMapper.readTree(body).get("check_in_id").asText();

        mockMvc.perform(get("/v1/check-ins/{id}/recommendations", checkInId)
                        .param("fee", "all")
                        .param("place_type", "all")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.check_in_id").value(checkInId))
                .andExpect(jsonPath("$.filters.fee").value("all"))
                .andExpect(jsonPath("$.filters.place_type").value("all"))
                .andExpect(jsonPath("$.filters.radius_m").value(0))
                .andExpect(jsonPath("$.filters.max_walk_time_min").value(0))
                .andExpect(jsonPath("$.filters.sort").value("distance_asc"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].place_id").value(167))
                .andExpect(jsonPath("$.page.limit").value(10))
                .andExpect(jsonPath("$.page.has_more").exists());

        mockMvc.perform(get("/v1/check-ins/{id}/recommendations", checkInId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].reason").isString());
    }

    @Test
    void getPlace_returns200() throws Exception {
        mockMvc.perform(get("/v1/places/{id}", 167))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.place_id").value(167))
                .andExpect(jsonPath("$.name").value("테스트장소"))
                .andExpect(jsonPath("$.is_open_now").isBoolean());
    }

    @Test
    void getPlace_unknown_returns404() throws Exception {
        mockMvc.perform(get("/v1/places/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void postVisit_withoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/v1/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"place_id\": 167}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void postVisit_and_patchRating_happyPath() throws Exception {
        String visitBody = mockMvc.perform(post("/v1/visits")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"place_id\": 167}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.place_id").value(167))
                .andExpect(jsonPath("$.rating").value(org.hamcrest.Matchers.nullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode visitJson = objectMapper.readTree(visitBody);
        String visitId = visitJson.get("visit_id").asText();

        mockMvc.perform(patch("/v1/visits/{id}/rating", visitId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\": 5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visit_id").value(visitId))
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void patchRating_invalid_returns400() throws Exception {
        String visitBody = mockMvc.perform(post("/v1/visits")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"place_id\": 167}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String visitId = objectMapper.readTree(visitBody).get("visit_id").asText();

        mockMvc.perform(patch("/v1/visits/{id}/rating", visitId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\": 99}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void postVisit_duplicate_returns409() throws Exception {
        mockMvc.perform(post("/v1/visits")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"place_id\": 167}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/visits")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"place_id\": 167}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void authSocial_kakao_returnsUserAndTokens() throws Exception {
        mockMvc.perform(post("/v1/auth/social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "provider": "kakao",
                                  "id_token": "kakao-sub-integration",
                                  "access_token": null
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.user_id").isString())
                .andExpect(jsonPath("$.user.provider").value("kakao"))
                .andExpect(jsonPath("$.tokens.access_token").isString())
                .andExpect(jsonPath("$.tokens.refresh_token").isString())
                .andExpect(jsonPath("$.tokens.token_type").value("Bearer"))
                .andExpect(jsonPath("$.tokens.expires_in").value((int) TokenStore.ACCESS_TOKEN_TTL_SECONDS));
    }

    @Test
    void authRefresh_returnsNewTokens() throws Exception {
        String loginBody = mockMvc.perform(post("/v1/auth/social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "provider": "kakao",
                                  "id_token": "kakao-refresh-flow",
                                  "access_token": null
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String refresh =
                objectMapper.readTree(loginBody).get("tokens").get("refresh_token").asText();

        mockMvc.perform(post("/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refresh_token\": \"" + refresh + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokens.access_token").isString())
                .andExpect(jsonPath("$.tokens.refresh_token").isString())
                .andExpect(jsonPath("$.tokens.token_type").value("Bearer"));
    }

    @Test
    void authLogout_invalidatesRefresh() throws Exception {
        String loginBody = mockMvc.perform(post("/v1/auth/social")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "provider": "kakao",
                                  "id_token": "kakao-logout-flow",
                                  "access_token": null
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode tokens = objectMapper.readTree(loginBody).get("tokens");
        String access = tokens.get("access_token").asText();
        String refresh = tokens.get("refresh_token").asText();

        mockMvc.perform(post("/v1/auth/logout")
                        .header("Authorization", "Bearer " + access)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refresh_token\": \"" + refresh + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(post("/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refresh_token\": \"" + refresh + "\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void deleteMe_softDeletesAndRevokesTokens() throws Exception {
        mockMvc.perform(delete("/v1/users/me").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(userId))
                .andExpect(jsonPath("$.deleted_at").exists());

        assertThat(userRepository.findById(userId)).hasValueSatisfying(u -> assertThat(u.getDeletedAt()).isNotNull());

        mockMvc.perform(post("/v1/visits")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"place_id\": 167}"))
                .andExpect(status().isUnauthorized());
    }
}
