DROP DATABASE Control;
CREATE DATABASE Control;

USE Control;

CREATE TABLE Staff
(
    staff_id   INT AUTO_INCREMENT PRIMARY KEY,
    worker_id  VARCHAR(5)   NOT NULL UNIQUE,
    email      VARCHAR(128) NOT NULL UNIQUE,
    name       VARCHAR(64)  NOT NULL,
    last_name  VARCHAR(256) NOT NULL,
    password   VARCHAR(64)  NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Role
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    role     ENUM ('ADMIN', 'COORDINATOR', 'SUPERVISOR', 'TUTOR') NOT NULL,
    staff_id INT                                                  NOT NULL,
    FOREIGN KEY (staff_id) REFERENCES Staff (staff_id) ON DELETE CASCADE
);

CREATE TABLE Program
(
    program_id INT AUTO_INCREMENT PRIMARY KEY,
    acronym    VARCHAR(8)   NOT NULL UNIQUE,
    name       VARCHAR(128) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Coordinates
(
    staff_id   INT NOT NULL,
    program_id INT NOT NULL,
    PRIMARY KEY (staff_id, program_id),
    FOREIGN KEY (staff_id) REFERENCES Staff (staff_id) ON DELETE CASCADE,
    FOREIGN KEY (program_id) REFERENCES Program (program_id),
    UNIQUE (staff_id)
);

CREATE TABLE Tutored
(
    tutored_id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment VARCHAR(8)                 NOT NULL UNIQUE,
    email      VARCHAR(128)               NOT NULL UNIQUE,
    name       VARCHAR(64)                NOT NULL,
    last_name  VARCHAR(256)               NOT NULL,
    state      ENUM ('IN_RISK', 'STABLE') NOT NULL DEFAULT 'STABLE',
    program_id INT                        NOT NULL,
    tutor_id   INT,
    created_at TIMESTAMP                  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (program_id) REFERENCES Program (program_id),
    FOREIGN KEY (tutor_id) REFERENCES Staff (staff_id) ON DELETE SET NULL
);

DROP VIEW IF EXISTS CompleteTutoredView;
CREATE VIEW CompleteTutoredView AS
SELECT T.tutored_id,
       T.enrollment,
       T.email,
       T.name,
       T.last_name,
       T.state,
       P.program_id                     as program_id,
       P.name                           as program_name,
       T.tutor_id,
       CONCAT(S.name, ' ', S.last_name) AS tutor_name,
       T.created_at
FROM Tutored T
         LEFT JOIN
     Program P ON T.program_id = P.program_id
         LEFT JOIN
     Staff S ON T.tutor_id = S.staff_id;

DROP VIEW IF EXISTS CompleteProgramView;
CREATE VIEW CompleteProgramView AS
SELECT P.program_id,
       S.staff_id                       AS coordinator_id,
       P.name,
       CONCAT(S.name, ' ', S.last_name) AS name_coordinator,
       P.acronym,
       P.created_at
FROM Program P
         LEFT JOIN
     Coordinates C ON P.program_id = C.program_id
         LEFT JOIN
     Staff S ON C.staff_id = S.staff_id;

# Connects Tutors to the Programs they tutor
# A Tutor can tutor in multiple Programs
CREATE TABLE Tutors
(
    program_id INT NOT NULL,
    staff_id   INT NOT NULL,
    PRIMARY KEY (staff_id, program_id),
    FOREIGN KEY (staff_id) REFERENCES Staff (staff_id) ON DELETE CASCADE,
    FOREIGN KEY (program_id) REFERENCES Program (program_id)
);

DROP VIEW IF EXISTS StaffRoleList;
CREATE VIEW StaffRoleList AS
SELECT S.staff_id,
       S.name,
       GROUP_CONCAT(r.role ORDER BY r.role ASC SEPARATOR ', ') AS roles
FROM Staff S
         JOIN
     Role R ON S.staff_id = R.staff_id
GROUP BY s.staff_id, S.name;

DROP VIEW IF EXISTS CompleteStaffView;
CREATE VIEW CompleteStaffView AS
SELECT S.staff_id,
       S.worker_id,
       S.email,
       S.name,
       S.last_name,
       GROUP_CONCAT(R.role ORDER BY R.role ASC SEPARATOR ', ') AS roles,
       CP.program_id                                           as id_coordinated_program,
       CP.name                                                 as coordinated_program_name,
       S.password,
       S.created_at
FROM Staff S
         LEFT JOIN Role R ON R.staff_id = S.staff_id
         LEFT JOIN CompleteProgramView CP ON CP.coordinator_id = S.staff_id
GROUP BY staff_id, program_id;

CREATE TABLE Period
(
    year     INT                         NOT NULL,
    semester ENUM ('AUG_JAN', 'FEB_JUL') NOT NULL,
    PRIMARY KEY (year, semester)
);

CREATE TABLE TutoringSessionPlan
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    period_year      INT                                                                                  NOT NULL,
    period_semester  ENUM ('AUG_JAN', 'FEB_JUL')                                                          NOT NULL,
    appointment_date DATETIME                                                                             NOT NULL,
    program_id       INT                                                                                  NOT NULL,
    kind             ENUM ('FIRST_TUTORING_SESSION', 'SECOND_TUTORING_SESSION', 'THIRD_TUTORING_SESSION') NOT NULL,
    created_at       TIMESTAMP                                                                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (period_year, period_semester) REFERENCES Period (year, semester),
    FOREIGN KEY (program_id) REFERENCES Program (program_id),
    UNIQUE (program_id, period_year, period_semester, kind)
);

