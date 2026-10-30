package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RegistrationRecord {
    private final StringProperty studentId;
    private final StringProperty studentName;
    private final StringProperty department;
    private final StringProperty regDate;
    private final StringProperty status;

    public RegistrationRecord(String studentId, String studentName, String department, String regDate, String status) {
        this.studentId = new SimpleStringProperty(studentId);
        this.studentName = new SimpleStringProperty(studentName);
        this.department = new SimpleStringProperty(department);
        this.regDate = new SimpleStringProperty(regDate);
        this.status = new SimpleStringProperty(status);
    }

    public String getStudentId() { return studentId.get(); }
    public StringProperty studentIdProperty() { return studentId; }

    public String getStudentName() { return studentName.get(); }
    public StringProperty studentNameProperty() { return studentName; }

    public String getDepartment() { return department.get(); }
    public StringProperty departmentProperty() { return department; }

    public String getRegDate() { return regDate.get(); }
    public StringProperty regDateProperty() { return regDate; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
}
