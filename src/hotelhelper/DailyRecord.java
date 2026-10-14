package hotelhelper;

/** Results and statistics for one simulated day. */
public class DailyRecord {

    private final int day;
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
    private final int[] demandByType;     // same order as HotelData.roomTypes
    private final int[] shortageByType;   // same order as HotelData.roomTypes

    /** All counts start at 0; both arrays have size numRoomTypes. */
    public DailyRecord(int day, int numRoomTypes) {
        this.day = day;
        this.demandByType = new int[numRoomTypes];
        this.shortageByType = new int[numRoomTypes];
    }

    /** Adds one to the demand for the room type at typeIndex. */
    public void incrementDemand(int typeIndex) {
        demandByType[typeIndex]++;
    }

    /** Adds one to the shortage for the room type at typeIndex. */
    public void incrementShortage(int typeIndex) {
        shortageByType[typeIndex]++;
    }

    public int getDay() {
        return day;
    }

    public int getTotalGuests() {
        return totalGuests;
    }

    public void setTotalGuests(int n) {
        totalGuests = n;
    }

    public int getServedGuests() {
        return servedGuests;
    }

    public void setServedGuests(int n) {
        servedGuests = n;
    }

    public int getUnservedGuests() {
        return unservedGuests;
    }

    public void setUnservedGuests(int n) {
        unservedGuests = n;
    }

    public int getCheckIns() {
        return checkIns;
    }

    public void setCheckIns(int n) {
        checkIns = n;
    }

    public int getCheckOuts() {
        return checkOuts;
    }

    public void setCheckOuts(int n) {
        checkOuts = n;
    }

    public int getOccupiedRooms() {
        return occupiedRooms;
    }

    public void setOccupiedRooms(int n) {
        occupiedRooms = n;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(int n) {
        availableRooms = n;
    }

    public int getRoomsBeingCleaned() {
        return roomsBeingCleaned;
    }

    public void setRoomsBeingCleaned(int n) {
        roomsBeingCleaned = n;
    }

    public int getWaitingGuests() {
        return waitingGuests;
    }

    public void setWaitingGuests(int n) {
        waitingGuests = n;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public void setAverageWaitingTime(double t) {
        averageWaitingTime = t;
    }

    public int[] getDemandByType() {
        return demandByType;
    }

    public int[] getShortageByType() {
        return shortageByType;
    }
}
