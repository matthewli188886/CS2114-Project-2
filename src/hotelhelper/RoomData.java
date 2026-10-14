package hotelhelper;

/** Stores every room and answers availability questions. */
public class RoomData {

    private LinkedChain<RoomRecord> rooms;

    /** Builds one RoomRecord per room using roomTypes and roomCounts. O(R + T). */
    public RoomData(HotelData hotelData) {
        // TODO
    }

    /** Returns null if no room of that type is free. O(R). */
    public RoomRecord findAvailableRoom(String roomType) {
        // TODO
        return null;
    }

    /** Returns null if not found. O(R). */
    public RoomRecord findRoom(int roomNumber) {
        // TODO
        return null;
    }

    /** Occupied rooms whose stay ends that day. O(R). */
    public LinkedChain<RoomRecord> getRoomsEndingOn(int day) {
        // TODO
        return null;
    }

    public int countByStatus(RoomStatus status) {
        // TODO
        return 0;
    }

    public int countAvailableByType(String roomType) {
        // TODO
        return 0;
    }

    public int getTotalRooms() {
        // TODO
        return 0;
    }

    public LinkedChain<RoomRecord> getRooms() {
        // TODO
        return null;
    }
}
