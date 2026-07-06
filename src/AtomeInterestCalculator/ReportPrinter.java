package AtomeInterestCalculator;

import java.util.List;

import static AtomeInterestCalculator.Constants.MONEY;
import static AtomeInterestCalculator.Constants.MONTH_FORMAT;

// Handles all printed output: the previous installment status, per-plan breakdowns,
// and the final summary comparison table.
public class ReportPrinter {

    public static void printPreviousInstallmentStatus(PreviousInstallment previous) {
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
    }

    public static void printPlanDetails(PaymentPlan plan, List<MonthlyEntry> breakdown,
                                         double previousMonthlyPayment) {
        double totalDueThisMonth = breakdown.get(0).total();
        double grandTotalDue = breakdown.stream().mapToDouble(MonthlyEntry::total).sum();

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
            System.out.println("    " + BreakdownCalculator.padRight(label, 16) + " Php "
                    + BreakdownCalculator.padLeft(MONEY.format(entry.total()), 12)
                    + "   (" + detail + ")");
        }

        System.out.println("  ---------------------------------------------");
        System.out.println("  TOTAL DUE (all months combined): Php " + MONEY.format(grandTotalDue));
    }

    // Prints all three plans side by side so the customer can compare at a glance
    public static void printSummaryTable(List<SummaryRow> rows) {
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
}
