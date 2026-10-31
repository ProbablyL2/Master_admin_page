--Teacher Queries

--Basic Query
--Manage Teacher Page
SELECT t.teacher_id, t.full_name, d.dept_code, t.position, tci.primary_phone_number
FROM
    teacher t
    JOIN user_account ua ON t.user_id = ua.user_id
    JOIN department d ON t.dept_id = d.dept_id
    JOIN teacher_contact_information tci ON t.teacher_id = tci.teacher_id
WHERE
    1 = 1
    AND ua.status = TRUE

--Append Dynamically when searchbar is not null
AND ( t.full_name LIKE ? OR tci.primary_phone_number LIKE ? )

--Append Dynamically when department is filtered
AND (d.dept_code = ?)

--Append Dynamically when position is filtered
AND (t.position = ?)

--------------------------------------------------------------
--------------------------------------------------------------

--Full Teacher Information Query
SELECT
    t.teacher_id,
    t.user_id,
    t.full_name,
    d.dept_code AS faculty,
    t.position,
    t.date_of_birth,
    t.gender,
    t.nationality,
    t.nrc,
    t.profile_image_url,
    tci.primary_phone_number,
    tci.email_address,
    tci.current_address,
    tci.permanent_address,
    tab.highest_degree,
    tab.university,
    tab.year_completed,
    tab.specialization,
    tab.emergency_contact
FROM
    teacher t
    LEFT JOIN department d ON t.dept_id = d.dept_id
    LEFT JOIN teacher_contact_information tci ON t.teacher_id = tci.teacher_id
    LEFT JOIN teacher_academic_background tab ON t.teacher_id = tab.teacher_id
WHERE
    t.teacher_id = ?;

--Add Teacher Query
INSERT INTO
    user_account (
        username,
        email,
        password_hash,
        user_role
    )
VALUES (?, ?, ?, 'TEACHER')

INSERT INTO
    teacher (
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
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)

INSERT INTO
    teacher_contact_information (
        teacher_id,
        primary_phone_number,
        email_address,
        current_address,
        permanent_address
    )
VALUES (?, ?, ?, ?, ?)

INSERT INTO
    teacher_academic_background (
        teacher_id,
        highest_degree,
        university,
        year_completed,
        specialization,
        emergency_contact
    )
VALUES (?, ?, ?, ?, ?, ?);

--Modify Teacher Query
UPDATE teacher
SET
    full_name = ?,
    dept_id = ?,
    position = ?,
    date_of_birth = ?,
    gender = ?,
    nationality = ?,
    nrc = ?,
    profile_image_url = ?
WHERE
    teacher_id = ?

--Contact SQL
UPDATE teacher_contact_information
SET
    primary_phone_number = ?,
    email_address = ?,
    current_address = ?,
    permanent_address = ?
WHERE
    teacher_id = ?

--Academic Background SQL
UPDATE teacher_academic_background
SET
    highest_degree = ?,
    university = ?,
    year_completed = ?,
    specialization = ?,
    emergency_contact = ?
WHERE
    teacher_id = ?;

--Delete Teacher SQL
UPDATE user_account
SET
    `status` = FALSE
WHERE
    user_id = (
        SELECT user_id
        FROM teacher
        WHERE
            teacher_id = ?
    );