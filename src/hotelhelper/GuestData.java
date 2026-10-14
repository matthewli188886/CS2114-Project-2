package hotelhelper;

/** Stores every guest and the guests who left without service. */
public class GuestData {

    private LinkedChain<GuestRecord> guests;
    private LinkedChain<GuestRecord> unservedGuests;
    private int nextArrivalOrder;

    public GuestData() {
        // TODO
    }

    /** Hands out the next guestId and arrivalOrder. Throws IllegalArgumentException if lengthOfStay < 1. */
    public GuestRecord addGuest(String requestedRoomType, int arrivalDay, int lengthOfStay) {
        // TODO
        return null;
    }

    /** Returns null if no guest has that ID. O(G). */
    public GuestRecord findGuest(int guestId) {
        // TODO
        return null;
    }

    /** WAITING guests in arrival order. O(G). */
    public LinkedChain<GuestRecord> getWaitingGuests() {
        // TODO
        return null;
    }

    /** Sets the guest's status and adds them to unservedGuests. */
    public void markUnserved(GuestRecord guest) {
        // TODO
    }

    public LinkedChain<GuestRecord> getGuests() {
        // TODO
        return null;
    }

    public LinkedChain<GuestRecord> getUnservedGuests() {
        // TODO
        return null;
    }

    public int getTotalGuests() {
        // TODO
        return 0;
    }

    public int getUnservedCount() {
        // TODO
        return 0;
    }
}
