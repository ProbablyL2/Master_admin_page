--Student Queries

--Basic Query
--Manage Student Page
WITH
    RankedStudents AS (
        SELECT s.student_id, s.roll_no, s.full_name, d.dept_code, s.current_year, co.term, ci.primary_phone_number, ua.status, ROW_NUMBER() OVER (
                PARTITION BY
                    s.student_id
                ORDER BY ce.enrollment_id DESC
            ) AS rn
        FROM
            student s
            JOIN user_account ua ON s.user_id = ua.user_id
            JOIN department d ON s.dept_id = d.dept_id
            JOIN student_contact_information ci ON s.student_id = ci.student_id
            JOIN course_enrollment ce ON s.student_id = ce.student_id
            JOIN course_offering co ON ce.offering_id = co.offering_id
    )
SELECT
    student_id,
    roll_no,
    full_name,
    dept_code,
    current_year,
    term,
    primary_phone_number
FROM RankedStudents
WHERE
    rn = 1
    AND `status` = TRUE
    AND 1 = 1

--Append dynamically when searchbar is not null
AND (
    full_name LIKE ?
    OR roll_no LIKE ?
    OR primary_phone_number LIKE ?
)
--Append dynamically when major is filtered
AND (dept_code = ?)

--Append dynamically when current_year is filtered
AND (current_year = ?)

--Append dynamically when semester is filtered
AND (term = ?);

--------------------------------------------------------------
--------------------------------------------------------------

--Full student Information Query
WITH
    RankedStudent AS (
        SELECT
            s.student_id,
            s.roll_no,
            s.full_name,
            d.dept_name AS faculty,
            s.current_year,
            co.term,
            s.date_of_birth,
            s.gender,
            s.nationality,
            s.nrc,
            s.profile_image_url,
            ci.primary_phone_number,
            ci.email_address,
            ci.current_address,
            ci.permanent_address,
            sg.father_full_name AS father_name,
            sg.father_occupation AS father_occupation,
            sg.father_phone_number AS father_phone,
            sg.father_email_address AS father_email,
            sg.mother_full_name AS mother_name,
            sg.mother_occupation AS mother_occupation,
            sg.mother_phone_number AS mother_phone,
            sg.mother_email_address AS mother_email,
            seb.previous_school,
            seb.year_completed,
            seb.additional_info,
            ROW_NUMBER() OVER (
                ORDER BY ce.enrollment_id DESC
            ) AS rn
        FROM
            student s
            LEFT JOIN department d ON s.dept_id = d.dept_id
            LEFT JOIN student_contact_information ci ON s.student_id = ci.student_id
            LEFT JOIN student_guardian sg ON s.student_id = sg.student_id
            LEFT JOIN student_education_background seb ON s.student_id = seb.student_id
            JOIN course_enrollment ce ON s.student_id = ce.student_id
            JOIN course_offering co ON ce.offering_id = co.offering_id
        WHERE
            s.student_id = ?
    )
SELECT
    student_id,
    roll_no,
    full_name,
    faculty,
    current_year,
    term,
    date_of_birth,
    gender,
    nationality,
    nrc,
    profile_image_url,
    primary_phone_number,
    email_address,
    current_address,
    permanent_address,
    father_name,
    father_occupation,
    father_phone,
    father_email,
    mother_name,
    mother_occupation,
    mother_phone,
    mother_email,
    previous_school,
    year_completed,
    additional_info
FROM RankedStudent
WHERE
    rn = 1;

--------------------------------------------------------------
--------------------------------------------------------------

--Add Student Query
INSERT INTO
    user_account (
        username,
        email,
        password_hash,
        user_role
    )
VALUES (?, ?, ?, 'STUDENT');

INSERT INTO
    student (
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
        ?,
        ?,
        ?,
        ?,
        ?,
        ?,
        ?,
        ?,
        ?,
        ?,
        ?,
        ?
    );

INSERT INTO
    student_contact_information (
        student_id,
        primary_phone_number,
        secondary_phone_number,
        email_address,
        current_address,
        permanent_address
    )
VALUES (?, ?, ?, ?, ?, ?);

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
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);

--Modify Student Query

--Main Table SQL
UPDATE student
SET
    roll_no = ?,
    full_name = ?,
    dept_id = ?,
    current_year = ?,
    gpa = ?,
    date_of_birth = ?,
    gender = ?,
    nationality = ?,
    nrc = ?,
    profile_image_url = ?
WHERE
    student_id = ?

--Contact SQL
UPDATE student_contact_information
SET
    primary_phone_number = ?,
    secondary_phone_number = ?,
    email_address = ?,
    current_address = ?,
    permanent_address = ?
WHERE
    student_id = ?

--Guardian SQL
UPDATE student_guardian
SET
    father_full_name = ?,
    father_occupation = ?,
    father_phone_number = ?,
    father_email_address = ?,
    mother_full_name = ?,
    mother_occupation = ?,
    mother_phone_number = ?,
    mother_email_address = ?
WHERE
    student_id = ?

--DELETE student query
UPDATE user_account
SET
    `status` = FALSE
WHERE
    user_id = (
        SELECT user_id
        FROM student
        WHERE
            student_id = ?
    );