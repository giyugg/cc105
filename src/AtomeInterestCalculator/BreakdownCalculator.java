package AtomeInterestCalculator;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

// Builds the month-by-month payment schedule for a given bill plan, folding in
// whatever's left of the previous installment for the months they overlap.
public class BreakdownCalculator {

    public static List<MonthlyEntry> buildBreakdown(PaymentPlan plan, PreviousInstallment previous) {
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

    public static String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }

    public static String padLeft(String text, int width) {
        return String.format("%" + width + "s", text);
    }
}
