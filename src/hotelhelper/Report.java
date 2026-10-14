package hotelhelper;

/**
 * Builds daily and final reports from stored data. The final report
 * includes totals, averages, shortages, the busiest day and predefined
 * recommendations for recurring problems.
 *
 * @author Jaidev Gogineni
 * @version October 2026
 */
public class Report {

    /** A type is frequently short if it runs out on this share of days. */
    private static final double FREQUENT_SHORTAGE_SHARE = 0.25;

    /** An unserved share above this triggers a recommendation. */
    private static final double HIGH_UNSERVED_SHARE = 0.10;

    /** An average wait above this many days triggers a recommendation. */
    private static final double LONG_WAIT_DAYS = 1.0;

    /** An average occupancy below this share triggers a recommendation. */
    private static final double LOW_OCCUPANCY_SHARE = 0.50;

    private HotelData hotelData;
    private GuestData guestData;
    private DailyData dailyData;

    /**
     * Creates a report generator.
     *
     * @param hotelData the hotel configuration
     * @param guestData the guest records
     * @param dailyData the stored daily results
     * @throws IllegalArgumentException if any argument is null
     * @complexity O(1)
     */
    public Report(HotelData hotelData, GuestData guestData, DailyData dailyData) {
        if (hotelData == null || guestData == null || dailyData == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }

        this.hotelData = hotelData;
        this.guestData = guestData;
        this.dailyData = dailyData;
    }

    /**
     * Builds the report for one simulated day.
     * Throws IllegalArgumentException if that day hasn't been simulated.
     *
     * @param day the day number, starting at 1
     * @return the report text
     * @throws IllegalArgumentException if that day hasn't been simulated
     * @complexity O(D + T)
     */
    public String dailyReport(int day) {
        if (day < 1 || day > dailyData.getNumberOfDays()) {
            throw new IllegalArgumentException(
                    "Day " + day + " has not been simulated");
        }

        DailyRecord r = dailyData.getDay(day);
        String[] roomTypes = hotelData.getRoomTypes();
        int[] demand = r.getDemandByType();
        int[] shortage = r.getShortageByType();

        StringBuilder sb = new StringBuilder();
        sb.append("===== Day ").append(day).append(" Report =====\n");
        sb.append("Guests\n");
        sb.append("  New arrivals:        ").append(r.getTotalGuests())
                .append("\n");
        sb.append("  Served (checked in): ").append(r.getServedGuests())
                .append("\n");
        sb.append("  Left unserved:       ").append(r.getUnservedGuests())
                .append("\n");
        sb.append("  Still waiting:       ").append(r.getWaitingGuests())
                .append("\n");
        sb.append(String.format("  Average wait:        %.2f days%n",
                r.getAverageWaitingTime()));
        sb.append("Activity\n");
        sb.append("  Check-ins:  ").append(r.getCheckIns()).append("\n");
        sb.append("  Check-outs: ").append(r.getCheckOuts()).append("\n");
        sb.append("Rooms (end of day)\n");
        sb.append("  Occupied:  ").append(r.getOccupiedRooms()).append("\n");
        sb.append("  Available: ").append(r.getAvailableRooms()).append("\n");
        sb.append("  Cleaning:  ").append(r.getRoomsBeingCleaned())
                .append("\n");
        sb.append("Demand and shortages by room type\n");
        for (int i = 0; i < roomTypes.length; i++) {
            sb.append(String.format("  %-15s demand %3d   shortage %3d%n",
                    roomTypes[i], demand[i], shortage[i]));
        }

        return sb.toString();
    }

    /**
     * Builds the final report for every day simulated so far.
     * Must not divide by zero.
     *
     * @return the report text
     * @complexity O(D * T)
     */
    public String finalReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== Final Report ==========\n");

        int numDays = dailyData.getNumberOfDays();
        if (numDays == 0) {
            sb.append("No days have been simulated yet.\n");
            return sb.toString();
        }

        int totalArrivals = 0;
        int totalCheckIns = 0;
        int totalCheckOuts = 0;
        int totalUnserved = 0;
        long occupiedSum = 0;
        double waitSum = 0.0; // each day's average wait times its check-ins

        for (DailyRecord r : allDays()) {
            totalArrivals += r.getTotalGuests();
            totalCheckIns += r.getCheckIns();
            totalCheckOuts += r.getCheckOuts();
            totalUnserved += r.getUnservedGuests();
            occupiedSum += r.getOccupiedRooms();
            waitSum += r.getAverageWaitingTime() * r.getCheckIns();
        }

        int totalRooms = hotelData.getTotalRooms();
        double avgOccupied = (double) occupiedSum / numDays;
        double avgWait = totalCheckIns > 0 ? waitSum / totalCheckIns : 0.0;
        double unservedShare = totalArrivals > 0
                ? (double) totalUnserved / totalArrivals : 0.0;
        DailyRecord lastDay = dailyData.getDay(numDays);

