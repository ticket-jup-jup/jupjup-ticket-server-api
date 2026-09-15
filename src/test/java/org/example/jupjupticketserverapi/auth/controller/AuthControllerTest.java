package org.example.jupjupticketserverapi.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.example.jupjupticketserverapi.auth.controller.AuthController;
import org.example.jupjupticketserverapi.auth.dto.AuthVerifyResponse;
import org.example.jupjupticketserverapi.auth.exception.InvalidPasswordException;
import org.example.jupjupticketserverapi.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureRestDocs
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AuthService authService;

    @Test
    void 사용자_인증() throws Exception {
        String body = """
                {"email": "user@example.com", "password": "password1!"}
                """;
        given(authService.verify(any()))
                .willReturn(List.of(new AuthVerifyResponse(1L, "user@example.com", "홍길동")));

        mockMvc.perform(post("/api/auth/verify")
                        .header("X-API-KEY", "{apiKey}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andDo(document("auth-verify",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("X-API-KEY").description("줍줍 서버에 발급된 API Key")
                        ),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일. 이메일 형식"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                fieldWithPath("data[].email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("error").type(JsonFieldType.OBJECT).optional().description("성공 시 null")
                        )
                ));
    }

    /**
     * 실패 응답 형식의 대표 예시.
     * 모든 API 의 실패 응답은 GlobalExceptionHandler 가 같은 구조로 만들므로 프로젝트 전체에서 이 테스트 하나만 둔다.
     * index.adoc 의 "공통 응답 형식 > 실패" 섹션에서 사용한다.
     */
    @Test
    void 공통_실패_응답() throws Exception {
        String body = """
                {"email": "user@example.com", "password": "wrong-password"}
                """;
        willThrow(new InvalidPasswordException("비밀번호가 일치하지 않습니다."))
                .given(authService).verify(any());

        mockMvc.perform(post("/api/auth/verify")
                        .header("X-API-KEY", "{apiKey}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andDo(document("common-error",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("실패 시 false"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY).optional().description("실패 시 null"),
                                fieldWithPath("error.code").type(JsonFieldType.STRING).description("에러 코드. 각 API 의 에러 표 참고"),
                                fieldWithPath("error.message").type(JsonFieldType.STRING).description("에러 메시지")
                        )
                ));
    }
}