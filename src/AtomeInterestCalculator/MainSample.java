package AtomeInterestCalculator;

import java.text.DecimalFormat;

public class MainSample {

    // Represents one payment plan option (e.g. "Pay in 3 months")
    static class PaymentPlan {
        String name;
        int months;
        double monthlyPayment;

        PaymentPlan(String name, int months, double monthlyPayment) {
            this.name = name;
            this.months = months;
            this.monthlyPayment = monthlyPayment;
        }

        double totalPayment() {
            return monthlyPayment * months;
        }

        double interest(double principal) {
            return totalPayment() - principal;
        }

        double interestRate(double principal) {
            return (interest(principal) / principal) * 100.0;
        }
    }

    public static void main(String[] args) {
        DecimalFormat money = new DecimalFormat("#,##0.00");

        // --- Current month's bill ---
        double billAmount = 33877.84;

        // --- Previous installment plan: last month's payment (no new interest, just due) ---
        double previousInstallmentLastPayment = 10194.63;

        // --- Available plans for the current bill ---
        PaymentPlan payFull = new PaymentPlan("Pay in Full", 1, billAmount);
        PaymentPlan pay3Months = new PaymentPlan("Pay in 3 Months", 3, 12647.73);
        PaymentPlan pay6Months = new PaymentPlan("Pay in 6 Months", 6, 7001.41);

        PaymentPlan[] plans = { payFull, pay3Months, pay6Months };

        System.out.println("========================================================");
        System.out.println("               ATOME BILL INTEREST CALCULATOR");
        System.out.println("========================================================");
        System.out.println("Current Bill Amount:            Php " + money.format(billAmount));
        System.out.println("Previous Installment (Last Pay):Php " + money.format(previousInstallmentLastPayment));
        System.out.println("--------------------------------------------------------");

        for (PaymentPlan plan : plans) {
            double interest = plan.interest(billAmount);
            double rate = plan.interestRate(billAmount);
            double totalDueThisMonth = plan.monthlyPayment + previousInstallmentLastPayment;

            System.out.println();
            System.out.println(plan.name + ":");
            System.out.println("  Monthly Payment:        Php " + money.format(plan.monthlyPayment));
            System.out.println("  Total Payment (" + plan.months + " mo):   Php " + money.format(plan.totalPayment()));

            if (interest > 0.005) {
                System.out.println("  Interest Charged:       Php " + money.format(interest)
                        + "  (" + money.format(rate) + "%)");
            } else {
                System.out.println("  Interest Charged:       Php 0.00  (0% - No interest)");
            }

            System.out.println("  + Previous Installment: Php " + money.format(previousInstallmentLastPayment)
                    + "  (final payment, no added interest)");
            System.out.println("  ---------------------------------------------");
            System.out.println("  TOTAL DUE THIS MONTH:   Php " + money.format(totalDueThisMonth));
        }

        System.out.println();
        System.out.println("========================================================");
    }
}