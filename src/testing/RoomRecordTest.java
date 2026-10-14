package testing;

import hotelhelper.*;

public class RoomRecordTest
    extends student.TestCase
{

    private RoomRecord room;

    /**
     * Creates a new RoomRecord before each test
     */
    public void setUp()
    {
        room = new RoomRecord(204, "Double");
    }


    /**
     * Tests constructor initializes fields
     */
    public void testConstructorInitializesFields()
    {
        assertEquals(RoomStatus.AVAILABLE, room.getStatus());
        assertEquals(-1, room.getCurrentGuestId());
        assertEquals(-1, room.getStayEndDay());
    }


    /**
     * Tests available to occupied
     */
    public void testAvailableToOccupied()
    {
        room.occupy(12, 9);

        assertEquals(RoomStatus.OCCUPIED, room.getStatus());
        assertEquals(12, room.getCurrentGuestId());
        assertEquals(9, room.getStayEndDay());
    }


    /**
     * Tests occupied to cleaning
     */
    public void testOccupiedToCleaning()
    {
        room.occupy(12, 9);
        room.startCleaning();

        assertEquals(RoomStatus.CLEANING, room.getStatus());
    }


    /**
     * Tests cleaning to available
     */
    public void testCleaningToAvailable()
    {
        room.occupy(12, 9);
        room.startCleaning();
        room.makeAvailable();

        assertEquals(RoomStatus.AVAILABLE, room.getStatus());
        assertEquals(-1, room.getCurrentGuestId());
        assertEquals(-1, room.getStayEndDay());

        // the room can be occupied again after cleaning
        room.occupy(13, 11);
        assertEquals(RoomStatus.OCCUPIED, room.getStatus());
        assertEquals(13, room.getCurrentGuestId());
        assertEquals(11, room.getStayEndDay());
    }


    /**
     * Tests invalid transitions throw illegal state exception
     */
    public void testInvalidTransitionsThrowIllegalStateException()
    {
        // AVAILABLE: cannot start cleaning or be made available
        Exception thrown = null;
        try
        {
            room.startCleaning();
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);

        thrown = null;
        try
        {
            room.makeAvailable();
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(RoomStatus.AVAILABLE, room.getStatus());

        // OCCUPIED: cannot be occupied again or be made available
        room.occupy(12, 9);
        thrown = null;
        try
        {
            room.occupy(13, 11);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(12, room.getCurrentGuestId());
        assertEquals(9, room.getStayEndDay());

        thrown = null;
        try
        {
            room.makeAvailable();
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(RoomStatus.OCCUPIED, room.getStatus());

        // CLEANING: cannot be occupied or start cleaning again
        room.startCleaning();
        thrown = null;
        try
        {
            room.occupy(13, 11);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(-1, room.getCurrentGuestId());

        thrown = null;
        try
        {
            room.startCleaning();
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(RoomStatus.CLEANING, room.getStatus());
    }


    /**
     * Tests start cleaning clears guest and stay end
     */
    public void testStartCleaningClearsGuestAndStayEnd()
    {
        room.occupy(12, 9);
        room.startCleaning();

        assertEquals(-1, room.getCurrentGuestId());
        assertEquals(-1, room.getStayEndDay());
    }


    /**
     * Tests getters return correct values
     */
    public void testGettersReturnCorrectValues()
    {
        assertEquals(204, room.getRoomNumber());
        assertEquals("Double", room.getRoomType());

        room.occupy(12, 9);
        assertEquals(204, room.getRoomNumber());
        assertEquals("Double", room.getRoomType());
        assertEquals(12, room.getCurrentGuestId());
        assertEquals(9, room.getStayEndDay());
    }
}
