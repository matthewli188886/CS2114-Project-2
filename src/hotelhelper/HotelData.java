package hotel;

/**
 * The hotel's fixed setup. The three arrays are parallel: index i in each
 * describes the same room type.
 */
public class HotelData {

    private int simulationLength;
    private int expectedGuestsPerDay;
    private int maxWaitDays;
    private String[] roomTypes;
    private int[] roomCounts;
    private double[] demandPercents;

    /**
     * Throws IllegalArgumentException if any setup rule is broken (simulationLength >= 1,
     * guests/wait >= 0, equal array lengths, unique non-empty types, counts >= 0 with at
     * least 1 room, demand percents total 100).
     */
    public HotelData(int simulationLength, int expectedGuestsPerDay, int maxWaitDays,
            String[] roomTypes, int[] roomCounts, double[] demandPercents) {
        // TODO
    }

    /** Returns -1 if the type doesn't exist. O(T). */
    public int getRoomTypeIndex(String roomType) {
        // TODO
        return 0;
    }

    public int getNumRoomTypes() {
        // TODO
        return 0;
    }

    /** O(T). */
    public int getTotalRooms() {
        // TODO
        return 0;
    }

    public int getSimulationLength() {
        // TODO
        return 0;
    }

    public int getExpectedGuestsPerDay() {
        // TODO
        return 0;
    }

    public int getMaxWaitDays() {
        // TODO
        return 0;
    }

    public String[] getRoomTypes() {
        // TODO
        return null;
    }

    public int[] getRoomCounts() {
        // TODO
        return null;
    }

    public double[] getDemandPercents() {
        // TODO
        return null;
    }
}
