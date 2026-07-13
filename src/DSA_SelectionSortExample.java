import java.util.Arrays;
import java.util.Scanner;

public class DSA_SelectionSortExample {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Size of an Array: ");
        int size = input.nextInt();

        int[] myArray = new int[size];
        int arrayLength = myArray.length;

        System.out.print("Enter " + size + " integers:");
        for (int index = 0; index < size; index++) {
            myArray[index] = input.nextInt();
        }

        // Selection Sort Algorithm
        for (int pass = 0; pass < arrayLength - 1; pass++) {
            int minIndex = pass;

            for (int currentIndex = pass + 1; currentIndex < arrayLength; currentIndex++) {
                if (myArray[currentIndex] < myArray[minIndex]) {
                    minIndex = currentIndex;
                }
            }

            // Swap the found minimum element with the first unsorted element
            if (minIndex != pass) {
                int temp = myArray[pass];
                myArray[pass] = myArray[minIndex];
                myArray[minIndex] = temp;
            }

            System.out.println("After pass " + (pass + 1) + ": " + Arrays.toString(myArray));
        }

        // Print the sorted array
        System.out.println("Sorted array: " + Arrays.toString(myArray));
    }
}