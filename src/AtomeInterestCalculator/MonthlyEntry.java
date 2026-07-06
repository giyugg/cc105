package AtomeInterestCalculator;

import java.time.YearMonth;

// One row of the month-by-month breakdown
public class MonthlyEntry {
    YearMonth month;
    double billPortion;      // this new bill's installment for that month (0 if already finished)
    double previousPortion;  // previous installment's payment for that month (0 if already finished)

    public MonthlyEntry(YearMonth month, double billPortion, double previousPortion) {
        this.month = month;
        this.billPortion = billPortion;
        this.previousPortion = previousPortion;
    }

    public double total() {
        return billPortion + previousPortion;
    }
}
