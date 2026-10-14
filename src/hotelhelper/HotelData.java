package hotelhelper;

/**
 * Stores the hotel's fixed configuration for the simulation.
 * The three arrays are parallel: index i in each array describes
 * the same room type.
 *
 * @author Zichen Fu
 * @version October 2026
 */
public class HotelData {

    /** The number of days to simulate. */
    private int simulationLength;

    /** The expected number of guest requests per day. */
    private int expectedGuestsPerDay;

    /** The maximum number of days a guest may wait. */
    private int maxWaitDays;

    /** The configured room type names. */
    private String[] roomTypes;

    /** The number of rooms for each room type. */
    private int[] roomCounts;

    /** The demand percentage for each room type. */
    private double[] demandPercents;

    /**
     * Creates the hotel's fixed configuration and validates all inputs.
     *
     * @param simulationLength the number of simulation days; must be at least 1
     * @param expectedGuestsPerDay the expected number of guests per day;
     *        must be nonnegative
     * @param maxWaitDays the maximum guest waiting period; must be nonnegative
     * @param roomTypes the configured room type names
     * @param roomCounts the number of rooms for each type
     * @param demandPercents the demand percentage for each room type
     * @throws IllegalArgumentException if a configuration value is invalid
     * @complexity O(T^2), where T is the number of room types
     */
    public HotelData(int simulationLength, int expectedGuestsPerDay,
            int maxWaitDays, String[] roomTypes, int[] roomCounts,
            double[] demandPercents) {

        if (simulationLength < 1) {
            throw new IllegalArgumentException(
                    "Simulation length must be at least 1");
        }

        if (expectedGuestsPerDay < 0) {
            throw new IllegalArgumentException(
                    "Expected guests per day must be nonnegative");
        }

        if (maxWaitDays < 0) {
            throw new IllegalArgumentException(
                    "Maximum wait days must be nonnegative");
        }

        if (roomTypes == null || roomCounts == null
                || demandPercents == null) {
            throw new IllegalArgumentException(
                    "Room configuration arrays must not be null");
        }

        if (roomTypes.length != roomCounts.length
                || roomTypes.length != demandPercents.length) {
            throw new IllegalArgumentException(
                    "Room configuration arrays must have equal lengths");
        }

        if (roomTypes.length == 0) {
            throw new IllegalArgumentException(
                    "At least one room type must be configured");
        }

        double totalDemand = 0.0;
        int totalRooms = 0;

        for (int i = 0; i < roomTypes.length; i++) {
            if (roomTypes[i] == null
                    || roomTypes[i].trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Room type names must not be empty");
            }

            for (int j = 0; j < i; j++) {
                if (roomTypes[i].trim().equalsIgnoreCase(
                        roomTypes[j].trim())) {
                    throw new IllegalArgumentException(
                            "Room type names must be unique");
                }
            }

            if (roomCounts[i] < 0) {
                throw new IllegalArgumentException(
                        "Room counts must be nonnegative");
            }

            if (Double.isNaN(demandPercents[i])
                    || Double.isInfinite(demandPercents[i])
                    || demandPercents[i] < 0.0
                    || demandPercents[i] > 100.0) {
                throw new IllegalArgumentException(
                        "Demand percentages must be between 0 and 100");
            }

            totalRooms += roomCounts[i];
            totalDemand += demandPercents[i];
        }

        if (totalRooms < 1) {
            throw new IllegalArgumentException(
                    "The hotel must have at least one room");
        }

        if (Math.abs(totalDemand - 100.0) > 0.01) {
            throw new IllegalArgumentException(
                    "Demand percentages must total 100");
        }

        this.simulationLength = simulationLength;
        this.expectedGuestsPerDay = expectedGuestsPerDay;
        this.maxWaitDays = maxWaitDays;

        this.roomTypes = new String[roomTypes.length];
        for (int i = 0; i < roomTypes.length; i++) {
            this.roomTypes[i] = roomTypes[i].trim();
        }

        this.roomCounts = roomCounts.clone();
        this.demandPercents = demandPercents.clone();
    }

    /**
     * Finds the index of a configured room type.
     *
     * @param roomType the room type name to find
     * @return the index of the room type, or -1 if it does not exist
     * @complexity O(T)
     */
    public int getRoomTypeIndex(String roomType) {
        if (roomType == null) {
            return -1;
        }

        for (int i = 0; i < roomTypes.length; i++) {
            if (roomTypes[i].equalsIgnoreCase(roomType.trim())) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Gets the number of configured room types.
     *
     * @return the number of room types
     * @complexity O(1)
     */
    public int getNumRoomTypes() {
        return roomTypes.length;
    }

    /**
     * Calculates the total number of rooms across all room types.
     *
     * @return the total number of rooms
     * @complexity O(T)
     */
    public int getTotalRooms() {
        int total = 0;

        for (int count : roomCounts) {
            total += count;
        }

        return total;
    }

    /**
     * Gets the number of days in the simulation.
     *
     * @return the simulation length
     * @complexity O(1)
     */
    public int getSimulationLength() {
        return simulationLength;
    }

    /**
     * Gets the expected number of guests per day.
     *
     * @return the expected daily guest count
     * @complexity O(1)
     */
    public int getExpectedGuestsPerDay() {
        return expectedGuestsPerDay;
    }

    /**
     * Gets the maximum number of days a guest may wait.
     *
     * @return the maximum waiting period in days
     * @complexity O(1)
     */
    public int getMaxWaitDays() {
        return maxWaitDays;
    }

    /**
     * Gets a copy of the configured room type names.
     *
     * @return a copy of the room type array
     * @complexity O(T)
     */
    public String[] getRoomTypes() {
        return roomTypes.clone();
    }

    /**
     * Gets a copy of the configured room counts.
     *
     * @return a copy of the room count array
     * @complexity O(T)
     */
    public int[] getRoomCounts() {
        return roomCounts.clone();
    }

    /**
     * Gets a copy of the configured room demand percentages.
     *
     * @return a copy of the demand percentage array
     * @complexity O(T)
     */
    public double[] getDemandPercents() {
        return demandPercents.clone();
    }
}