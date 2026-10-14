package testing;

import hotelhelper.*;

/**
 * Tests the Report class using hand-built daily records so every number
 * in the reports is known in advance.
 *
 * @author Matthew Li
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
     * Creates a new Report before each test. The hotel has 3 Single and
     * 2 Double rooms and a 4 day simulation, of which 3 days are stored.
     */
    public void setUp()
    {
        hotelData = new HotelData(4, 10, 2, new String[] { "Single",
            "Double" }, new int[] { 3, 2 }, new double[] { 60.0, 40.0 });
        guestData = new GuestData();
        dailyData = new DailyData(4, 5);

        // Day 1: 4 arrivals, 3 served, 1 unserved
        DailyRecord d1 = new DailyRecord(1, 2);
        d1.setTotalGuests(4);
        d1.setServedGuests(3);
        d1.setUnservedGuests(1);
        d1.setCheckIns(3);
        d1.setOccupiedRooms(3);
        d1.setAvailableRooms(2);
        d1.setAverageWaitingTime(0.5);
        for (int i = 0; i < 3; i++)
        {
            d1.incrementDemand(0);
        }
        d1.incrementDemand(1);
        d1.incrementShortage(0);
        dailyData.addDay(d1);

        // Day 2: the busiest day
        DailyRecord d2 = new DailyRecord(2, 2);
        d2.setTotalGuests(10);
        d2.setServedGuests(5);
        d2.setUnservedGuests(2);
        d2.setCheckIns(5);
        d2.setCheckOuts(2);
        d2.setWaitingGuests(3);
        d2.setOccupiedRooms(5);
        d2.setAvailableRooms(0);
        d2.setAverageWaitingTime(1.0);
        d2.incrementShortage(0);
        d2.incrementShortage(1);
        dailyData.addDay(d2);

        // Day 3
        DailyRecord d3 = new DailyRecord(3, 2);
        d3.setTotalGuests(2);
        d3.setServedGuests(2);
        d3.setCheckIns(2);
        d3.setCheckOuts(3);
        d3.setWaitingGuests(1);
        d3.setOccupiedRooms(4);
        d3.setAvailableRooms(1);
        d3.setAverageWaitingTime(2.0);
        dailyData.addDay(d3);

        report = new Report(hotelData, guestData, dailyData);
    }


    /**
     * Tests daily report for completed day
     */
    public void testDailyReportForCompletedDay()
    {
        String text = report.dailyReport(1);
        assertTrue(text.contains("Day 1 Report"));
        assertTrue(text.contains("New arrivals:        4"));
        assertTrue(text.contains("Served (checked in): 3"));
        assertTrue(text.contains("Left unserved:       1"));
        assertTrue(text.contains("Still waiting:       0"));
        assertTrue(text.contains("0.50 days"));
        assertTrue(text.contains("Check-ins:  3"));
        assertTrue(text.contains("Occupied:  3"));
        assertTrue(text.contains("Available: 2"));
        assertTrue(text.contains("Cleaning:  0"));
        assertTrue(text.contains("Single"));
        assertTrue(text.contains("Double"));
        assertTrue(text.contains("demand   3   shortage   1"));
        assertTrue(text.contains("demand   1   shortage   0"));

        String second = report.dailyReport(2);
        assertTrue(second.contains("Day 2 Report"));
        assertTrue(second.contains("Check-outs: 2"));
        assertTrue(second.contains("Still waiting:       3"));
    }


    /**
     * Tests daily report for unsimulated day throws
     */
    public void testDailyReportForUnsimulatedDayThrows()
    {
        int[] badDays = { 0, -1, 4, 50 };
        for (int day : badDays)
        {
            try
            {
                report.dailyReport(day);
                fail("Day " + day + " has not been simulated");
            }
            catch (IllegalArgumentException e)
            {
                assertNotNull(e.getMessage());
            }
        }

        try
        {
            new Report(null, guestData, dailyData);
            fail("Null argument should be rejected");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
        try
        {
            new Report(hotelData, null, dailyData);
            fail("Null argument should be rejected");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
        try
        {
            new Report(hotelData, guestData, null);
            fail("Null argument should be rejected");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }


    /**
     * Tests final report after simulation
     */
    public void testFinalReportAfterSimulation()
    {
        String text = report.finalReport();
        assertTrue(text.contains("Final Report"));
        assertTrue(text.contains("Days simulated: 3 of 4"));
        assertTrue(text.contains("Shortages by room type"));
        assertTrue(text.contains("Busiest day: Day 2 (10 arrivals)"));
        assertTrue(text.contains("Recommendations"));

        // With no days simulated there is nothing to divide by
        Report empty = new Report(hotelData, guestData, new DailyData(4, 5));
        String emptyText = empty.finalReport();
        assertTrue(emptyText.contains("No days have been simulated yet."));
    }


    /**
     * Tests totals and averages match daily records
     */
    public void testTotalsAndAveragesMatchDailyRecords()
    {
        String text = report.finalReport();
        assertTrue(text.contains("Total arrivals:   16"));
        assertTrue(text.contains("Checked in:       10"));
        assertTrue(text.contains("Checked out:      5"));
        assertTrue(text.contains("Left unserved:    3 (18.8%)"));
        assertTrue(text.contains("Still waiting:    1"));
        // (0.5 * 3 + 1.0 * 5 + 2.0 * 2) / 10 check-ins
        assertTrue(text.contains("Average wait:     1.05 days"));
        assertTrue(text.contains("Avg arrivals/day: 5.33"));
        assertTrue(text.contains("Average occupied: 4.00 of 5 (80.0%)"));
    }


    /**
     * Tests identifies shortages unserved waiting and busy days
     */
    public void testIdentifiesShortagesUnservedWaitingAndBusyDays()
    {
        String text = report.finalReport();
        // Single ran short on 2 days, Double on 1
        assertTrue(text.contains("Single          2"));
        assertTrue(text.contains("Double          1"));
        assertTrue(text.contains("Busiest day: Day 2"));
        assertTrue(text.contains("Left unserved:    3"));
        assertTrue(text.contains("Still waiting:    1"));

        // Ties go to the earlier day
        DailyData tied = new DailyData(2, 5);
        for (int day = 1; day <= 2; day++)
        {
            DailyRecord r = new DailyRecord(day, 2);
            r.setTotalGuests(7);
            r.setOccupiedRooms(5);
            tied.addDay(r);
        }
        Report tiedReport = new Report(hotelData, guestData, tied);
        assertTrue(tiedReport.finalReport().contains("Busiest day: Day 1 (7"));
    }


    /**
     * Tests recommendations for recurring shortages
     */
    public void testRecommendationsForRecurringShortages()
    {
        String text = report.finalReport();
        assertTrue(text.contains("Add more Single rooms: "
            + "there was a shortage on 2 of 3 days"));
        assertTrue(text.contains("Add more Double rooms: "
            + "there was a shortage on 1 of 3 days"));
        assertTrue(text.contains("Over 10% of guests left unserved"));
        assertTrue(text.contains("wait more than a day"));
        assertFalse(text.contains("below 50%"));
        assertFalse(text.contains("No recurring problems found"));

        // A healthy hotel gets no complaints
        DailyData healthy = new DailyData(2, 5);
        for (int day = 1; day <= 2; day++)
        {
            DailyRecord r = new DailyRecord(day, 2);
            r.setTotalGuests(4);
            r.setServedGuests(4);
            r.setCheckIns(4);
            r.setOccupiedRooms(4);
            r.setAvailableRooms(1);
            healthy.addDay(r);
        }
        String healthyText =
            new Report(hotelData, guestData, healthy).finalReport();
        assertTrue(healthyText.contains("No recurring problems found"));
        assertFalse(healthyText.contains("Add more"));

        // An empty hotel is flagged as over-built
        DailyData quiet = new DailyData(2, 5);
        DailyRecord r = new DailyRecord(1, 2);
        r.setOccupiedRooms(1);
        r.setAvailableRooms(4);
        quiet.addDay(r);
        String quietText =
            new Report(hotelData, guestData, quiet).finalReport();
        assertTrue(quietText.contains("below 50%"));
    }
}
