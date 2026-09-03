package org.example.jupjupticketserverapi.ticket.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // <-- 이 어노테이션 추가
@Sql(scripts = "/data/test-tickets.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DataJpaTest
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void 전체_티켓_조회() {

        // when
        List<Object[]> result =
                ticketRepository.findTicketList(null, null);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    void 프로그램_ID로_필터링() {

        // when
        List<Object[]> result =
                ticketRepository.findTicketList(1L, null);

        // then
        assertThat(result)
                .isNotEmpty()
                .allMatch(ticket ->
                        ((Long) ticket[1]).equals(1L)
                );
    }

    @Test
    void 회차_ID로_필터링() {

        // when
        List<Object[]> result =
                ticketRepository.findTicketList(null, 1L);

        // then
        assertThat(result)
                .isNotEmpty()
                .allMatch(ticket ->
                        ((Long) ticket[2]).equals(1L)
                );
    }

    @Test
    void 프로그램과_회차_ID로_필터링() {

        // when
        List<Object[]> result =
                ticketRepository.findTicketList(1L, 1L);

        // then
        assertThat(result)
                .isNotEmpty()
                .allMatch(ticket ->
                        ((Long) ticket[1]).equals(1L)
                                && ((Long) ticket[2]).equals(1L)
                );
    }
}