DROP VIEW IF EXISTS CompleteTutoringSessionPlanView;
CREATE VIEW CompleteTutoringSessionPlanView AS
SELECT TSP.id,
       TSP.program_id,
       TSP.period_year,
       TSP.period_semester,
       IF(TSP.appointment_date < NOW(), 'COMPLETED', 'SCHEDULED') AS state,
       TSP.appointment_date,
       TSP.kind,
       TSP.created_at
FROM TutoringSessionPlan TSP;

CREATE TABLE TutoringSession
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    tutored_id      INT                                       NOT NULL,
    tutor_id        INT                                       NOT NULL,
    session_plan_id INT                                       NOT NULL,
    state           ENUM ('SCHEDULED', 'COMPLETED', 'MISSED') NOT NULL DEFAULT 'SCHEDULED',
    hour            VARCHAR(5)                                NOT NULL CHECK (hour REGEXP '^([01]?[0-9]|2[0-3]):[0-5][0-9]$'),
    created_at      TIMESTAMP                                 NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tutored_id) REFERENCES Tutored (tutored_id) ON DELETE CASCADE,
    FOREIGN KEY (tutor_id) REFERENCES Staff (staff_id) ON DELETE CASCADE,
    FOREIGN KEY (session_plan_id) REFERENCES TutoringSessionPlan (id),
    UNIQUE (tutored_id, session_plan_id)
);

DROP VIEW IF EXISTS CompleteTutoringSessionView;
CREATE VIEW CompleteTutoringSessionView AS
SELECT TS.id,
       T.tutored_id,
       TP.id                            AS session_plan_id,
       S.staff_id                       AS tutor_id,
       CONCAT(S.name, ' ', S.last_name) AS tutor_name,
       CONCAT(T.name, ' ', T.last_name) AS tutored_name,
       T.enrollment                     as tutored_enrollment,
       TP.kind                          AS session_kind,
       TS.state                         AS state,
       TS.hour,
       TP.period_year,
       TP.period_semester,
       TP.appointment_date,
       TS.created_at
FROM TutoringSession TS
         JOIN Tutored T ON TS.tutored_id = T.tutored_id
         JOIN TutoringSessionPlan TP ON TS.session_plan_id = TP.id
         JOIN Staff S ON TS.tutor_id = S.staff_id;

CREATE TABLE Report
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    staff_id        INT                                                NOT NULL,
    session_plan_id INT                                                NOT NULL,
    content         VARCHAR(1024)                                      NOT NULL,
    type            ENUM ('TUTORING_SESSION_REPORT', 'GENERAL_REPORT') NOT NULL,
    state           ENUM ('DRAFT', 'REVIEWED', 'SENT')                 NOT NULL DEFAULT 'DRAFT',
    created_at      TIMESTAMP                                          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES Staff (staff_id) ON DELETE CASCADE,
    FOREIGN KEY (session_plan_id) REFERENCES TutoringSessionPlan (id)
);

CREATE TABLE Evidence
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    report_id    INT       NOT NULL,
    byte_content LONGBLOB  NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (report_id) REFERENCES Report (id) ON DELETE CASCADE
);

CREATE TABLE ReportResponse
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    staff_id   INT           NOT NULL,
    report_id  INT           NOT NULL,
    response   VARCHAR(1024) NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (report_id) REFERENCES ReportResponse (id) ON DELETE CASCADE,
    FOREIGN KEY (staff_id) REFERENCES Staff (staff_id) ON DELETE CASCADE
);

