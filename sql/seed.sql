-- Disable foreign key checks temporarily for clean insertion order
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE student_grade;

TRUNCATE TABLE assessment;

TRUNCATE TABLE attendance;

TRUNCATE TABLE course_enrollment;

TRUNCATE TABLE course_offering;

TRUNCATE TABLE student_education_background;

TRUNCATE TABLE student_guardian;

TRUNCATE TABLE student_contact_information;

TRUNCATE TABLE student;

TRUNCATE TABLE teacher_academic_background;

TRUNCATE TABLE teacher_contact_information;

TRUNCATE TABLE teacher;

TRUNCATE TABLE prerequisite_course;

TRUNCATE TABLE course;

TRUNCATE TABLE department;

TRUNCATE TABLE user_account;

TRUNCATE TABLE master_admin;

INSERT INTO
    master_admin (
        admin_id,
        email,
        password_hash,
        status
    )
VALUES (
        1,
        'superadmin@ytu.edu.mm',
        '$2a$10$X7e...hash1',
        TRUE
    ),
    (
        2,
        'inactive.admin@ytu.edu.mm',
        '$2a$10$X7e...hash2',
        FALSE
    );
-- Edge Case: Disabled admin account
INSERT INTO
    user_account (
        user_id,
        username,
        email,
        password_hash,
        user_role,
        status
    )
VALUES (
        1,
        'mg_mg',
        'mgmg@student.ytu.edu.mm',
        '$2a$10$hash_s1',
        'STUDENT',
        TRUE
    ),
    (
        2,
        'su_su',
        'susu@student.ytu.edu.mm',
        '$2a$10$hash_s2',
        'STUDENT',
        TRUE
    ),
    (
        3,
        'aung_aung',
        'aungaung@student.ytu.edu.mm',
        '$2a$10$hash_s3',
        'STUDENT',
        TRUE
    ),
    (
        4,
        'hla_hla',
        'hlahla@student.ytu.edu.mm',
        '$2a$10$hash_s4',
        'STUDENT',
        TRUE
    ),
    -- Aligning teacher user_ids to match the teacher table IDs (101-106)
    (
        101,
        'dr_aung',
        'aung.kyaw.thu@ytu.edu.mm',
        '$2a$10$hash_t1',
        'TEACHER',
        TRUE
    ),
    (
        102,
        'dr_myint',
        'thin.thin.hlaing@ytu.edu.mm',
        '$2a$10$hash_t2',
        'TEACHER',
        TRUE
    ),
    (
        103,
        'min_khant',
        'min.khant@ytu.edu.mm',
        '$2a$10$hash_t3',
        'TEACHER',
        TRUE
    ),
    (
        104,
        'su_myat',
        'su.myat@ytu.edu.mm',
        '$2a$10$hash_t4',
        'TEACHER',
        TRUE
    ),
    (
        105,
        'htet_naing',
        'htet.naing.win@ytu.edu.mm',
        '$2a$10$hash_t5',
        'TEACHER',
        TRUE
    ),
    (
        106,
        'ei_mon',
        'ei.mon.phyo@ytu.edu.mm',
        '$2a$10$hash_t6',
        'TEACHER',
        TRUE
    ),
    (
        8,
        'sys_admin',
        'admin@ytu.edu.mm',
        '$2a$10$hash_admin',
        'ADMIN',
        TRUE
    ),
    (
        9,
        'ghost_user',
        'orphan.user@ytu.edu.mm',
        '$2a$10$hash_ghost',
        'STUDENT',
        FALSE
    );
-- Edge Case: User account with NO matching student profile row
INSERT INTO
    department (
        dept_id,
        dept_head_id,
        dept_code,
        dept_name,
        established_date,
        dept_location
    )
VALUES (
        1,
        1,
        'CEIT',
        'Computer Engineering and Information Technology',
        '1998-05-12',
        'Main Campus - Building A'
    ),
    (
        2,
        2,
        'ME',
        'Mechanical Engineering',
        '1955-09-01',
        'Main Campus - Building B'
    ),
    (
        3,
        3,
        'EC',
        'Electronic Engineering',
        '1970-03-15',
        'Main Campus - Building C'
    ),
    (
        4,
        NULL,
        'Arch',
        'Architecture',
        '2005-11-20',
        'West Wing - Building D'
    );
