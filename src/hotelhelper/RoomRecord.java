package hotel;

/** One room. */
public class RoomRecord {

    private int roomNumber;
    private String roomType;
    private RoomStatus status;
    private int currentGuestId;   // -1 when empty
    private int stayEndDay;       // -1 when empty

    /** Starts as AVAILABLE; currentGuestId and stayEndDay are -1. */
    public RoomRecord(int roomNumber, String roomType) {
        // TODO
    }

    /** AVAILABLE -> OCCUPIED. Throws IllegalStateException otherwise. */
    public void occupy(int guestId, int stayEndDay) {
        // TODO
    }

    /** OCCUPIED -> CLEANING; clears the guest and stayEndDay. Throws IllegalStateException otherwise. */
    public void startCleaning() {
        // TODO
    }

    /** CLEANING -> AVAILABLE. Throws IllegalStateException otherwise. */
    public void makeAvailable() {
        // TODO
    }

    public int getRoomNumber() {
        // TODO
        return 0;
    }

    public String getRoomType() {
        // TODO
        return null;
    }

    public RoomStatus getStatus() {
        // TODO
        return null;
    }

    public int getCurrentGuestId() {
        // TODO
        return 0;
    }

    public int getStayEndDay() {
        // TODO
        return 0;
    }
}
