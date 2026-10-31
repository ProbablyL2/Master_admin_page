package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student {
    private final IntegerProperty no;
    private final StringProperty rollNo;
    private final StringProperty name;
    private final StringProperty major;
    private final StringProperty year;
    private final StringProperty semester;
    private final StringProperty phoneNo;

    private final StringProperty dob;
    private final StringProperty gender;
    private final StringProperty nationality;
    private final StringProperty nrc;
    private final StringProperty email;
    private final StringProperty currentAddress;
    private final StringProperty permanentAddress;
    private final StringProperty fatherName;
    private final StringProperty fatherOccupation;
    private final StringProperty fatherPhone;
    private final StringProperty fatherEmail;
    private final StringProperty motherName;
    private final StringProperty motherOccupation;
    private final StringProperty motherPhone;
    private final StringProperty motherEmail;
    private final StringProperty prevSchool;
    private final StringProperty yearCompleted;
    private final StringProperty additionalInfo;
    private final StringProperty photoPath;

    // Original backups for reset functionality
    private final String origRollNo, origName, origMajor, origYear, origSemester, origPhoneNo;
    private final String origDob, origGender, origNationality, origNrc, origEmail, origCurrentAddress, origPermanentAddress;
    private final String origFatherName, origFatherOccupation, origFatherPhone, origFatherEmail;
    private final String origMotherName, origMotherOccupation, origMotherPhone, origMotherEmail;
    private final String origPrevSchool, origYearCompleted, origAdditionalInfo;

    // Blank constructor for new student addition
    public Student(int no, String rollNo, String name, String major, String year, String semester, String phoneNo) {
        this(no, rollNo, name, major, year, semester, phoneNo, 
             "", "", "", "", name.toLowerCase().replace(" ", "") + "", 
             "", "", "", "", "", "", 
             "", "", "", "", 
             "", "", "", null);
    }

    public Student(int no, String rollNo, String name, String major, String year, String semester, String phoneNo,
                   String dob, String gender, String nationality, String nrc, String email, 
                   String currentAddress, String permanentAddress, String fatherName, String fatherOccupation, 
                   String fatherPhone, String fatherEmail, String motherName, String motherOccupation, 
                   String motherPhone, String motherEmail, String prevSchool, String yearCompleted, String additionalInfo, String photoPath) {
        this.no = new SimpleIntegerProperty(no);
        this.rollNo = new SimpleStringProperty(rollNo);
        this.name = new SimpleStringProperty(name);
        this.major = new SimpleStringProperty(major);
        this.year = new SimpleStringProperty(year);
        this.semester = new SimpleStringProperty(semester != null ? semester : "Semester I");
        this.phoneNo = new SimpleStringProperty(phoneNo);
        this.dob = new SimpleStringProperty(dob != null ? dob : "");
        this.gender = new SimpleStringProperty(gender != null ? gender : "");
        this.nationality = new SimpleStringProperty(nationality != null ? nationality : "Myanmar");
        this.nrc = new SimpleStringProperty(nrc != null ? nrc : "");
        this.email = new SimpleStringProperty(email != null ? email : "");
        this.currentAddress = new SimpleStringProperty(currentAddress != null ? currentAddress : "");
        this.permanentAddress = new SimpleStringProperty(permanentAddress != null ? permanentAddress : "");
        this.fatherName = new SimpleStringProperty(fatherName != null ? fatherName : "");
        this.fatherOccupation = new SimpleStringProperty(fatherOccupation != null ? fatherOccupation : "");
        this.fatherPhone = new SimpleStringProperty(fatherPhone != null ? fatherPhone : "");
        this.fatherEmail = new SimpleStringProperty(fatherEmail != null ? fatherEmail : "");
        this.motherName = new SimpleStringProperty(motherName != null ? motherName : "");
        this.motherOccupation = new SimpleStringProperty(motherOccupation != null ? motherOccupation : "");
        this.motherPhone = new SimpleStringProperty(motherPhone != null ? motherPhone : "");
        this.motherEmail = new SimpleStringProperty(motherEmail != null ? motherEmail : "");
        this.prevSchool = new SimpleStringProperty(prevSchool != null ? prevSchool : "");
        this.yearCompleted = new SimpleStringProperty(yearCompleted != null ? yearCompleted : "");
        this.additionalInfo = new SimpleStringProperty(additionalInfo != null ? additionalInfo : "");
        this.photoPath = new SimpleStringProperty(photoPath != null ? photoPath : "");

        this.origRollNo = rollNo;
        this.origName = name;
        this.origMajor = major;
        this.origYear = year;
        this.origSemester = semester != null ? semester : "Semester I";
        this.origPhoneNo = phoneNo;
        this.origDob = dob != null ? dob : "";
        this.origGender = gender != null ? gender : "";
        this.origNationality = nationality != null ? nationality : "Myanmar";
        this.origNrc = nrc != null ? nrc : "";
        this.origEmail = email != null ? email : "";
        this.origCurrentAddress = currentAddress != null ? currentAddress : "";
        this.origPermanentAddress = permanentAddress != null ? permanentAddress : "";
        this.origFatherName = fatherName != null ? fatherName : "";
        this.origFatherOccupation = fatherOccupation != null ? fatherOccupation : "";
        this.origFatherPhone = fatherPhone != null ? fatherPhone : "";
        this.origFatherEmail = fatherEmail != null ? fatherEmail : "";
        this.origMotherName = motherName != null ? motherName : "";
        this.origMotherOccupation = motherOccupation != null ? motherOccupation : "";
        this.origMotherPhone = motherPhone != null ? motherPhone : "";
        this.origMotherEmail = motherEmail != null ? motherEmail : "";
        this.origPrevSchool = prevSchool != null ? prevSchool : "";
        this.origYearCompleted = yearCompleted != null ? yearCompleted : "";
        this.origAdditionalInfo = additionalInfo != null ? additionalInfo : "";
    }

    public void resetToOriginal() {
        rollNo.set(origRollNo);
        name.set(origName);
        major.set(origMajor);
        year.set(origYear);
        semester.set(origSemester);
        phoneNo.set(origPhoneNo);
        dob.set(origDob);
        gender.set(origGender);
        nationality.set(origNationality);
        nrc.set(origNrc);
        email.set(origEmail);
        currentAddress.set(origCurrentAddress);
        permanentAddress.set(origPermanentAddress);
        fatherName.set(origFatherName);
        fatherOccupation.set(origFatherOccupation);
        fatherPhone.set(origFatherPhone);
        fatherEmail.set(origFatherEmail);
        motherName.set(origMotherName);
        motherOccupation.set(origMotherOccupation);
        motherPhone.set(origMotherPhone);
        motherEmail.set(origMotherEmail);
        prevSchool.set(origPrevSchool);
        yearCompleted.set(origYearCompleted);
        additionalInfo.set(origAdditionalInfo);
    }

    public int getNo() { return no.get(); }
    public IntegerProperty noProperty() { return no; }
    public void setNo(int no) { this.no.set(no); }

    public String getRollNo() { return rollNo.get(); }
    public StringProperty rollNoProperty() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo.set(rollNo); }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public void setName(String name) { this.name.set(name); }

    public String getMajor() { return major.get(); }
    public StringProperty majorProperty() { return major; }
    public void setMajor(String major) { this.major.set(major); }

    public String getYear() { return year.get(); }
    public StringProperty yearProperty() { return year; }
    public void setYear(String year) { this.year.set(year); }

    public String getSemester() { return semester.get(); }
    public StringProperty semesterProperty() { return semester; }
    public void setSemester(String semester) { this.semester.set(semester); }

    public String getPhoneNo() { return phoneNo.get(); }
    public StringProperty phoneNoProperty() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo.set(phoneNo); }

    public String getDob() { return dob.get(); }
    public void setDob(String dob) { this.dob.set(dob); }

    public String getGender() { return gender.get(); }
    public void setGender(String gender) { this.gender.set(gender); }

    public String getNationality() { return nationality.get(); }
    public void setNationality(String nationality) { this.nationality.set(nationality); }

    public String getNrc() { return nrc.get(); }
    public void setNrc(String nrc) { this.nrc.set(nrc); }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }

    public String getCurrentAddress() { return currentAddress.get(); }
    public void setCurrentAddress(String currentAddress) { this.currentAddress.set(currentAddress); }

    public String getPermanentAddress() { return permanentAddress.get(); }
    public void setPermanentAddress(String permanentAddress) { this.permanentAddress.set(permanentAddress); }

    public String getFatherName() { return fatherName.get(); }
    public void setFatherName(String fatherName) { this.fatherName.set(fatherName); }

    public String getFatherOccupation() { return fatherOccupation.get(); }
    public void setFatherOccupation(String fatherOccupation) { this.fatherOccupation.set(fatherOccupation); }

    public String getFatherPhone() { return fatherPhone.get(); }
    public void setFatherPhone(String fatherPhone) { this.fatherPhone.set(fatherPhone); }

    public String getFatherEmail() { return fatherEmail.get(); }
    public void setFatherEmail(String fatherEmail) { this.fatherEmail.set(fatherEmail); }

    public String getMotherName() { return motherName.get(); }
    public void setMotherName(String motherName) { this.motherName.set(motherName); }

    public String getMotherOccupation() { return motherOccupation.get(); }
    public void setMotherOccupation(String motherOccupation) { this.motherOccupation.set(motherOccupation); }

    public String getMotherPhone() { return motherPhone.get(); }
    public void setMotherPhone(String motherPhone) { this.motherPhone.set(motherPhone); }

    public String getMotherEmail() { return motherEmail.get(); }
    public void setMotherEmail(String motherEmail) { this.motherEmail.set(motherEmail); }

    public String getPrevSchool() { return prevSchool.get(); }
    public void setPrevSchool(String prevSchool) { this.prevSchool.set(prevSchool); }

    public String getYearCompleted() { return yearCompleted.get(); }
    public void setYearCompleted(String yearCompleted) { this.yearCompleted.set(yearCompleted); }

    public String getAdditionalInfo() { return additionalInfo.get(); }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo.set(additionalInfo); }

    public String getPhotoPath() { 
        return photoPath.get(); 
    }

    public void setPhotoPath(String path) {
        this.photoPath.set(path);
    }
}