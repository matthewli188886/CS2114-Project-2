package hotelhelper;

/** One guest. */
public class GuestRecord {

    private final int guestId;
    private final String requestedRoomType;
    private final int arrivalDay;
    private final int arrivalOrder;
    private final int lengthOfStay;
    private int checkInDay;           // -1 until checked in
    private int checkOutDay;          // -1 until checked out
    private int assignedRoomNumber;   // -1 until a room is assigned
    private GuestStatus status;

    /** Starts as WAITING; checkInDay, checkOutDay and assignedRoomNumber are -1. */
    public GuestRecord(int guestId, String requestedRoomType, int arrivalDay,
            int arrivalOrder, int lengthOfStay) {
        this.guestId = guestId;
        this.requestedRoomType = requestedRoomType;
        this.arrivalDay = arrivalDay;
        this.arrivalOrder = arrivalOrder;
        this.lengthOfStay = lengthOfStay;
        this.checkInDay = -1;
        this.checkOutDay = -1;
        this.assignedRoomNumber = -1;
        this.status = GuestStatus.WAITING;
    }

    /** WAITING -> CHECKED_IN. Throws IllegalStateException otherwise. */
    public void checkIn(int day, int roomNumber) {
        if (status != GuestStatus.WAITING) {
            throw new IllegalStateException(
                "Guest " + guestId + " cannot check in: status is " + status);
        }
        this.checkInDay = day;
        this.assignedRoomNumber = roomNumber;
        this.status = GuestStatus.CHECKED_IN;
    }

    /** CHECKED_IN -> CHECKED_OUT. Throws IllegalStateException otherwise. */
    public void checkOut(int day) {
        if (status != GuestStatus.CHECKED_IN) {
            throw new IllegalStateException(
                "Guest " + guestId + " cannot check out: status is " + status);
        }
        this.checkOutDay = day;
        this.status = GuestStatus.CHECKED_OUT;
    }

    /** WAITING -> LEFT_UNSERVED. Throws IllegalStateException otherwise. */
    public void markUnserved() {
        if (status != GuestStatus.WAITING) {
            throw new IllegalStateException(
                "Guest " + guestId + " cannot be marked unserved: status is " + status);
        }
        this.status = GuestStatus.LEFT_UNSERVED;
    }

    public int getGuestId() {
        return guestId;
    }

    public String getRequestedRoomType() {
        return requestedRoomType;
    }

    public int getArrivalDay() {
        return arrivalDay;
    }

    public int getArrivalOrder() {
        return arrivalOrder;
    }

    public int getLengthOfStay() {
        return lengthOfStay;
    }

    public int getCheckInDay() {
        return checkInDay;
    }

    public int getCheckOutDay() {
        return checkOutDay;
    }

    public int getAssignedRoomNumber() {
        return assignedRoomNumber;
    }

    public GuestStatus getStatus() {
        return status;
    }
}