        sb.append("Days simulated: ").append(numDays).append(" of ")
                .append(hotelData.getSimulationLength()).append("\n");
        sb.append("Guests\n");
        sb.append("  Total arrivals:   ").append(totalArrivals).append("\n");
        sb.append("  Checked in:       ").append(totalCheckIns).append("\n");
        sb.append("  Checked out:      ").append(totalCheckOuts).append("\n");
        sb.append(String.format("  Left unserved:    %d (%.1f%%)%n",
                totalUnserved, unservedShare * 100));
        sb.append("  Still waiting:    ").append(lastDay.getWaitingGuests())
                .append("\n");
        sb.append("  Guest records:    ").append(guestData.getTotalGuests())
                .append(" total, ").append(guestData.getUnservedCount())
                .append(" unserved\n");
        sb.append(String.format("  Average wait:     %.2f days%n", avgWait));
        sb.append(String.format("  Avg arrivals/day: %.2f%n",
                (double) totalArrivals / numDays));
        sb.append("Rooms\n");
        sb.append(String.format("  Average occupied: %.2f of %d (%.1f%%)%n",
                avgOccupied, totalRooms, avgOccupied / totalRooms * 100));

        sb.append("Shortages by room type\n");
        String[] roomTypes = hotelData.getRoomTypes();
        int[] shortages = totalShortageByType();
        for (int i = 0; i < roomTypes.length; i++) {
            sb.append(String.format("  %-15s %d%n", roomTypes[i],
                    shortages[i]));
        }

        int busiest = busiestDay();
        sb.append("Busiest day: Day ").append(busiest).append(" (")
                .append(dailyData.getDay(busiest).getTotalGuests())
                .append(" arrivals)\n");

        sb.append("Recommendations\n");
        sb.append(recommendations());
        return sb.toString();
    }

    /**
     * Adds up each room type's shortages over every simulated day.
     *
     * @return total shortages, in the same order as HotelData.roomTypes
     * @complexity O(D * T)
     */
    private int[] totalShortageByType() {
        int[] totals = new int[hotelData.getNumRoomTypes()];

        for (DailyRecord r : allDays()) {
            int[] shortage = r.getShortageByType();
            for (int t = 0; t < totals.length; t++) {
                totals[t] += shortage[t];
            }
        }

        return totals;
    }

    /**
     * Finds the day with the most arrivals. Ties go to the earlier day.
     *
     * @return the busiest day number, or -1 if no days were simulated
     * @complexity O(D)
     */
    private int busiestDay() {
        int best = -1;
        int bestCount = -1;

        for (DailyRecord r : allDays()) {
            if (r.getTotalGuests() > bestCount) {
                bestCount = r.getTotalGuests();
                best = r.getDay();
            }
        }

        return best;
    }

    /**
     * Builds predefined recommendations from recurring problems: room
     * types that run short on at least a quarter of the days, a high
     * unserved share, long average waits, and low occupancy.
     *
     * @return one recommendation per line
     * @complexity O(D * T)
     */
    private String recommendations() {
        StringBuilder sb = new StringBuilder();
        String[] roomTypes = hotelData.getRoomTypes();
        int numDays = dailyData.getNumberOfDays();
        if (numDays == 0) {
            return "  - No days have been simulated yet.\n";
        }

        int[] daysShort = new int[roomTypes.length];
        int totalArrivals = 0;
        int totalUnserved = 0;
        int totalCheckIns = 0;
        long occupiedSum = 0;
        double waitSum = 0.0;

        for (DailyRecord r : allDays()) {
            int[] shortage = r.getShortageByType();
            for (int t = 0; t < roomTypes.length; t++) {
                if (shortage[t] > 0) {
                    daysShort[t]++;
                }
            }
            totalArrivals += r.getTotalGuests();
            totalUnserved += r.getUnservedGuests();
            totalCheckIns += r.getCheckIns();
            occupiedSum += r.getOccupiedRooms();
            waitSum += r.getAverageWaitingTime() * r.getCheckIns();
        }

        for (int t = 0; t < roomTypes.length; t++) {
            if ((double) daysShort[t] / numDays >= FREQUENT_SHORTAGE_SHARE) {
                sb.append(String.format("  - Add more %s rooms: there was a "
                        + "shortage on %d of %d days.%n", roomTypes[t],
                        daysShort[t], numDays));
            }
        }

        if (totalArrivals > 0
                && (double) totalUnserved / totalArrivals
                > HIGH_UNSERVED_SHARE) {
            sb.append("  - Over 10% of guests left unserved; consider adding "
                    + "rooms overall.\n");
        }

        if (totalCheckIns > 0 && waitSum / totalCheckIns > LONG_WAIT_DAYS) {
            sb.append("  - Guests wait more than a day on average before "
                    + "getting a room; more capacity would cut wait times.\n");
        }

        if ((double) occupiedSum / numDays / hotelData.getTotalRooms()
                < LOW_OCCUPANCY_SHARE) {
            sb.append("  - Average occupancy is below 50%; the hotel may have "
                    + "more rooms than it needs.\n");
        }

        if (sb.length() == 0) {
            sb.append("  - No recurring problems found. The current room "
                    + "setup handles demand well.\n");
        }

        return sb.toString();
    }

    /**
     * Copies every stored day into an array so it can be walked in O(D)
     * instead of calling LinkedChain.get repeatedly.
     *
     * @return all daily records, day 1 first
     */
    private DailyRecord[] allDays() {
        LinkedChain<DailyRecord> days = dailyData.getDays();
        return days.toArray(new DailyRecord[days.size()]);
    }
}
