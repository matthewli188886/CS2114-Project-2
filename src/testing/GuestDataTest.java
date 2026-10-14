
package testing;

import hotelhelper.*;

/**
 * Tests the GuestData class.
 *
 * @author Zichen Fu
 * @version October 2026
 */
public class GuestDataTest extends student.TestCase {

    /** The GuestData instance used in each test. */
    private GuestData guestData;

    /**
     * Creates a new GuestData before each test.
     */
    public void setUp() {
        guestData = new GuestData();
    }

    /**
     * Tests that guest IDs are unique and increase as guests are added.
     */
    public void testAssignsUniqueGuestIds() {
        GuestRecord guest1 = guestData.addGuest("Single", 1, 2);
        GuestRecord guest2 = guestData.addGuest("Double", 1, 3);
        GuestRecord guest3 = guestData.addGuest("Suite", 2, 1);

        assertEquals(1, guest1.getGuestId());
        assertEquals(2, guest2.getGuestId());
        assertEquals(3, guest3.getGuestId());

        assertTrue(guest1.getGuestId() != guest2.getGuestId());
        assertTrue(guest2.getGuestId() != guest3.getGuestId());
        assertTrue(guest1.getGuestId() != guest3.getGuestId());
    }

    /**
     * Tests that arrival orders increase as guests are added.
     */
    public void testAssignsIncreasingArrivalOrder() {
        GuestRecord guest1 = guestData.addGuest("Single", 1, 2);
        GuestRecord guest2 = guestData.addGuest("Double", 1, 3);
        GuestRecord guest3 = guestData.addGuest("Suite", 2, 1);

        assertEquals(1, guest1.getArrivalOrder());
        assertEquals(2, guest2.getArrivalOrder());
        assertEquals(3, guest3.getArrivalOrder());
    }

    /**
     * Tests that guests are stored in arrival order.
     */
    public void testStoresGuestsInArrivalOrder() {
        GuestRecord guest1 = guestData.addGuest("Single", 1, 2);
        GuestRecord guest2 = guestData.addGuest("Double", 1, 3);
        GuestRecord guest3 = guestData.addGuest("Suite", 2, 1);

        assertEquals(3, guestData.getGuests().size());
        assertSame(guest1, guestData.getGuests().get(0));
        assertSame(guest2, guestData.getGuests().get(1));
        assertSame(guest3, guestData.getGuests().get(2));

        assertEquals(0, guestData.getUnservedCount());
        assertEquals(0, guestData.getUnservedGuests().size());
    }

    /**
     * Tests finding guests by ID, including invalid IDs.
     */
    public void testFindGuest() {
        GuestRecord guest1 = guestData.addGuest("Single", 1, 2);
        GuestRecord guest2 = guestData.addGuest("Double", 2, 1);

        assertSame(guest1, guestData.findGuest(1));
        assertSame(guest2, guestData.findGuest(2));

        assertNull(guestData.findGuest(0));
        assertNull(guestData.findGuest(-1));
        assertNull(guestData.findGuest(3));
        assertNull(guestData.findGuest(100));
    }

    /**
     * Tests that getWaitingGuests returns only waiting guests
     * in their original arrival order.
     */
    public void testGetWaitingGuests() {
        GuestRecord guest1 = guestData.addGuest("Single", 1, 2);
        GuestRecord guest2 = guestData.addGuest("Double", 1, 3);
        GuestRecord guest3 = guestData.addGuest("Suite", 2, 1);

        LinkedChain<GuestRecord> waiting =
                guestData.getWaitingGuests();

        assertEquals(3, waiting.size());
        assertSame(guest1, waiting.get(0));
        assertSame(guest2, waiting.get(1));
        assertSame(guest3, waiting.get(2));

        // Mark one guest as unserved; they should no longer be waiting.
        guestData.markUnserved(guest2);

        waiting = guestData.getWaitingGuests();

        assertEquals(2, waiting.size());
        assertSame(guest1, waiting.get(0));
        assertSame(guest3, waiting.get(1));

        // The original guest collection should still contain all guests.
        assertEquals(3, guestData.getTotalGuests());
    }

    /**
     * Tests marking guests as unserved and updating the count.
     */
    public void testMarkUnserved() {
        GuestRecord guest1 = guestData.addGuest("Single", 1, 2);
        GuestRecord guest2 = guestData.addGuest("Double", 1, 1);

        assertEquals(0, guestData.getUnservedCount());

        guestData.markUnserved(guest1);

        assertEquals(1, guestData.getUnservedCount());
        assertEquals(1, guestData.getUnservedGuests().size());
        assertSame(guest1, guestData.getUnservedGuests().get(0));
        assertEquals(GuestStatus.UNSERVED, guest1.getStatus());

        guestData.markUnserved(guest2);

        assertEquals(2, guestData.getUnservedCount());
        assertSame(guest2, guestData.getUnservedGuests().get(1));
        assertEquals(GuestStatus.UNSERVED, guest2.getStatus());

        // Marking a guest unserved must not remove them from all guests.
        assertEquals(2, guestData.getTotalGuests());
        assertSame(guest1, guestData.findGuest(guest1.getGuestId()));
    }

    /**
     * Tests that invalid lengths of stay are rejected.
     */
    public void testRejectsInvalidLengthOfStay() {
        try {
            guestData.addGuest("Single", 1, 0);
            fail("A zero-length stay should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        try {
            guestData.addGuest("Single", 1, -1);
            fail("A negative length of stay should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // Failed additions must not create guests or consume IDs.
        assertEquals(0, guestData.getTotalGuests());

        GuestRecord guest = guestData.addGuest("Single", 1, 1);
        assertEquals(1, guest.getGuestId());
        assertEquals(1, guest.getArrivalOrder());
    }

    /**
     * Tests the total guest count as guests are added.
     */
    public void testGetTotalGuests() {
        assertEquals(0, guestData.getTotalGuests());

        guestData.addGuest("Single", 1, 1);
        assertEquals(1, guestData.getTotalGuests());

        guestData.addGuest("Double", 1, 2);
        assertEquals(2, guestData.getTotalGuests());

        guestData.addGuest("Suite", 2, 3);
        assertEquals(3, guestData.getTotalGuests());
    }

    /**
     * Tests the unserved guest count when no guests have been
     * marked unserved and after guests are marked unserved.
     */
    public void testGetUnservedCount() {
        assertEquals(0, guestData.getUnservedCount());

        GuestRecord guest1 = guestData.addGuest("Single", 1, 1);
        GuestRecord guest2 = guestData.addGuest("Double", 1, 2);

        assertEquals(0, guestData.getUnservedCount());

        guestData.markUnserved(guest1);
        assertEquals(1, guestData.getUnservedCount());

        guestData.markUnserved(guest2);
        assertEquals(2, guestData.getUnservedCount());
    }

    /**
     * Tests that null or unrelated guest records cannot be marked
     * as unserved.
     */
    public void testRejectsInvalidUnservedGuest() {
        try {
            guestData.markUnserved(null);
            fail("A null guest should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        GuestData anotherData = new GuestData();
        GuestRecord unrelatedGuest =
                anotherData.addGuest("Single", 1, 2);

        try {
            guestData.markUnserved(unrelatedGuest);
            fail("A guest from another GuestData should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        assertEquals(0, guestData.getTotalGuests());
        assertEquals(0, guestData.getUnservedCount());
    }
}
