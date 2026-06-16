-- ========================================================
--  SAMS – Student Attendance Management System
--  MySQL Database Script
--  Auto-created by Hibernate (hbm2ddl=update),
--  but this script provides the full schema + sample data
-- ========================================================

CREATE DATABASE IF NOT EXISTS sams_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sams_db;

-- ─────────────────────────────────────────────
-- TABLE: users
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id    VARCHAR(10)  NOT NULL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       ENUM('ADMIN','LECTURER') NOT NULL DEFAULT 'LECTURER',
    full_name  VARCHAR(100) NOT NULL,
    email      VARCHAR(100) UNIQUE,
    is_active  TINYINT(1)   NOT NULL DEFAULT 1
);

-- ─────────────────────────────────────────────
-- TABLE: courses
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS courses (
    course_id       VARCHAR(10)  NOT NULL PRIMARY KEY,
    course_name     VARCHAR(150) NOT NULL,
    course_code     VARCHAR(20)  NOT NULL UNIQUE,
    description     VARCHAR(500),
    duration_months INT          NOT NULL DEFAULT 12
);

-- ─────────────────────────────────────────────
-- TABLE: subjects
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS subjects (
    subject_id   VARCHAR(10)  NOT NULL PRIMARY KEY,
    subject_name VARCHAR(150) NOT NULL,
    subject_code VARCHAR(20)  NOT NULL UNIQUE,
    credits      INT          NOT NULL DEFAULT 3,
    description  VARCHAR(500),
    course_id    VARCHAR(10)  NOT NULL,
    CONSTRAINT fk_subject_course FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- ─────────────────────────────────────────────
-- TABLE: lecturers
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS lecturers (
    lecturer_id   VARCHAR(10)  NOT NULL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    phone         VARCHAR(15)  NOT NULL,
    qualification VARCHAR(200),
    department    VARCHAR(100),
    is_active     TINYINT(1)   NOT NULL DEFAULT 1
);

-- ─────────────────────────────────────────────
-- TABLE: subject_lecturers  (ManyToMany join)
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS subject_lecturers (
    subject_id  VARCHAR(10) NOT NULL,
    lecturer_id VARCHAR(10) NOT NULL,
    PRIMARY KEY (subject_id, lecturer_id),
    CONSTRAINT fk_sl_subject  FOREIGN KEY (subject_id)  REFERENCES subjects(subject_id),
    CONSTRAINT fk_sl_lecturer FOREIGN KEY (lecturer_id) REFERENCES lecturers(lecturer_id)
);

-- ─────────────────────────────────────────────
-- TABLE: students
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS students (
    student_id      VARCHAR(10)  NOT NULL PRIMARY KEY,
    reg_number      VARCHAR(20)  NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(100) UNIQUE,
    phone           VARCHAR(15)  NOT NULL,
    date_of_birth   DATE,
    address         VARCHAR(255),
    enrollment_date DATE         NOT NULL,
    course_id       VARCHAR(10)  NOT NULL,
    CONSTRAINT fk_student_course FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

-- ─────────────────────────────────────────────
-- TABLE: class_sessions
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS class_sessions (
    session_id   VARCHAR(10) NOT NULL PRIMARY KEY,
    session_date DATE        NOT NULL,
    start_time   TIME,
    end_time     TIME,
    venue        VARCHAR(100),
    notes        VARCHAR(500),
    subject_id   VARCHAR(10) NOT NULL,
    lecturer_id  VARCHAR(10) NOT NULL,
    CONSTRAINT fk_session_subject  FOREIGN KEY (subject_id)  REFERENCES subjects(subject_id),
    CONSTRAINT fk_session_lecturer FOREIGN KEY (lecturer_id) REFERENCES lecturers(lecturer_id)
);

-- ─────────────────────────────────────────────
-- TABLE: attendance
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id VARCHAR(10)  NOT NULL PRIMARY KEY,
    status        ENUM('PRESENT','ABSENT','LATE','EXCUSED') NOT NULL DEFAULT 'ABSENT',
    marked_date   DATE,
    remarks       VARCHAR(300),
    student_id    VARCHAR(10)  NOT NULL,
    session_id    VARCHAR(10)  NOT NULL,
    CONSTRAINT fk_att_student FOREIGN KEY (student_id) REFERENCES students(student_id),
    CONSTRAINT fk_att_session FOREIGN KEY (session_id) REFERENCES class_sessions(session_id),
    UNIQUE KEY uq_student_session (student_id, session_id)
);

-- ========================================================
--  SAMPLE DATA
-- ========================================================

-- Default admin (password: Admin@1234 – bcrypt hashed by app on first run)
-- The AppInitializer seeds this automatically. Included here for manual reference.

-- Sample Courses
INSERT IGNORE INTO courses VALUES
('C001', 'Diploma in Information Technology', 'DIT', 'Core IT diploma covering programming, databases, and networking.', 24),
('C002', 'Higher National Diploma in Computing', 'HND', 'Advanced computing course with specialisations.', 24),
('C003', 'Bachelor of Science in Software Engineering', 'BSE', 'Full software engineering degree programme.', 48);

-- Sample Subjects
INSERT IGNORE INTO subjects VALUES
('SUB001', 'Object Oriented Programming', 'OOP101', 3, 'Java OOP concepts', 'C001'),
('SUB002', 'Database Management Systems', 'DBMS101', 3, 'MySQL and relational theory', 'C001'),
('SUB003', 'Web Application Development', 'WAD101', 3, 'HTML, CSS, JS and frameworks', 'C001'),
('SUB004', 'Advanced Java Programming', 'AJP201', 4, 'JavaFX, Spring and enterprise', 'C002'),
('SUB005', 'Software Engineering Principles', 'SEP301', 4, 'SDLC, agile, and design patterns', 'C003');

-- Sample Lecturers
INSERT IGNORE INTO lecturers VALUES
('L001', 'Dr. Samantha Perera', 'samantha.perera@sams.lk', '0771234501', 'PhD in Computer Science', 'IT Department', 1),
('L002', 'Mr. Kamal Silva', 'kamal.silva@sams.lk', '0771234502', 'MSc in Software Engineering', 'IT Department', 1),
('L003', 'Ms. Nimal Fernando', 'nimal.fernando@sams.lk', '0771234503', 'BSc in Information Systems', 'Computing Department', 1);

-- Sample Students
INSERT IGNORE INTO students VALUES
('STU001', '2025/STU/001', 'Amara Dissanayake', 'amara.d@student.sams.lk', '0712345601', '2002-05-14', 'No.12, Galle Road, Colombo', CURDATE(), 'C001'),
('STU002', '2025/STU/002', 'Bimal Jayawardena', 'bimal.j@student.sams.lk', '0712345602', '2001-08-22', 'No.45, Kandy Road, Peradeniya', CURDATE(), 'C001'),
('STU003', '2025/STU/003', 'Chamari Rathnayake', 'chamari.r@student.sams.lk', '0712345603', '2003-01-10', 'No.7, Temple Road, Gampaha', CURDATE(), 'C002'),
('STU004', '2025/STU/004', 'Dilan Wickramasinghe', 'dilan.w@student.sams.lk', '0712345604', '2002-11-30', 'No.89, Main Street, Kurunegala', CURDATE(), 'C002'),
('STU005', '2025/STU/005', 'Eshan Mendis', 'eshan.m@student.sams.lk', '0712345605', '2001-03-17', 'No.23, Lake Road, Matara', CURDATE(), 'C003');

-- Sample Class Sessions
INSERT IGNORE INTO class_sessions VALUES
('SES001', CURDATE(), '08:00:00', '10:00:00', 'Hall A', 'OOP Lab session', 'SUB001', 'L001'),
('SES002', CURDATE(), '10:00:00', '12:00:00', 'Room 102', 'Database queries', 'SUB002', 'L002'),
('SES003', DATE_SUB(CURDATE(), INTERVAL 1 DAY), '08:00:00', '10:00:00', 'Hall B', 'Web forms lab', 'SUB003', 'L003');

-- Sample Attendance
INSERT IGNORE INTO attendance VALUES
('ATT001', 'PRESENT', CURDATE(), '', 'STU001', 'SES001'),
('ATT002', 'PRESENT', CURDATE(), '', 'STU002', 'SES001'),
('ATT003', 'ABSENT',  CURDATE(), 'Sick leave', 'STU001', 'SES002'),
('ATT004', 'LATE',    CURDATE(), 'Traffic', 'STU002', 'SES002');
