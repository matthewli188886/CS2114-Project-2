
package testing;

import hotelhelper.*;

/**
 * Tests the HotelData class.
 *
 * @author Zichen Fu
 * @version October 2026
 */
public class HotelDataTest extends student.TestCase {

    /** The HotelData instance used in each test. */
    private HotelData hotelData;

    /**
     * Creates a new HotelData before each test.
     */
    public void setUp() {
        String[] types = {"Single", "Double", "Suite"};
        int[] counts = {2, 3, 1};
        double[] demands = {30.0, 50.0, 20.0};

        hotelData = new HotelData(
                30, 10, 2, types, counts, demands);
    }

    /**
     * Tests a valid configuration and all basic getters.
     */
    public void testValidConfiguration() {
        assertEquals(30, hotelData.getSimulationLength());
        assertEquals(10, hotelData.getExpectedGuestsPerDay());
        assertEquals(2, hotelData.getMaxWaitDays());

        assertEquals(3, hotelData.getNumRoomTypes());
        assertEquals(6, hotelData.getTotalRooms());

        String[] types = hotelData.getRoomTypes();
        assertEquals("Single", types[0]);
        assertEquals("Double", types[1]);
        assertEquals("Suite", types[2]);

        int[] counts = hotelData.getRoomCounts();
        assertEquals(2, counts[0]);
        assertEquals(3, counts[1]);
        assertEquals(1, counts[2]);

        double[] demands = hotelData.getDemandPercents();
        assertEquals(30.0, demands[0], 0.001);
        assertEquals(50.0, demands[1], 0.001);
        assertEquals(20.0, demands[2], 0.001);
    }

