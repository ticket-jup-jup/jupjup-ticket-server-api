package org.example.jupjupticketserverapi.program.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import org.example.jupjupticketserverapi.program.controller.ProgramController;
import org.example.jupjupticketserverapi.program.dto.ProgramGetResponse;
import org.example.jupjupticketserverapi.program.entity.ProgramType;
import org.example.jupjupticketserverapi.program.service.ProgramService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProgramController.class)
@AutoConfigureRestDocs
class ProgramControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ProgramService programService;

    @Test
    void 프로그램_목록_조회() throws Exception {
        LocalDateTime now = LocalDateTime.of(2026, 9, 14, 10, 0);
        given(programService.getAll()).willReturn(List.of(
                new ProgramGetResponse(1L, "KTX 서울-부산", ProgramType.TRAIN, "고속열차 노선", now, now, null),
                new ProgramGetResponse(2L, "오페라의 유령", ProgramType.MUSICAL, "샤롯데씨어터", now, now, null)
        ));

        mockMvc.perform(get("/api/programs")
                        .header("X-API-KEY", "{apiKey}"))
                .andExpect(status().isOk())
                .andDo(document("program-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("X-API-KEY").description("줍줍 서버에 발급된 API Key")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("프로그램 ID"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("프로그램명"),
                                fieldWithPath("data[].type").type(JsonFieldType.STRING).description("유형. TRAIN, MUSICAL, CONCERT"),
                                fieldWithPath("data[].description").type(JsonFieldType.STRING).description("설명"),
                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("생성 시각"),
                                fieldWithPath("data[].updatedAt").type(JsonFieldType.STRING).description("수정 시각"),
                                fieldWithPath("data[].deletedAt").type(JsonFieldType.STRING).optional().description("삭제 시각. 삭제되지 않았으면 null"),
                                fieldWithPath("error").type(JsonFieldType.OBJECT).optional().description("성공 시 null")
                        )
                ));
    }
}
