package DSA_260723;
import java.util.*;

public class FailedTripletsActivity {
    public static void main(String[] args) {
        // Goal: Find the triplets
        Scanner input = new Scanner(System.in);

        System.out.print("Enter array size: ");
        int arraySize = input.nextInt();
        input.nextLine();

        System.out.print("Input numbers: ");
        String numbersLine = input.nextLine();

        String[] numbersStrings = numbersLine.split(" ");

        int[] numbers = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            numbers[i] = Integer.parseInt(numbersStrings[i]);
        }

        int tripletCount = 0;
        for (int i = 0; i <= arraySize - 3; i++) {
            int first = numbers[i];
            int second = numbers[i+1];
            int third = numbers[i+2];

            if (first == second && second == third) {
                tripletCount = tripletCount +1;
            }
        }

        System.out.println("Triplets: " + tripletCount);
    }
}