    /**
     * Tests that invalid simulation lengths are rejected.
     */
    public void testRejectsInvalidSimulationLength() {
        try {
            new HotelData(0, 10, 2,
                    new String[] {"Single"},
                    new int[] {1},
                    new double[] {100.0});
            fail("A zero simulation length should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        try {
            new HotelData(-1, 10, 2,
                    new String[] {"Single"},
                    new int[] {1},
                    new double[] {100.0});
            fail("A negative simulation length should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        try {
            new HotelData(1, -1, 2,
                    new String[] {"Single"},
                    new int[] {1},
                    new double[] {100.0});
            fail("A negative guest count should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        try {
            new HotelData(1, 10, -1,
                    new String[] {"Single"},
                    new int[] {1},
                    new double[] {100.0});
            fail("A negative maximum wait should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    /**
     * Tests that invalid room counts are rejected.
     */
    public void testRejectsInvalidRoomCounts() {
        // A negative room count is invalid.
        try {
            new HotelData(10, 5, 2,
                    new String[] {"Single", "Double"},
                    new int[] {1, -1},
                    new double[] {50.0, 50.0});
            fail("A negative room count should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // The hotel must contain at least one room.
        try {
            new HotelData(10, 5, 2,
                    new String[] {"Single", "Double"},
                    new int[] {0, 0},
                    new double[] {50.0, 50.0});
            fail("A hotel with no rooms should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    /**
     * Tests that invalid room types are rejected.
     */
    public void testRejectsInvalidRoomTypes() {
        // A null room type is invalid.
        try {
            new HotelData(10, 5, 2,
                    new String[] {null},
                    new int[] {1},
                    new double[] {100.0});
            fail("A null room type should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // An empty room type is invalid.
        try {
            new HotelData(10, 5, 2,
                    new String[] {""},
                    new int[] {1},
                    new double[] {100.0});
            fail("An empty room type should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // A whitespace-only room type is invalid.
        try {
            new HotelData(10, 5, 2,
                    new String[] {"   "},
                    new int[] {1},
                    new double[] {100.0});
            fail("A blank room type should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // Duplicate names are invalid, ignoring case and outer spaces.
        try {
            new HotelData(10, 5, 2,
                    new String[] {"Single", " single "},
                    new int[] {1, 1},
                    new double[] {50.0, 50.0});
            fail("Duplicate room types should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // Null configuration arrays are invalid.
        try {
            new HotelData(10, 5, 2,
                    null,
                    new int[] {1},
                    new double[] {100.0});
            fail("A null room type array should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        try {
            new HotelData(10, 5, 2,
                    new String[] {"Single"},
                    null,
                    new double[] {100.0});
            fail("A null room count array should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        try {
            new HotelData(10, 5, 2,
                    new String[] {"Single"},
                    new int[] {1},
                    null);
            fail("A null demand array should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // Empty configuration arrays are invalid.
        try {
            new HotelData(10, 5, 2,
                    new String[0],
                    new int[0],
                    new double[0]);
            fail("Empty configuration arrays should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    /**
     * Tests that invalid demand percentages are rejected.
     */
    public void testRejectsInvalidDemandPercents() {
        // The percentages do not total 100.
        try {
            new HotelData(10, 5, 2,
                    new String[] {"Single", "Double"},
                    new int[] {1, 1},
                    new double[] {40.0, 40.0});
            fail("Demand percentages must total 100.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // A negative percentage is invalid.
        try {
            new HotelData(10, 5, 2,
                    new String[] {"Single", "Double"},
                    new int[] {1, 1},
                    new double[] {-10.0, 110.0});
            fail("Negative demand percentages should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // A percentage greater than 100 is invalid.
        try {
            new HotelData(10, 5, 2,
                    new String[] {"Single", "Double"},
                    new int[] {1, 1},
                    new double[] {110.0, -10.0});
            fail("Percentages above 100 should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // NaN is invalid.
        try {
            new HotelData(10, 5, 2,
                    new String[] {"Single", "Double"},
                    new int[] {1, 1},
                    new double[] {Double.NaN, 100.0});
            fail("NaN demand should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // Infinity is invalid.
        try {
            new HotelData(10, 5, 2,
                    new String[] {"Single", "Double"},
                    new int[] {1, 1},
                    new double[] {Double.POSITIVE_INFINITY, 0.0});
            fail("Infinite demand should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        // A small floating-point difference is allowed.
        HotelData approximate = new HotelData(
                10, 5, 2,
                new String[] {"Single", "Double"},
                new int[] {1, 1},
                new double[] {33.33, 66.665});

        assertEquals(2, approximate.getNumRoomTypes());
    }

    /**
     * Tests that mismatched array lengths are rejected.
     */
    public void testRejectsMismatchedArrayLengths() {
        try {
            new HotelData(10, 5, 2,
                    new String[] {"Single", "Double"},
                    new int[] {1},
                    new double[] {50.0, 50.0});
            fail("Mismatched room count length should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }

        try {
            new HotelData(10, 5, 2,
                    new String[] {"Single", "Double"},
                    new int[] {1, 1},
                    new double[] {100.0});
            fail("Mismatched demand length should be rejected.");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    /**
     * Tests room type lookup, including missing and null types.
     */
    public void testGetRoomTypeIndex() {
        assertEquals(0, hotelData.getRoomTypeIndex("Single"));
        assertEquals(1, hotelData.getRoomTypeIndex("Double"));
        assertEquals(2, hotelData.getRoomTypeIndex("Suite"));

        // Lookup is case-insensitive.
        assertEquals(0, hotelData.getRoomTypeIndex("single"));
        assertEquals(1, hotelData.getRoomTypeIndex("DOUBLE"));

        // Outer spaces are ignored by the current implementation.
        assertEquals(2, hotelData.getRoomTypeIndex(" Suite "));

        assertEquals(-1, hotelData.getRoomTypeIndex("King"));
        assertEquals(-1, hotelData.getRoomTypeIndex(""));
        assertEquals(-1, hotelData.getRoomTypeIndex(null));
    }

    /**
     * Tests the total number of rooms.
     */
    public void testGetTotalRooms() {
        assertEquals(6, hotelData.getTotalRooms());

        HotelData oneRoom = new HotelData(
                1, 0, 0,
                new String[] {"Single"},
                new int[] {1},
                new double[] {100.0});

        assertEquals(1, oneRoom.getTotalRooms());

        HotelData zeroInOneType = new HotelData(
                1, 0, 0,
                new String[] {"Single", "Double"},
                new int[] {0, 2},
                new double[] {50.0, 50.0});

        assertEquals(2, zeroInOneType.getTotalRooms());
    }

    /**
     * Tests that constructor inputs are copied rather than stored directly.
     */
    public void testConstructorCopiesArrays() {
        String[] types = {"Single", "Double"};
        int[] counts = {1, 1};
        double[] demands = {50.0, 50.0};

        HotelData data = new HotelData(
                10, 5, 2, types, counts, demands);

        types[0] = "Suite";
        counts[0] = 99;
        demands[0] = 100.0;

        assertEquals("Single", data.getRoomTypes()[0]);
        assertEquals(1, data.getRoomCounts()[0]);
        assertEquals(50.0, data.getDemandPercents()[0], 0.001);
    }

    /**
     * Tests that getters return copies of the configuration arrays.
     */
    public void testGettersReturnCopies() {
        String[] types = hotelData.getRoomTypes();
        int[] counts = hotelData.getRoomCounts();
        double[] demands = hotelData.getDemandPercents();

        types[0] = "Changed";
        counts[0] = 99;
        demands[0] = 99.0;

        assertEquals("Single", hotelData.getRoomTypes()[0]);
        assertEquals(2, hotelData.getRoomCounts()[0]);
        assertEquals(30.0,
                hotelData.getDemandPercents()[0], 0.001);
    }
}