-- Edge Case: Department with NO department head assigned yet

INSERT INTO
    course (
        course_id,
        course_code,
        course_name,
        dept_id,
        course_type,
        credit,
        is_active
    )
VALUES (
        1,
        'CEIT-3101',
        'Advanced Database Systems',
        1,
        'Bachelor',
        3.0,
        TRUE
    ),
    (
        2,
        'CEIT-3102',
        'Data Structures & Algorithms',
        1,
        'Bachelor',
        3.0,
        TRUE
    ),
    (
        3,
        'ME-3101',
        'Thermodynamics II',
        2,
        'Bachelor',
        3.0,
        TRUE
    ),
    (
        4,
        'EC-3101',
        'Digital Signal Processing',
        3,
        'Bachelor',
        3.0,
        TRUE
    ),
    (
        5,
        'CEIT-OLD-99',
        'Legacy Fortran Programming',
        1,
        'Bachelor',
        2.0,
        FALSE
    );
-- Edge Case: Inactive course that should be filtered out of active course selectors

INSERT INTO
    prerequisite_course (
        course_id,
        prerequisite_course_id
    )
VALUES (1, 2);
-- Advanced Database requires Data Structures
INSERT INTO
    teacher (
        teacher_id,
        user_id,
        full_name,
        dept_id,
        position,
        date_of_birth,
        gender,
        nationality,
        nrc,
        profile_image_url
    )
VALUES (
        1,
        101,
        'Dr. Aung Kyaw Thu',
        1,
        'Professor & Head',
        '1975-04-12',
        'Male',
        'Myanmar',
        '12/BaHNa(N)123456',
        NULL
    ),
    (
        2,
        102,
        'Dr. Thin Thin Hlaing',
        2,
        'Professor',
        '1980-09-25',
        'Female',
        'Myanmar',
        '12/LaMaTa(N)654321',
        NULL
    ),
    (
        3,
        103,
        'U Min Khant',
        1,
        'Associate Professor',
        '1985-03-15',
        'Male',
        'Myanmar',
        '12/KaMaYa(N)112233',
        NULL
    ),
    (
        4,
        104,
        'Daw Su Myat',
        4,
        'Lecturer',
        '1990-11-08',
        'Female',
        'Myanmar',
        '12/SaGaNa(N)445566',
        NULL
    ),
    (
        5,
        105,
        'U Htet Naing Win',
        1,
        'Assistant Lecturer',
        '1993-07-20',
        'Male',
        'Myanmar',
        '12/DaGaNa(N)789012',
        NULL
    ),
    (
        6,
        106,
        'Daw Ei Mon Phyo',
        1,
        'Tutor',
        '1998-12-03',
        'Female',
        'Myanmar',
        '12/AhBeNa(N)345678',
        NULL
    );

INSERT INTO
    teacher_contact_information (
        teacher_id,
        primary_phone_number,
        email_address,
        current_address,
        permanent_address
    )
VALUES (
        1,
        '09-400000001',
        'aung.kyaw.thu@ytu.edu.mm',
        'Insein Township, Yangon',
        'Insein Township, Yangon'
    ),
    (
        2,
        '09-400000002',
        'thin.thin.hlaing@ytu.edu.mm',
        'Kamayut Township, Yangon',
        'Mandalay'
    ),
    (
        3,
        '09-400000003',
        'min.khant@ytu.edu.mm',
        'Hlaing Township, Yangon',
        'Hlaing Township, Yangon'
    ),
    (
        4,
        '09-400000004',
        'su.myat@ytu.edu.mm',
        'Bahan Township, Yangon',
        'Bahan Township, Yangon'
    ),
    (
        5,
        '09-400000005',
        'htet.naing.win@ytu.edu.mm',
        'Mayangone Township, Yangon',
        'Pegu'
    ),
    (
        6,
        '09-400000006',
        'ei.mon.phyo@ytu.edu.mm',
        'Sanchaung Township, Yangon',
        'Sanchaung Township, Yangon'
    );

-- 2. Teacher Academic Background Seed Data
INSERT INTO
    teacher_academic_background (
        teacher_id,
        highest_degree,
        university,
        year_completed,
        specialization,
        emergency_contact
    )
