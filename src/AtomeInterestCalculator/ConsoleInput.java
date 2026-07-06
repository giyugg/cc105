package AtomeInterestCalculator;

import java.util.Scanner;

// Handles all user input: reading and validating values typed at the console.
public class ConsoleInput {

    // Asks whether the customer has a previous installment, and if so, collects its details.
    public static PreviousInstallment readPreviousInstallment(Scanner scanner) {
        System.out.print("Does the customer have a previous installment plan? (yes/no): ");
        String answer = scanner.nextLine().trim().toLowerCase();

        if (!answer.equals("yes") && !answer.equals("y")) {
            return null;
        }

        int planMonths = readPlanMonths(scanner, "Is the previous installment a 3-month or 6-month plan? (enter 3 or 6): ");
        double monthlyPayment = readPositiveDouble(scanner, "Enter the previous installment's monthly payment (Php): ");
        int monthsRemaining = readMonthsRemaining(scanner, planMonths,
                "Enter how many months remain to be paid, including this month's payment (1 to " + planMonths + "): ");

        return new PreviousInstallment(planMonths, monthlyPayment, monthsRemaining);
    }

    // Restricts input to 3 or 6
    public static int readPlanMonths(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equals("3") || input.equals("6")) {
                return Integer.parseInt(input);
            }
            System.out.println("Please enter either 3 or 6.");
        }
    }

    // Restricts input to a valid range of remaining months (1 to planMonths)
    public static int readMonthsRemaining(Scanner scanner, int planMonths, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= 1 && value <= planMonths) {
                    return value;
                }
                System.out.println("Please enter a value between 1 and " + planMonths + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number (e.g. 1).");
            }
        }
    }

    // Keeps asking until the user enters a number greater than 0
    public static double readPositiveDouble(Scanner scanner, String prompt) {
        double value;
        while (true) {
            System.out.print(prompt);
            try {
                value = Double.parseDouble(scanner.nextLine().trim());
                if (value > 0) {
                    return value;
                }
                System.out.println("Please enter a value greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value (e.g. 33877.84).");
            }
        }
    }
}
