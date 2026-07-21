package DSA_260716;

import java.util.ArrayList;
import java.util.Scanner;

public class NewRunnerImproved {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        // CHANGE #1:
        // Using ArrayList so we can store unlimited Student objects.
        ArrayList<Student> students = new ArrayList<>();

        System.out.print("How many students do you want to enter? ");
        int numberOfStudents = input.nextInt();
        input.nextLine();

        // CHANGE #1:
        // Loop instead of manually creating s1, s2, s3...
        for (int i = 0; i < numberOfStudents; i++) {

            System.out.println("\nStudent #" + (i + 1));

            System.out.print("ID Number: ");
            String id = input.nextLine();

            System.out.print("Name: ");
            String name = input.nextLine();

            System.out.print("Course: ");
            String course = input.nextLine();

            students.add(new Student(id, name, course));
        }

        System.out.println("\n===== STUDENT LIST =====");

        // Display all students
        for (Student s : students) {
            s.displayInfo();
        }

        // CHANGE #3:
        // Allow updating a student's course.
        System.out.print("\nEnter student number to update course (1-" + students.size() + "): ");
        int choice = input.nextInt();
        input.nextLine();

        if (choice >= 1 && choice <= students.size()) {

            System.out.print("Enter new course: ");
            String newCourse = input.nextLine();

            students.get(choice - 1).updateCourse(newCourse);

            System.out.println("\nUpdated Student Information:");
            students.get(choice - 1).displayInfo();

        } else {
            System.out.println("Invalid student number.");
        }

        // CHANGE #2:
        // Display statistics.
        System.out.println("\n========== STATISTICS ==========");
        System.out.println("Student Objects Created : " + Student.getObjectCount());
        System.out.println("displayInfo() Calls     : " + Student.getDisplayCount());
        System.out.println("updateCourse() Calls    : " + Student.getUpdateCount());

        input.close();
    }
}

class Student {

    private String idNumber;
    private String name;
    private String course;

    // CHANGE #2:
    // Static variables count all Student objects and method calls.
    private static int objectCount = 0;
    private static int displayCount = 0;
    private static int updateCount = 0;

    Student() {
        objectCount++;
    }

    Student(String id, String name, String course) {

        this.idNumber = id;
        this.name = name;
        this.course = course;

        // CHANGE #2:
        // Count every Student object created.
        objectCount++;
    }

    public void displayInfo() {

        // CHANGE #2:
        // Count every displayInfo() call.
        displayCount++;

        System.out.println("ID     : " + idNumber);
        System.out.println("Name   : " + name);
        System.out.println("Course : " + course);
        System.out.println("--------------------------");
    }

    public void updateCourse(String newCourse) {

        // CHANGE #2:
        // Count every updateCourse() call.
        updateCount++;

        this.course = newCourse;
        System.out.println(name + " has been shifted to " + newCourse);
    }

    public String getCourse() {
        return course;
    }

    public String getIDnumber() {
        return idNumber;
    }

    public String getName() {
        return name;
    }

    // CHANGE #2:
    // Getter methods for statistics.
    public static int getObjectCount() {
        return objectCount;
    }

    public static int getDisplayCount() {
        return displayCount;
    }

    public static int getUpdateCount() {
        return updateCount;
    }
}