package testing;

import hotelhelper.*;

/**
 * Tests the DailyData class.
 *
 * @author Dallan Barnum
 * @version October 2026
 */
public class DailyDataTest
    extends student.TestCase
{

    private DailyData dailyData;

    /**
     * Creates a new DailyData before each test (3 days, 10 rooms)
     */
    public void setUp()
    {
        dailyData = new DailyData(3, 10);
    }


    /**
     * Builds a record whose room counts add up to 10.
     *
     * @param day the day number
     * @param occupied occupied rooms
     * @param available available rooms
     * @param cleaning rooms being cleaned
     * @return the record
     */
    private DailyRecord record(
        int day,
        int occupied,
        int available,
        int cleaning)
    {
        DailyRecord r = new DailyRecord(day, 2);
        r.setOccupiedRooms(occupied);
        r.setAvailableRooms(available);
        r.setRoomsBeingCleaned(cleaning);
        return r;
    }


    /**
     * Tests add days in order
     */
    public void testAddDaysInOrder()
    {
        assertEquals(0, dailyData.getNumberOfDays());
        assertEquals(0, dailyData.getDays().size());

        DailyRecord d1 = record(1, 4, 6, 0);
        DailyRecord d2 = record(2, 5, 3, 2);
        DailyRecord d3 = record(3, 0, 10, 0);
        dailyData.addDay(d1);
        assertEquals(1, dailyData.getNumberOfDays());
        dailyData.addDay(d2);
        dailyData.addDay(d3);

        assertEquals(3, dailyData.getNumberOfDays());
        assertSame(d1, dailyData.getDays().get(0));
        assertSame(d2, dailyData.getDays().get(1));
        assertSame(d3, dailyData.getDays().get(2));
    }


    /**
     * Tests rejects duplicate day
     */
    public void testRejectsDuplicateDay()
    {
        dailyData.addDay(record(1, 4, 6, 0));
        try
        {
            dailyData.addDay(record(1, 4, 6, 0));
            fail("Duplicate day should be rejected");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
        assertEquals(1, dailyData.getNumberOfDays());

        try
        {
            dailyData.addDay(null);
            fail("Null record should be rejected");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }


    /**
     * Tests rejects skipped day
     */
    public void testRejectsSkippedDay()
    {
        try
        {
            dailyData.addDay(record(2, 4, 6, 0));
            fail("Skipping day 1 should be rejected");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
        assertEquals(0, dailyData.getNumberOfDays());

        try
        {
            dailyData.addDay(record(0, 4, 6, 0));
            fail("Day 0 should be rejected");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }


    /**
     * Tests rejects too many days
     */
    public void testRejectsTooManyDays()
    {
        dailyData.addDay(record(1, 4, 6, 0));
        dailyData.addDay(record(2, 4, 6, 0));
        dailyData.addDay(record(3, 4, 6, 0));
        try
        {
            dailyData.addDay(record(4, 4, 6, 0));
            fail("A fourth day should be rejected");
        }
        catch (IllegalStateException e)
        {
            assertNotNull(e.getMessage());
        }
        assertEquals(3, dailyData.getNumberOfDays());
    }


    /**
     * Tests rejects room count mismatch
     */
    public void testRejectsRoomCountMismatch()
    {
        try
        {
            dailyData.addDay(record(1, 4, 5, 0));
            fail("Too few rooms accounted for");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }

        try
        {
            dailyData.addDay(record(1, 6, 5, 1));
            fail("Too many rooms accounted for");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
        assertEquals(0, dailyData.getNumberOfDays());

        // A bad constructor argument is rejected too
        try
        {
            new DailyData(0, 10);
            fail("Zero days should be rejected");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
        try
        {
            new DailyData(3, 0);
            fail("Zero rooms should be rejected");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }


    /**
     * Tests get day
     */
    public void testGetDay()
    {
        DailyRecord d1 = record(1, 4, 6, 0);
        DailyRecord d2 = record(2, 5, 3, 2);
        dailyData.addDay(d1);
        dailyData.addDay(d2);

        assertSame(d1, dailyData.getDay(1));
        assertSame(d2, dailyData.getDay(2));

        int[] badDays = { 0, -1, 3, 100 };
        for (int day : badDays)
        {
            try
            {
                dailyData.getDay(day);
                fail("Day " + day + " has not been simulated");
            }
            catch (IllegalArgumentException e)
            {
                assertNotNull(e.getMessage());
            }
        }
    }
}
