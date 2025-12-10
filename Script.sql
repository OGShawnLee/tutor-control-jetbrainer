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

DROP TRIGGER IF EXISTS set_tutored_tutor_id_to_null_on_role_tutor_delete;
CREATE TRIGGER set_tutored_tutor_id_to_null_on_role_tutor_delete
    BEFORE DELETE
    ON Role
    FOR EACH ROW
BEGIN
    IF OLD.role = 'TUTOR' THEN
        DELETE FROM Tutors WHERE staff_id = OLD.staff_id;
    END IF;
END;

DROP TRIGGER IF EXISTS set_tutored_tutor_id_to_null_on_tutors_delete;
CREATE TRIGGER set_tutored_tutor_id_to_null_on_tutors_delete
    BEFORE DELETE
    ON Tutors
    FOR EACH ROW
BEGIN
    UPDATE Tutored SET tutor_id = NULL WHERE tutor_id = OLD.staff_id;
END;

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