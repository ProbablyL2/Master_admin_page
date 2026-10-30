package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import application.Teacher;

public class TeacherDAO {
    public List<Teacher> getAllTeacher() {
        List<Teacher> teachers = new ArrayList<>();
        String sql = """
                        SELECT t.teacher_id, t.full_name, d.dept_code, t.position, tci.primary_phone_number
                FROM
                    teacher t
                    JOIN user_account ua ON t.user_id = ua.user_id
                    JOIN department d ON t.dept_id = d.dept_id
                    JOIN teacher_contact_information tci ON t.teacher_id = tci.teacher_id
                WHERE
                    1 = 1
                    AND ua.status = TRUE;
                        """;
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int teacherId = rs.getInt("teacher_id");
                String name = rs.getString("full_name");
                String dept = rs.getString("dept_code");
                String position = rs.getString("position");
                String phone = rs.getString("primary_phone_number");

                Teacher teacher = new Teacher(teacherId, name, dept, position, phone);
                teachers.add(teacher);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teachers;
    }

    public List<Teacher> getFilteredTeachers(String searchText, String department, String position) {
        StringBuilder sql = new StringBuilder(
                """
                        SELECT t.teacher_id, t.full_name, d.dept_code, t.position, tci.primary_phone_number
                            FROM
                                teacher t
                                JOIN user_account ua ON t.user_id = ua.user_id
                                JOIN department d ON t.dept_id = d.dept_id
                                JOIN teacher_contact_information tci ON t.teacher_id = tci.teacher_id
                            WHERE
                                1 = 1
                                AND ua.status = TRUE
                                    """);
        List<Object> parameters = new ArrayList<>();
        if (searchText != null && !searchText.trim().isEmpty()) {
            sql.append("AND ( t.full_name LIKE ? OR tci.primary_phone_number LIKE ? )");
            parameters.add("%" + searchText.toLowerCase() + "%");
            parameters.add("%" + searchText.toLowerCase() + "%");
        }
        if (department != null && !department.equalsIgnoreCase("All Departments")) {
            sql.append("AND d.dept_code = ?");
            parameters.add(department);
        }
        if (position != null && !position.equalsIgnoreCase("All Positions")) {
            sql.append("AND t.position = ?");
            parameters.add(position);
        }
        List<Teacher> teachers = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int teacherId = rs.getInt("teacher_id");
                    String name = rs.getString("full_name");
                    String dept = rs.getString("dept_code");
                    String teacherPosition = rs.getString("position");
                    String phone = rs.getString("primary_phone_number");

                    Teacher teacher = new Teacher(teacherId, name, dept, teacherPosition, phone);
                    teachers.add(teacher);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teachers;
    }

    public Teacher getFullTeacher(int teacherId) {
        String sql = """
                        SELECT
                    t.teacher_id, t.user_id, t.full_name, d.dept_name AS faculty, t.position, t.date_of_birth, t.gender, t.nationality, t.nrc, t.profile_image_url,
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
                        """;
        try (Connection conn = DatabaseConnector.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    java.sql.Date sqlDate = rs.getDate("date_of_birth");
                    String dobString = (sqlDate != null) ? sqlDate.toString() : "";
                    return new Teacher(
                            rs.getInt("teacher_id"),
                            rs.getString("full_name"),
                            rs.getString("faculty"),
                            rs.getString("position"),
                            rs.getString("primary_phone_number"),
                            String.valueOf(rs.getInt("user_id")), // mapping user_id to teacherId parameter
                            dobString,
                            rs.getString("gender"),
                            rs.getString("nationality"),
                            rs.getString("nrc"),
                            rs.getString("email_address"),
                            rs.getString("current_address"),
                            rs.getString("permanent_address"),
                            rs.getString("highest_degree"),
                            rs.getString("university"),
                            String.valueOf(rs.getInt("year_completed")), // year_completed is an int in the table
                            rs.getString("specialization"),
                            rs.getString("emergency_contact"),
                            rs.getString("profile_image_url"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
