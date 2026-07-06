package AtomeInterestCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static AtomeInterestCalculator.Constants.MONEY;
import static AtomeInterestCalculator.Constants.SIX_MONTH_RATE;
import static AtomeInterestCalculator.Constants.THREE_MONTH_RATE;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("========================================================");
        System.out.println("               ATOME BILL INTEREST CALCULATOR");
        System.out.println("========================================================");

        double billAmount = ConsoleInput.readPositiveDouble(scanner, "Enter this month's bill amount (Php): ");
        PreviousInstallment previous = ConsoleInput.readPreviousInstallment(scanner);

        PaymentPlan payFull = new PaymentPlan("Pay in Full", 1, 0.0, billAmount);
        PaymentPlan pay3Months = new PaymentPlan("Pay in 3 Months", 3, THREE_MONTH_RATE, billAmount);
        PaymentPlan pay6Months = new PaymentPlan("Pay in 6 Months", 6, SIX_MONTH_RATE, billAmount);

        PaymentPlan[] plans = { payFull, pay3Months, pay6Months };
        double previousMonthlyPayment = (previous != null) ? previous.monthlyPayment : 0.0;

        System.out.println();
        System.out.println("========================================================");
        System.out.println("Current Bill Amount:            Php " + MONEY.format(billAmount));

        ReportPrinter.printPreviousInstallmentStatus(previous);

        System.out.println("--------------------------------------------------------");

        List<SummaryRow> summaryRows = new ArrayList<>();

        for (PaymentPlan plan : plans) {
            List<MonthlyEntry> breakdown = BreakdownCalculator.buildBreakdown(plan, previous);
            double totalDueThisMonth = breakdown.get(0).total();
            double grandTotalDue = breakdown.stream().mapToDouble(MonthlyEntry::total).sum();

            summaryRows.add(new SummaryRow(plan.name, plan.interestRate, plan.interest(),
                    plan.monthlyPayment(), totalDueThisMonth, grandTotalDue));

            ReportPrinter.printPlanDetails(plan, breakdown, previousMonthlyPayment);
        }

        System.out.println();
        ReportPrinter.printSummaryTable(summaryRows);

        System.out.println();
        System.out.println("========================================================");

        scanner.close();
    }
}
