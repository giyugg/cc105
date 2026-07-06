package AtomeInterestCalculator;

// Represents an existing installment plan from a PREVIOUS bill.
// Its interest was already fixed when it was created, so no new interest applies here.
public class PreviousInstallment {
    int planMonths;        // the plan's original term: 3 or 6
    double monthlyPayment; // fixed amount, does not change
    int monthsRemaining;   // months left to pay, INCLUDING the payment being made this month

    public PreviousInstallment(int planMonths, double monthlyPayment, int monthsRemaining) {
        this.planMonths = planMonths;
        this.monthlyPayment = monthlyPayment;
        this.monthsRemaining = monthsRemaining;
    }

    public boolean isLastPayment() {
        return monthsRemaining <= 1;
    }

    public int monthsRemainingAfterThisPayment() {
        return Math.max(0, monthsRemaining - 1);
    }
}
