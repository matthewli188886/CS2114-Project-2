package hotelhelper;

import java.util.Random;
import java.util.Scanner;

/**
 * Entry point: reads the configuration, wires everything together, runs
 * the menu. Every value the user types is checked here, and bad input is
 * rejected with a message and asked for again instead of crashing.
 *
 * @author Jaidev Gogineni
 * @version October 2026
 */
public class HotelSimulator {

    private static final int MAX_DAYS = 3650;
    private static final int MAX_GUESTS_PER_DAY = 100000;
    private static final int MAX_WAIT_DAYS = 365;
    private static final int MAX_ROOM_TYPES = 20;
    private static final int MAX_ROOMS_PER_TYPE = 10000;
    private static final double PERCENT_TOLERANCE = 0.01;

    private HotelData hotelData;
    private GuestData guestData;
    private RoomData roomData;
    private DailyData dailyData;
    private Simulation simulation;
    private Report report;
    private Scanner input;

    /**
     * Creates a simulator that reads from standard input.
     */
    public HotelSimulator() {
        this(new Scanner(System.in));
    }

    /**
     * Creates a simulator that reads from the given Scanner, so tests can
     * feed it scripted input.
     *
     * @param input where the user's input is read from
     * @throws IllegalArgumentException if input is null
     */
    public HotelSimulator(Scanner input) {
        if (input == null) {
            throw new IllegalArgumentException("Input must not be null");
        }
        this.input = input;
    }

    /**
     * Creates a HotelSimulator and calls run().
     *
     * @param args not used
     */
    public static void main(String[] args) {
        new HotelSimulator().run();
    }

    /**
     * Reads the configuration, builds the data classes, and runs the menu
     * until the user quits or the input ends.
     */
    public void run() {
        System.out.println("=== Hotel Simulator ===");
        hotelData = readConfiguration();
        if (hotelData == null) {
            return; // input ended before the setup was finished
        }

        guestData = new GuestData();
        roomData = new RoomData(hotelData);
        dailyData = new DailyData(hotelData.getSimulationLength(),
                hotelData.getTotalRooms());
        simulation = new Simulation(hotelData, guestData, roomData,
                dailyData, new Random());
        report = new Report(hotelData, guestData, dailyData);

        boolean running = true;
        while (running) {
            showMenu();
            int choice = readInt("Choose an option: ", 1, 5);

            if (choice == -1 || choice == 5) {
                running = false;
            }
            else if (choice == 1) {
                runOneDay();
            }
            else if (choice == 2) {
                runRemainingDays();
            }
            else if (choice == 3) {
                viewDayReport();
            }
            else {
                if (simulation.hasMoreDays()) {
                    System.out.println("(The simulation isn't finished; "
                            + "showing results so far.)");
                }
                System.out.println(report.finalReport());
            }
        }

        System.out.println("Goodbye!");
    }

    /**
     * Gets the hotel configuration that was read, for testing.
     *
     * @return the configuration, or null if none has been read
     */
    public HotelData getHotelData() {
        return hotelData;
    }

    /**
     * Gets the day history, for testing.
     *
     * @return the day history, or null before the simulation is set up
     */
    public DailyData getDailyData() {
        return dailyData;
    }

    /**
     * Reads and checks the hotel configuration. Any part that breaks the
     * rules is asked for again.
     *
     * @return the configuration, or null if the input ended
     */
    private HotelData readConfiguration() {
        int simulationLength = readInt("Number of days to simulate: ",
                1, MAX_DAYS);
        if (simulationLength == -1) {
            return null;
        }

        int expectedGuests = readInt("Expected guests per day: ",
                0, MAX_GUESTS_PER_DAY);
        if (expectedGuests == -1) {
            return null;
        }

        int maxWaitDays = readInt("Max days a guest will wait: ",
                0, MAX_WAIT_DAYS);
        if (maxWaitDays == -1) {
            return null;
        }

        int numTypes = readInt("Number of room types: ", 1, MAX_ROOM_TYPES);
        if (numTypes == -1) {
            return null;
        }

        String[] roomTypes = new String[numTypes];
        int[] roomCounts = new int[numTypes];
        double[] demandPercents = new double[numTypes];

        // Room names and counts; start over if the hotel has no rooms.
        boolean roomsValid = false;
        while (!roomsValid) {
            int totalRooms = 0;
            for (int i = 0; i < numTypes; i++) {
                String name = readRoomTypeName(i, roomTypes);
                if (name == null) {
                    return null;
                }
                roomTypes[i] = name;

                roomCounts[i] = readInt("  Number of " + name + " rooms: ",
                        0, MAX_ROOMS_PER_TYPE);
                if (roomCounts[i] == -1) {
                    return null;
                }
                totalRooms += roomCounts[i];
            }

            if (totalRooms >= 1) {
                roomsValid = true;
            }
            else {
                System.out.println("The hotel needs at least 1 room. "
                        + "Please enter the room types again.");
                roomTypes = new String[numTypes];
            }
        }

        // Demand percentages; ask again until they add up to 100.
        boolean percentsValid = false;
        while (!percentsValid) {
            double total = 0.0;
            for (int i = 0; i < numTypes; i++) {
                demandPercents[i] = readDouble("  Demand % for "
                        + roomTypes[i] + ": ", 0.0, 100.0);
                if (Double.isNaN(demandPercents[i])) {
                    return null;
                }
                total += demandPercents[i];
            }

            if (Math.abs(total - 100.0) <= PERCENT_TOLERANCE) {
                percentsValid = true;
            }
            else {
                System.out.printf("The percentages add up to %.2f, not 100. "
                        + "Please enter them again.%n", total);
            }
        }

        return new HotelData(simulationLength, expectedGuests, maxWaitDays,
                roomTypes, roomCounts, demandPercents);
    }