DROP VIEW IF EXISTS CompleteReportView;
CREATE VIEW CompleteReportView AS
SELECT R.id,
       R.staff_id,
       R.session_plan_id,
       TSP.program_id,
       RR.id                            as response_id,
       CONCAT(S.name, ' ', S.last_name) AS name_staff,
       R.content,
       TSP.kind                         AS session_kind,
       R.type,
       R.state,
       TSP.period_year,
       TSP.period_semester,
       R.created_at
FROM Report R
         JOIN Staff S ON S.staff_id = R.staff_id
         JOIN TutoringSessionPlan TSP on R.session_plan_id = TSP.id
         LEFT JOIN ReportResponse RR on RR.report_id = R.id;

CREATE TABLE Issue
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    tutored_id      INT                                 NOT NULL,
    staff_id        INT                                 NOT NULL,
    period_year     INT                                 NOT NULL,
    period_semester ENUM ('AUG_JAN', 'FEB_JUL')         NOT NULL,
    content         VARCHAR(1024)                       NOT NULL,
    state           ENUM ('RESOLVED', 'ON_GOING_ISSUE') NOT NULL DEFAULT 'ON_GOING_ISSUE',
    created_at      TIMESTAMP                           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tutored_id) REFERENCES Tutored (tutored_id) ON DELETE CASCADE,
    FOREIGN KEY (staff_id) REFERENCES Staff (staff_id) ON DELETE CASCADE,
    FOREIGN KEY (period_year, period_semester) REFERENCES Period (year, semester)
);

DROP VIEW IF EXISTS CompleteIssueView;
CREATE VIEW CompleteIssueView AS
SELECT I.id,
       T.tutored_id,
       CONCAT(T.name, ' ', T.last_name) AS tutored_name,
       S.staff_id                       AS staff_id,
       CONCAT(S.name, ' ', S.last_name) AS staff_name,
       I.period_year,
       I.period_semester,
       I.content,
       I.state,
       I.created_at
FROM Issue I
         JOIN Tutored T ON I.tutored_id = T.tutored_id
         JOIN Staff S ON I.staff_id = S.staff_id;

# Authentication and Authorization Setup

DROP USER IF EXISTS 'control_admin'@'localhost';
DROP ROLE IF EXISTS control_admin_role;

CREATE USER control_admin@localhost IDENTIFIED BY 'ADMIN_CONTROL';
CREATE ROLE control_admin_role;
GRANT control_admin_role TO control_admin@localhost;
GRANT EXECUTE, SELECT, INSERT, UPDATE, DELETE ON Control.* TO control_admin_role;
SET DEFAULT ROLE ALL TO control_admin@localhost;

# Automatically create a new Period based on the current date
# If the current month is between January and June, create a Period for FEB_JUL of the current year
# Else (if the current month is between July and December) create a Period for AUG_JAN of the next year
# If there is already a Period for that year and semester, do not create a duplicate
DROP PROCEDURE IF EXISTS create_period;
CREATE PROCEDURE create_period()
BEGIN
    DECLARE current_month INT;
    DECLARE current_year INT;
    DECLARE current_semester ENUM ('AUG_JAN', 'FEB_JUL');

    SET current_month = MONTH(CURDATE());

    IF current_month BETWEEN 1 AND 6 THEN
        SET current_semester = 'FEB_JUL';
        SET current_year = YEAR(CURDATE());
    ELSE
        SET current_semester = 'AUG_JAN';
        SET current_year = YEAR(CURDATE()) + 1;
    END IF;

    INSERT IGNORE INTO Period (year, semester)
    VALUES (current_year, current_semester);
END;

# Automatically get the latest Period stored in the Period table
DROP PROCEDURE IF EXISTS get_or_create_latest_period;
CREATE PROCEDURE get_or_create_latest_period()
BEGIN
    CALL create_period();

    SELECT year, semester
    FROM Period
    ORDER BY year DESC,
             CASE semester
                 WHEN 'AUG_JAN' THEN 2
                 WHEN 'FEB_JUL' THEN 1
                 END DESC
    LIMIT 1;
END;

