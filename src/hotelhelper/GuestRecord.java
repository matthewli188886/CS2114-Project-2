package hotelhelper;

/** One guest. */
public class GuestRecord {

    private int guestId;
    private String requestedRoomType;
    private int arrivalDay;
    private int arrivalOrder;
    private int lengthOfStay;
    private int checkInDay;           // -1 until checked in
    private int checkOutDay;          // -1 until checked out
    private int assignedRoomNumber;   // -1 until a room is assigned
    private GuestStatus status;

    /** Starts as WAITING; checkInDay, checkOutDay and assignedRoomNumber are -1. */
    public GuestRecord(int guestId, String requestedRoomType, int arrivalDay,
            int arrivalOrder, int lengthOfStay) {
        // TODO
    }

    /** WAITING -> CHECKED_IN. Throws IllegalStateException otherwise. */
    public void checkIn(int day, int roomNumber) {
        // TODO
    }

    /** CHECKED_IN -> CHECKED_OUT. Throws IllegalStateException otherwise. */
    public void checkOut(int day) {
        // TODO
    }

    /** WAITING -> LEFT_UNSERVED. Throws IllegalStateException otherwise. */
    public void markUnserved() {
        // TODO
    }

    public int getGuestId() {
        // TODO
        return 0;
    }

    public String getRequestedRoomType() {
        // TODO
        return null;
    }

    public int getArrivalDay() {
        // TODO
        return 0;
    }

    public int getArrivalOrder() {
        // TODO
        return 0;
    }

    public int getLengthOfStay() {
        // TODO
        return 0;
    }

    public int getCheckInDay() {
        // TODO
        return 0;
    }

    public int getCheckOutDay() {
        // TODO
        return 0;
    }

    public int getAssignedRoomNumber() {
        // TODO
        return 0;
    }

    public GuestStatus getStatus() {
        // TODO
        return null;
    }
}
