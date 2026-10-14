package testing;

import hotelhelper.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Tests the HotelSimulator class by feeding it scripted input and checking
 * what it prints and the configuration it builds.
 *
 * @author Jaidev Gogineni
 * @version October 2026
 */
public class HotelSimulatorTest
    extends student.TestCase
{

    /**
     * A valid setup: 5 days, 3 guests a day, wait 1 day, 2 room types
     * (2 Single, 1 Suite), demand 70% / 30%.
     */
    private static final String VALID_CONFIG =
        "5\n3\n1\n2\nSingle\n2\nSuite\n1\n70\n30\n";

    private HotelSimulator simulator;

    /**
     * Creates a new HotelSimulator before each test
     */
    public void setUp()
    {
        simulator = new HotelSimulator(new Scanner(""));
    }


    /**
     * Runs a new simulator on the given input and returns everything it
     * printed.
     *
     * @param text the user's input, one answer per line
     * @return the program's output
     */
    private String runWith(String text)
    {
        PrintStream oldOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true));
        try
        {
            simulator = new HotelSimulator(new Scanner(text));
            simulator.run();
        }
        finally
        {
            System.setOut(oldOut);
        }
        return captured.toString();
    }


    /**
     * Tests the constructors
     */
    public void testConstructors()
    {
        assertNull(simulator.getHotelData());
        assertNull(simulator.getDailyData());
        assertNotNull(new HotelSimulator());

        Exception thrown = null;
        try
        {
            new HotelSimulator(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Tests main reads from standard input and stops when it ends
     */
    public void testMain()
    {
        InputStream oldIn = System.in;
        PrintStream oldOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try
        {
            System.setIn(new ByteArrayInputStream(new byte[0]));
            System.setOut(new PrintStream(captured, true));
            HotelSimulator.main(new String[0]);
        }
        finally
        {
            System.setIn(oldIn);
            System.setOut(oldOut);
        }
        assertTrue(captured.toString().contains("Hotel Simulator"));
    }


    /**
     * Tests valid configuration
     */
    public void testValidConfiguration()
    {
        String out = runWith(VALID_CONFIG + "5\n");

        HotelData data = simulator.getHotelData();
        assertNotNull(data);
        assertEquals(5, data.getSimulationLength());
        assertEquals(3, data.getExpectedGuestsPerDay());
        assertEquals(1, data.getMaxWaitDays());
        assertEquals(2, data.getNumRoomTypes());
        assertEquals("Single", data.getRoomTypes()[0]);
        assertEquals("Suite", data.getRoomTypes()[1]);
        assertEquals(2, data.getRoomCounts()[0]);
        assertEquals(1, data.getRoomCounts()[1]);
        assertEquals(70.0, data.getDemandPercents()[0], 0.0001);
        assertEquals(30.0, data.getDemandPercents()[1], 0.0001);
        assertEquals(3, data.getTotalRooms());

        // nothing has been simulated before quitting
        assertEquals(0, simulator.getDailyData().getNumberOfDays());
        assertTrue(out.contains("Day 0 of 5 complete."));
        assertTrue(out.contains("Goodbye!"));
    }


    /**
     * Tests non numeric input rejected
     */
    public void testNonNumericInputRejected()
    {
        String out = runWith("five\n5\n3\n1\n2\nSingle\n2\nSuite\n1\n"
            + "seventy\n70\n30\n" + "quit\n5\n");

        assertTrue(out.contains("That isn't a whole number."));
        assertTrue(out.contains("That isn't a number."));
        assertEquals(5, simulator.getHotelData().getSimulationLength());
        assertEquals(70.0, simulator.getHotelData().getDemandPercents()[0],
            0.0001);
        assertTrue(out.contains("Goodbye!"));
    }


    /**
     * Tests out of range input rejected
     */
    public void testOutOfRangeInputRejected()
    {
        // 0 days, -1 guests, 0 room types, 150% demand, menu option 9
        String out = runWith("0\n5\n-1\n3\n1\n0\n2\nSingle\n2\nSuite\n1\n"
            + "150\n70\n30\n" + "9\n5\n");

        assertTrue(out.contains("Please enter a number from 1 to 3650."));
        assertTrue(out.contains("Please enter a number from 0 to 100000."));
        assertTrue(out.contains("Please enter a number from 1 to 20."));
        assertTrue(out.contains("Please enter a number from 0.0 to 100.0."));
        assertTrue(out.contains("Please enter a number from 1 to 5."));

        HotelData data = simulator.getHotelData();
        assertEquals(5, data.getSimulationLength());
        assertEquals(3, data.getExpectedGuestsPerDay());
        assertEquals(2, data.getNumRoomTypes());
    }


    /**
     * Tests duplicate or empty room types rejected
     */
    public void testDuplicateOrEmptyRoomTypesRejected()
    {
        // empty name, then "single" repeating "Single"
        String out = runWith("5\n3\n1\n2\nSingle\n2\n\n  \nsingle\n"
            + "Suite\n1\n70\n30\n5\n");

        assertTrue(out.contains("The name can't be empty."));
        assertTrue(out.contains("That room type was already entered."));
        assertEquals("Suite", simulator.getHotelData().getRoomTypes()[1]);

        // a hotel with 0 rooms has to be entered again
        out = runWith("5\n3\n1\n2\nSingle\n0\nSuite\n0\n"
            + "Single\n2\nSuite\n1\n70\n30\n5\n");
        assertTrue(out.contains("The hotel needs at least 1 room."));
        assertEquals(3, simulator.getHotelData().getTotalRooms());
    }


    /**
     * Tests demand percents not totaling100 rejected
     */
    public void testDemandPercentsNotTotaling100Rejected()
    {
        String out = runWith("5\n3\n1\n2\nSingle\n2\nSuite\n1\n"
            + "50\n30\n" + "60\n60\n" + "70\n30\n" + "5\n");

        assertTrue(out.contains("The percentages add up to 80.00, not 100."));
        assertTrue(out.contains(
            "The percentages add up to 120.00, not 100."));
        double[] percents = simulator.getHotelData().getDemandPercents();
        assertEquals(70.0, percents[0], 0.0001);
        assertEquals(30.0, percents[1], 0.0001);
    }


    /**
     * Tests invalid input does not crash
     */
    public void testInvalidInputDoesNotCrash()
    {
        // input that stops partway through the setup, at every step
        String[] answers = VALID_CONFIG.split("\n");
        for (int count = 0; count < answers.length; count++)
        {
            StringBuilder partial = new StringBuilder();
            for (int i = 0; i < count; i++)
            {
                partial.append(answers[i]).append("\n");
            }
            String out = runWith(partial.toString());
            assertNull(simulator.getHotelData());
            assertFalse(out.contains("Goodbye!"));
        }

        // input that stops at the menu or while choosing a day
        String out = runWith(VALID_CONFIG);
        assertNotNull(simulator.getHotelData());
        assertTrue(out.contains("Goodbye!"));

        out = runWith(VALID_CONFIG + "1\n3\n");
        assertEquals(1, simulator.getDailyData().getNumberOfDays());
        assertTrue(out.contains("Goodbye!"));

        // junk everywhere is rejected without crashing
        out = runWith("!!\n-4\n5\n\n3\nx\n1\n2.5\n2\nSingle\n-2\n2\n"
            + "Suite\nabc\n1\n%\n70\n30\nhello\n5\n");
        assertNotNull(simulator.getHotelData());
        assertTrue(out.contains("Goodbye!"));
    }


    /**
     * Tests menu run one day run all reports and quit
     */
    public void testMenuRunOneDayRunAllReportsAndQuit()
    {
        String out = runWith(VALID_CONFIG
            + "3\n"     // view a day before any are run
            + "4\n"     // final report before any are run
            + "1\n"     // run day 1
            + "3\n1\n"  // view day 1
            + "4\n"     // final report part way through
            + "2\n"     // run the remaining days
            + "1\n"     // run next day when none are left
            + "2\n"     // run all when none are left
            + "3\n5\n"  // view day 5
            + "5\n");   // quit

        assertTrue(out.contains("No days have been simulated yet."));
        assertTrue(out.contains("Day 1 Report"));
        assertTrue(out.contains("The simulation isn't finished"));
        assertTrue(out.contains("Simulated through day 5."));
        assertTrue(out.contains("Final Report"));
        assertTrue(out.contains("Days simulated: 5 of 5"));
        assertTrue(out.contains("All days have already been simulated."));
        assertTrue(out.contains("Day 5 Report"));
        assertTrue(out.contains("Day 5 of 5 complete."));
        assertTrue(out.contains("Goodbye!"));

        assertEquals(5, simulator.getDailyData().getNumberOfDays());
    }
}
