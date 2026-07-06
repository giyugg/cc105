package AtomeInterestCalculator;

// One row of the final side-by-side summary comparison table
public class SummaryRow {
    String planName;
    double interestRate;
    double interestCharged;
    double monthlyPayment;
    double totalDueThisMonth;
    double grandTotalDue;

    public SummaryRow(String planName, double interestRate, double interestCharged,
                       double monthlyPayment, double totalDueThisMonth, double grandTotalDue) {
        this.planName = planName;
        this.interestRate = interestRate;
        this.interestCharged = interestCharged;
        this.monthlyPayment = monthlyPayment;
        this.totalDueThisMonth = totalDueThisMonth;
        this.grandTotalDue = grandTotalDue;
    }
}
