package AtomeInterestCalculator;

import java.text.DecimalFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StraightMain {

    // Default flat interest rates set by Atome for each plan
    static final double THREE_MONTH_RATE = 0.12; // 12%
    static final double SIX_MONTH_RATE = 0.24;   // 24%

    static final DecimalFormat MONEY = new DecimalFormat("#,##0.00");
    static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MMMM yyyy");

    // Represents one payment plan option for the CURRENT bill (e.g. "Pay in 3 months")
    static class PaymentPlan {
        String name;
        int months;
        double interestRate; // e.g. 0.12 for 12%
        double principal;

        PaymentPlan(String name, int months, double interestRate, double principal) {
            this.name = name;
            this.months = months;
            this.interestRate = interestRate;
            this.principal = principal;
        }

        double interest() {
            return principal * interestRate;
        }

        double totalPayment() {
            return principal + interest();
        }

        double monthlyPayment() {
            return totalPayment() / months;
        }
    }

    // Represents an existing installment plan from a PREVIOUS bill.
    // Its interest was already fixed when it was created, so no new interest applies here.
    static class PreviousInstallment {
        int planMonths;        // the plan's original term: 3 or 6
        double monthlyPayment; // fixed amount, does not change
        int monthsRemaining;   // months left to pay, INCLUDING the payment being made this month

        PreviousInstallment(int planMonths, double monthlyPayment, int monthsRemaining) {
            this.planMonths = planMonths;
            this.monthlyPayment = monthlyPayment;
            this.monthsRemaining = monthsRemaining;
        }

        boolean isLastPayment() {
            return monthsRemaining <= 1;
        }

        int monthsRemainingAfterThisPayment() {
            return Math.max(0, monthsRemaining - 1);
        }
    }

    // One row of the month-by-month breakdown
    static class MonthlyEntry {
        YearMonth month;
        double billPortion;      // this new bill's installment for that month (0 if already finished)
        double previousPortion;  // previous installment's payment for that month (0 if already finished)

        MonthlyEntry(YearMonth month, double billPortion, double previousPortion) {
            this.month = month;
            this.billPortion = billPortion;
            this.previousPortion = previousPortion;
        }

        double total() {
            return billPortion + previousPortion;
        }
    }

    // One row of the final side-by-side summary table
    static class SummaryRow {
        String planName;
        double interestRate;
        double interestCharged;
        double monthlyPayment;
        double totalDueThisMonth;
        double grandTotalDue;

        SummaryRow(String planName, double interestRate, double interestCharged,
                   double monthlyPayment, double totalDueThisMonth, double grandTotalDue) {
            this.planName = planName;
            this.interestRate = interestRate;
            this.interestCharged = interestCharged;
            this.monthlyPayment = monthlyPayment;
            this.totalDueThisMonth = totalDueThisMonth;
            this.grandTotalDue = grandTotalDue;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("========================================================");
        System.out.println("               ATOME BILL INTEREST CALCULATOR");
        System.out.println("========================================================");

        double billAmount = readPositiveDouble(scanner, "Enter this month's bill amount (Php): ");

        PreviousInstallment previous = readPreviousInstallment(scanner);

        PaymentPlan payFull = new PaymentPlan("Pay in Full", 1, 0.0, billAmount);
        PaymentPlan pay3Months = new PaymentPlan("Pay in 3 Months", 3, THREE_MONTH_RATE, billAmount);
        PaymentPlan pay6Months = new PaymentPlan("Pay in 6 Months", 6, SIX_MONTH_RATE, billAmount);

        PaymentPlan[] plans = { payFull, pay3Months, pay6Months };

        double previousMonthlyPayment = (previous != null) ? previous.monthlyPayment : 0.0;

        System.out.println();
        System.out.println("========================================================");
        System.out.println("Current Bill Amount:            Php " + MONEY.format(billAmount));

        if (previous != null) {
            System.out.println("--------------------------------------------------------");
            System.out.println("PREVIOUS INSTALLMENT PLAN (" + previous.planMonths + "-month plan):");
            System.out.println("  Monthly Payment:         Php " + MONEY.format(previous.monthlyPayment));
            System.out.println("  No new interest is added to this — it's a fixed, existing plan.");
            if (previous.isLastPayment()) {
                System.out.println("  This is the LAST month for this previous installment.");
                System.out.println("  Months remaining after this payment: 0 (fully paid off)");
            } else {
                System.out.println("  Months remaining (including this payment): " + previous.monthsRemaining);
                System.out.println("  Months remaining after this payment:       "
                        + previous.monthsRemainingAfterThisPayment());
            }
        } else {
            System.out.println("--------------------------------------------------------");
            System.out.println("No previous installment plan.");
        }

        System.out.println("--------------------------------------------------------");

        List<SummaryRow> summaryRows = new ArrayList<>();

        for (PaymentPlan plan : plans) {
            List<MonthlyEntry> breakdown = buildBreakdown(plan, previous);
            double totalDueThisMonth = breakdown.get(0).total();
            double grandTotalDue = breakdown.stream().mapToDouble(MonthlyEntry::total).sum();

            summaryRows.add(new SummaryRow(plan.name, plan.interestRate, plan.interest(),
                    plan.monthlyPayment(), totalDueThisMonth, grandTotalDue));

            System.out.println();
            System.out.println(plan.name + ":");
            System.out.println("  Interest Rate:          " + MONEY.format(plan.interestRate * 100) + "%");
            System.out.println("  Interest Charged:       Php " + MONEY.format(plan.interest()));
            System.out.println("  Total Payment (" + plan.months + " mo):   Php " + MONEY.format(plan.totalPayment()));
            System.out.println("  Monthly Payment:        Php " + MONEY.format(plan.monthlyPayment()));
            System.out.println("  + Previous Installment: Php " + MONEY.format(previousMonthlyPayment)
                    + "  (final payment, no added interest)");
            System.out.println("  ---------------------------------------------");
            System.out.println("  TOTAL DUE THIS MONTH:   Php " + MONEY.format(totalDueThisMonth));
            System.out.println();
            System.out.println("  Monthly Breakdown:");
            for (MonthlyEntry entry : breakdown) {
                String label = entry.month.format(MONTH_FORMAT);
                StringBuilder detail = new StringBuilder();
                if (entry.billPortion > 0) {
                    detail.append("This bill: Php ").append(MONEY.format(entry.billPortion));
                }
                if (entry.previousPortion > 0) {
                    if (detail.length() > 0) detail.append(" + ");
                    detail.append("Previous installment: Php ").append(MONEY.format(entry.previousPortion));
                }
                System.out.println("    " + padRight(label, 16) + " Php " + padLeft(MONEY.format(entry.total()), 12)
                        + "   (" + detail + ")");
            }
            System.out.println("  ---------------------------------------------");
            System.out.println("  TOTAL DUE (all months combined): Php " + MONEY.format(grandTotalDue));
        }

        System.out.println();
        printSummaryTable(summaryRows);

        System.out.println();
        System.out.println("========================================================");

        scanner.close();
    }

    // Prints all three plans side by side so the customer can compare at a glance
    private static void printSummaryTable(List<SummaryRow> rows) {
        String rowFormat = "%-18s %-10s %-16s %-16s %-18s %-18s%n";

        System.out.println("========================================================");
        System.out.println("               SUMMARY COMPARISON TABLE");
        System.out.println("========================================================");
        System.out.printf(rowFormat, "Plan", "Rate", "Interest", "Monthly Pay", "Due This Month", "Total Due (All)");
        System.out.println("--------------------------------------------------------------------------------------------");

        for (SummaryRow row : rows) {
            System.out.printf(rowFormat,
                    row.planName,
                    MONEY.format(row.interestRate * 100) + "%",
                    "Php " + MONEY.format(row.interestCharged),
                    "Php " + MONEY.format(row.monthlyPayment),
                    "Php " + MONEY.format(row.totalDueThisMonth),
                    "Php " + MONEY.format(row.grandTotalDue));
        }
    }

    // Builds the month-by-month payment schedule for a given bill plan, folding in
    // whatever's left of the previous installment for the months they overlap.
    private static List<MonthlyEntry> buildBreakdown(PaymentPlan plan, PreviousInstallment previous) {
        int previousMonths = (previous != null) ? previous.monthsRemaining : 0;
        int totalMonths = Math.max(plan.months, previousMonths);

        List<MonthlyEntry> entries = new ArrayList<>();
        YearMonth start = YearMonth.now();

        for (int i = 0; i < totalMonths; i++) {
            double billPortion = (i < plan.months) ? plan.monthlyPayment() : 0.0;
            double previousPortion = (i < previousMonths) ? previous.monthlyPayment : 0.0;
            entries.add(new MonthlyEntry(start.plusMonths(i), billPortion, previousPortion));
        }

        return entries;
    }

    private static String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }

    private static String padLeft(String text, int width) {
        return String.format("%" + width + "s", text);
    }

    // Asks whether the customer has a previous installment, and if so, collects its details.
    private static PreviousInstallment readPreviousInstallment(Scanner scanner) {
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
    private static int readPlanMonths(Scanner scanner, String prompt) {
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
    private static int readMonthsRemaining(Scanner scanner, int planMonths, String prompt) {
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
    private static double readPositiveDouble(Scanner scanner, String prompt) {
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