VALUES (
        1,
        'Ph.D. in Information Technology',
        'University of Computer Studies, Yangon (UCSY)',
        2010,
        'Distributed Systems',
        'Daw Mya Mya (Wife) - 09-500000001'
    ),
    (
        2,
        'Ph.D. in Computer Engineering',
        'Yangon Technological University (YTU)',
        2012,
        'Artificial Intelligence',
        'U Ba Win (Husband) - 09-500000002'
    ),
    (
        3,
        'M.E. in Information Technology',
        'Yangon Technological University (YTU)',
        2014,
        'Database Management Systems',
        'Daw Nu Nu (Mother) - 09-500000003'
    ),
    (
        4,
        'M.E. in Computer Engineering',
        'Yangon Technological University (YTU)',
        2017,
        'Computer Networks',
        'U Tun Tun (Father) - 09-500000004'
    ),
    (
        5,
        'B.E. (Information Technology)',
        'Yangon Technological University (YTU)',
        2018,
        'Software Engineering',
        'Daw Hla Hla (Mother) - 09-500000005'
    ),
    (
        6,
        'B.E. (Computer Engineering)',
        'Yangon Technological University (YTU)',
        2021,
        'Web Technologies',
        'U Myint Soe (Father) - 09-500000006'
    );

INSERT INTO
    student (
        student_id,
        user_id,
        roll_no,
        full_name,
        dept_id,
        current_year,
        total_credit,
        gpa,
        date_of_birth,
        gender,
        nationality,
        nrc,
        profile_image_url
    )
VALUES (
        1,
        1,
        'III-CEIT-1',
        'Mg Mg',
        1,
        'Third year',
        45.0,
        3.85,
        '2005-01-10',
        'Male',
        'Myanmar',
        '12/LaMaTa(N)111111',
        NULL
    ),
    (
        2,
        2,
        'V-CEIT-2',
        'Su Su',
        1,
        'Fifth year',
        105.0,
        3.92,
        '2003-06-15',
        'Female',
        'Myanmar',
        '12/TaTaNa(N)222222',
        'https://ytu.edu.mm/images/susu.jpg'
    ),
    (
        3,
        3,
        'III-CEIT-2',
        'Aung Aung',
        1,
        'Third year',
        42.0,
        3.10,
        '2005-09-20',
        'Male',
        'Myanmar',
        '12/BaBaNa(N)333333',
        NULL
    ),
    (
        4,
        4,
        'IV-EC-1',
        'Hla Hla',
        3,
        'Fourth year',
        75.0,
        3.65,
        '2004-03-11',
        'Female',
        'Myanmar',
        '9/MaTaNa(N)444444',
        NULL
    );

-- Edge Case: Mg Mg & Aung Aung have secondary numbers, Su Su & Hla Hla have NULL
INSERT INTO
    student_contact_information (
        student_id,
        primary_phone_number,
        secondary_phone_number,
        email_address,
        current_address,
        permanent_address
    )
VALUES (
        1,
        '09-111111111',
        '09-123456789',
        'mgmg@student.ytu.edu.mm',
        'Hlaing Township, Yangon',
        'Hlaing Township, Yangon'
    ),
    (
        2,
        '09-222222222',
        NULL,
        'susu@student.ytu.edu.mm',
        'Bahan Township, Yangon',
        'Bahan Township, Yangon'
    ),
    (
        3,
        '09-333333333',
        '09-987654321',
        'aungaung@student.ytu.edu.mm',
        'Mayangone Township, Yangon',
        'Pyin Oo Lwin'
    ),
    (
        4,
        '09-444444444',
        NULL,
        'hlahla@student.ytu.edu.mm',
        'Sanchaung Township, Yangon',
        'Sanchaung Township, Yangon'
    );

INSERT INTO
    student_guardian (
        student_id,
        father_full_name,
        father_occupation,
        father_phone_number,
        father_email_address,
        mother_full_name,
        mother_occupation,
        mother_phone_number,
        mother_email_address
    )
