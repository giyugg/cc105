package DSA_260716;

public class MyRunnerClass {
    public static void main(String[] args) {
        Student s1 = new Student("IS001", "Kyle", "BSIS");
        Student s2 = new Student("IS002", "Giyu", "BSCS");
        s1.displayInfo();
        s2.displayInfo();
        s2.updateCourse("CHTM");
        System.out.println(s2.getCourse());
    }
}
class StudentInfo {
    private String idNumber,name,course;

    StudentInfo() {
        System.out.println("Hello");
    }
    StudentInfo(String s1, String s2, String s3) {
        this.idNumber = s1;
        this.name = s2;
        this.course = s3;
    }

    public void displayInfo() {
        System.out.println(idNumber);
        System.out.println(name);
        System.out.println(course);
        System.out.println("----------------------");
    }

    public void updateCourse(String newCourse) {
        this.course = newCourse;
        System.out.println(name + " has been shifted to " + newCourse);
    }

    public String getCourse() {
        return this.course;
    }
    public String getIDnumber() {
        return this.idNumber;
    }
    public String getName() {
        return this.name;
    }
}