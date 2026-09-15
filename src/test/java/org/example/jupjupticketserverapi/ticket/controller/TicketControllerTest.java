package org.example.jupjupticketserverapi.ticket.controller;

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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.example.jupjupticketserverapi.ticket.controller.TicketController;
import org.example.jupjupticketserverapi.ticket.dto.TicketGetResponse;
import org.example.jupjupticketserverapi.ticket.dto.TicketInternalGetResponse;
import org.example.jupjupticketserverapi.ticket.entity.TicketStatus;
import org.example.jupjupticketserverapi.ticket.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TicketController.class)
@AutoConfigureRestDocs
class TicketControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TicketService ticketService;

    /** 일반 요청용 API. 서버 간 허용 목록에 없으므로 X-API-KEY 를 보내지 않는다. */
    @Test
    void 티켓_목록_조회() throws Exception {
        given(ticketService.getTickets(any(), any())).willReturn(List.of(
                new TicketGetResponse(1L, 1L, 1L, "A", "A-1-1", new BigDecimal("59800"), TicketStatus.AVAILABLE),
                new TicketGetResponse(2L, 1L, 1L, "A", "A-1-2", new BigDecimal("59800"), TicketStatus.SOLD)
        ));

        mockMvc.perform(get("/api/tickets")
                        .queryParam("program", "1")
                        .queryParam("performance", "1"))
                .andExpect(status().isOk())
                .andDo(document("ticket-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("program").description("프로그램 ID. 없으면 전체").optional(),
                                parameterWithName("performance").description("공연(회차) ID. 없으면 전체").optional()
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("티켓 ID"),
                                fieldWithPath("data[].programId").type(JsonFieldType.NUMBER).description("프로그램 ID"),
                                fieldWithPath("data[].performanceId").type(JsonFieldType.NUMBER).description("공연(회차) ID"),
                                fieldWithPath("data[].section").type(JsonFieldType.STRING).description("구역"),
                                fieldWithPath("data[].seat").type(JsonFieldType.STRING).description("좌석 표기"),
                                fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING).description("상태. AVAILABLE, RESERVED, SOLD"),
                                fieldWithPath("error").type(JsonFieldType.OBJECT).optional().description("성공 시 null")
                        )
                ));
    }

    /** 서버 간 API. 줍줍 서버가 X-API-KEY 로 호출한다. */
    @Test
    void 티켓_목록_조회_서버간() throws Exception {
        LocalDateTime now = LocalDateTime.of(2026, 9, 14, 10, 0);
        given(ticketService.getInternalTickets(any(), any())).willReturn(List.of(
                new TicketInternalGetResponse(1L, 1L, "KTX 서울-부산",
                        LocalDateTime.of(2026, 10, 1, 8, 0), LocalDateTime.of(2026, 10, 1, 10, 30),
                        "서울역", 1L, new BigDecimal("59800"), TicketStatus.AVAILABLE, now, now)
        ));

        mockMvc.perform(get("/api/internal/tickets")
                        .header("X-API-KEY", "{apiKey}")
                        .queryParam("performance", "1")
                        .queryParam("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andDo(document("ticket-internal-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("X-API-KEY").description("줍줍 서버에 발급된 API Key")
                        ),
                        queryParameters(
                                parameterWithName("performance").description("공연(회차) ID. 없으면 전체").optional(),
                                parameterWithName("status").description("티켓 상태 필터. AVAILABLE, RESERVED, SOLD").optional()
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("티켓 ID"),
                                fieldWithPath("data[].performanceId").type(JsonFieldType.NUMBER).description("공연(회차) ID"),
                                fieldWithPath("data[].programName").type(JsonFieldType.STRING).description("프로그램명"),
                                fieldWithPath("data[].startAt").type(JsonFieldType.STRING).description("시작 시각"),
                                fieldWithPath("data[].endAt").type(JsonFieldType.STRING).description("종료 시각"),
                                fieldWithPath("data[].venue").type(JsonFieldType.STRING).description("장소"),
                                fieldWithPath("data[].seatId").type(JsonFieldType.NUMBER).description("좌석 ID"),
                                fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING).description("상태. AVAILABLE, RESERVED, SOLD"),
                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("생성 시각"),
                                fieldWithPath("data[].updatedAt").type(JsonFieldType.STRING).description("수정 시각"),
                                fieldWithPath("error").type(JsonFieldType.OBJECT).optional().description("성공 시 null")
                        )
                ));
    }
}