INSERT INTO Program (acronym, name)
VALUES ('LIS', 'Lic. en Ingeniería de Software');
INSERT INTO Program (acronym, name)
VALUES ('LISTI', 'Lic. en Ingeniería de Sistemas y Tecnologías de la Información');
INSERT INTO Program (acronym, name)
VALUES ('LCD', 'Lic. en Ingeniería de Ciencia de Datos');
INSERT INTO Program (acronym, name)
VALUES ('LICIC', 'Lic. en Ingeniería de Ciberseguridad e Infraestructura de Cómputo');

INSERT INTO Staff (worker_id, email, name, last_name, password)
VALUES (1, 'Lee@email.com', 'Shawn', 'Lee', 'not-a-real-password'),
       (2, 'Ocharan@email.com', 'Octavio', 'Ocharan', 'not-a-real-password'),
       (3, 'Polo@email.com', 'Ana Luz', 'Polo Estrella', 'not-a-real-password'),
       (4, 'Zarate@email.com', 'William', 'Zarate', 'not-a-real-password'),
       (5, 'Dolores@email.com', 'Maria', 'Dolores', 'not-a-real-password'),
       (6, 'Perez@email.com', 'Juan', 'Pérez López', 'not-a-real-password'),
       (7, 'Castro@email.com', 'Sofía', 'Castro Mena', 'not-a-real-password'),
       (8, 'Soto@email.com', 'Miguel', 'Soto Bravo', 'not-a-real-password'),
       (9, 'Rios@email.com', 'Elena', 'Ríos Cano', 'not-a-real-password'),
       (10, 'Ruiz@email.com', 'Andrés', 'Ruiz Paz', 'not-a-real-password')
;

INSERT INTO Role (role, staff_id)
VALUES ('ADMIN', 1),       -- Shawn Lee (Admin Global)
       ('ADMIN', 2),       -- Octavio Ocharan (Admin Secundario)
       ('COORDINATOR', 2), -- Octavio Ocharan (Coo P1)
       ('TUTOR', 2),       -- Octavio Ocharan (también tutor)
       ('TUTOR', 3),       -- Ana Luz Polo
       ('COORDINATOR', 4), -- William Zarate (Coo P2)
       ('TUTOR', 4),       -- William Zarate (también tutor)
       ('TUTOR', 5),       -- Maria Dolores
       ('COORDINATOR', 6), -- Juan Pérez (Coo P3)
       ('TUTOR', 6),       -- Juan Pérez (también tutor)
       ('COORDINATOR', 7), -- Sofía Castro (Coo P4)
       ('SUPERVISOR', 8),  -- Miguel Soto
       ('TUTOR', 9),       -- Elena Ríos
       ('TUTOR', 10) -- Andrés Ruiz
;

INSERT INTO Coordinates
VALUES (2, 1), -- Octavio Ocharan coordina P1 (LIS)
       (4, 2), -- William Zarate coordina P2 (LISTI)
       (6, 3), -- Juan Pérez coordina P3 (LCD)
       (7, 4) -- Sofía Castro coordina P4 (LICIC)
;

INSERT INTO Tutors (program_id, staff_id)
VALUES (1, 2),  -- Octavio Ocharan es tutor de LIS   P1
       (1, 3),  -- Ana Luz Polo es tutor de LIS      P1
       (1, 4),  -- William Zarate es tutor de LIS    P1
       (2, 4),  -- William Zarate es tutor de LISTI  P2
       (1, 5),  -- Maria Dolores es tutor de LIS     P1
       (2, 5),  -- Maria Dolores es tutor de LISTI   P2
       (3, 5),  -- Maria Dolores es tutor de LCD     P3
       (3, 6),  -- Juan Pérez es tutor de LCD        P3
       (4, 6),  -- Juan Pérez es tutor de LICIC      P4
       (2, 9),  -- Elena Ríos es tutora de LISTI     P2
       (3, 9),  -- Elena Ríos es tutora de LCD       P3
       (3, 10), -- Andrés Ruiz es tutor de LCD       P3
       (4, 10); -- Andrés Ruiz es tutor de LICIC     P4
;

UPDATE Staff
SET password = '$2a$10$6qGGEFr0Qs11RP7zfuP55u.yhxOX/PjOndPMgUPgQttxNxdTEoa3G';

