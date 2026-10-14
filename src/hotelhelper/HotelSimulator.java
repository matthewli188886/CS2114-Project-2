package hotelhelper;

import java.util.Scanner;

/** Entry point: reads the configuration, wires everything together, runs the menu. */
public class HotelSimulator {

    private HotelData hotelData;
    private GuestData guestData;
    private RoomData roomData;
    private DailyData dailyData;
    private Simulation simulation;
    private Report report;
    private Scanner input;

    public HotelSimulator() {
        // TODO
    }

    /** Creates a HotelSimulator and calls run(). */
    public static void main(String[] args) {
        // TODO
    }

    public void run() {
        // TODO
    }

    private HotelData readConfiguration() {
        // TODO
        return null;
    }

    /** Options: run next day, run all days, view a day's report, view the final report, quit. */
    private void showMenu() {
        // TODO
    }

    private int readInt(String prompt, int min, int max) {
        // TODO
        return 0;
    }

    private double readDouble(String prompt, double min, double max) {
        // TODO
        return 0.0;
    }
}
