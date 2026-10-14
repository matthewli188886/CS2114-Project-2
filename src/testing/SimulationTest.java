package testing;

import hotelhelper.*;
import java.util.Random;

/**
 * Tests the Simulation class. Most tests use an expected arrival count of 0
 * and add guests by hand, so every day's result is known in advance.
 *
 * @author Dallan Barnum
 * @version October 2026
 */
public class SimulationTest
    extends student.TestCase
{

    private Simulation simulation;
    private HotelData hotelData;
    private GuestData guestData;
    private RoomData roomData;
    private DailyData dailyData;

    /**
     * Creates a new Simulation before each test: 5 days, no random
     * arrivals, guests wait at most 2 days, 1 Single and 1 Double room.
     */
    public void setUp()
    {
        build(5, 0, 2, new Random(1));
    }


    /**
     * Rebuilds all the data classes and the simulation.
     *
     * @param days the simulation length
     * @param expected expected guests per day
     * @param maxWait the longest a guest will wait
     * @param random the random source
     */
    private void build(int days, int expected, int maxWait, Random random)
    {
        hotelData = new HotelData(days, expected, maxWait, new String[] {
            "Single", "Double" }, new int[] { 1, 1 },
            new double[] { 50.0, 50.0 });
        guestData = new GuestData();
        roomData = new RoomData(hotelData);
        dailyData = new DailyData(days, hotelData.getTotalRooms());
        simulation =
            new Simulation(hotelData, guestData, roomData, dailyData, random);
    }


    /**
     * Tests no guests no division by zero
     */
    public void testNoGuestsNoDivisionByZero()
    {
        simulation.runAll();
        assertEquals(5, dailyData.getNumberOfDays());
        for (int day = 1; day <= 5; day++)
        {
            DailyRecord r = dailyData.getDay(day);
            assertEquals(0, r.getTotalGuests());
            assertEquals(0, r.getCheckIns());
            assertEquals(0, r.getUnservedGuests());
            assertEquals(0, r.getWaitingGuests());
            assertEquals(0.0, r.getAverageWaitingTime(), 0.0001);
            assertEquals(2, r.getAvailableRooms());
        }
        assertEquals(0, guestData.getTotalGuests());
    }


    /**
     * Tests enough rooms for every guest
     */
    public void testEnoughRoomsForEveryGuest()
    {
        guestData.addGuest("Single", 1, 2);
        guestData.addGuest("Double", 1, 1);

        DailyRecord r = simulation.runNextDay();
        assertEquals(2, r.getCheckIns());
        assertEquals(2, r.getServedGuests());
        assertEquals(0, r.getWaitingGuests());
        assertEquals(0, r.getUnservedGuests());
        assertEquals(2, r.getOccupiedRooms());
        assertEquals(0, r.getAvailableRooms());
        assertEquals(0, r.getShortageByType()[0]);
        assertEquals(0, r.getShortageByType()[1]);
        assertEquals(0.0, r.getAverageWaitingTime(), 0.0001);
    }


    /**
     * Tests more guests than rooms
     */
    public void testMoreGuestsThanRooms()
    {
        // Only one Single room exists
        guestData.addGuest("Single", 1, 5);
        guestData.addGuest("Single", 1, 5);
        guestData.addGuest("Single", 1, 5);

        DailyRecord r = simulation.runNextDay();
        assertEquals(1, r.getCheckIns());
        assertEquals(2, r.getWaitingGuests());
        assertEquals(0, r.getUnservedGuests());
        // Each guest who cannot get a room of their type is one shortage
        assertEquals(2, r.getShortageByType()[0]);
        assertEquals(0, r.getShortageByType()[1]);
        assertEquals(1, r.getOccupiedRooms());
        assertEquals(1, r.getAvailableRooms());
    }


    /**
     * Tests guests checked in in arrival order
     */
    public void testGuestsCheckedInInArrivalOrder()
    {
        build(5, 0, 5, new Random(1));
        GuestRecord first = guestData.addGuest("Single", 1, 1);
        GuestRecord second = guestData.addGuest("Single", 1, 1);
        GuestRecord third = guestData.addGuest("Single", 1, 1);

        simulation.runNextDay();
        assertEquals(GuestStatus.CHECKED_IN, first.getStatus());
        assertEquals(GuestStatus.WAITING, second.getStatus());
        assertEquals(GuestStatus.WAITING, third.getStatus());
        assertEquals(1, first.getCheckInDay());

        // Day 2: first checks out, room cleans. Nobody checks in.
        simulation.runNextDay();
        assertEquals(GuestStatus.CHECKED_OUT, first.getStatus());
        assertEquals(GuestStatus.WAITING, second.getStatus());

        // Day 3: room is available again, second goes first
        DailyRecord r = simulation.runNextDay();
        assertEquals(GuestStatus.CHECKED_IN, second.getStatus());
        assertEquals(3, second.getCheckInDay());
        assertEquals(GuestStatus.WAITING, third.getStatus());
        assertEquals(1, r.getCheckIns());
        // The second guest waited from day 1 to day 3
        assertEquals(2.0, r.getAverageWaitingTime(), 0.0001);
    }


    /**
     * Tests guests leave unserved after max wait
     */
    public void testGuestsLeaveUnservedAfterMaxWait()
    {
        guestData.addGuest("Single", 1, 7);
        GuestRecord waiter = guestData.addGuest("Single", 1, 1);

        DailyRecord d1 = simulation.runNextDay();
        assertEquals(0, d1.getUnservedGuests());
        assertEquals(1, d1.getWaitingGuests());

        DailyRecord d2 = simulation.runNextDay();
        assertEquals(0, d2.getUnservedGuests());
        assertEquals(GuestStatus.WAITING, waiter.getStatus());

        // On day 3 the guest has waited 2 days (the maximum) and leaves
        DailyRecord d3 = simulation.runNextDay();
        assertEquals(1, d3.getUnservedGuests());
        assertEquals(0, d3.getWaitingGuests());
        assertEquals(GuestStatus.LEFT_UNSERVED, waiter.getStatus());
        assertEquals(1, guestData.getUnservedCount());

        // With a max wait of 0, an unlucky guest leaves on arrival day
        build(3, 0, 0, new Random(1));
        guestData.addGuest("Single", 1, 7);
        GuestRecord instant = guestData.addGuest("Single", 1, 1);
        DailyRecord r = simulation.runNextDay();
        assertEquals(1, r.getUnservedGuests());
        assertEquals(GuestStatus.LEFT_UNSERVED, instant.getStatus());
    }


    /**
     * Tests rooms checked out and cleaned on correct days
     */
    public void testRoomsCheckedOutAndCleanedOnCorrectDays()
    {
        GuestRecord guest = guestData.addGuest("Single", 1, 2);
        RoomRecord room = roomData.findRoom(1);
        assertEquals("Single", room.getRoomType());

        simulation.runNextDay(); // day 1: check in, stay ends day 3
        assertEquals(RoomStatus.OCCUPIED, room.getStatus());
        assertEquals(3, room.getStayEndDay());

        simulation.runNextDay(); // day 2: still occupied
        assertEquals(RoomStatus.OCCUPIED, room.getStatus());
        assertEquals(0, dailyData.getDay(2).getCheckOuts());

        DailyRecord d3 = simulation.runNextDay(); // day 3: check out
        assertEquals(1, d3.getCheckOuts());
        assertEquals(GuestStatus.CHECKED_OUT, guest.getStatus());
        assertEquals(RoomStatus.CLEANING, room.getStatus());
        assertEquals(1, d3.getRoomsBeingCleaned());
        assertEquals(0, d3.getOccupiedRooms());

        DailyRecord d4 = simulation.runNextDay(); // day 4: cleaned
        assertEquals(RoomStatus.AVAILABLE, room.getStatus());
        assertEquals(0, d4.getRoomsBeingCleaned());
        assertEquals(2, d4.getAvailableRooms());
    }


    /**
     * Tests daily records have accurate counts
     */
    public void testDailyRecordsHaveAccurateCounts()
    {
        build(10, 20, 3, new Random(7));
        simulation.runAll();

        int arrivals = 0;
        int checkIns = 0;
        int unserved = 0;
        for (int day = 1; day <= 10; day++)
        {
            DailyRecord r = dailyData.getDay(day);
            assertEquals(day, r.getDay());
            assertEquals(2, r.getOccupiedRooms() + r.getAvailableRooms()
                + r.getRoomsBeingCleaned());
            assertEquals(r.getCheckIns(), r.getServedGuests());
            assertTrue(r.getTotalGuests() >= 16 && r.getTotalGuests() <= 24);

            int demand = r.getDemandByType()[0] + r.getDemandByType()[1];
            assertEquals(r.getTotalGuests(), demand);

            arrivals += r.getTotalGuests();
            checkIns += r.getCheckIns();
            unserved += r.getUnservedGuests();
        }

        assertEquals(arrivals, guestData.getTotalGuests());
        assertEquals(unserved, guestData.getUnservedCount());
        // Every guest is checked in, unserved, or still waiting
        int waiting = dailyData.getDay(10).getWaitingGuests();
        assertEquals(arrivals, checkIns + unserved + waiting);
    }


    /**
     * Tests run next day after all days throws
     */
    public void testRunNextDayAfterAllDaysThrows()
    {
        assertTrue(simulation.hasMoreDays());
        assertEquals(0, simulation.getCurrentDay());
        simulation.runAll();
        assertFalse(simulation.hasMoreDays());
        assertEquals(5, simulation.getCurrentDay());

        try
        {
            simulation.runNextDay();
            fail("No days are left to run");
        }
        catch (IllegalStateException e)
        {
            assertNotNull(e.getMessage());
        }
        assertEquals(5, dailyData.getNumberOfDays());

        // Constructor rejects null arguments
        try
        {
            new Simulation(hotelData, guestData, roomData, dailyData, null);
            fail("Null random should be rejected");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
        try
        {
            new Simulation(null, guestData, roomData, dailyData,
                new Random());
            fail("Null hotel data should be rejected");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }


    /**
     * Tests run all completes every remaining day once
     */
    public void testRunAllCompletesEveryRemainingDayOnce()
    {
        simulation.runNextDay();
        simulation.runNextDay();
        assertEquals(2, simulation.getCurrentDay());
        assertEquals(2, dailyData.getNumberOfDays());

        simulation.runAll();
        assertEquals(5, simulation.getCurrentDay());
        assertEquals(5, dailyData.getNumberOfDays());
        for (int day = 1; day <= 5; day++)
        {
            assertEquals(day, dailyData.getDay(day).getDay());
        }

        // Running again changes nothing
        simulation.runAll();
        assertEquals(5, dailyData.getNumberOfDays());
    }


    /**
     * Tests seeded random is repeatable
     */
    public void testSeededRandomIsRepeatable()
    {
        build(8, 15, 2, new Random(42));
        simulation.runAll();
        DailyData first = dailyData;
        GuestData firstGuests = guestData;

        build(8, 15, 2, new Random(42));
        simulation.runAll();

        assertEquals(firstGuests.getTotalGuests(), guestData.getTotalGuests());
        for (int day = 1; day <= 8; day++)
        {
            DailyRecord a = first.getDay(day);
            DailyRecord b = dailyData.getDay(day);
            assertEquals(a.getTotalGuests(), b.getTotalGuests());
            assertEquals(a.getCheckIns(), b.getCheckIns());
            assertEquals(a.getCheckOuts(), b.getCheckOuts());
            assertEquals(a.getUnservedGuests(), b.getUnservedGuests());
            assertEquals(a.getWaitingGuests(), b.getWaitingGuests());
            assertEquals(a.getDemandByType()[0], b.getDemandByType()[0]);
            assertEquals(a.getAverageWaitingTime(),
                b.getAverageWaitingTime(), 0.0001);
        }
    }
}
