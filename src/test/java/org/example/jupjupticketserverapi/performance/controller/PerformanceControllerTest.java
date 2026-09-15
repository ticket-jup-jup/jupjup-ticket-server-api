package org.example.jupjupticketserverapi.performance.controller;

import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import org.example.jupjupticketserverapi.performance.controller.PerformanceController;
import org.example.jupjupticketserverapi.performance.dto.PerformanceGetResponse;
import org.example.jupjupticketserverapi.performance.entity.PerformanceStatus;
import org.example.jupjupticketserverapi.performance.service.PerformanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PerformanceController.class)
@AutoConfigureRestDocs
class PerformanceControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PerformanceService performanceService;

    @Test
    void 공연_목록_조회() throws Exception {
        LocalDateTime now = LocalDateTime.of(2026, 9, 14, 10, 0);
        given(performanceService.getAll(any())).willReturn(List.of(
                new PerformanceGetResponse(1L, 1L,
                        LocalDateTime.of(2026, 10, 1, 19, 30), LocalDateTime.of(2026, 10, 1, 22, 0),
                        "샤롯데씨어터", PerformanceStatus.UPCOMING, now, now, null)
        ));

        mockMvc.perform(get("/api/performances")
                        .header("X-API-KEY", "{apiKey}")
                        .queryParam("program", "1"))
                .andExpect(status().isOk())
                .andDo(document("performance-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("X-API-KEY").description("줍줍 서버에 발급된 API Key")
                        ),
                        queryParameters(
                                parameterWithName("program").description("프로그램 ID. 없으면 전체 조회").optional()
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("공연(회차) ID"),
                                fieldWithPath("data[].programId").type(JsonFieldType.NUMBER).description("프로그램 ID"),
                                fieldWithPath("data[].startAt").type(JsonFieldType.STRING).description("시작 시각"),
                                fieldWithPath("data[].endAt").type(JsonFieldType.STRING).description("종료 시각"),
                                fieldWithPath("data[].venue").type(JsonFieldType.STRING).description("장소"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING).description("상태. UPCOMING, ONGOING, ENDED, CANCELLED"),
                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("생성 시각"),
                                fieldWithPath("data[].updatedAt").type(JsonFieldType.STRING).description("수정 시각"),
                                fieldWithPath("data[].deletedAt").type(JsonFieldType.STRING).optional().description("삭제 시각. 삭제되지 않았으면 null"),
                                fieldWithPath("error").type(JsonFieldType.OBJECT).optional().description("성공 시 null")
                        )
                ));
    }
}
