package org.example.jupjupticketserverapi.reservation.service;

import org.example.jupjupticketserverapi.reservation.dto.ReservationCreateRequest;
import org.example.jupjupticketserverapi.reservation.exception.ReservationAlreadyExistsException;
import org.example.jupjupticketserverapi.reservation.repository.ReservationRepository;
import org.example.jupjupticketserverapi.ticket.entity.Ticket;
import org.example.jupjupticketserverapi.ticket.repository.TicketRepository;
import org.example.jupjupticketserverapi.user.entity.User;
import org.example.jupjupticketserverapi.user.respository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

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

    private ReservationCreateRequest createRequest(Long userId, Long ticketId) {
        ReservationCreateRequest request = new ReservationCreateRequest();
        ReflectionTestUtils.setField(request, "userId", userId);
        ReflectionTestUtils.setField(request, "ticketId", ticketId);
        return request;
    }

    private User createTestUser(String email, String name) {
        User user = new User(email, "password123", name);
        return userRepository.save(user);
    }

    @Test
    @DisplayName("동일 티켓에 동시 2건의 예약이 들어오면 정확히 1건만 성공하고 1건은 실패해야 한다")
    void concurrencyReservationTest() throws InterruptedException {
        // given
        reservationRepository.deleteAll();

        User user1 = createTestUser("test1_" + System.currentTimeMillis() + "@test.com", "테스터1");
        User user2 = createTestUser("test2_" + System.currentTimeMillis() + "@test.com", "테스터2");
        Long user1Id = user1.getId();
        Long user2Id = user2.getId();

        Ticket ticket = ticketRepository.findAll().stream().findFirst()
                .orElseGet(() -> {
                    throw new IllegalStateException("CI DB에 티켓 데이터가 없습니다. flyway/data.sql을 주입하거나 엔티티를 생성해주세요.");
                });
        Long targetTicketId = ticket.getId();

        ReservationCreateRequest requestUser1 = createRequest(user1Id, targetTicketId);
        ReservationCreateRequest requestUser2 = createRequest(user2Id, targetTicketId);

        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // 임시예약 생성1
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

        // 임시예약 생성2
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