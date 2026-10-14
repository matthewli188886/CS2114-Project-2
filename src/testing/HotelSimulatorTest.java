package testing;

import hotelhelper.*;
import java.util.Scanner;

/**
 * Tests the HotelSimulator class using scripted input.
 *
 * @author Matthew Li
 * @version October 2026
 */
public class HotelSimulatorTest
    extends student.TestCase
{

    private HotelSimulator simulator;

    /** A valid configuration: 3 days, 0 guests, 1 Single, 100% demand. */
    private static final String CONFIG =
        "3\n0\n1\n1\nSingle\n2\n100\n";

    /**
     * Creates a new HotelSimulator before each test
     */
    public void setUp()
    {
        simulator = null;
    }


    /**
     * Runs a simulator on the given lines of input.
     *
     * @param script the text typed by the "user"
     * @return everything printed
     */
    private String run(String script)
    {
        simulator = new HotelSimulator(new Scanner(script));
        simulator.run();
        return systemOut().getHistory();
    }


    /**
     * Tests valid configuration
     */
    public void testValidConfiguration()
    {
        String out = run("5\n10\n2\n2\nSingle\n3\nDouble\n2\n60\n40\n5\n");

        HotelData data = simulator.getHotelData();
        assertNotNull(data);
        assertEquals(5, data.getSimulationLength());
        assertEquals(10, data.getExpectedGuestsPerDay());
        assertEquals(2, data.getMaxWaitDays());
        assertEquals(2, data.getNumRoomTypes());
        assertEquals(5, data.getTotalRooms());
        assertEquals("Single", data.getRoomTypes()[0]);
        assertEquals("Double", data.getRoomTypes()[1]);
        assertEquals(60.0, data.getDemandPercents()[0], 0.0001);

        assertNotNull(simulator.getDailyData());
        assertEquals(0, simulator.getDailyData().getNumberOfDays());
        assertTrue(out.contains("Goodbye!"));

        try
        {
            new HotelSimulator(null);
            fail("Null scanner should be rejected");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }


    /**
     * Tests non numeric input rejected
     */
    public void testNonNumericInputRejected()
    {
        String out = run("abc\n3\nx\n0\n1\n1\nSingle\n2\n100\n5\n");
        assertTrue(out.contains("That isn't a whole number."));
        assertEquals(3, simulator.getHotelData().getSimulationLength());
        assertEquals(2, simulator.getHotelData().getTotalRooms());

        out = run("3\n0\n1\n1\nSingle\n2\nlots\n100\n5\n");
        assertTrue(out.contains("That isn't a number."));
        assertEquals(100.0,
            simulator.getHotelData().getDemandPercents()[0], 0.0001);
    }


    /**
     * Tests out of range input rejected
     */
    public void testOutOfRangeInputRejected()
    {
        String out = run("0\n-4\n99999\n3\n0\n1\n1\nSingle\n2\n100\n5\n");
        assertTrue(out.contains("Please enter a number from 1 to 3650."));
        assertEquals(3, simulator.getHotelData().getSimulationLength());

        // Negative room counts and percentages above 100 are rejected too
        out = run("3\n0\n1\n1\nSingle\n-1\n2\n150\n100\n5\n");
        assertTrue(out.contains("Please enter a number from 0 to 10000."));
        assertTrue(out.contains("Please enter a number from 0.0 to 100.0."));
        assertEquals(2, simulator.getHotelData().getTotalRooms());

        // A hotel with no rooms starts the room types over
        out = run("3\n0\n1\n1\nSingle\n0\nSingle\n1\n100\n5\n");
        assertTrue(out.contains("The hotel needs at least 1 room."));
        assertEquals(1, simulator.getHotelData().getTotalRooms());
    }


    /**
     * Tests duplicate or empty room types rejected
     */
    public void testDuplicateOrEmptyRoomTypesRejected()
    {
        String out = run("3\n0\n1\n2\n\nSingle\n3\nsingle\nDouble\n2\n"
            + "50\n50\n5\n");
        assertTrue(out.contains("The name can't be empty."));
        assertTrue(out.contains("That room type was already entered."));
        HotelData data = simulator.getHotelData();
        assertEquals("Single", data.getRoomTypes()[0]);
        assertEquals("Double", data.getRoomTypes()[1]);
        assertEquals(5, data.getTotalRooms());
    }


    /**
     * Tests demand percents not totaling100 rejected
     */
    public void testDemandPercentsNotTotaling100Rejected()
    {
        String out = run("3\n0\n1\n2\nSingle\n3\nDouble\n2\n"
            + "50\n40\n60\n40\n5\n");
        assertTrue(out.contains("add up to 90.00, not 100"));
        HotelData data = simulator.getHotelData();
        assertEquals(60.0, data.getDemandPercents()[0], 0.0001);
        assertEquals(40.0, data.getDemandPercents()[1], 0.0001);
    }


    /**
     * Tests invalid input does not crash
     */
    public void testInvalidInputDoesNotCrash()
    {
        // Input ending at every stage of setup just stops the program
        String[] partial = { "", "3\n", "3\n0\n", "3\n0\n1\n",
            "3\n0\n1\n1\n", "3\n0\n1\n1\nSingle\n",
            "3\n0\n1\n1\nSingle\n2\n", "3\n0\n1\n1\nSingle\n2\n100" };
        for (int i = 0; i < partial.length - 1; i++)
        {
            run(partial[i]);
            assertNull(simulator.getHotelData());
            assertNull(simulator.getDailyData());
        }

        // Bad menu choices are rejected, and the program survives the
        // input ending without a Quit
        String out = run(CONFIG + "9\nabc\n0\n");
        assertTrue(out.contains("Please enter a number from 1 to 5."));
        assertTrue(out.contains("That isn't a whole number."));
        assertTrue(out.contains("Goodbye!"));
    }


    /**
     * Tests menu run one day run all reports and quit
     */
    public void testMenuRunOneDayRunAllReportsAndQuit()
    {
        // View a day before any has run
        String out = run(CONFIG + "3\n5\n");
        assertTrue(out.contains("No days have been simulated yet."));

        out = run(CONFIG + "1\n5\n");
        assertTrue(out.contains("Day 1 Report"));
        assertEquals(1, simulator.getDailyData().getNumberOfDays());

        // Run a day, view it, ask for the final report mid-simulation
        out = run(CONFIG + "1\n3\n1\n4\n5\n");
        assertTrue(out.contains("Which day (1-1)?"));
        assertTrue(out.contains("The simulation isn't finished"));
        assertTrue(out.contains("Final Report"));

        // Run everything, then try to run more
        out = run(CONFIG + "2\n1\n2\n4\n5\n");
        assertTrue(out.contains("Simulated through day 3."));
        assertTrue(out.contains("All days have already been simulated."));
        assertEquals(3, simulator.getDailyData().getNumberOfDays());
        assertTrue(out.contains("Goodbye!"));
    }
}
