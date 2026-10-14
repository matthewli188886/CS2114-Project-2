package hotelhelper;

import java.util.Random;

/**
 * Runs the hotel simulation one day at a time.
 *
 * Each day runs these steps in order:
 * finishCleaning (rooms cleaned yesterday become available),
 * processCheckOuts (stays ending today end, and those rooms start
 * cleaning), generateArrivals (new guests arrive), processCheckIns
 * (waiting guests get rooms in arrival order), removeLongWaiters (guests
 * who waited too long leave), and recordEndOfDay (room and waiting counts
 * are recorded). The finished record is saved to DailyData.
 *
 * @author Jaidev Gogineni
 * @version October 2026
 */
public class Simulation {

    /** Daily guest counts vary by up to this share of the expected count. */
    private static final double GUEST_VARIATION = 0.2;

    /** The shortest possible stay, in days. */
    private static final int MIN_STAY = 1;

    /** The longest possible stay, in days. */
    private static final int MAX_STAY = 7;

    private HotelData hotelData;
    private GuestData guestData;
    private RoomData roomData;
    private DailyData dailyData;
    private int currentDay;
    private Random random;

    /**
     * Creates a simulation that has not run any days yet.
     * Random is a parameter so tests can pass in a seeded one.
     *
     * @param hotelData the hotel configuration
     * @param guestData where guests are stored
     * @param roomData where rooms are stored
     * @param dailyData where each day's results are saved
     * @param random the source of daily variation
     * @throws IllegalArgumentException if any argument is null
     * @complexity O(1)
     */
    public Simulation(HotelData hotelData, GuestData guestData, RoomData roomData,
            DailyData dailyData, Random random) {
        if (hotelData == null || guestData == null || roomData == null
                || dailyData == null || random == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }

        this.hotelData = hotelData;
        this.guestData = guestData;
        this.roomData = roomData;
        this.dailyData = dailyData;
        this.random = random;
        this.currentDay = 0;
    }

    /**
     * Checks whether any days are left to simulate.
     *
     * @return true if fewer than simulationLength days have run
     * @complexity O(1)
     */
    public boolean hasMoreDays() {
        return currentDay < hotelData.getSimulationLength();
    }

    /**
     * Runs one full day, saves the record to DailyData and returns it.
     * Calls the private helpers in order: finishCleaning, processCheckOuts,
     * generateArrivals, processCheckIns, removeLongWaiters, recordEndOfDay.
     * Throws IllegalStateException if every day has already been run.
     *
     * @return the finished record for the day
     * @throws IllegalStateException if every day has already been run
     * @complexity O(R * G)
     */
    public DailyRecord runNextDay() {
        if (!hasMoreDays()) {
            throw new IllegalStateException(
                    "All days have already been simulated");
        }

        currentDay++;
        DailyRecord r = new DailyRecord(currentDay,
                hotelData.getNumRoomTypes());

        finishCleaning(r);
        processCheckOuts(r);
        generateArrivals(r);
        processCheckIns(r);
        removeLongWaiters(r);
        recordEndOfDay(r);

        dailyData.addDay(r);
        return r;
    }

    /**
     * Runs every remaining day.
     *
     * @complexity O(D * R * G)
     */
    public void runAll() {
        while (hasMoreDays()) {
            runNextDay();
        }
    }

    /**
     * Gets the last day that was simulated.
     *
     * @return the current day, or 0 before the first day runs
     * @complexity O(1)
     */
    public int getCurrentDay() {
        return currentDay;
    }

    /**
     * Rooms that were cleaned yesterday become available again.
     *
     * @param r today's record
     * @complexity O(R)
     */
    private void finishCleaning(DailyRecord r) {
        for (RoomRecord room : allRooms()) {
            if (room.getStatus() == RoomStatus.CLEANING) {
                room.makeAvailable();
            }
        }
    }

    /**
     * Guests whose stay ends today check out, and their rooms start
     * being cleaned. A cleaned room is free again tomorrow.
     *
     * @param r today's record
     * @complexity O(R * G)
     */
    private void processCheckOuts(DailyRecord r) {
        LinkedChain<RoomRecord> ending = roomData.getRoomsEndingOn(currentDay);
        RoomRecord[] endingRooms =
                ending.toArray(new RoomRecord[ending.size()]);

        for (RoomRecord room : endingRooms) {
            GuestRecord guest = guestData.findGuest(room.getCurrentGuestId());
            if (guest != null) {
                guest.checkOut(currentDay);
            }
            room.startCleaning();
        }

        r.setCheckOuts(endingRooms.length);
    }

    /**
     * Creates today's new guests with random room types and stay lengths,
     * and records the demand for each room type.
     *
     * @param r today's record
     * @complexity O(A * T)
     */
    private void generateArrivals(DailyRecord r) {
        int count = generateGuestCount();

        for (int i = 0; i < count; i++) {
            String roomType = pickRoomType();
            guestData.addGuest(roomType, currentDay, generateLengthOfStay());
            r.incrementDemand(hotelData.getRoomTypeIndex(roomType));
        }

        r.setTotalGuests(count);
    }

