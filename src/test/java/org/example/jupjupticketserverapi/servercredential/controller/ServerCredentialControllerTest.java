package org.example.jupjupticketserverapi.servercredential.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

import org.example.jupjupticketserverapi.servercredential.dto.ServerCredentialCreateResponse;
import org.example.jupjupticketserverapi.servercredential.service.ServerCredentialService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** API Key 를 발급하는 API. 키를 받기 전이므로 X-API-KEY 헤더가 없다. */
@WebMvcTest(ServerCredentialController.class)
@AutoConfigureRestDocs
class ServerCredentialControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ServerCredentialService serverCredentialService;

    @Test
    void API_Key_발급() throws Exception {
        String body = """
                {"serviceName": "jupjup"}
                """;
        given(serverCredentialService.create(any()))
                .willReturn(List.of(new ServerCredentialCreateResponse("a1b2c3d4-e5f6-7890-abcd-ef1234567890")));

        mockMvc.perform(post("/api/server-credential")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andDo(document("server-credential-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("serviceName").type(JsonFieldType.STRING).description("서비스명. 30자 이내")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data[].apiKey").type(JsonFieldType.STRING).description("발급된 API Key. 이후 요청의 X-API-KEY 헤더에 사용"),
                                fieldWithPath("error").type(JsonFieldType.OBJECT).optional().description("성공 시 null")
                        )
                ));
    }
}