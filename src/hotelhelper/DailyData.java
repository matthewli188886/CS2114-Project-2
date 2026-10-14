package hotelhelper;

/** Keeps one DailyRecord per simulated day, in order. */
public class DailyData {

    private LinkedChain<DailyRecord> days;

    public DailyData(int simulationLength, int totalRooms) {
        // TODO
    }

    /**
     * Throws if the day isn't exactly getNumberOfDays() + 1, the chain already holds
     * simulationLength days, or occupied + available + cleaning != totalRooms.
     */
    public void addDay(DailyRecord record) {
        // TODO
    }

    /** Day numbers start at 1. O(D). */
    public DailyRecord getDay(int day) {
        // TODO
        return null;
    }

    public int getNumberOfDays() {
        // TODO
        return 0;
    }

    public LinkedChain<DailyRecord> getDays() {
        // TODO
        return null;
    }
}