INSERT INTO Tutored (enrollment, email, name, last_name, program_id, tutor_id)
VALUES
    -- PROGRAMA 1: LIS (Tutores: 2, 3, 4, 5)
    (23014115, 'zS23014115@estudiantes.uv.mx', 'Edgar', 'Vázquez García', 1, 2),    -- Tutor 2
    (23014116, 'zS23014116@estudiantes.uv.mx', 'Ana', 'López Pérez', 1, 2),         -- Tutor 2
    (23014117, 'zS23014117@estudiantes.uv.mx', 'Luis', 'Hernández Ruiz', 1, 2),     -- Tutor 2
    (23014118, 'zS23014118@estudiantes.uv.mx', 'Sofía', 'Martínez Díaz', 1, 3),     -- Tutor 3
    (23014119, 'zS23014119@estudiantes.uv.mx', 'Javier', 'Gómez Castro', 1, 3),     -- Tutor 3
    (24014120, 'zS24014120@estudiantes.uv.mx', 'María', 'Reyes Flores', 1, 4),      -- Tutor 4
    (24014121, 'zS24014121@estudiantes.uv.mx', 'Carlos', 'Jiménez Sosa', 1, 4),     -- Tutor 4
    (24014122, 'zS24014122@estudiantes.uv.mx', 'Elena', 'Torres Vega', 1, 5),       -- Tutor 5
    (25014123, 'zS25014123@estudiantes.uv.mx', 'Ricardo', 'Navarro Cruz', 1, NULL), -- Matrícula 25 -> NULL
    (25014124, 'zS25014124@estudiantes.uv.mx', 'Andrea', 'Paredes Cano', 1, NULL),  -- Matrícula 25 -> NULL
    -- PROGRAMA 2: LISTI (Tutores: 4, 5, 9)
    (23014125, 'zS23014125@estudiantes.uv.mx', 'Pedro', 'Mendoza Salas', 2, 4),
    (23014126, 'zS23014126@estudiantes.uv.mx', 'Laura', 'Díaz Ochoa', 2, 4),
    (23014127, 'zS23014127@estudiantes.uv.mx', 'Miguel', 'Vargas Polo', 2, 4),
    (23014128, 'zS23014128@estudiantes.uv.mx', 'Isabel', 'Herrera Gil', 2, 5),
    (23014129, 'zS23014129@estudiantes.uv.mx', 'Diego', 'Rojas León', 2, 5),
    (24014130, 'zS24014130@estudiantes.uv.mx', 'Natalia', 'Cáceres Solis', 2, 5),
    (24014131, 'zS24014131@estudiantes.uv.mx', 'Oscar', 'Quintero Mora', 2, 9),
    (24014132, 'zS24014132@estudiantes.uv.mx', 'Valeria', 'Guerrero Ríos', 2, 9),
    (25014133, 'zS25014133@estudiantes.uv.mx', 'Felipe', 'Ortiz Ruiz', 2, NULL),    -- Matrícula 25 -> NULL
    (25014134, 'zS25014134@estudiantes.uv.mx', 'Gaby', 'Sandoval Pérez', 2, NULL),  -- Matrícula 25 -> NULL
    -- PROGRAMA 3: LCD (Tutores: 5, 6, 9, 10)
    (23014135, 'zS23014135@estudiantes.uv.mx', 'Héctor', 'Chávez López', 3, 5),
    (23014136, 'zS23014136@estudiantes.uv.mx', 'Daniela', 'Acosta Gómez', 3, 5),
    (23014137, 'zS23014137@estudiantes.uv.mx', 'Raúl', 'Bravo Díaz', 3, 5),
    (23014138, 'zS23014138@estudiantes.uv.mx', 'Mónica', 'Castañeda Luna', 3, 6),
    (23014139, 'zS23014139@estudiantes.uv.mx', 'Jorge', 'Fuentes Garza', 3, 6),
    (24014140, 'zS24014140@estudiantes.uv.mx', 'Paulina', 'Ibarra Soto', 3, 9),
    (24014141, 'zS24014141@estudiantes.uv.mx', 'Adrián', 'Juárez Ramos', 3, 9),
    (24014142, 'zS24014142@estudiantes.uv.mx', 'Karla', 'Leal Valdez', 3, 10),
    (25014143, 'zS25014143@estudiantes.uv.mx', 'Emmanuel', 'Molina Rico', 3, NULL), -- Matrícula 25 -> NULL
    (25014144, 'zS25014144@estudiantes.uv.mx', 'Fernanda', 'Núñez Ríos', 3, NULL),  -- Matrícula 25 -> NULL
    -- PROGRAMA 4: LICIC (Tutores: 6, 10)
    (23014145, 'zS23014145@estudiantes.uv.mx', 'Roberto', 'Ochoa Vega', 4, 6),
    (23014146, 'zS23014146@estudiantes.uv.mx', 'Cecilia', 'Quiróz Peña', 4, 6),
    (23014147, 'zS23014147@estudiantes.uv.mx', 'Guillermo', 'Salazar Ríos', 4, 6),
    (23014148, 'zS23014148@estudiantes.uv.mx', 'Diana', 'Tapia Ruiz', 4, 6),
    (23014149, 'zS23014149@estudiantes.uv.mx', 'Juan', 'Ulloa Soto', 4, 6),
    (24014150, 'zS24014150@estudiantes.uv.mx', 'Silvia', 'Velázquez Cruz', 4, 10),
    (24014151, 'zS24014151@estudiantes.uv.mx', 'Arturo', 'Zavala Blanco', 4, 10),
    (24014152, 'zS25014152@estudiantes.uv.mx', 'Brenda', 'Alonso Prado', 4, 10),
    (25014153, 'zS25014153@estudiantes.uv.mx', 'Christian', 'Báez López', 4, NULL), -- Matrícula 25 -> NULL
    (25014154, 'zS25014154@estudiantes.uv.mx', 'Damaris', 'Cervantes Mora', 4, NULL) -- Matrícula 25 -> NULL
