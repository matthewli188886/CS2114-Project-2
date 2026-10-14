package testing;

import hotelhelper.*;

/**
 * Tests the DailyData class.
 *
 * @author Jaidev Gogineni
 * @version October 2026
 */
public class DailyDataTest
    extends student.TestCase
{

    private DailyData dailyData;

    /**
     * Creates a new DailyData before each test
     */
    public void setUp()
    {
        // 3 days, 4 rooms
        dailyData = new DailyData(3, 4);
    }


    /**
     * Builds a record for one day with the given end-of-day room counts.
     *
     * @param day the day number
     * @param occupied rooms occupied
     * @param available rooms available
     * @param cleaning rooms being cleaned
     * @return the record
     */
    private DailyRecord makeDay(int day, int occupied, int available,
        int cleaning)
    {
        DailyRecord record = new DailyRecord(day, 2);
        record.setOccupiedRooms(occupied);
        record.setAvailableRooms(available);
        record.setRoomsBeingCleaned(cleaning);
        return record;
    }


    /**
     * Tests the constructor starts empty and rejects bad arguments
     */
    public void testConstructor()
    {
        assertEquals(0, dailyData.getNumberOfDays());
        assertNotNull(dailyData.getDays());
        assertTrue(dailyData.getDays().isEmpty());

        Exception thrown = null;
        try
        {
            new DailyData(0, 4);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        thrown = null;
        try
        {
            new DailyData(3, 0);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Tests add days in order
     */
    public void testAddDaysInOrder()
    {
        DailyRecord day1 = makeDay(1, 4, 0, 0);
        DailyRecord day2 = makeDay(2, 2, 1, 1);
        DailyRecord day3 = makeDay(3, 0, 4, 0);

        dailyData.addDay(day1);
        assertEquals(1, dailyData.getNumberOfDays());
        dailyData.addDay(day2);
        assertEquals(2, dailyData.getNumberOfDays());
        dailyData.addDay(day3);
        assertEquals(3, dailyData.getNumberOfDays());

        // the chain keeps the days in the order they were added
        LinkedChain<DailyRecord> days = dailyData.getDays();
        assertEquals(3, days.size());
        assertSame(day1, days.get(0));
        assertSame(day2, days.get(1));
        assertSame(day3, days.get(2));
    }


    /**
     * Tests rejects duplicate day
     */
    public void testRejectsDuplicateDay()
    {
        dailyData.addDay(makeDay(1, 4, 0, 0));

        Exception thrown = null;
        try
        {
            dailyData.addDay(makeDay(1, 4, 0, 0));
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(1, dailyData.getNumberOfDays());
    }


    /**
     * Tests rejects skipped day
     */
    public void testRejectsSkippedDay()
    {
        // day 2 cannot come before day 1
        Exception thrown = null;
        try
        {
            dailyData.addDay(makeDay(2, 4, 0, 0));
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(0, dailyData.getNumberOfDays());

        // day 3 cannot follow day 1
        dailyData.addDay(makeDay(1, 4, 0, 0));
        thrown = null;
        try
        {
            dailyData.addDay(makeDay(3, 4, 0, 0));
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(1, dailyData.getNumberOfDays());
    }


    /**
     * Tests rejects too many days
     */
    public void testRejectsTooManyDays()
    {
        dailyData.addDay(makeDay(1, 4, 0, 0));
        dailyData.addDay(makeDay(2, 4, 0, 0));
        dailyData.addDay(makeDay(3, 4, 0, 0));

        Exception thrown = null;
        try
        {
            dailyData.addDay(makeDay(4, 4, 0, 0));
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(3, dailyData.getNumberOfDays());
    }


    /**
     * Tests rejects room count mismatch
     */
    public void testRejectsRoomCountMismatch()
    {
        // too few rooms accounted for
        Exception thrown = null;
        try
        {
            dailyData.addDay(makeDay(1, 1, 1, 1));
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        // too many rooms accounted for
        thrown = null;
        try
        {
            dailyData.addDay(makeDay(1, 2, 2, 1));
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        // null record
        thrown = null;
        try
        {
            dailyData.addDay(null);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
        assertEquals(0, dailyData.getNumberOfDays());

        // counts that add up to 4 are accepted
        dailyData.addDay(makeDay(1, 1, 2, 1));
        assertEquals(1, dailyData.getNumberOfDays());
    }


    /**
     * Tests get day
     */
    public void testGetDay()
    {
        DailyRecord day1 = makeDay(1, 4, 0, 0);
        DailyRecord day2 = makeDay(2, 3, 1, 0);
        dailyData.addDay(day1);
        dailyData.addDay(day2);

        // day numbers start at 1
        assertSame(day1, dailyData.getDay(1));
        assertSame(day2, dailyData.getDay(2));

        Exception thrown = null;
        try
        {
            dailyData.getDay(0);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);

        // day 3 has not been added yet
        thrown = null;
        try
        {
            dailyData.getDay(3);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalArgumentException);
    }
}
