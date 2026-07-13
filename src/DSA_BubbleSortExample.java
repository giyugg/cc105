import java.util.Arrays;
import java.util.Scanner;

public class DSA_BubbleSortExample {
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

        for (int pass = 0; pass < arrayLength - 1; pass++) {
            for (int currentIndex = 0; currentIndex < arrayLength - pass - 1; currentIndex++) {
                if (myArray[currentIndex] > myArray[currentIndex + 1]) {
                    int temp = myArray[currentIndex];
                    myArray[currentIndex] = myArray[currentIndex + 1];
                    myArray[currentIndex + 1] = temp;
                }
            }
            System.out.println("After pass " + (pass + 1) + ": " + Arrays.toString(myArray));
        }

        // Bubble Sort Algorithm
        for (int pass = 0; pass < arrayLength - 1; pass++) {
            for (int currentIndex = 0; currentIndex < arrayLength - pass - 1; currentIndex++) {
                if (myArray[currentIndex] > myArray[currentIndex + 1]) {
                    // Swap elements using a temporary variable
                    int temp = myArray[currentIndex];
                    myArray[currentIndex] = myArray[currentIndex + 1];
                    myArray[currentIndex + 1] = temp;
                }
            }
        }

        // Print the sorted array
        System.out.println("Sorted array: " + Arrays.toString(myArray));
    }
}