
package testing;

import hotelhelper.*;

/**
 * Tests the RoomData class, including room creation, room lookup,
 * room availability, checkout-day lookup, and room counts.
 *
 * @author Zichen Fu
 * @version October 2026
 */
public class RoomDataTest extends student.TestCase {

    /** The RoomData instance used in each test. */
    private RoomData roomData;

    /**
     * Creates a new RoomData before each test.
     * The hotel has 2 Single rooms, 1 Double room, and 2 Suite rooms.
     */
    public void setUp() {
        String[] roomTypes = {"Single", "Double", "Suite"};
        int[] roomCounts = {2, 1, 2};
        double[] demandPercents = {40.0, 20.0, 40.0};

        HotelData hotelData = new HotelData(
                30, 10, 2,
                roomTypes, roomCounts, demandPercents);

        roomData = new RoomData(hotelData);
    }

    /**
     * Tests that the constructor creates the correct number and types
     * of rooms, with sequential room numbers and AVAILABLE status.
     */
    public void testCreatesCorrectNumberAndTypesOfRooms() {
        assertEquals(5, roomData.getTotalRooms());
        assertEquals(2, roomData.countAvailableByType("Single"));
        assertEquals(1, roomData.countAvailableByType("Double"));
        assertEquals(2, roomData.countAvailableByType("Suite"));

        LinkedChain<RoomRecord> rooms = roomData.getRooms();

        assertEquals(5, rooms.size());

        for (int i = 0; i < rooms.size(); i++) {
            RoomRecord room = rooms.get(i);
            assertEquals(i + 1, room.getRoomNumber());
            assertEquals(RoomStatus.AVAILABLE, room.getStatus());
            assertEquals(-1, room.getCurrentGuestId());
            assertEquals(-1, room.getStayEndDay());
        }

        assertEquals("Single", rooms.get(0).getRoomType());
        assertEquals("Single", rooms.get(1).getRoomType());
        assertEquals("Double", rooms.get(2).getRoomType());
        assertEquals("Suite", rooms.get(3).getRoomType());
        assertEquals("Suite", rooms.get(4).getRoomType());
    }

    /**
     * Tests finding rooms by number, including the first room,
     * last room, nonexistent numbers, and invalid numbers.
     */
    public void testFindRoomByNumber() {
        assertNotNull(roomData.findRoom(1));
        assertNotNull(roomData.findRoom(5));

        assertEquals(1, roomData.findRoom(1).getRoomNumber());
        assertEquals(5, roomData.findRoom(5).getRoomNumber());

        assertEquals("Single", roomData.findRoom(1).getRoomType());
        assertEquals("Suite", roomData.findRoom(5).getRoomType());

        assertNull(roomData.findRoom(0));
        assertNull(roomData.findRoom(-1));
        assertNull(roomData.findRoom(6));
        assertNull(roomData.findRoom(100));
    }

    /**
     * Tests finding available rooms by type, including missing types,
     * null input, and rooms that are occupied or being cleaned.
     */
    public void testFindAvailableRoomByType() {
        RoomRecord single = roomData.findAvailableRoom("Single");

        assertNotNull(single);
        assertEquals(1, single.getRoomNumber());

        assertNotNull(roomData.findAvailableRoom("Double"));
        assertNotNull(roomData.findAvailableRoom("Suite"));

        assertNull(roomData.findAvailableRoom("Penthouse"));
        assertNull(roomData.findAvailableRoom(""));
        assertNull(roomData.findAvailableRoom(null));

        single.occupy(101, 3);

        RoomRecord nextSingle = roomData.findAvailableRoom("Single");
        assertNotNull(nextSingle);
        assertEquals(2, nextSingle.getRoomNumber());

        nextSingle.occupy(102, 4);
        assertNull(roomData.findAvailableRoom("Single"));

        RoomRecord suite = roomData.findAvailableRoom("Suite");
        suite.occupy(103, 3);
        suite.startCleaning();

        assertEquals(RoomStatus.CLEANING, suite.getStatus());

        RoomRecord otherSuite = roomData.findAvailableRoom("Suite");
        assertNotNull(otherSuite);
        assertEquals(5, otherSuite.getRoomNumber());
    }

