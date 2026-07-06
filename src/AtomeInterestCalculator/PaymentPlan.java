package AtomeInterestCalculator;

// Represents one payment plan option for the CURRENT bill (e.g. "Pay in 3 months")
public class PaymentPlan {
    String name;
    int months;
    double interestRate; // e.g. 0.12 for 12%
    double principal;

    public PaymentPlan(String name, int months, double interestRate, double principal) {
        this.name = name;
        this.months = months;
        this.interestRate = interestRate;
        this.principal = principal;
    }

    public double interest() {
        return principal * interestRate;
    }

    public double totalPayment() {
        return principal + interest();
    }

    public double monthlyPayment() {
        return totalPayment() / months;
    }
}
