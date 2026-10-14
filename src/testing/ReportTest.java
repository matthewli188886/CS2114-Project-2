package testing;

import hotelhelper.*;
import java.util.Random;

/**
 * Tests the Report class. Most tests build the daily records by hand so the
 * expected totals and averages are known exactly.
 *
 * @author Jaidev Gogineni
 * @version October 2026
 */
public class ReportTest
    extends student.TestCase
{

    private Report report;
    private HotelData hotelData;
    private GuestData guestData;
    private DailyData dailyData;

    /**
     * Creates a new Report before each test
     */
    public void setUp()
    {
        // 4 days, 2 Single rooms and 1 Suite (3 rooms total)
        hotelData = new HotelData(4, 5, 2, new String[] { "Single", "Suite" },
            new int[] { 2, 1 }, new double[] { 60.0, 40.0 });
        guestData = new GuestData();
        dailyData = new DailyData(4, 3);
        report = new Report(hotelData, guestData, dailyData);
    }


    /**
     * Builds and stores the next day's record.
     *
     * @param arrivals guests who arrived
     * @param checkIns guests checked in (also the served count)
     * @param checkOuts guests checked out
     * @param unserved guests who left unserved
     * @param waiting guests still waiting at the end of the day
     * @param avgWait average wait of the day's check-ins
     * @param occupied rooms occupied at the end of the day
     * @param cleaning rooms being cleaned at the end of the day
     * @param shortages shortages for Single and Suite
     * @return the stored record
     */
    private DailyRecord addDay(int arrivals, int checkIns, int checkOuts,
        int unserved, int waiting, double avgWait, int occupied, int cleaning,
        int[] shortages)
    {
        DailyRecord r = new DailyRecord(dailyData.getNumberOfDays() + 1, 2);
        r.setTotalGuests(arrivals);
        r.setCheckIns(checkIns);
        r.setServedGuests(checkIns);
        r.setCheckOuts(checkOuts);
        r.setUnservedGuests(unserved);
        r.setWaitingGuests(waiting);
        r.setAverageWaitingTime(avgWait);
        r.setOccupiedRooms(occupied);
        r.setRoomsBeingCleaned(cleaning);
        r.setAvailableRooms(3 - occupied - cleaning);
        for (int t = 0; t < 2; t++)
        {
            for (int i = 0; i < shortages[t]; i++)
            {
                r.incrementShortage(t);
            }
        }
        dailyData.addDay(r);
        return r;
    }


    /**
     * Adds three days where Singles run short every day, many guests leave,
     * and waits are long. Day 2 and day 3 tie for the most arrivals.
     */
    private void addBusyDays()
    {
        addDay(4, 2, 0, 0, 2, 0.0, 2, 0, new int[] { 2, 0 });
        addDay(6, 1, 1, 3, 4, 3.0, 2, 1, new int[] { 3, 0 });
        addDay(6, 1, 0, 3, 3, 3.0, 3, 0, new int[] { 1, 0 });
    }


    /**
     * Tests the constructor rejects null arguments
     */
    public void testConstructorRejectsNull()
    {
        Object[][] badArgs = {
            { null, guestData, dailyData },
            { hotelData, null, dailyData },
            { hotelData, guestData, null } };

        for (Object[] args : badArgs)
        {
            Exception thrown = null;
            try
            {
                new Report((HotelData)args[0], (GuestData)args[1],
                    (DailyData)args[2]);
            }
            catch (Exception e)
            {
                thrown = e;
            }
            assertTrue(thrown instanceof IllegalArgumentException);
        }
    }


    /**
     * Tests daily report for completed day
     */
    public void testDailyReportForCompletedDay()
    {
        DailyRecord r = addDay(5, 2, 1, 1, 2, 0.5, 2, 1, new int[] { 3, 1 });
        r.incrementDemand(0);
        r.incrementDemand(0);
        r.incrementDemand(0);
        r.incrementDemand(1);
        r.incrementDemand(1);

        String text = report.dailyReport(1);
        assertTrue(text.contains("Day 1 Report"));
        assertTrue(text.contains("New arrivals:        5"));
        assertTrue(text.contains("Served (checked in): 2"));
        assertTrue(text.contains("Left unserved:       1"));
        assertTrue(text.contains("Still waiting:       2"));
        assertTrue(text.contains("Average wait:        0.50 days"));
        assertTrue(text.contains("Check-ins:  2"));
        assertTrue(text.contains("Check-outs: 1"));
        assertTrue(text.contains("Occupied:  2"));
        assertTrue(text.contains("Available: 0"));
        assertTrue(text.contains("Cleaning:  1"));
        assertTrue(text.contains(String.format(
            "  %-15s demand %3d   shortage %3d", "Single", 3, 3)));
        assertTrue(text.contains(String.format(
            "  %-15s demand %3d   shortage %3d", "Suite", 2, 1)));
    }


    /**
     * Tests daily report for unsimulated day throws
     */
    public void testDailyReportForUnsimulatedDayThrows()
    {
        // no days yet
        Exception thrown = null;
        try
        {
            report.dailyReport(1);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        addDay(1, 1, 0, 0, 0, 0.0, 1, 0, new int[] { 0, 0 });

        thrown = null;
        try
        {
            report.dailyReport(0);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try
        {
            report.dailyReport(2);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        assertTrue(report.dailyReport(1).contains("Day 1 Report"));
    }


    /**
     * Tests final report after simulation
     */
    public void testFinalReportAfterSimulation()
    {
        // before any days are run
        String empty = report.finalReport();
        assertTrue(empty.contains("Final Report"));
        assertTrue(empty.contains("No days have been simulated yet."));

        // after a real simulation runs every day
        Simulation simulation = new Simulation(hotelData, guestData,
            new RoomData(hotelData), dailyData, new Random(5));
        simulation.runAll();

        String text = report.finalReport();
        assertTrue(text.contains("Days simulated: 4 of 4"));
        assertTrue(text.contains("Total arrivals:   "
            + guestData.getTotalGuests()));
        assertTrue(text.contains("Guest records:    "
            + guestData.getTotalGuests() + " total, "
            + guestData.getUnservedCount() + " unserved"));
        assertTrue(text.contains("Shortages by room type"));
        assertTrue(text.contains("Busiest day: Day "));
        assertTrue(text.contains("Recommendations"));
        assertFalse(text.contains("No days have been simulated yet."));
    }


    /**
     * Tests totals and averages match daily records
     */
    public void testTotalsAndAveragesMatchDailyRecords()
    {
        addBusyDays();

        String text = report.finalReport();
        assertTrue(text.contains("Days simulated: 3 of 4"));
        assertTrue(text.contains("Total arrivals:   16"));
        assertTrue(text.contains("Checked in:       4"));
        assertTrue(text.contains("Checked out:      1"));
        // 6 of 16 guests left
        assertTrue(text.contains("Left unserved:    6 (37.5%)"));
        // (0*2 + 3*1 + 3*1) / 4 check-ins = 1.50 days
        assertTrue(text.contains("Average wait:     1.50 days"));
        // 16 arrivals over 3 days
        assertTrue(text.contains("Avg arrivals/day: 5.33"));
        // (2 + 2 + 3) / 3 days = 2.33 of 3 rooms
        assertTrue(text.contains("Average occupied: 2.33 of 3 (77.8%)"));
    }


    /**
     * Tests identifies shortages unserved waiting and busy days
     */
    public void testIdentifiesShortagesUnservedWaitingAndBusyDays()
    {
        addBusyDays();

        String text = report.finalReport();
        // shortages add up across the days for each type
        assertTrue(text.contains(String.format("  %-15s %d", "Single", 6)));
        assertTrue(text.contains(String.format("  %-15s %d", "Suite", 0)));
        // waiting count comes from the last day
        assertTrue(text.contains("Still waiting:    3"));
        assertTrue(text.contains("Left unserved:    6"));
        // days 2 and 3 tie at 6 arrivals; the earlier day wins
        assertTrue(text.contains("Busiest day: Day 2 (6 arrivals)"));
    }


    /**
     * Tests recommendations for recurring shortages
     */
    public void testRecommendationsForRecurringShortages()
    {
        addBusyDays();

        String text = report.finalReport();
        assertTrue(text.contains(
            "Add more Single rooms: there was a shortage on 3 of 3 days."));
        assertFalse(text.contains("Add more Suite rooms"));
        assertTrue(text.contains("Over 10% of guests left unserved"));
        assertTrue(text.contains("wait more than a day on average"));
        assertFalse(text.contains("below 50%"));
        assertFalse(text.contains("No recurring problems found"));
    }


    /**
     * Tests a hotel with no problems gets the all-clear message
     */
    public void testRecommendationsWhenNoProblems()
    {
        addDay(2, 2, 0, 0, 0, 0.0, 2, 0, new int[] { 0, 0 });
        addDay(3, 1, 1, 0, 0, 0.0, 2, 1, new int[] { 0, 0 });

        String text = report.finalReport();
        assertTrue(text.contains("No recurring problems found"));
        assertFalse(text.contains("Add more"));
        assertFalse(text.contains("Over 10%"));
        assertFalse(text.contains("below 50%"));
    }


    /**
     * Tests low occupancy and an occasional shortage
     */
    public void testRecommendationsForLowOccupancy()
    {
        // a Suite shortage on 1 of 4 days counts (25%); occupancy is low
        addDay(1, 1, 0, 0, 0, 0.0, 1, 0, new int[] { 0, 1 });
        addDay(0, 0, 0, 0, 0, 0.0, 0, 0, new int[] { 0, 0 });
        addDay(0, 0, 1, 0, 0, 0.0, 0, 1, new int[] { 0, 0 });
        addDay(0, 0, 0, 0, 0, 0.0, 0, 0, new int[] { 0, 0 });

        String text = report.finalReport();
        assertTrue(text.contains("below 50%"));
        assertTrue(text.contains(
            "Add more Suite rooms: there was a shortage on 1 of 4 days."));
        assertFalse(text.contains("Add more Single rooms"));
        assertTrue(text.contains("Average wait:     0.00 days"));
    }
}
