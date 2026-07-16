package DSA_260716;

import java.util.Scanner;

public class MyRunnerClass2 {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter details for Student 1");
        System.out.print("ID Number: ");
        String id1 = input.nextLine();

        System.out.print("Name: ");
        String name1 = input.nextLine();

        System.out.print("Course: ");
        String course1 = input.nextLine();

        Student s1 = new Student(id1, name1, course1);

        // Input for Student 2
        System.out.println("\nEnter details for Student 2");
        System.out.print("ID Number: ");
        String id2 = input.nextLine();

        System.out.print("Name: ");
        String name2 = input.nextLine();

        System.out.print("Course: ");
        String course2 = input.nextLine();

        Student s2 = new Student(id2, name2, course2);

        // Display student information
        System.out.println("\nStudent 1 Information");
        s1.displayInfo();

        System.out.println("\nStudent 2 Information");
        s2.displayInfo();

        input.close();
    }
}

class Student {
    private String idNumber, name, course;

    Student() {
        System.out.println("Hello");
    }

    Student(String s1, String s2, String s3) {
        this.idNumber = s1;
        this.name = s2;
        this.course = s3;
    }

    public void displayInfo() {
        System.out.println("ID Number: " + idNumber);
        System.out.println("Name: " + name);
        System.out.println("Course: " + course);
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