    /**
     * Options: run next day, run all days, view a day's report, view the
     * final report, quit.
     */
    private void showMenu() {
        System.out.println();
        System.out.println("Day " + simulation.getCurrentDay() + " of "
                + hotelData.getSimulationLength() + " complete.");
        System.out.println("1. Run next day");
        System.out.println("2. Run all remaining days");
        System.out.println("3. View a day's report");
        System.out.println("4. View the final report");
        System.out.println("5. Quit");
    }

    /**
     * Reads a whole number from min to max, asking again until the input
     * is valid.
     *
     * @param prompt the text shown to the user
     * @param min the smallest allowed value
     * @param max the largest allowed value
     * @return the number, or -1 if the input ended
     */
    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            if (!input.hasNextLine()) {
                return -1;
            }

            String line = input.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number from " + min
                        + " to " + max + ".");
            }
            catch (NumberFormatException e) {
                System.out.println("That isn't a whole number.");
            }
        }
    }

    /**
     * Reads a decimal number from min to max, asking again until the input
     * is valid.
     *
     * @param prompt the text shown to the user
     * @param min the smallest allowed value
     * @param max the largest allowed value
     * @return the number, or NaN if the input ended
     */
    private double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            if (!input.hasNextLine()) {
                return Double.NaN;
            }

            String line = input.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (!Double.isNaN(value) && value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number from " + min
                        + " to " + max + ".");
            }
            catch (NumberFormatException e) {
                System.out.println("That isn't a number.");
            }
        }
    }

    /**
     * Reads a room type name that is not empty and not already used.
     * Names are compared ignoring case, the same way HotelData does.
     *
     * @param index the position of this room type
     * @param existing the names entered so far
     * @return the trimmed name, or null if the input ended
     */
    private String readRoomTypeName(int index, String[] existing) {
        while (true) {
            System.out.print("Name of room type " + (index + 1) + ": ");
            if (!input.hasNextLine()) {
                return null;
            }

            String name = input.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("The name can't be empty.");
                continue;
            }

            boolean duplicate = false;
            for (int j = 0; j < index; j++) {
                if (existing[j] != null && existing[j].equalsIgnoreCase(name)) {
                    duplicate = true;
                }
            }

            if (duplicate) {
                System.out.println("That room type was already entered.");
            }
            else {
                return name;
            }
        }
    }

    /**
     * Runs the next day and prints its report.
     */
    private void runOneDay() {
        if (!simulation.hasMoreDays()) {
            System.out.println("All days have already been simulated.");
            return;
        }

        DailyRecord r = simulation.runNextDay();
        System.out.println(report.dailyReport(r.getDay()));
    }

    /**
     * Runs every remaining day and prints the final report.
     */
    private void runRemainingDays() {
        if (!simulation.hasMoreDays()) {
            System.out.println("All days have already been simulated.");
            return;
        }

        simulation.runAll();
        System.out.println("Simulated through day "
                + simulation.getCurrentDay() + ".");
        System.out.println(report.finalReport());
    }

    /**
     * Asks for a day that has already been simulated and prints its
     * report.
     */
    private void viewDayReport() {
        int daysDone = dailyData.getNumberOfDays();
        if (daysDone == 0) {
            System.out.println("No days have been simulated yet.");
            return;
        }

        int day = readInt("Which day (1-" + daysDone + ")? ", 1, daysDone);
        if (day != -1) {
            System.out.println(report.dailyReport(day));
        }
    }
}
