
package hotelhelper;

/**
 * Stores every guest and the guests who left without service.
 *
 * @author Zichen Fu
 * @version October 2026
 */
public class GuestData {

    /** All guests in arrival order. */
    private LinkedChain<GuestRecord> guests;

    /** Guests who left without receiving service. */
    private LinkedChain<GuestRecord> unservedGuests;

    /** The arrival order assigned to the next guest. */
    private int nextArrivalOrder;

    /**
     * Creates empty guest collections and initializes guest numbering.
     */
    public GuestData() {
        guests = new LinkedChain<GuestRecord>();
        unservedGuests = new LinkedChain<GuestRecord>();
        nextArrivalOrder = 1;
    }

    /**
     * Creates a guest, assigns a unique ID and arrival order,
     * and adds the guest to the guest collection.
     *
     * @param requestedRoomType the room type requested by the guest
     * @param arrivalDay the day the guest arrives
     * @param lengthOfStay the requested stay length in days
     * @return the newly created guest record
     * @throws IllegalArgumentException if lengthOfStay is less than 1
     */
    public GuestRecord addGuest(
            String requestedRoomType, int arrivalDay, int lengthOfStay) {

        if (lengthOfStay < 1) {
            throw new IllegalArgumentException(
                    "Length of stay must be at least 1");
        }

        int guestId = guests.size() + 1;
        GuestRecord guest = new GuestRecord(
                guestId,
                requestedRoomType,
                arrivalDay,
                nextArrivalOrder,
                lengthOfStay);

        guests.addToEnd(guest);
        nextArrivalOrder++;

        return guest;
    }

    /**
     * Finds a guest by their unique ID.
     *
     * @param guestId the ID of the guest to find
     * @return the matching guest, or null if no guest has that ID
     */
    public GuestRecord findGuest(int guestId) {
        GuestRecord[] guestArray =
                guests.toArray(new GuestRecord[guests.size()]);

        for (GuestRecord guest : guestArray) {
            if (guest.getGuestId() == guestId) {
                return guest;
            }
        }

        return null;
    }

    /**
     * Gets all guests who are waiting for a room, in arrival order.
     *
     * @return a new linked chain containing the waiting guests
     */
    public LinkedChain<GuestRecord> getWaitingGuests() {
        LinkedChain<GuestRecord> waitingGuests =
                new LinkedChain<GuestRecord>();

        GuestRecord[] guestArray =
                guests.toArray(new GuestRecord[guests.size()]);

        for (GuestRecord guest : guestArray) {
            if (guest.getStatus() == GuestStatus.WAITING) {
                waitingGuests.addToEnd(guest);
            }
        }

        return waitingGuests;
    }

    /**
     * Marks a guest as unserved and records the guest in the
     * unserved guest collection.
     *
     * @param guest the guest who left without service
     * @throws IllegalArgumentException if the guest is null or is not
     *         managed by this GuestData instance
     * @throws IllegalStateException if the guest cannot transition
     *         to the unserved status
     */
    public void markUnserved(GuestRecord guest) {
        if (guest == null || !guests.contains(guest)) {
            throw new IllegalArgumentException(
                    "Guest must belong to this GuestData instance");
        }

        guest.markUnserved();
        unservedGuests.addToEnd(guest);
    }

    /**
     * Gets the linked chain containing all guests in arrival order.
     *
     * @return the internal chain of all guests
     */
    public LinkedChain<GuestRecord> getGuests() {
        return guests;
    }

    /**
     * Gets the linked chain containing all unserved guests.
     *
     * @return the internal chain of unserved guests
     */
    public LinkedChain<GuestRecord> getUnservedGuests() {
        return unservedGuests;
    }

    /**
     * Gets the total number of guests created.
     *
     * @return the total number of guests
     */
    public int getTotalGuests() {
        return guests.size();
    }

    /**
     * Gets the number of guests who left without service.
     *
     * @return the number of unserved guests
     */
    public int getUnservedCount() {
        return unservedGuests.size();
    }
}
