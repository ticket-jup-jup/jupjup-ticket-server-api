package org.example.jupjupticketserverapi.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.example.jupjupticketserverapi.payment.dto.PaymentResponse;
import org.example.jupjupticketserverapi.payment.entity.PaymentMethod;
import org.example.jupjupticketserverapi.payment.entity.PaymentStatus;
import org.example.jupjupticketserverapi.reservation.controller.ReservationController;
import org.example.jupjupticketserverapi.reservation.dto.ReservationCancelResponse;
import org.example.jupjupticketserverapi.reservation.dto.ReservationConfirmResponse;
import org.example.jupjupticketserverapi.reservation.dto.ReservationCreateResponse;
import org.example.jupjupticketserverapi.reservation.dto.ReservationGetResponse;
import org.example.jupjupticketserverapi.reservation.dto.ReservationResponse;
import org.example.jupjupticketserverapi.reservation.entity.ReservationStatus;
import org.example.jupjupticketserverapi.reservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReservationController.class)
@AutoConfigureRestDocs
class ReservationControllerTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 14, 10, 0);

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ReservationService reservationService;

    /** 일반 요청용 API. 서버 간 허용 목록에 없으므로 X-API-KEY 를 보내지 않는다. */
    @Test
    void 예약_목록_조회() throws Exception {
        given(reservationService.getAll()).willReturn(List.of(
                new ReservationGetResponse(1L, 1L, 10L, ReservationStatus.CONFIRMED, NOW.plusMinutes(10), NOW, NOW)
        ));

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andDo(document("reservation-list",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("예약 ID"),
                                fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                fieldWithPath("data[].ticketId").type(JsonFieldType.NUMBER).description("티켓 ID"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING).description("예약 상태. PENDING, CONFIRMED, EXPIRED, REFUNDED"),
                                fieldWithPath("data[].expiresAt").type(JsonFieldType.STRING).description("임시 예약 만료 시각"),
                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("생성 시각"),
                                fieldWithPath("data[].updatedAt").type(JsonFieldType.STRING).description("수정 시각"),
                                fieldWithPath("error").type(JsonFieldType.OBJECT).optional().description("성공 시 null")
                        )
                ));
    }

    @Test
    void 임시_예약_생성() throws Exception {
        String body = """
                {"userId": 1, "ticketId": 10}
                """;
        given(reservationService.create(any())).willReturn(List.of(
                new ReservationCreateResponse(
                        new ReservationResponse(1L, 1L, 10L, "PENDING", NOW.plusMinutes(10), NOW, NOW),
                        new PaymentResponse(1L, 1L, new BigDecimal("59800"), PaymentMethod.CARD, PaymentStatus.PENDING, null, NOW)
                )
        ));

        mockMvc.perform(post("/api/reservations")
                        .header("X-API-KEY", "{apiKey}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andDo(document("reservation-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("X-API-KEY").description("줍줍 서버에 발급된 API Key")
                        ),
                        requestFields(
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                fieldWithPath("ticketId").type(JsonFieldType.NUMBER).description("티켓 ID")
                        ),
                        responseFields(envelope(reservationAndPaymentFields()))
                ));
    }

    @Test
    void 예약_확정() throws Exception {
        String body = """
                {"paymentMethod": "CARD"}
                """;
        given(reservationService.confirm(any(), any())).willReturn(List.of(
                new ReservationConfirmResponse(
                        new ReservationResponse(1L, 1L, 10L, "CONFIRMED", NOW.plusMinutes(10), NOW, NOW),
                        new PaymentResponse(1L, 1L, new BigDecimal("59800"), PaymentMethod.CARD, PaymentStatus.COMPLETED, NOW, NOW)
                )
        ));

        mockMvc.perform(post("/api/reservations/{reservationId}/confirm", 1L)
                        .header("X-API-KEY", "{apiKey}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andDo(document("reservation-confirm",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("X-API-KEY").description("줍줍 서버에 발급된 API Key")
                        ),
                        pathParameters(
                                parameterWithName("reservationId").description("확정할 예약 ID")
                        ),
                        requestFields(
                                fieldWithPath("paymentMethod").type(JsonFieldType.STRING).description("결제 수단. CARD, CASH")
                        ),
                        responseFields(envelope(reservationAndPaymentFields()))
                ));
    }

    @Test
    void 예약_취소() throws Exception {
        given(reservationService.cancel(any()))
                .willReturn(new ReservationCancelResponse(1L, "REFUNDED", 10L));

        mockMvc.perform(post("/api/reservations/{reservationId}/cancel", 1L)
                        .header("X-API-KEY", "{apiKey}"))
                .andExpect(status().isOk())
                .andDo(document("reservation-cancel",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("X-API-KEY").description("줍줍 서버에 발급된 API Key")
                        ),
                        pathParameters(
                                parameterWithName("reservationId").description("취소할 예약 ID")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data[].reservationId").type(JsonFieldType.NUMBER).description("예약 ID"),
                                fieldWithPath("data[].status").type(JsonFieldType.STRING).description("취소 후 예약 상태. REFUNDED"),
                                fieldWithPath("data[].ticketId").type(JsonFieldType.NUMBER).description("복구된 티켓 ID"),
                                fieldWithPath("error").type(JsonFieldType.OBJECT).optional().description("성공 시 null")
                        )
                ));
    }

    // ---------- helper ----------

    /** ApiResponse 껍데기(success, error)로 data 필드 목록을 감싼다. */
    private static List<FieldDescriptor> envelope(List<FieldDescriptor> dataFields) {
        List<FieldDescriptor> fields = new ArrayList<>();
        fields.add(fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"));
        fields.addAll(dataFields);
        fields.add(fieldWithPath("error").type(JsonFieldType.OBJECT).optional().description("성공 시 null"));
        return fields;
    }

    /** 예약 생성/확정 응답에 공통으로 들어가는 reservation, payment 중첩 객체 필드. */
    private static List<FieldDescriptor> reservationAndPaymentFields() {
        return List.of(
                fieldWithPath("data[].reservation.reservationId").type(JsonFieldType.NUMBER).description("예약 ID"),
                fieldWithPath("data[].reservation.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                fieldWithPath("data[].reservation.ticketId").type(JsonFieldType.NUMBER).description("티켓 ID"),
                fieldWithPath("data[].reservation.status").type(JsonFieldType.STRING).description("예약 상태. PENDING, CONFIRMED, EXPIRED, REFUNDED"),
                fieldWithPath("data[].reservation.expiresAt").type(JsonFieldType.STRING).description("임시 예약 만료 시각"),
                fieldWithPath("data[].reservation.createdAt").type(JsonFieldType.STRING).description("생성 시각"),
                fieldWithPath("data[].reservation.updatedAt").type(JsonFieldType.STRING).description("수정 시각"),
                fieldWithPath("data[].payment.paymentId").type(JsonFieldType.NUMBER).description("결제 ID"),
                fieldWithPath("data[].payment.reservationId").type(JsonFieldType.NUMBER).description("예약 ID"),
                fieldWithPath("data[].payment.amount").type(JsonFieldType.NUMBER).description("결제 금액"),
                fieldWithPath("data[].payment.paymentMethod").type(JsonFieldType.STRING).description("결제 수단. CARD, CASH"),
                fieldWithPath("data[].payment.status").type(JsonFieldType.STRING).description("결제 상태. PENDING, COMPLETED, FAILED, CANCELLED"),
                fieldWithPath("data[].payment.paidAt").type(JsonFieldType.STRING).optional().description("결제 완료 시각. 결제 전이면 null"),
                fieldWithPath("data[].payment.createdAt").type(JsonFieldType.STRING).description("생성 시각")
        );
    }
}