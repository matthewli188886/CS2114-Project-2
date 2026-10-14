package hotel;

/** Results and statistics for one simulated day. */
public class DailyRecord {

    private int day;
    private int totalGuests;
    private int servedGuests;
    private int unservedGuests;
    private int checkIns;
    private int checkOuts;
    private int occupiedRooms;
    private int availableRooms;
    private int roomsBeingCleaned;
    private int waitingGuests;
    private double averageWaitingTime;
    private int[] demandByType;     // same order as HotelData.roomTypes
    private int[] shortageByType;   // same order as HotelData.roomTypes

    /** All counts start at 0; both arrays have size numRoomTypes. */
    public DailyRecord(int day, int numRoomTypes) {
        // TODO
    }

    public void incrementDemand(int typeIndex) {
        // TODO
    }

    public void incrementShortage(int typeIndex) {
        // TODO
    }

    public int getDay() {
        // TODO
        return 0;
    }

    public int getTotalGuests() {
        // TODO
        return 0;
    }

    public void setTotalGuests(int n) {
        // TODO
    }

    public int getServedGuests() {
        // TODO
        return 0;
    }

    public void setServedGuests(int n) {
        // TODO
    }

    public int getUnservedGuests() {
        // TODO
        return 0;
    }

    public void setUnservedGuests(int n) {
        // TODO
    }

    public int getCheckIns() {
        // TODO
        return 0;
    }

    public void setCheckIns(int n) {
        // TODO
    }

    public int getCheckOuts() {
        // TODO
        return 0;
    }

    public void setCheckOuts(int n) {
        // TODO
    }

    public int getOccupiedRooms() {
        // TODO
        return 0;
    }

    public void setOccupiedRooms(int n) {
        // TODO
    }

    public int getAvailableRooms() {
        // TODO
        return 0;
    }

    public void setAvailableRooms(int n) {
        // TODO
    }

    public int getRoomsBeingCleaned() {
        // TODO
        return 0;
    }

    public void setRoomsBeingCleaned(int n) {
        // TODO
    }

    public int getWaitingGuests() {
        // TODO
        return 0;
    }

    public void setWaitingGuests(int n) {
        // TODO
    }

    public double getAverageWaitingTime() {
        // TODO
        return 0.0;
    }

    public void setAverageWaitingTime(double t) {
        // TODO
    }

    public int[] getDemandByType() {
        // TODO
        return null;
    }

    public int[] getShortageByType() {
        // TODO
        return null;
    }
}
