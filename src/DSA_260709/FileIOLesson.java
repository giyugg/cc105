package DSA_260709;

import java.io.*;
import java.util.*;

public class FileIOLesson {
    static String fileName = "students.txt";

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int choice;

        do {
            System.out.println("File Handling Menu :D");
            System.out.println("1. Create File");
            System.out.println("2. Add data");
            System.out.println("3. Retrieve data");
            System.out.println("4. Update data");
            System.out.println("5. Delete data");
            System.out.println("6. Delete file");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            choice = input.nextInt();
            input.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    createFile();
                    break;
                case 2:
                    System.out.print("Enter student name to add: ");
                    String newData = input.nextLine();
                    addData(newData);
                    break;
                case 3:
                    retrieveData();
                    break;
                case 4:
                    System.out.print("Enter old data to update: ");
                    String oldData = input.nextLine();
                    System.out.print("Enter new data: ");
                    String newUpdatedData = input.nextLine();
                    updateData(oldData,newUpdatedData);
                    break;
                case 5:
                    System.out.print("Enter data to delete: ");
                    String dataToDelete = input.nextLine();
                    deleteData(dataToDelete);
                    break;
                case 6:
                    deleteFile();
                    break;
                case 7:
                    System.out.println("Program ended.");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 7);
    }

    // Method to create file
    public static void createFile() {
        try {
            File file = new File(fileName);

            if (file.createNewFile()) {
                System.out.println("File created successfully: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (Exception e) {
            System.out.println("Error in creating file: " + e.getMessage());
        }
    }

    // Method to delete the whole file
    public static void deleteFile() {
        try {
            File file = new File(fileName);

            if (file.delete()) {
                System.out.println("File deleted successfully.");
            } else {
                System.out.println("File could not be deleted or does not exist.");
            }
        } catch (Exception e) {
            System.out.println("Error in deleting file: " + e.getMessage());
        }
    }

    // Method to add a data within/inside a file.
    public static void addData (String data) {
        try {
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(data + "\n");
            writer.close();
            System.out.println("Data added successfully.");
        } catch (IOException e) {
            System.out.println("Error in adding data: " + e.getMessage());
        }
    }

    // Method to retrieve/read data.
    public static void retrieveData() {
        try {
            File file = new File(fileName);
            Scanner reader = new Scanner(file);

            System.out.println("File Content:");
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                System.out.println(line);
            } reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error in reading file " + e.getMessage());
        }
    }

    public static void updateData(String oldData, String newData) {
        try {
            File file = new File(fileName);
            Scanner reader = new Scanner(file);
            String fileContent = "";
            boolean found = false;

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.equals(oldData)) {
                    fileContent += newData + "\n";
                    found = true;
                } else {
                    fileContent += line + "\n";
                }
            } reader.close();

            FileWriter writer = new FileWriter(fileName);
            writer.write(fileContent);
            writer.close();

            if (found) {
                System.out.println("Data updated successfully");
            } else {
                System.out.println("Data not found.");
            }

        } catch (IOException e) {
            System.out.println("Error in updating data: " + e.getMessage());
        }
    }

    public static void deleteData(String dataToDelete) {
        try {
            File file = new File(fileName);
            Scanner reader = new Scanner(file);
            String fileContent = "";
            boolean found = false;

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.equals(dataToDelete)) {
                    found = true;
                } else {
                    fileContent += line + "\n";
                }
            } reader.close();

            FileWriter writer = new FileWriter(fileName);
            writer.write(fileContent);
            writer.close();

            if (found) {
                System.out.println("Data deleted successfully.");
            } else {
                System.out.println("Data not found.");
            }
        } catch (IOException e) {
            System.out.println("Error in deleting data: " + e.getMessage());
        }
    }
}