package AtomeInterestCalculator;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

// Shared constants used across the calculator: default interest rates and formatters.
public class Constants {
    // Default flat interest rates set by Atome for each plan
    public static final double THREE_MONTH_RATE = 0.12; // 12%
    public static final double SIX_MONTH_RATE = 0.24;   // 24%

    public static final DecimalFormat MONEY = new DecimalFormat("#,##0.00");
    public static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MMMM yyyy");
}
