package hotelhelper;

import java.util.Random;

/** Runs the hotel simulation one day at a time. */
public class Simulation {

    private HotelData hotelData;
    private GuestData guestData;
    private RoomData roomData;
    private DailyData dailyData;
    private int currentDay;
    private Random random;

    /** Random is a parameter so tests can pass in a seeded one. */
    public Simulation(HotelData hotelData, GuestData guestData, RoomData roomData,
            DailyData dailyData, Random random) {
        // TODO
    }

    public boolean hasMoreDays() {
        // TODO
        return false;
    }

    /**
     * Runs one full day, saves the record to DailyData and returns it.
     * Calls the private helpers in order: finishCleaning, processCheckOuts,
     * generateArrivals, processCheckIns, removeLongWaiters, recordEndOfDay.
     * Throws IllegalStateException if every day has already been run.
     */
    public DailyRecord runNextDay() {
        // TODO
        return null;
    }

    public void runAll() {
        // TODO
    }

    public int getCurrentDay() {
        // TODO
        return 0;
    }

    private void finishCleaning(DailyRecord r) {
        // TODO
    }

    private void processCheckOuts(DailyRecord r) {
        // TODO
    }

    private void generateArrivals(DailyRecord r) {
        // TODO
    }

    private void processCheckIns(DailyRecord r) {
        // TODO
    }

    private void removeLongWaiters(DailyRecord r) {
        // TODO
    }

    private void recordEndOfDay(DailyRecord r) {
        // TODO
    }

    private int generateGuestCount() {
        // TODO
        return 0;
    }

    private String pickRoomType() {
        // TODO
        return null;
    }

    private int generateLengthOfStay() {
        // TODO
        return 0;
    }
}
