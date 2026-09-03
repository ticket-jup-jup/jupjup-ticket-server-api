-- 외래 키 검사 일시 해제 후 테이블 초기화 (MySQL 문법)
SET
FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE reservations;
TRUNCATE TABLE tickets;
TRUNCATE TABLE seats;
TRUNCATE TABLE performances;
TRUNCATE TABLE programs;
SET
FOREIGN_KEY_CHECKS = 1;

-- 1. 프로그램 데이터 (id 명시)
INSERT INTO programs (id,
                      name,
                      description,
                      type,
                      created_at,
                      updated_at,
                      deleted_at)
VALUES (1, '2026 JUPJUP 콘서트', '줍줍 테스트용 콘서트', 'CONCERT', NOW(), NOW(), NULL),
       (2, '2026 JUPJUP 뮤지컬', '줍줍 테스트용 뮤지컬', 'MUSICAL', NOW(), NOW(), NULL),
       (3, '서울 부산 특별열차', '줍줍 테스트용 열차', 'TRAIN', NOW(), NOW(), NULL);

-- 2. 회차(공연) 데이터 (id 명시)
INSERT INTO performances (id,
                          start_at,
                          end_at,
                          status,
                          venue,
                          program_id,
                          created_at,
                          updated_at)
VALUES (1, '2026-09-10 19:00:00', '2026-09-10 21:30:00', 'UPCOMING', '서울 올림픽공원', 1, NOW(), NOW()),
       (2, '2026-09-11 19:00:00', '2026-09-11 21:30:00', 'UPCOMING', '서울 올림픽공원', 1, NOW(), NOW()),
       (3, '2026-09-20 14:00:00', '2026-09-20 17:00:00', 'UPCOMING', '블루스퀘어', 2, NOW(), NOW()),
       (4, '2026-09-20 19:00:00', '2026-09-20 22:00:00', 'UPCOMING', '블루스퀘어', 2, NOW(), NOW()),
       (5, '2026-10-01 09:00:00', '2026-10-01 12:00:00', 'UPCOMING', '서울역', 3, NOW(), NOW()),
       (6, '2026-10-02 09:00:00', '2026-10-02 12:00:00', 'UPCOMING', '서울역', 3, NOW(), NOW());

-- 3. 좌석 데이터 (id 명시)
INSERT INTO seats (id, section, seat_row, seat_number)
VALUES (1, 'A', 'A', 1),
       (2, 'A', 'A', 2),
       (3, 'A', 'A', 3),
       (4, 'A', 'A', 4),
       (5, 'A', 'A', 5),
       (6, 'B', 'B', 1),
       (7, 'B', 'B', 2),
       (8, 'B', 'B', 3),
       (9, 'B', 'B', 4),
       (10, 'B', 'B', 5);

-- 4. 티켓 데이터
INSERT INTO tickets (performance_id, seat_id, price, created_at, updated_at)
VALUES (1, 1, 50000, NOW(), NOW()),
       (1, 2, 50000, NOW(), NOW()),
       (1, 3, 50000, NOW(), NOW()),
       (1, 4, 50000, NOW(), NOW()),
       (1, 5, 50000, NOW(), NOW()),
       (1, 6, 70000, NOW(), NOW()),
       (1, 7, 70000, NOW(), NOW()),
       (1, 8, 70000, NOW(), NOW()),
       (1, 9, 70000, NOW(), NOW()),
       (1, 10, 70000, NOW(), NOW());

-- 공연 2
INSERT INTO tickets (performance_id, seat_id, price, created_at, updated_at)
SELECT 2, id, CASE WHEN section = 'A' THEN 50000 ELSE 70000 END, NOW(), NOW()
FROM seats;

-- 공연 3
INSERT INTO tickets (performance_id, seat_id, price, created_at, updated_at)
SELECT 3, id, CASE WHEN section = 'A' THEN 60000 ELSE 80000 END, NOW(), NOW()
FROM seats;

-- 공연 4
INSERT INTO tickets (performance_id, seat_id, price, created_at, updated_at)
SELECT 4, id, CASE WHEN section = 'A' THEN 60000 ELSE 80000 END, NOW(), NOW()
FROM seats;

-- 공연 5
INSERT INTO tickets (performance_id, seat_id, price, created_at, updated_at)
SELECT 5, id, 45000, NOW(), NOW()
FROM seats;