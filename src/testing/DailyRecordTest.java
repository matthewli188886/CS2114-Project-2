package testing;

import hotelhelper.*;

public class DailyRecordTest
    extends student.TestCase
{

    private DailyRecord record;

    /**
     * Creates a new DailyRecord before each test
     */
    public void setUp()
    {
        record = new DailyRecord(5, 3);
    }


    /**
     * Tests constructor initializes fields
     */
    public void testConstructorInitializesFields()
    {
        assertEquals(5, record.getDay());
        assertEquals(0, record.getTotalGuests());
        assertEquals(0, record.getServedGuests());
        assertEquals(0, record.getUnservedGuests());
        assertEquals(0, record.getCheckIns());
        assertEquals(0, record.getCheckOuts());
        assertEquals(0, record.getOccupiedRooms());
        assertEquals(0, record.getAvailableRooms());
        assertEquals(0, record.getRoomsBeingCleaned());
        assertEquals(0, record.getWaitingGuests());
        assertEquals(0.0, record.getAverageWaitingTime(), 0.0001);

        assertEquals(3, record.getDemandByType().length);
        assertEquals(3, record.getShortageByType().length);
        for (int i = 0; i < 3; i++)
        {
            assertEquals(0, record.getDemandByType()[i]);
            assertEquals(0, record.getShortageByType()[i]);
        }
    }


    /**
     * Tests increment demand
     */
    public void testIncrementDemand()
    {
        record.incrementDemand(0);
        record.incrementDemand(2);
        record.incrementDemand(2);

        int[] demand = record.getDemandByType();
        assertEquals(1, demand[0]);
        assertEquals(0, demand[1]);
        assertEquals(2, demand[2]);

        // demand and shortage are tracked separately
        int[] shortage = record.getShortageByType();
        assertEquals(0, shortage[0]);
        assertEquals(0, shortage[2]);
    }


    /**
     * Tests increment shortage
     */
    public void testIncrementShortage()
    {
        record.incrementShortage(1);
        record.incrementShortage(1);
        record.incrementShortage(1);

        int[] shortage = record.getShortageByType();
        assertEquals(0, shortage[0]);
        assertEquals(3, shortage[1]);
        assertEquals(0, shortage[2]);

        // demand and shortage are tracked separately
        assertEquals(0, record.getDemandByType()[1]);
    }


    /**
     * Tests setters and getters update values
     */
    public void testSettersAndGettersUpdateValues()
    {
        record.setTotalGuests(20);
        record.setServedGuests(15);
        record.setUnservedGuests(5);
        record.setCheckIns(12);
        record.setCheckOuts(8);
        record.setOccupiedRooms(30);
        record.setAvailableRooms(10);
        record.setRoomsBeingCleaned(4);
        record.setWaitingGuests(6);
        record.setAverageWaitingTime(1.75);

        assertEquals(20, record.getTotalGuests());
        assertEquals(15, record.getServedGuests());
        assertEquals(5, record.getUnservedGuests());
        assertEquals(12, record.getCheckIns());
        assertEquals(8, record.getCheckOuts());
        assertEquals(30, record.getOccupiedRooms());
        assertEquals(10, record.getAvailableRooms());
        assertEquals(4, record.getRoomsBeingCleaned());
        assertEquals(6, record.getWaitingGuests());
        assertEquals(1.75, record.getAverageWaitingTime(), 0.0001);

        // the day is fixed at construction
        assertEquals(5, record.getDay());
    }
}