    /**
     * Tests finding occupied rooms whose stays end on a given day.
     * Verifies multiple matches, no matches, and exclusion of rooms
     * that are available or being cleaned.
     */
    public void testGetRoomsEndingOn() {
        RoomRecord room1 = roomData.findRoom(1);
        RoomRecord room2 = roomData.findRoom(2);
        RoomRecord room3 = roomData.findRoom(3);
        RoomRecord room4 = roomData.findRoom(4);

        room1.occupy(101, 2);
        room2.occupy(102, 2);
        room3.occupy(103, 3);
        room4.occupy(104, 2);
        room4.startCleaning();

        LinkedChain<RoomRecord> day2Rooms =
                roomData.getRoomsEndingOn(2);

        assertEquals(2, day2Rooms.size());
        assertTrue(day2Rooms.contains(room1));
        assertTrue(day2Rooms.contains(room2));
        assertFalse(day2Rooms.contains(room3));
        assertFalse(day2Rooms.contains(room4));

        LinkedChain<RoomRecord> day3Rooms =
                roomData.getRoomsEndingOn(3);

        assertEquals(1, day3Rooms.size());
        assertTrue(day3Rooms.contains(room3));

        assertEquals(0, roomData.getRoomsEndingOn(1).size());
        assertEquals(0, roomData.getRoomsEndingOn(4).size());
        assertEquals(0, roomData.getRoomsEndingOn(0).size());
        assertEquals(0, roomData.getRoomsEndingOn(-1).size());

        // Querying must not change the original room collection.
        assertEquals(5, roomData.getTotalRooms());
    }

    /**
     * Tests counting rooms by status, including all possible statuses,
     * null input, and changes caused by room state transitions.
     */
    public void testCountByStatus() {
        assertEquals(5, roomData.countByStatus(RoomStatus.AVAILABLE));
        assertEquals(0, roomData.countByStatus(RoomStatus.OCCUPIED));
        assertEquals(0, roomData.countByStatus(RoomStatus.CLEANING));
        assertEquals(0, roomData.countByStatus(null));

        RoomRecord room1 = roomData.findRoom(1);
        RoomRecord room2 = roomData.findRoom(2);

        room1.occupy(101, 3);
        room2.occupy(102, 4);

        assertEquals(3, roomData.countByStatus(RoomStatus.AVAILABLE));
        assertEquals(2, roomData.countByStatus(RoomStatus.OCCUPIED));
        assertEquals(0, roomData.countByStatus(RoomStatus.CLEANING));

        room1.startCleaning();

        assertEquals(3, roomData.countByStatus(RoomStatus.AVAILABLE));
        assertEquals(1, roomData.countByStatus(RoomStatus.OCCUPIED));
        assertEquals(1, roomData.countByStatus(RoomStatus.CLEANING));

        room1.makeAvailable();

        assertEquals(4, roomData.countByStatus(RoomStatus.AVAILABLE));
        assertEquals(1, roomData.countByStatus(RoomStatus.OCCUPIED));
        assertEquals(0, roomData.countByStatus(RoomStatus.CLEANING));
    }

    /**
     * Tests counting available rooms by type, including missing types,
     * null input, and changes in room availability.
     */
    public void testCountAvailableByType() {
        assertEquals(2, roomData.countAvailableByType("Single"));
        assertEquals(1, roomData.countAvailableByType("Double"));
        assertEquals(2, roomData.countAvailableByType("Suite"));

        assertEquals(0, roomData.countAvailableByType("Penthouse"));
        assertEquals(0, roomData.countAvailableByType(""));
        assertEquals(0, roomData.countAvailableByType(null));

        RoomRecord single = roomData.findRoom(1);
        single.occupy(101, 3);

        assertEquals(1, roomData.countAvailableByType("Single"));

        single.startCleaning();

        assertEquals(1, roomData.countAvailableByType("Single"));

        single.makeAvailable();

        assertEquals(2, roomData.countAvailableByType("Single"));
    }

    /**
     * Tests that getTotalRooms returns the current number of rooms.
     */
    public void testGetTotalRooms() {
        assertEquals(5, roomData.getTotalRooms());

        roomData.getRooms().remove();

        assertEquals(4, roomData.getTotalRooms());
    }

    /**
     * Tests that getRooms returns the chain containing the original
     * RoomRecord objects and that changes to the chain are reflected
     * in RoomData.
     */
    public void testGetRooms() {
        LinkedChain<RoomRecord> rooms = roomData.getRooms();

        assertNotNull(rooms);
        assertEquals(5, rooms.size());
        assertSame(roomData.findRoom(1), rooms.get(0));

        RoomRecord room = rooms.get(0);
        room.occupy(101, 3);

        assertEquals(RoomStatus.OCCUPIED,
                roomData.findRoom(room.getRoomNumber()).getStatus());

        rooms.remove(room);

        assertNull(roomData.findRoom(room.getRoomNumber()));
        assertEquals(4, roomData.getTotalRooms());
    }

    /**
     * Tests that the constructor rejects a null HotelData argument.
     */
    public void testConstructorRejectsNullHotelData() {
        try {
            new RoomData(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }
}
