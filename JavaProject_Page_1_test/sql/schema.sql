SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS assessment;

DROP TABLE IF EXISTS student_grade;

DROP TABLE IF EXISTS attendance;

DROP TABLE IF EXISTS course_enrollment;

DROP TABLE IF EXISTS course_offering;

DROP TABLE IF EXISTS student_education_background;

DROP TABLE IF EXISTS student_guardian;

DROP TABLE IF EXISTS student_contact_information;

DROP TABLE IF EXISTS student;

DROP TABLE IF EXISTS teacher_academic_background;

DROP TABLE IF EXISTS teacher_contact_information;

DROP TABLE IF EXISTS teacher;

DROP TABLE IF EXISTS prerequisite_course;

DROP TABLE IF EXISTS course;

DROP TABLE IF EXISTS department;

DROP TABLE IF EXISTS user_account;

DROP TABLE IF EXISTS master_admin;

CREATE TABLE master_admin (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    `status` BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_account (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    user_role ENUM('STUDENT', 'TEACHER', 'ADMIN') NOT NULL,
    `status` BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE department (
    dept_id INT PRIMARY KEY AUTO_INCREMENT,
    dept_head_id INT UNIQUE NULL,
    dept_code VARCHAR(30) NOT NULL,
    dept_name VARCHAR(100) NOT NULL,
    established_date DATE NOT NULL,
    dept_location VARCHAR(50) NOT NULL,
    FOREIGN KEY (dept_head_id) REFERENCES teacher (teacher_id) ON DELETE SET NULL
);

CREATE TABLE course (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(100) UNIQUE NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    dept_id INT NOT NULL,
    course_type ENUM('Bachelor', 'Master', 'PHD') DEFAULT 'Bachelor',
    credit DECIMAL(3, 1) NOT NULL DEFAULT 3.0,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (dept_id) REFERENCES department (dept_id) ON DELETE RESTRICT
);

CREATE TABLE prerequisite_course (
    prerequisite_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    prerequisite_course_id INT NOT NULL,
    UNIQUE (
        course_id,
        prerequisite_course_id
    ),
    CHECK (
        course_id != prerequisite_course_id
    ),
    FOREIGN KEY (course_id) REFERENCES course (course_id) ON DELETE CASCADE,
    FOREIGN KEY (prerequisite_course_id) REFERENCES course (course_id) ON DELETE CASCADE
);

CREATE TABLE teacher (
    teacher_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    dept_id INT NOT NULL,
    position ENUM(
        'Professor & Head',
        'Professor',
        'Associate Professor',
        'Lecturer',
        'Assistant Lecturer',
        'Tutor'
    ) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('Male', 'Female') NOT NULL,
    nationality VARCHAR(30),
    nrc VARCHAR(40) UNIQUE NOT NULL,
    profile_image_url VARCHAR(255) NULL,
    FOREIGN KEY (user_id) REFERENCES user_account (user_id) ON DELETE CASCADE,
    FOREIGN KEY (dept_id) REFERENCES department (dept_id) ON DELETE RESTRICT
);

CREATE TABLE teacher_contact_information (
    teacher_id INT PRIMARY KEY,
    primary_phone_number VARCHAR(50) NOT NULL,
    email_address VARCHAR(100) NOT NULL,
    current_address VARCHAR(255) NOT NULL,
    permanent_address VARCHAR(255) NOT NULL,
    FOREIGN KEY (teacher_id) REFERENCES teacher (teacher_id) ON DELETE CASCADE
);

CREATE TABLE teacher_academic_background (
    teacher_id INT PRIMARY KEY,
    highest_degree VARCHAR(100) NOT NULL,
    university VARCHAR(150) NOT NULL,
    year_completed INT NOT NULL,
    specialization VARCHAR(100) NULL,
    emergency_contact VARCHAR(100) NULL,
    FOREIGN KEY (teacher_id) REFERENCES teacher (teacher_id) ON DELETE CASCADE
);

CREATE TABLE student (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE,
    roll_no VARCHAR(30) NOT NULL,
    full_name VARCHAR(50) NOT NULL,
    dept_id INT NOT NULL,
    current_year ENUM(
        'First year',
        'Second year',
        'Third year',
        'Fourth year',
        'Fifth year'
    ) NOT NULL,
    total_credit DECIMAL(5, 1) DEFAULT 0.0,
    gpa DECIMAL(3, 2) DEFAULT 0.00,
    date_of_birth DATE NOT NULL,
    gender ENUM('Male', 'Female') NOT NULL,
    nationality VARCHAR(30),
    nrc VARCHAR(40) UNIQUE NOT NULL,
    profile_image_url VARCHAR(255) NULL,
    FOREIGN KEY (user_id) REFERENCES user_account (user_id) ON DELETE CASCADE,
    FOREIGN KEY (dept_id) REFERENCES department (dept_id) ON DELETE RESTRICT
);

CREATE TABLE student_contact_information (
    student_id INT PRIMARY KEY,
    primary_phone_number VARCHAR(50) NOT NULL,
    secondary_phone_number VARCHAR(50) NULL,
    email_address VARCHAR(100) NOT NULL,
    current_address VARCHAR(255) NOT NULL,
    permanent_address VARCHAR(255) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE
);

CREATE TABLE student_guardian (
    student_id INT PRIMARY KEY,
    father_full_name VARCHAR(100) NOT NULL,
    father_occupation VARCHAR(100),
    father_phone_number VARCHAR(50),
    father_email_address VARCHAR(100),
    mother_full_name VARCHAR(100) NOT NULL,
    mother_occupation VARCHAR(100),
    mother_phone_number VARCHAR(50),
    mother_email_address VARCHAR(100),
    FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE
);

CREATE TABLE student_education_background (
    student_id INT PRIMARY KEY,
    previous_school VARCHAR(100) NOT NULL,
    year_completed INT,
    additional_info TEXT NULL,
    FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE CASCADE
);

CREATE TABLE course_offering (
    offering_id INT PRIMARY KEY AUTO_INCREMENT,
    teacher_id INT NOT NULL,
    course_id INT NOT NULL,
    `year` INT NOT NULL,
    total_sessions INT NOT NULL DEFAULT 0,
    term ENUM(
        'First Semester',
        'Second Semester'
    ) NOT NULL,
    UNIQUE (course_id, `year`, term),
    FOREIGN KEY (teacher_id) REFERENCES teacher (teacher_id) ON DELETE RESTRICT,
    FOREIGN KEY (course_id) REFERENCES course (course_id) ON DELETE RESTRICT
);

CREATE TABLE course_enrollment (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    offering_id INT NOT NULL,
    student_id INT NOT NULL,
    enrollment_date DATE NOT NULL DEFAULT(CURRENT_DATE),
    status ENUM(
        'ENROLLED',
        'DROPPED',
        'COMPLETED'
    ) DEFAULT 'ENROLLED',
    UNIQUE (offering_id, student_id),
    FOREIGN KEY (student_id) REFERENCES student (student_id) ON DELETE RESTRICT,
    FOREIGN KEY (offering_id) REFERENCES course_offering (offering_id) ON DELETE RESTRICT
);

CREATE TABLE attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    absent_date DATE NOT NULL,
    enrollment_id INT NOT NULL,
    status ENUM('UNEXCUSED', 'EXCUSED') DEFAULT 'UNEXCUSED',
    reason VARCHAR(255) NULL,
    UNIQUE (enrollment_id, absent_date),
    FOREIGN KEY (enrollment_id) REFERENCES course_enrollment (enrollment_id) ON DELETE CASCADE
);

CREATE TABLE assessment (
    assessment_id INT PRIMARY KEY AUTO_INCREMENT,
    offering_id INT NOT NULL,
    title VARCHAR(100) NOT NULL, -- e.g., 'Tutorial 1', 'Lab Test', 'Assignment 3'
    assessment_type ENUM(
        'TUTORIAL',
        'ASSIGNMENT',
        'LAB_TEST',
        'PRACTICAL',
        'FINAL'
    ) NOT NULL,
    max_marks DECIMAL(5, 2) NOT NULL,
    weightage_percent DECIMAL(5, 2) NULL,
    FOREIGN KEY (offering_id) REFERENCES course_offering (offering_id) ON DELETE CASCADE
);

CREATE TABLE student_grade (
    grade_id INT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id INT NOT NULL,
    assessment_id INT NOT NULL,
    marks_obtained DECIMAL(5, 2) NOT NULL,
    UNIQUE (enrollment_id, assessment_id),
    FOREIGN KEY (enrollment_id) REFERENCES course_enrollment (enrollment_id) ON DELETE CASCADE,
    FOREIGN KEY (assessment_id) REFERENCES assessment (assessment_id) ON DELETE CASCADE
);

SET FOREIGN_KEY_CHECKS = 1;