    /**
     * Gives rooms to waiting guests in arrival order. A guest who cannot
     * get a room of their type today counts as one shortage for that type.
     * Also records how long today's checked-in guests waited.
     *
     * @param r today's record
     * @complexity O(G + W * R)
     */
    private void processCheckIns(DailyRecord r) {
        LinkedChain<GuestRecord> waiting = guestData.getWaitingGuests();
        GuestRecord[] waitingGuests =
                waiting.toArray(new GuestRecord[waiting.size()]);

        // Once a type runs out today, skip searching for it again.
        boolean[] typeFull = new boolean[hotelData.getNumRoomTypes()];
        int checkIns = 0;
        int totalWait = 0;

        for (GuestRecord guest : waitingGuests) {
            int typeIndex = hotelData.getRoomTypeIndex(
                    guest.getRequestedRoomType());

            RoomRecord room = null;
            if (typeIndex >= 0 && !typeFull[typeIndex]) {
                room = roomData.findAvailableRoom(guest.getRequestedRoomType());
            }

            if (room != null) {
                room.occupy(guest.getGuestId(),
                        currentDay + guest.getLengthOfStay());
                guest.checkIn(currentDay, room.getRoomNumber());
                checkIns++;
                totalWait += currentDay - guest.getArrivalDay();
            }
            else if (typeIndex >= 0) {
                typeFull[typeIndex] = true;
                r.incrementShortage(typeIndex);
            }
        }

        r.setCheckIns(checkIns);
        r.setServedGuests(checkIns);

        if (checkIns > 0) {
            r.setAverageWaitingTime((double) totalWait / checkIns);
        }
        else {
            r.setAverageWaitingTime(0.0);
        }
    }

    /**
     * Guests who have waited maxWaitDays or more without a room leave
     * unserved. With maxWaitDays of 0, a guest who gets no room on their
     * arrival day leaves that same day.
     *
     * @param r today's record
     * @complexity O(G) per guest who leaves, because GuestData checks
     *             that the guest belongs to it
     */
    private void removeLongWaiters(DailyRecord r) {
        LinkedChain<GuestRecord> waiting = guestData.getWaitingGuests();
        GuestRecord[] waitingGuests =
                waiting.toArray(new GuestRecord[waiting.size()]);
        int left = 0;

        for (GuestRecord guest : waitingGuests) {
            if (currentDay - guest.getArrivalDay()
                    >= hotelData.getMaxWaitDays()) {
                guestData.markUnserved(guest);
                left++;
            }
        }

        r.setUnservedGuests(left);
    }

    /**
     * Records the room counts and the number of waiting guests at the
     * end of the day.
     *
     * @param r today's record
     * @complexity O(R + G)
     */
    private void recordEndOfDay(DailyRecord r) {
        int occupied = 0;
        int available = 0;
        int cleaning = 0;

        for (RoomRecord room : allRooms()) {
            if (room.getStatus() == RoomStatus.OCCUPIED) {
                occupied++;
            }
            else if (room.getStatus() == RoomStatus.AVAILABLE) {
                available++;
            }
            else {
                cleaning++;
            }
        }

        r.setOccupiedRooms(occupied);
        r.setAvailableRooms(available);
        r.setRoomsBeingCleaned(cleaning);
        r.setWaitingGuests(guestData.getWaitingGuests().size());
    }

    /**
     * Picks today's guest count: the expected count plus or minus up to
     * 20 percent, never below 0.
     *
     * @return the number of guests arriving today
     * @complexity O(1)
     */
    private int generateGuestCount() {
        int expected = hotelData.getExpectedGuestsPerDay();
        if (expected == 0) {
            return 0;
        }

        int variation = Math.max(1,
                (int) Math.round(expected * GUEST_VARIATION));
        int count = expected - variation
                + random.nextInt(2 * variation + 1);

        return Math.max(0, count);
    }

    /**
     * Picks a room type using the demand percentages.
     *
     * @return the chosen room type name
     * @complexity O(T)
     */
    private String pickRoomType() {
        String[] roomTypes = hotelData.getRoomTypes();
        double[] percents = hotelData.getDemandPercents();
        double roll = random.nextDouble() * 100.0;
        double cumulative = 0.0;
        int lastWithDemand = 0;

        for (int i = 0; i < percents.length; i++) {
            if (percents[i] > 0) {
                lastWithDemand = i;
                cumulative += percents[i];
                if (roll < cumulative) {
                    return roomTypes[i];
                }
            }
        }

        // Only reached through rounding error in the percentages.
        return roomTypes[lastWithDemand];
    }

    /**
     * Picks a random stay length from MIN_STAY to MAX_STAY days.
     *
     * @return the stay length in days
     * @complexity O(1)
     */
    private int generateLengthOfStay() {
        return MIN_STAY + random.nextInt(MAX_STAY - MIN_STAY + 1);
    }

    /**
     * Copies every room into an array so it can be walked in O(R)
     * instead of calling LinkedChain.get repeatedly.
     *
     * @return all rooms in creation order
     */
    private RoomRecord[] allRooms() {
        LinkedChain<RoomRecord> rooms = roomData.getRooms();
        return rooms.toArray(new RoomRecord[rooms.size()]);
    }
}
