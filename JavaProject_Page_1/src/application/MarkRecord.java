package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MarkRecord {
    private final StringProperty studentId;
    private final StringProperty studentName;
    private final StringProperty subject;
    private final StringProperty marks;
    private final StringProperty status;

    public MarkRecord(String studentId, String studentName, String subject, String marks, String status) {
        this.studentId = new SimpleStringProperty(studentId);
        this.studentName = new SimpleStringProperty(studentName);
        this.subject = new SimpleStringProperty(subject);
        this.marks = new SimpleStringProperty(marks);
        this.status = new SimpleStringProperty(status);
    }

    public String getStudentId() { return studentId.get(); }
    public StringProperty studentIdProperty() { return studentId; }

    public String getStudentName() { return studentName.get(); }
    public StringProperty studentNameProperty() { return studentName; }

    public String getSubject() { return subject.get(); }
    public StringProperty subjectProperty() { return subject; }

    public String getMarks() { return marks.get(); }
    public StringProperty marksProperty() { return marks; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
}