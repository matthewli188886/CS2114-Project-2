package testing;

import hotelhelper.*;

public class GuestRecordTest
    extends student.TestCase
{

    private GuestRecord guest;

    /**
     * Creates a new GuestRecord before each test
     */
    public void setUp()
    {
        guest = new GuestRecord(7, "Suite", 2, 4, 3);
    }


    /**
     * Tests constructor initializes fields
     */
    public void testConstructorInitializesFields()
    {
        assertEquals(GuestStatus.WAITING, guest.getStatus());
        assertEquals(-1, guest.getCheckInDay());
        assertEquals(-1, guest.getCheckOutDay());
        assertEquals(-1, guest.getAssignedRoomNumber());
    }


    /**
     * Tests waiting to checked in
     */
    public void testWaitingToCheckedIn()
    {
        guest.checkIn(3, 101);

        assertEquals(GuestStatus.CHECKED_IN, guest.getStatus());
        assertEquals(3, guest.getCheckInDay());
        assertEquals(101, guest.getAssignedRoomNumber());
        assertEquals(-1, guest.getCheckOutDay());
    }


    /**
     * Tests checked in to checked out
     */
    public void testCheckedInToCheckedOut()
    {
        guest.checkIn(3, 101);
        guest.checkOut(6);

        assertEquals(GuestStatus.CHECKED_OUT, guest.getStatus());
        assertEquals(6, guest.getCheckOutDay());
        // check-in info is kept after checking out
        assertEquals(3, guest.getCheckInDay());
        assertEquals(101, guest.getAssignedRoomNumber());
    }


    /**
     * Tests waiting to left unserved
     */
    public void testWaitingToLeftUnserved()
    {
        guest.markUnserved();

        assertEquals(GuestStatus.LEFT_UNSERVED, guest.getStatus());
        assertEquals(-1, guest.getCheckInDay());
        assertEquals(-1, guest.getCheckOutDay());
        assertEquals(-1, guest.getAssignedRoomNumber());
    }


    /**
     * Tests invalid transitions throw illegal state exception
     */
    public void testInvalidTransitionsThrowIllegalStateException()
    {
        // WAITING: cannot check out
        Exception thrown = null;
        try
        {
            guest.checkOut(5);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(GuestStatus.WAITING, guest.getStatus());
        assertEquals(-1, guest.getCheckOutDay());

        // CHECKED_IN: cannot check in again or be marked unserved
        guest.checkIn(3, 101);
        thrown = null;
        try
        {
            guest.checkIn(4, 202);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(3, guest.getCheckInDay());
        assertEquals(101, guest.getAssignedRoomNumber());

        thrown = null;
        try
        {
            guest.markUnserved();
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(GuestStatus.CHECKED_IN, guest.getStatus());

        // CHECKED_OUT: cannot check out, check in, or be marked unserved
        guest.checkOut(6);
        thrown = null;
        try
        {
            guest.checkOut(7);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(6, guest.getCheckOutDay());

        thrown = null;
        try
        {
            guest.checkIn(7, 303);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);

        thrown = null;
        try
        {
            guest.markUnserved();
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(GuestStatus.CHECKED_OUT, guest.getStatus());

        // LEFT_UNSERVED: cannot check in or be marked unserved again
        GuestRecord unserved = new GuestRecord(8, "Single", 1, 1, 2);
        unserved.markUnserved();
        thrown = null;
        try
        {
            unserved.checkIn(2, 104);
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(-1, unserved.getAssignedRoomNumber());

        thrown = null;
        try
        {
            unserved.markUnserved();
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(GuestStatus.LEFT_UNSERVED, unserved.getStatus());
    }


    /**
     * Tests getters return correct values
     */
    public void testGettersReturnCorrectValues()
    {
        assertEquals(7, guest.getGuestId());
        assertEquals("Suite", guest.getRequestedRoomType());
        assertEquals(2, guest.getArrivalDay());
        assertEquals(4, guest.getArrivalOrder());
        assertEquals(3, guest.getLengthOfStay());
    }
}
