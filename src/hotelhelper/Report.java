package hotelhelper;

/** Builds daily and final reports from stored data. */
public class Report {

    private HotelData hotelData;
    private GuestData guestData;
    private DailyData dailyData;

    public Report(HotelData hotelData, GuestData guestData, DailyData dailyData) {
        // TODO
    }

    /** Throws IllegalArgumentException if that day hasn't been simulated. */
    public String dailyReport(int day) {
        // TODO
        return null;
    }

    /** Must not divide by zero. */
    public String finalReport() {
        // TODO
        return null;
    }

    private int[] totalShortageByType() {
        // TODO
        return null;
    }

    private int busiestDay() {
        // TODO
        return 0;
    }

    private String recommendations() {
        // TODO
        return null;
    }
}
