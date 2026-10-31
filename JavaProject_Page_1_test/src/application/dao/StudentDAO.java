package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import application.Student;

public class StudentDAO {
    public List<Student> getAllStudent() {
        List<Student> students = new ArrayList<>();
        String sql = """
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
                    AND 1 = 1;""";
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("student_id");
                String rollNum = rs.getString("roll_no");
                String name = rs.getString("full_name");
                String dept_code = rs.getString("dept_code");
                String current_year = rs.getString("current_year");
                String term = rs.getString("term");
                String primary_phone_number = rs.getString("primary_phone_number");

                Student student = new Student(id, rollNum, name, dept_code, current_year, term, primary_phone_number);

                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public List<Student> getFilteredStudents(String searchText, String department, String currentYear,
            String semester) {
        StringBuilder sql = new StringBuilder(
                """
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
                                    """);
        List<Object> parameters = new ArrayList<>();
        if (searchText != null && !searchText.trim().isEmpty()) {
            sql.append("""
                         AND (
                         full_name LIKE ?
                         OR roll_no LIKE ?
                         OR primary_phone_number LIKE ?
                        )
                    """);
            parameters.add("%" + searchText.toLowerCase() + "%");
            parameters.add("%" + searchText.toLowerCase() + "%");
            parameters.add("%" + searchText.toLowerCase() + "%");
        }
        if (department != null && !department.equalsIgnoreCase("All Majors")) {
            sql.append("AND (dept_code = ?)");
            parameters.add(department);
        }
        if (currentYear != null && !currentYear.equalsIgnoreCase("All Years")) {
            sql.append("AND (current_year = ?)");
            parameters.add(currentYear);
        }
        if (semester != null && !semester.equalsIgnoreCase("All Semesters")) {
            sql.append("AND (term = ?)");
            parameters.add(semester);
        }

        List<Student> students = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("student_id");
                    String rollNum = rs.getString("roll_no");
                    String name = rs.getString("full_name");
                    String dept_code = rs.getString("dept_code");
                    String current_year = rs.getString("current_year");
                    String term = rs.getString("term");
                    String primary_phone_number = rs.getString("primary_phone_number");

                    Student student = new Student(id, rollNum, name, dept_code, current_year, term,
                            primary_phone_number);

                    students.add(student);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public Student getStudentbyId(int studentId) {
        String sql = """
                      WITH
                    RankedStudent AS (
                        SELECT
                            s.student_id,
                            s.roll_no,
                            s.full_name,
                            d.dept_code AS faculty,
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
                      """;
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    java.sql.Date sqlDate = rs.getDate("date_of_birth");
                    String dobString = (sqlDate != null) ? sqlDate.toString() : "";

                    return new Student(
                            rs.getInt("student_id"),
                            rs.getString("roll_no"),
                            rs.getString("full_name"),
                            rs.getString("faculty"),
                            rs.getString("current_year"),
                            rs.getString("term"),
                            rs.getString("primary_phone_number"), // 7: phoneNo
                            dobString,                            // 8: dob
                            rs.getString("gender"),               // 9: gender ("Female")
                            rs.getString("nationality"),          // 10: nationality ("Myanmar")
                            rs.getString("nrc"),                  // 11: nrc ("12/TaTaNa(N)...")
                            rs.getString("email_address"),        // 12: email
                            rs.getString("current_address"),      // 13: currentAddress
                            rs.getString("permanent_address"),    // 14: permanentAddress
                            rs.getString("father_name"),          // 15: fatherName
                            rs.getString("father_occupation"),    // 16: fatherOccupation
                            rs.getString("father_phone"),         // 17: fatherPhone
                            rs.getString("father_email"),         // 18: fatherEmail
                            rs.getString("mother_name"),          // 19: motherName
                            rs.getString("mother_occupation"),    // 20: motherOccupation
                            rs.getString("mother_phone"),         // 21: motherPhone
                            rs.getString("mother_email"),         // 22: motherEmail
                            rs.getString("previous_school"),      // 23: prevSchool
                            rs.getString("year_completed"),       // 24: yearCompleted
                            rs.getString("additional_info"),      // 25: additionalInfo
                            rs.getString("profile_image_url")     // 26: photoPath
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
