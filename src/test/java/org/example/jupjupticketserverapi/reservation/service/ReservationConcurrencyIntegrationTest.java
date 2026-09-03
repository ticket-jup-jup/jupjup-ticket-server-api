package org.example.jupjupticketserverapi.reservation.service;

import org.example.jupjupticketserverapi.performance.entity.Performance;
import org.example.jupjupticketserverapi.performance.entity.PerformanceStatus;
import org.example.jupjupticketserverapi.performance.repository.PerformanceRepository;
import org.example.jupjupticketserverapi.program.entity.Program;
import org.example.jupjupticketserverapi.program.entity.ProgramType;
import org.example.jupjupticketserverapi.program.repository.ProgramRepository;
import org.example.jupjupticketserverapi.reservation.dto.ReservationCreateRequest;
import org.example.jupjupticketserverapi.reservation.exception.ReservationAlreadyExistsException;
import org.example.jupjupticketserverapi.reservation.repository.ReservationRepository;
import org.example.jupjupticketserverapi.seat.entity.Seat;
import org.example.jupjupticketserverapi.seat.repository.SeatRepository;
import org.example.jupjupticketserverapi.ticket.entity.Ticket;
import org.example.jupjupticketserverapi.ticket.repository.TicketRepository;
import org.example.jupjupticketserverapi.user.entity.User;
import org.example.jupjupticketserverapi.user.respository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReservationConcurrencyIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private SeatRepository seatRepository;

    private Long targetTicketId;
    private Long user1Id;
    private Long user2Id;

    private ReservationCreateRequest createRequest(Long userId, Long ticketId) {
        ReservationCreateRequest request = new ReservationCreateRequest();
        ReflectionTestUtils.setField(request, "userId", userId);
        ReflectionTestUtils.setField(request, "ticketId", ticketId);
        return request;
    }

    @BeforeEach
    void setUp() {
        // 기존 예약 초기화
        reservationRepository.deleteAll();

        // 1. 유저 2명 생성 (생성자 파라미터가 맞지 않다면 ReflectionTestUtils로 주입)
        User user1 = userRepository.save(new User("ci_user1@test.com", "pass123", "테스터1"));
        User user2 = userRepository.save(new User("ci_user2@test.com", "pass123", "테스터2"));
        user1Id = user1.getId();
        user2Id = user2.getId();

        // 2. 부모 엔티티 Program, Performance, Seat 생성
        // (각 엔티티 생성자나 필수 필드에 맞춰 작성)
        Program program = new Program("테스트 공연", ProgramType.CONCERT, "설명");
        programRepository.save(program);

        Performance performance = new Performance(
                program,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                "서울 경기장",
                PerformanceStatus.UPCOMING
        );
        
        performanceRepository.save(performance);

        Seat seat = new Seat("A", "A", 1);
        seatRepository.save(seat);

        // 3. 테스트 대상 Ticket 생성
        Ticket ticket = ticketRepository.save(new Ticket(performance, seat, BigDecimal.valueOf(50000)));
        targetTicketId = ticket.getId();
    }

    @Test
    @DisplayName("동일 티켓에 동시 2건의 예약이 들어오면 정확히 1건만 성공하고 1건은 실패해야 한다")
    void concurrencyReservationTest() throws InterruptedException {
        // given
        ReservationCreateRequest requestUser1 = createRequest(user1Id, targetTicketId);
        ReservationCreateRequest requestUser2 = createRequest(user2Id, targetTicketId);

        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when: User 1 요청
        executorService.submit(() -> {
            try {
                startLatch.await();
                reservationService.create(requestUser1);
                successCount.incrementAndGet();
            } catch (ReservationAlreadyExistsException e) {
                failCount.incrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                endLatch.countDown();
            }
        });

        // when: User 2 요청
        executorService.submit(() -> {
            try {
                startLatch.await();
                reservationService.create(requestUser2);
                successCount.incrementAndGet();
            } catch (ReservationAlreadyExistsException e) {
                failCount.incrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                endLatch.countDown();
            }
        });

        // 동시 출발 신호
        startLatch.countDown();
        endLatch.await();
        executorService.shutdown();

        // then: 1건 성공, 1건 실패 검증
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
    }
}