package hotelhelper;

/** One room. */
public class RoomRecord {

    private final int roomNumber;
    private final String roomType;
    private RoomStatus status;
    private int currentGuestId;   // -1 when empty
    private int stayEndDay;       // -1 when empty

    /** Starts as AVAILABLE; currentGuestId and stayEndDay are -1. */
    public RoomRecord(int roomNumber, String roomType) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.status = RoomStatus.AVAILABLE;
        this.currentGuestId = -1;
        this.stayEndDay = -1;
    }

    /** AVAILABLE -> OCCUPIED. Throws IllegalStateException otherwise. */
    public void occupy(int guestId, int stayEndDay) {
        if (status != RoomStatus.AVAILABLE) {
            throw new IllegalStateException(
                "Room " + roomNumber + " cannot be occupied: status is " + status);
        }
        this.currentGuestId = guestId;
        this.stayEndDay = stayEndDay;
        this.status = RoomStatus.OCCUPIED;
    }

    /** OCCUPIED -> CLEANING; clears the guest and stayEndDay. Throws IllegalStateException otherwise. */
    public void startCleaning() {
        if (status != RoomStatus.OCCUPIED) {
            throw new IllegalStateException(
                "Room " + roomNumber + " cannot start cleaning: status is " + status);
        }
        this.currentGuestId = -1;
        this.stayEndDay = -1;
        this.status = RoomStatus.CLEANING;
    }

    /** CLEANING -> AVAILABLE. Throws IllegalStateException otherwise. */
    public void makeAvailable() {
        if (status != RoomStatus.CLEANING) {
            throw new IllegalStateException(
                "Room " + roomNumber + " cannot become available: status is " + status);
        }
        this.status = RoomStatus.AVAILABLE;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public int getCurrentGuestId() {
        return currentGuestId;
    }

    public int getStayEndDay() {
        return stayEndDay;
    }
}
