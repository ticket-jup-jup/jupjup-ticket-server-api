package org.example.jupjupticketserverapi.seat.controller;

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

import java.util.List;
import org.example.jupjupticketserverapi.seat.controller.SeatController;
import org.example.jupjupticketserverapi.seat.dto.SeatGetResponse;
import org.example.jupjupticketserverapi.seat.service.SeatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SeatController.class)
@AutoConfigureRestDocs
class SeatControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    SeatService seatService;

    @Test
    void 좌석_목록_조회() throws Exception {
        given(seatService.getAll(any())).willReturn(List.of(
                new SeatGetResponse(1L, 1L, "A", "1", 1),
                new SeatGetResponse(2L, 1L, "A", "1", 2)
        ));

        mockMvc.perform(get("/api/seats")
                        .header("X-API-KEY", "{apiKey}")
                        .queryParam("performance", "1"))
                .andExpect(status().isOk())
                .andDo(document("seat-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("X-API-KEY").description("줍줍 서버에 발급된 API Key")
                        ),
                        queryParameters(
                                parameterWithName("performance").description("공연(회차) ID. 없으면 전체 조회").optional()
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("좌석 ID"),
                                fieldWithPath("data[].performanceId").type(JsonFieldType.NUMBER).description("공연(회차) ID"),
                                fieldWithPath("data[].section").type(JsonFieldType.STRING).description("구역"),
                                fieldWithPath("data[].seatRow").type(JsonFieldType.STRING).description("열"),
                                fieldWithPath("data[].seatNumber").type(JsonFieldType.NUMBER).description("좌석 번호"),
                                fieldWithPath("error").type(JsonFieldType.OBJECT).optional().description("성공 시 null")
                        )
                ));
    }
}
