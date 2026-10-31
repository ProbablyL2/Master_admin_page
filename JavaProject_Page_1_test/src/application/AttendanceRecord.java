package application;

import java.time.LocalDate;
import javafx.beans.property.*;

public class AttendanceRecord {
    private final IntegerProperty no;
    private final StringProperty rollNo;
    private final StringProperty studentName;
    private final StringProperty department;
    private final StringProperty year;
    private final StringProperty semester;
    private final ObjectProperty<LocalDate> dateObj;
    private final IntegerProperty period;
    private final StringProperty subject;
    private final StringProperty status;
    private final StringProperty markedBy;
    private final StringProperty remarks;
    private final StringProperty attendedTotal;
    private final DoubleProperty percentage;

    // Constructor for Matrix & Individual Views
    public AttendanceRecord(int no, String rollNo, String studentName, String department, String year, String semester, String subject, String attendedTotal, double percentage, LocalDate dateObj) {
        this.no = new SimpleIntegerProperty(no);
        this.rollNo = new SimpleStringProperty(rollNo);
        this.studentName = new SimpleStringProperty(studentName);
        this.department = new SimpleStringProperty(department);
        this.year = new SimpleStringProperty(year);
        this.semester = new SimpleStringProperty(semester);
        this.subject = new SimpleStringProperty(subject);
        this.attendedTotal = new SimpleStringProperty(attendedTotal);
        this.percentage = new SimpleDoubleProperty(percentage);
        this.dateObj = new ObjectPropertyBase<LocalDate>(dateObj) {
            @Override public Object getBean() { return AttendanceRecord.this; }
            @Override public String getName() { return "dateObj"; }
        };
        this.period = new SimpleIntegerProperty(1);
        this.status = new SimpleStringProperty("-");
        this.markedBy = new SimpleStringProperty("-");
        this.remarks = new SimpleStringProperty("-");
    }

    // Constructor for Daily Log (with Period, Status, Remarks)
    public AttendanceRecord(int no, String rollNo, String studentName, String department, String year, String semester, LocalDate dateObj, int period, String subject, String status, String markedBy, String remarks) {
        this.no = new SimpleIntegerProperty(no);
        this.rollNo = new SimpleStringProperty(rollNo);
        this.studentName = new SimpleStringProperty(studentName);
        this.department = new SimpleStringProperty(department);
        this.year = new SimpleStringProperty(year);
        this.semester = new SimpleStringProperty(semester);
        this.dateObj = new ObjectPropertyBase<LocalDate>(dateObj) {
            @Override public Object getBean() { return AttendanceRecord.this; }
            @Override public String getName() { return "dateObj"; }
        };
        this.period = new SimpleIntegerProperty(period);
        this.subject = new SimpleStringProperty(subject);
        this.status = new SimpleStringProperty(status);
        this.markedBy = new SimpleStringProperty(markedBy);
        this.remarks = new SimpleStringProperty(remarks);
        this.attendedTotal = new SimpleStringProperty("-");
        this.percentage = new SimpleDoubleProperty(0.0);
    }

    public int getNo() { return no.get(); }
    public IntegerProperty noProperty() { return no; }

    public String getRollNo() { return rollNo.get(); }
    public StringProperty rollNoProperty() { return rollNo; }

    public String getStudentName() { return studentName.get(); }
    public StringProperty studentNameProperty() { return studentName; }

    public String getDepartment() { return department.get(); }
    public StringProperty departmentProperty() { return department; }

    public String getYear() { return year.get(); }
    public StringProperty yearProperty() { return year; }

    public String getSemester() { return semester.get(); }
    public StringProperty semesterProperty() { return semester; }

    public LocalDate getDateObj() { return dateObj.get(); }
    public ObjectProperty<LocalDate> dateObjProperty() { return dateObj; }
    public String getDate() { return dateObj.get() != null ? dateObj.get().toString() : ""; }
    public StringProperty dateProperty() { return new SimpleStringProperty(getDate()); }

    public int getPeriod() { return period.get(); }
    public IntegerProperty periodProperty() { return period; }

    public String getSubject() { return subject.get(); }
    public StringProperty subjectProperty() { return subject; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }

    public String getMarkedBy() { return markedBy.get(); }
    public StringProperty markedByProperty() { return markedBy; }

    public String getRemarks() { return remarks.get(); }
    public StringProperty remarksProperty() { return remarks; }

    public String getAttendedTotal() { return attendedTotal.get(); }
    public StringProperty attendedTotalProperty() { return attendedTotal; }

    public double getPercentage() { return percentage.get(); }
    public DoubleProperty percentageProperty() { return percentage; }

    public boolean isLowAttendance() {
        return percentage.get() > 0 && percentage.get() < 75.0;
    }
}