VALUES (
        1,
        'U Ba',
        'Engineer',
        '09-333333333',
        'uba@gmail.com',
        'Daw Mya',
        'Teacher',
        '09-444444444',
        'dawmya@gmail.com'
    ),
    (
        2,
        'U Tun Tun',
        'Businessman',
        '09-555555555',
        'utuntun@gmail.com',
        'Daw Khin Khin',
        'Doctor',
        '09-666666666',
        'dawkhin@gmail.com'
    ),
    (
        3,
        'U Mya Win',
        'Architect',
        '09-111222333',
        'myawin@gmail.com',
        'Daw Nilar',
        'Accountant',
        '09-444555666',
        'nilar@gmail.com'
    ),
    (
        4,
        'U Kyaw Swar',
        'Civil Servant',
        '09-777888999',
        'kyawswar@gmail.com',
        'Daw Than Than',
        'Housewife',
        '09-222333444',
        'thanthan@gmail.com'
    );

INSERT INTO
    student_education_background (
        student_id,
        previous_school,
        year_completed,
        additional_info
    )
VALUES (
        1,
        'BEHS No. 1 Dagon',
        2022,
        'Distinction in Mathematics and Physics'
    ),
    (
        2,
        'BEHS No. 2 Kamayut',
        2020,
        'Passed with 5 distinctions'
    ),
    (
        3,
        'BEHS No. 3 Bahan',
        2022,
        'Active in sports club'
    ),
    (
        4,
        'BEHS No. 1 Latha',
        2021,
        'Prefect head'
    );

INSERT INTO
    course_offering (
        offering_id,
        teacher_id,
        course_id,
        year,
        total_sessions,
        term
    )
VALUES (
        1,
        1,
        1,
        2026,
        30,
        'First Semester'
    ),
    (
        2,
        2,
        2,
        2026,
        30,
        'First Semester'
    ),
    (
        3,
        3,
        4,
        2026,
        25,
        'First Semester'
    ),
    (
        4,
        1,
        1,
        2025,
        30,
        'Second Semester'
    );
-- Historical offering for testing past archives

INSERT INTO
    course_enrollment (
        enrollment_id,
        offering_id,
        student_id,
        enrollment_date,
        status
    )
VALUES (
        1,
        1,
        1,
        '2026-06-01',
        'ENROLLED'
    ),
    (
        2,
        2,
        1,
        '2026-06-01',
        'ENROLLED'
    ),
    (
        3,
        1,
        3,
        '2026-06-01',
        'ENROLLED'
    ),
    (
        4,
        3,
        4,
        '2026-06-01',
        'ENROLLED'
    ),
    (
        5,
        1,
        2,
        '2026-06-01',
        'DROPPED'
    ), -- Edge Case: Student enrolled then dropped
    (
        6,
        4,
        1,
        '2025-11-01',
        'COMPLETED'
    );
-- Edge Case: Historical completed enrollment

INSERT INTO
    attendance (
        attendance_id,
        absent_date,
        enrollment_id,
        status,
        reason
    )
VALUES (
        1,
        '2026-06-10',
        1,
        'UNEXCUSED',
        NULL
    ),
    (
        2,
        '2026-06-12',
        1,
        'EXCUSED',
        'Medical leave - fever'
    ),
    (
        3,
        '2026-06-10',
        3,
        'UNEXCUSED',
        'Skipped class'
    ),
    (
        4,
        '2026-06-15',
        4,
        'EXCUSED',
        'University sports competition'
    );
-- Note: Enrollment ID 2 has zero absence logs (perfect attendance record)

INSERT INTO
    assessment (
        assessment_id,
        offering_id,
        title,
        assessment_type,
        max_marks,
        weightage_percent
    )
VALUES (
        1,
        1,
        'Midterm Exam',
        'LAB_TEST',
        50.00,
        40.00
    ),
    (
        2,
        1,
        'Final Project',
        'PRACTICAL',
        50.00,
        60.00
    ),
    (
        3,
        2,
        'Algorithm Quiz',
        'TUTORIAL',
        20.00,
        20.00
    );

INSERT INTO
    student_grade (
        grade_id,
        enrollment_id,
        assessment_id,
        marks_obtained
    )
VALUES (1, 1, 1, 42.50), -- Mg Mg's midterm grade for Advanced DB
    (2, 3, 1, 35.00), -- Aung Aung's midterm grade for Advanced DB
    (3, 2, 3, 18.00);
-- Mg Mg's quiz grade for Data Structures

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;