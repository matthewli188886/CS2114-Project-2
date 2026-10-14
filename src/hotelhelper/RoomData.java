
package hotelhelper;

/**
 * Stores all hotel rooms and provides methods to find rooms and check
 * room availability and status.
 *
 * @author Zichen Fu
 * @version October 2026
 */
public class RoomData {

    /** The linked chain containing all rooms in the hotel. */
    private LinkedChain<RoomRecord> rooms;

    /**
     * Builds one RoomRecord for each room defined in the hotel configuration.
     * Room numbers start at 1 and increase in creation order.
     *
     * @param hotelData the hotel configuration containing room types and counts
     * @throws IllegalArgumentException if hotelData is null
     * @complexity O(R + T), excluding the cost of adding each room
     */
    public RoomData(HotelData hotelData) {
        if (hotelData == null) {
            throw new IllegalArgumentException("HotelData must not be null");
        }

        rooms = new LinkedChain<RoomRecord>();

        String[] roomTypes = hotelData.getRoomTypes();
        int[] roomCounts = hotelData.getRoomCounts();

        int roomNumber = 1;

        for (int i = 0; i < roomTypes.length; i++) {
            for (int j = 0; j < roomCounts[i]; j++) {
                RoomRecord room = new RoomRecord(roomNumber, roomTypes[i]);
                rooms.addToEnd(room);
                roomNumber++;
            }
        }
    }

    /**
     * Finds the first available room of the specified type.
     *
     * @param roomType the requested room type
     * @return the first available matching room, or null if none exists
     * @complexity O(R^2) with the current LinkedChain implementation
     */
    public RoomRecord findAvailableRoom(String roomType) {
        if (roomType == null) {
            return null;
        }

        for (int i = 0; i < rooms.size(); i++) {
            RoomRecord room = rooms.get(i);

            if (room.getRoomType().equals(roomType)
                    && room.getStatus() == RoomStatus.AVAILABLE) {
                return room;
            }
        }

        return null;
    }

    /**
     * Finds a room by its unique room number.
     *
     * @param roomNumber the room number to search for
     * @return the matching room, or null if no room has that number
     * @complexity O(R^2) with the current LinkedChain implementation
     */
    public RoomRecord findRoom(int roomNumber) {
        for (int i = 0; i < rooms.size(); i++) {
            RoomRecord room = rooms.get(i);

            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }

        return null;
    }

    /**
     * Finds all occupied rooms whose scheduled stay ends on the specified day.
     * The returned chain contains references to the original RoomRecord objects.
     *
     * @param day the day on which the stays end
     * @return a new chain containing all matching occupied rooms
     * @complexity O(R^2) with the current LinkedChain implementation
     */
    public LinkedChain<RoomRecord> getRoomsEndingOn(int day) {
        LinkedChain<RoomRecord> endingRooms =
                new LinkedChain<RoomRecord>();

        for (int i = 0; i < rooms.size(); i++) {
            RoomRecord room = rooms.get(i);

            if (room.getStatus() == RoomStatus.OCCUPIED
                    && room.getStayEndDay() == day) {
                endingRooms.addToEnd(room);
            }
        }

        return endingRooms;
    }

    /**
     * Counts the rooms currently in the specified status.
     *
     * @param status the room status to count
     * @return the number of rooms in that status, or 0 if status is null
     * @complexity O(R^2) with the current LinkedChain implementation
     */
    public int countByStatus(RoomStatus status) {
        if (status == null) {
            return 0;
        }

        int count = 0;

        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getStatus() == status) {
                count++;
            }
        }

        return count;
    }

    /**
     * Counts the available rooms of the specified type.
     *
     * @param roomType the room type to count
     * @return the number of available rooms of that type,
     *         or 0 if roomType is null
     * @complexity O(R^2) with the current LinkedChain implementation
     */
    public int countAvailableByType(String roomType) {
        if (roomType == null) {
            return 0;
        }

        int count = 0;

        for (int i = 0; i < rooms.size(); i++) {
            RoomRecord room = rooms.get(i);

            if (room.getRoomType().equals(roomType)
                    && room.getStatus() == RoomStatus.AVAILABLE) {
                count++;
            }
        }

        return count;
    }

    /**
     * Gets the total number of rooms in the hotel.
     *
     * @return the total number of rooms
     * @complexity O(1)
     */
    public int getTotalRooms() {
        return rooms.size();
    }

    /**
     * Gets the linked chain containing all rooms in creation order.
     * The returned chain is the internal chain, so changes to it affect
     * the room collection maintained by this object.
     *
     * @return the linked chain of all rooms
     * @complexity O(1)
     */
    public LinkedChain<RoomRecord> getRooms() {
        return rooms;
    }
}