;

INSERT INTO Period (year, semester)
VALUES (2024, 'AUG_JAN'),
       (2024, 'FEB_JUL'),
       (2025, 'AUG_JAN'),
       (2025, 'FEB_JUL'),
       (2026, 'AUG_JAN');

INSERT INTO TutoringSessionPlan (period_year, period_semester, appointment_date, program_id, kind, created_at)
VALUES
    -- ------------------------------------------------------------------------------------------------
    -- PERIODO 1: 2024 - AUG_JAN (Septiembre a Diciembre 2023) - HISTÓRICO
    -- ------------------------------------------------------------------------------------------------
    -- LIS (P1)
    (2024, 'AUG_JAN', '2023-09-13 00:00:00', 1, 'FIRST_TUTORING_SESSION', '2023-09-08 00:00:00'),
    (2024, 'AUG_JAN', '2023-10-25 00:00:00', 1, 'SECOND_TUTORING_SESSION', '2023-10-20 00:00:00'),
    (2024, 'AUG_JAN', '2023-12-06 00:00:00', 1, 'THIRD_TUTORING_SESSION', '2023-12-01 00:00:00'),
    -- LISTI (P2)
    (2024, 'AUG_JAN', '2023-09-15 00:00:00', 2, 'FIRST_TUTORING_SESSION', '2023-09-10 00:00:00'),
    (2024, 'AUG_JAN', '2023-10-27 00:00:00', 2, 'SECOND_TUTORING_SESSION', '2023-10-22 00:00:00'),
    (2024, 'AUG_JAN', '2023-12-08 00:00:00', 2, 'THIRD_TUTORING_SESSION', '2023-12-03 00:00:00'),
    -- LCD (P3)
    (2024, 'AUG_JAN', '2023-09-17 00:00:00', 3, 'FIRST_TUTORING_SESSION', '2023-09-12 00:00:00'),
    (2024, 'AUG_JAN', '2023-10-29 00:00:00', 3, 'SECOND_TUTORING_SESSION', '2023-10-24 00:00:00'),
    (2024, 'AUG_JAN', '2023-12-10 00:00:00', 3, 'THIRD_TUTORING_SESSION', '2023-12-05 00:00:00'),
    -- LICIC (P4)
    (2024, 'AUG_JAN', '2023-09-19 00:00:00', 4, 'FIRST_TUTORING_SESSION', '2023-09-14 00:00:00'),
    (2024, 'AUG_JAN', '2023-10-31 00:00:00', 4, 'SECOND_TUTORING_SESSION', '2023-10-26 00:00:00'),
    (2024, 'AUG_JAN', '2023-12-12 00:00:00', 4, 'THIRD_TUTORING_SESSION', '2023-12-07 00:00:00'),

    -- ------------------------------------------------------------------------------------------------
    -- PERIODO 2: 2024 - FEB_JUL (Febrero a Mayo 2024) - HISTÓRICO
    -- ------------------------------------------------------------------------------------------------
    -- LIS (P1)
    (2024, 'FEB_JUL', '2024-02-15 00:00:00', 1, 'FIRST_TUTORING_SESSION', '2024-02-10 00:00:00'),
    (2024, 'FEB_JUL', '2024-03-28 00:00:00', 1, 'SECOND_TUTORING_SESSION', '2024-03-23 00:00:00'),
    (2024, 'FEB_JUL', '2024-05-09 00:00:00', 1, 'THIRD_TUTORING_SESSION', '2024-05-04 00:00:00'),
    -- LISTI (P2)
    (2024, 'FEB_JUL', '2024-02-17 00:00:00', 2, 'FIRST_TUTORING_SESSION', '2024-02-12 00:00:00'),
    (2024, 'FEB_JUL', '2024-03-30 00:00:00', 2, 'SECOND_TUTORING_SESSION', '2024-03-25 00:00:00'),
    (2024, 'FEB_JUL', '2024-05-11 00:00:00', 2, 'THIRD_TUTORING_SESSION', '2024-05-06 00:00:00'),
    -- LCD (P3)
    (2024, 'FEB_JUL', '2024-02-19 00:00:00', 3, 'FIRST_TUTORING_SESSION', '2024-02-14 00:00:00'),
    (2024, 'FEB_JUL', '2024-04-01 00:00:00', 3, 'SECOND_TUTORING_SESSION', '2024-03-27 00:00:00'),
    (2024, 'FEB_JUL', '2024-05-13 00:00:00', 3, 'THIRD_TUTORING_SESSION', '2024-05-08 00:00:00'),
    -- LICIC (P4)
    (2024, 'FEB_JUL', '2024-02-21 00:00:00', 4, 'FIRST_TUTORING_SESSION', '2024-02-16 00:00:00'),
    (2024, 'FEB_JUL', '2024-04-03 00:00:00', 4, 'SECOND_TUTORING_SESSION', '2024-03-29 00:00:00'),
    (2024, 'FEB_JUL', '2024-05-15 00:00:00', 4, 'THIRD_TUTORING_SESSION', '2024-05-10 00:00:00'),

    -- ------------------------------------------------------------------------------------------------
    -- PERIODO 3: 2025 - AUG_JAN (Septiembre a Diciembre 2024) - HISTÓRICO
    -- ------------------------------------------------------------------------------------------------
    -- LIS (P1)
    (2025, 'AUG_JAN', '2024-09-10 00:00:00', 1, 'FIRST_TUTORING_SESSION', '2024-09-05 00:00:00'),
    (2025, 'AUG_JAN', '2024-10-22 00:00:00', 1, 'SECOND_TUTORING_SESSION', '2024-10-17 00:00:00'),
    (2025, 'AUG_JAN', '2024-12-03 00:00:00', 1, 'THIRD_TUTORING_SESSION', '2024-11-28 00:00:00'),
    -- LISTI (P2)
    (2025, 'AUG_JAN', '2024-09-12 00:00:00', 2, 'FIRST_TUTORING_SESSION', '2024-09-07 00:00:00'),
    (2025, 'AUG_JAN', '2024-10-24 00:00:00', 2, 'SECOND_TUTORING_SESSION', '2024-10-19 00:00:00'),
    (2025, 'AUG_JAN', '2024-12-05 00:00:00', 2, 'THIRD_TUTORING_SESSION', '2024-11-30 00:00:00'),
    -- LCD (P3)
    (2025, 'AUG_JAN', '2024-09-14 00:00:00', 3, 'FIRST_TUTORING_SESSION', '2024-09-09 00:00:00'),
    (2025, 'AUG_JAN', '2024-10-26 00:00:00', 3, 'SECOND_TUTORING_SESSION', '2024-10-21 00:00:00'),
    (2025, 'AUG_JAN', '2024-12-07 00:00:00', 3, 'THIRD_TUTORING_SESSION', '2024-12-02 00:00:00'),
    -- LICIC (P4)
    (2025, 'AUG_JAN', '2024-09-16 00:00:00', 4, 'FIRST_TUTORING_SESSION', '2024-09-11 00:00:00'),
    (2025, 'AUG_JAN', '2024-10-28 00:00:00', 4, 'SECOND_TUTORING_SESSION', '2024-10-23 00:00:00'),
    (2025, 'AUG_JAN', '2024-12-09 00:00:00', 4, 'THIRD_TUTORING_SESSION', '2024-12-04 00:00:00'),

    -- ------------------------------------------------------------------------------------------------
    -- PERIODO 4: 2025 - FEB_JUL (Febrero a Mayo 2025) - HISTÓRICO RECIENTE
    -- ------------------------------------------------------------------------------------------------
    -- LIS (P1)
    (2025, 'FEB_JUL', '2025-02-15 00:00:00', 1, 'FIRST_TUTORING_SESSION', '2025-02-10 00:00:00'),
    (2025, 'FEB_JUL', '2025-03-28 00:00:00', 1, 'SECOND_TUTORING_SESSION', '2025-03-23 00:00:00'),
    (2025, 'FEB_JUL', '2025-05-09 00:00:00', 1, 'THIRD_TUTORING_SESSION', '2025-05-04 00:00:00'),
    -- LISTI (P2)
    (2025, 'FEB_JUL', '2025-02-17 00:00:00', 2, 'FIRST_TUTORING_SESSION', '2025-02-12 00:00:00'),
    (2025, 'FEB_JUL', '2025-03-30 00:00:00', 2, 'SECOND_TUTORING_SESSION', '2025-03-25 00:00:00'),
    (2025, 'FEB_JUL', '2025-05-11 00:00:00', 2, 'THIRD_TUTORING_SESSION', '2025-05-06 00:00:00'),
    -- LCD (P3)
    (2025, 'FEB_JUL', '2025-02-19 00:00:00', 3, 'FIRST_TUTORING_SESSION', '2025-02-14 00:00:00'),
    (2025, 'FEB_JUL', '2025-04-01 00:00:00', 3, 'SECOND_TUTORING_SESSION', '2025-03-27 00:00:00'),
    (2025, 'FEB_JUL', '2025-05-13 00:00:00', 3, 'THIRD_TUTORING_SESSION', '2025-05-08 00:00:00'),
    -- LICIC (P4)
    (2025, 'FEB_JUL', '2025-02-21 00:00:00', 4, 'FIRST_TUTORING_SESSION', '2025-02-16 00:00:00'),
    (2025, 'FEB_JUL', '2025-04-03 00:00:00', 4, 'SECOND_TUTORING_SESSION', '2025-03-29 00:00:00'),
    (2025, 'FEB_JUL', '2025-05-15 00:00:00', 4, 'THIRD_TUTORING_SESSION', '2025-05-10 00:00:00'),

    -- ------------------------------------------------------------------------------------------------
    -- PERIODO 5: 2026 - AUG_JAN (Septiembre a Diciembre 2025) - ACTUAL/AJUSTADO
    -- (Tercera sesión finalizada el 2025-12-06 o antes)
    -- ------------------------------------------------------------------------------------------------
    -- LIS (P1)
    (2026, 'AUG_JAN', '2025-09-14 00:00:00', 1, 'FIRST_TUTORING_SESSION', '2025-09-09 00:00:00'),
    (2026, 'AUG_JAN', '2025-10-26 00:00:00', 1, 'SECOND_TUTORING_SESSION', '2025-10-21 00:00:00'),
    -- (2026, 'AUG_JAN', '2025-12-06 00:00:00', 1, 'THIRD_TUTORING_SESSION', '2025-12-01 00:00:00'),
    -- LISTI (P2)
    (2026, 'AUG_JAN', '2025-09-10 00:00:00', 2, 'FIRST_TUTORING_SESSION', '2025-09-05 00:00:00'),
    (2026, 'AUG_JAN', '2025-10-22 00:00:00', 2, 'SECOND_TUTORING_SESSION', '2025-10-17 00:00:00'),
    (2026, 'AUG_JAN', '2025-12-03 00:00:00', 2, 'THIRD_TUTORING_SESSION', '2025-11-28 00:00:00'),
    -- LCD (P3)
    (2026, 'AUG_JAN', '2025-09-12 00:00:00', 3, 'FIRST_TUTORING_SESSION', '2025-09-07 00:00:00'),
    (2026, 'AUG_JAN', '2025-10-24 00:00:00', 3, 'SECOND_TUTORING_SESSION', '2025-10-19 00:00:00'),
    (2026, 'AUG_JAN', '2025-12-05 00:00:00', 3, 'THIRD_TUTORING_SESSION', '2025-11-30 00:00:00'),
    -- LICIC (P4)
    (2026, 'AUG_JAN', '2025-09-14 00:00:00', 4, 'FIRST_TUTORING_SESSION', '2025-09-09 00:00:00'),
    (2026, 'AUG_JAN', '2025-10-26 00:00:00', 4, 'SECOND_TUTORING_SESSION', '2025-10-21 00:00:00'),
    (2026, 'AUG_JAN', '2025-12-06 00:00:00', 4, 'THIRD_TUTORING_SESSION', '2025-12-01 00:00:00');
