package hotelhelper;

/**
 * Keeps one DailyRecord per simulated day, in order. Days must be added
 * exactly in sequence (1, 2, 3, ...), never past the simulation length,
 * and every day's room counts must account for every room in the hotel.
 *
 * @author Jaidev Gogineni
 * @version October 2026
 */
public class DailyData {

    /** One record per simulated day, day 1 first. */
    private LinkedChain<DailyRecord> days;

    /** The most days this history can hold. */
    private int simulationLength;

    /** The number of rooms in the hotel. */
    private int totalRooms;

    /**
     * Creates an empty day history.
     *
     * @param simulationLength the most days that can be stored; at least 1
     * @param totalRooms the number of rooms in the hotel; at least 1
     * @throws IllegalArgumentException if either value is less than 1
     * @complexity O(1)
     */
    public DailyData(int simulationLength, int totalRooms) {
        if (simulationLength < 1) {
            throw new IllegalArgumentException(
                    "Simulation length must be at least 1");
        }

        if (totalRooms < 1) {
            throw new IllegalArgumentException(
                    "Total rooms must be at least 1");
        }

        this.days = new LinkedChain<DailyRecord>();
        this.simulationLength = simulationLength;
        this.totalRooms = totalRooms;
    }

    /**
     * Adds the next day's record to the end of the history.
     *
     * Throws if the day isn't exactly getNumberOfDays() + 1, the chain
     * already holds simulationLength days, or
     * occupied + available + cleaning != totalRooms.
     *
     * @param record the record for the next day
     * @throws IllegalArgumentException if record is null, is not the next
     *         day, or its room counts do not add up to totalRooms
     * @throws IllegalStateException if every day is already stored
     * @complexity O(D) because LinkedChain.addToEnd walks the chain
     */
    public void addDay(DailyRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Record must not be null");
        }

        if (days.size() >= simulationLength) {
            throw new IllegalStateException(
                    "All " + simulationLength + " days are already stored");
        }

        if (record.getDay() != days.size() + 1) {
            throw new IllegalArgumentException(
                    "Expected day " + (days.size() + 1)
                    + " but got day " + record.getDay());
        }

        int roomTotal = record.getOccupiedRooms()
                + record.getAvailableRooms()
                + record.getRoomsBeingCleaned();

        if (roomTotal != totalRooms) {
            throw new IllegalArgumentException(
                    "Room counts add up to " + roomTotal
                    + " but the hotel has " + totalRooms + " rooms");
        }

        days.addToEnd(record);
    }

    /**
     * Gets the record for one day. Day numbers start at 1.
     *
     * @param day the day number
     * @return the record for that day
     * @throws IllegalArgumentException if that day has not been simulated
     * @complexity O(D)
     */
    public DailyRecord getDay(int day) {
        if (day < 1 || day > days.size()) {
            throw new IllegalArgumentException(
                    "Day " + day + " has not been simulated");
        }

        return days.get(day - 1);
    }

    /**
     * Gets how many days have been stored.
     *
     * @return the number of stored days
     * @complexity O(1)
     */
    public int getNumberOfDays() {
        return days.size();
    }

    /**
     * Gets the chain of all stored days, day 1 first.
     *
     * @return the internal chain of daily records
     * @complexity O(1)
     */
    public LinkedChain<DailyRecord> getDays() {
        return days;
    }
}
