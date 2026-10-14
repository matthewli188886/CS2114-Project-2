package testing;

import hotelhelper.*;
import java.util.Random;

/**
 * Tests the Simulation class. Every simulation uses a seeded Random so the
 * results are the same on every run.
 *
 * @author Jaidev Gogineni
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
     * Creates a new Simulation before each test
     */
    public void setUp()
    {
        // 10 days, about 4 guests a day, guests wait up to 2 days,
        // 3 Single rooms and 2 Suites
        build(10, 4, 2, new String[] { "Single", "Suite" },
            new int[] { 3, 2 }, new double[] { 60.0, 40.0 }, 42);
    }


    /**
     * Replaces the simulation and its data classes with a new setup.
     *
     * @param days simulation length
     * @param guestsPerDay expected guests per day
     * @param maxWait max days a guest waits
     * @param types room type names
     * @param counts rooms of each type
     * @param percents demand for each type
     * @param seed seed for the Random
     */
    private void build(int days, int guestsPerDay, int maxWait,
        String[] types, int[] counts, double[] percents, long seed)
    {
        hotelData = new HotelData(days, guestsPerDay, maxWait, types,
            counts, percents);
        guestData = new GuestData();
        roomData = new RoomData(hotelData);
        dailyData = new DailyData(days, hotelData.getTotalRooms());
        simulation = new Simulation(hotelData, guestData, roomData,
            dailyData, new Random(seed));
    }


    /**
     * Builds a hotel with a single room type.
     *
     * @param days simulation length
     * @param guestsPerDay expected guests per day
     * @param maxWait max days a guest waits
     * @param rooms number of rooms
     */
    private void buildOneType(int days, int guestsPerDay, int maxWait,
        int rooms)
    {
        build(days, guestsPerDay, maxWait, new String[] { "Single" },
            new int[] { rooms }, new double[] { 100.0 }, 7);
    }


    /**
     * Gets every guest as an array, in arrival order.
     *
     * @return all guests
     */
    private GuestRecord[] allGuests()
    {
        LinkedChain<GuestRecord> guests = guestData.getGuests();
        return guests.toArray(new GuestRecord[guests.size()]);
    }


    /**
     * Tests the constructor starts at day 0 and rejects null arguments
     */
    public void testConstructor()
    {
        assertEquals(0, simulation.getCurrentDay());
        assertTrue(simulation.hasMoreDays());

        Random random = new Random(1);
        Object[][] badArgs = {
            { null, guestData, roomData, dailyData, random },
            { hotelData, null, roomData, dailyData, random },
            { hotelData, guestData, null, dailyData, random },
            { hotelData, guestData, roomData, null, random },
            { hotelData, guestData, roomData, dailyData, null } };

        for (Object[] args : badArgs)
        {
            Exception thrown = null;
            try
            {
                new Simulation((HotelData)args[0], (GuestData)args[1],
                    (RoomData)args[2], (DailyData)args[3], (Random)args[4]);
            }
            catch (Exception e)
            {
                thrown = e;
            }
            assertTrue(thrown instanceof IllegalArgumentException);
        }
    }


    /**
     * Tests no guests no division by zero
     */
    public void testNoGuestsNoDivisionByZero()
    {
        buildOneType(5, 0, 1, 2);
        simulation.runAll();

        assertEquals(5, dailyData.getNumberOfDays());
        assertEquals(0, guestData.getTotalGuests());
        for (int day = 1; day <= 5; day++)
        {
            DailyRecord r = dailyData.getDay(day);
            assertEquals(0, r.getTotalGuests());
            assertEquals(0, r.getCheckIns());
            assertEquals(0, r.getCheckOuts());
            assertEquals(0, r.getWaitingGuests());
            assertEquals(0.0, r.getAverageWaitingTime(), 0.0001);
            assertEquals(2, r.getAvailableRooms());
        }

        // the report averages over zero guests without dividing by zero
        Report report = new Report(hotelData, guestData, dailyData);
        assertTrue(report.finalReport().contains("Average wait:     0.00"));
    }


    /**
     * Tests enough rooms for every guest
     */
    public void testEnoughRoomsForEveryGuest()
    {
        // at most 6 guests a day for 5 days, and 100 rooms
        buildOneType(5, 5, 0, 100);
        simulation.runAll();

        assertTrue(guestData.getTotalGuests() > 0);
        assertEquals(0, guestData.getUnservedCount());
        for (GuestRecord guest : allGuests())
        {
            assertTrue(guest.getStatus() != GuestStatus.WAITING);
            assertTrue(guest.getStatus() != GuestStatus.LEFT_UNSERVED);
            // everyone got a room on the day they arrived
            assertEquals(guest.getArrivalDay(), guest.getCheckInDay());
        }
        for (int day = 1; day <= 5; day++)
        {
            DailyRecord r = dailyData.getDay(day);
            assertEquals(r.getTotalGuests(), r.getCheckIns());
            assertEquals(0, r.getShortageByType()[0]);
            assertEquals(0, r.getUnservedGuests());
            assertEquals(0, r.getWaitingGuests());
        }
    }


    /**
     * Tests more guests than rooms
     */
    public void testMoreGuestsThanRooms()
    {
        // about 5 guests a day, 1 room, guests leave the day they arrive
        buildOneType(3, 5, 0, 1);
        simulation.runAll();

        assertTrue(guestData.getUnservedCount() > 0);
        int totalShortage = 0;
        for (int day = 1; day <= 3; day++)
        {
            DailyRecord r = dailyData.getDay(day);
            // one room means at most one check-in a day
            assertTrue(r.getCheckIns() <= 1);
            totalShortage += r.getShortageByType()[0];
        }
        assertTrue(totalShortage > 0);

        // everyone who was not checked in left unserved
        for (GuestRecord guest : allGuests())
        {
            if (guest.getCheckInDay() == -1)
            {
                assertEquals(GuestStatus.LEFT_UNSERVED, guest.getStatus());
                assertTrue(guestData.getUnservedGuests().contains(guest));
            }
        }
    }


    /**
     * Tests guests checked in in arrival order
     */
    public void testGuestsCheckedInInArrivalOrder()
    {
        // one room and guests who wait a long time, so they queue up
        buildOneType(20, 3, 30, 1);
        simulation.runAll();

        GuestRecord[] guests = allGuests();
        assertEquals(1, guests[0].getCheckInDay());

        int lastCheckIn = 0;
        boolean someoneSkipped = false;
        for (GuestRecord guest : guests)
        {
            if (guest.getCheckInDay() == -1)
            {
                someoneSkipped = true;
            }
            else
            {
                // nobody gets a room ahead of a guest who arrived earlier
                assertFalse(someoneSkipped);
                assertTrue(guest.getCheckInDay() >= lastCheckIn);
                lastCheckIn = guest.getCheckInDay();
            }
        }
    }


    /**
     * Tests guests leave unserved after max wait
     */
    public void testGuestsLeaveUnservedAfterMaxWait()
    {
        buildOneType(10, 3, 2, 1);

        while (simulation.hasMoreDays())
        {
            DailyRecord r = simulation.runNextDay();
            int today = r.getDay();

            // nobody still waiting has waited maxWaitDays or more
            LinkedChain<GuestRecord> waiting = guestData.getWaitingGuests();
            for (int i = 0; i < waiting.size(); i++)
            {
                assertTrue(today - waiting.get(i).getArrivalDay() < 2);
            }
            assertEquals(waiting.size(), r.getWaitingGuests());
        }

        assertTrue(guestData.getUnservedCount() > 0);

        // the record of who left matches the guests' statuses
        int unserved = 0;
        for (GuestRecord guest : allGuests())
        {
            if (guest.getStatus() == GuestStatus.LEFT_UNSERVED)
            {
                unserved++;
            }
        }
        assertEquals(guestData.getUnservedCount(), unserved);
    }


    /**
     * Tests rooms checked out and cleaned on correct days
     */
    public void testRoomsCheckedOutAndCleanedOnCorrectDays()
    {
        // one room; 2 to 4 guests arrive each day and wait a long time
        buildOneType(12, 3, 30, 1);
        RoomRecord room = roomData.findRoom(1);

        simulation.runNextDay();
        assertEquals(RoomStatus.OCCUPIED, room.getStatus());
        GuestRecord guest = guestData.findGuest(room.getCurrentGuestId());
        int endDay = room.getStayEndDay();
        assertEquals(1, guest.getCheckInDay());
        assertEquals(1 + guest.getLengthOfStay(), endDay);

        // the guest keeps the room until the day their stay ends
        while (simulation.getCurrentDay() < endDay - 1)
        {
            simulation.runNextDay();
            assertEquals(RoomStatus.OCCUPIED, room.getStatus());
            assertEquals(guest.getGuestId(), room.getCurrentGuestId());
        }

        // on the last day they check out and the room is cleaned
        DailyRecord checkOutDay = simulation.runNextDay();
        assertEquals(endDay, checkOutDay.getDay());
        assertEquals(GuestStatus.CHECKED_OUT, guest.getStatus());
        assertEquals(endDay, guest.getCheckOutDay());
        assertEquals(RoomStatus.CLEANING, room.getStatus());
        assertEquals(-1, room.getCurrentGuestId());
        assertEquals(1, checkOutDay.getCheckOuts());
        assertEquals(1, checkOutDay.getRoomsBeingCleaned());
        assertEquals(0, checkOutDay.getCheckIns());

        // the next day the room is free again and the next guest gets it
        DailyRecord nextDay = simulation.runNextDay();
        assertEquals(RoomStatus.OCCUPIED, room.getStatus());
        assertEquals(0, nextDay.getRoomsBeingCleaned());
        assertEquals(1, nextDay.getCheckIns());
        GuestRecord nextGuest = guestData.findGuest(room.getCurrentGuestId());
        assertEquals(endDay + 1, nextGuest.getCheckInDay());
    }


    /**
     * Tests daily records have accurate counts
     */
    public void testDailyRecordsHaveAccurateCounts()
    {
        simulation.runAll();

        int totalArrivals = 0;
        int totalCheckIns = 0;
        int totalCheckOuts = 0;
        int totalUnserved = 0;
        for (int day = 1; day <= 10; day++)
        {
            DailyRecord r = dailyData.getDay(day);
            assertEquals(day, r.getDay());

            int demand = r.getDemandByType()[0] + r.getDemandByType()[1];
            assertEquals(r.getTotalGuests(), demand);
            assertEquals(r.getCheckIns(), r.getServedGuests());
            assertEquals(5, r.getOccupiedRooms() + r.getAvailableRooms()
                + r.getRoomsBeingCleaned());
            assertTrue(r.getAverageWaitingTime() >= 0.0);
            assertTrue(r.getAverageWaitingTime() <= 2.0);

            totalArrivals += r.getTotalGuests();
            totalCheckIns += r.getCheckIns();
            totalCheckOuts += r.getCheckOuts();
            totalUnserved += r.getUnservedGuests();
        }

        // the daily totals match the guest records
        int checkedIn = 0;
        int checkedOut = 0;
        int stillIn = 0;
        for (GuestRecord guest : allGuests())
        {
            if (guest.getCheckInDay() != -1)
            {
                checkedIn++;
            }
            if (guest.getStatus() == GuestStatus.CHECKED_OUT)
            {
                checkedOut++;
            }
            if (guest.getStatus() == GuestStatus.CHECKED_IN)
            {
                stillIn++;
            }
        }
        assertEquals(guestData.getTotalGuests(), totalArrivals);
        assertEquals(checkedIn, totalCheckIns);
        assertEquals(checkedOut, totalCheckOuts);
        assertEquals(guestData.getUnservedCount(), totalUnserved);

        // the last day's room and waiting counts match the current state
        DailyRecord last = dailyData.getDay(10);
        assertEquals(stillIn, last.getOccupiedRooms());
        assertEquals(roomData.countByStatus(RoomStatus.OCCUPIED),
            last.getOccupiedRooms());
        assertEquals(roomData.countByStatus(RoomStatus.AVAILABLE),
            last.getAvailableRooms());
        assertEquals(roomData.countByStatus(RoomStatus.CLEANING),
            last.getRoomsBeingCleaned());
        assertEquals(guestData.getWaitingGuests().size(),
            last.getWaitingGuests());
    }


    /**
     * Tests a room type with 0% demand is never requested
     */
    public void testZeroDemandTypeNeverPicked()
    {
        build(10, 5, 1, new String[] { "Single", "Suite", "Penthouse" },
            new int[] { 2, 2, 1 }, new double[] { 0.0, 100.0, 0.0 }, 3);
        simulation.runAll();

        assertTrue(guestData.getTotalGuests() > 0);
        for (GuestRecord guest : allGuests())
        {
            assertEquals("Suite", guest.getRequestedRoomType());
        }
        for (int day = 1; day <= 10; day++)
        {
            assertEquals(0, dailyData.getDay(day).getDemandByType()[0]);
            assertEquals(0, dailyData.getDay(day).getDemandByType()[2]);
        }
    }


    /**
     * Tests run next day after all days throws
     */
    public void testRunNextDayAfterAllDaysThrows()
    {
        simulation.runAll();
        assertFalse(simulation.hasMoreDays());

        Exception thrown = null;
        try
        {
            simulation.runNextDay();
        }
        catch (Exception e)
        {
            thrown = e;
        }
        assertTrue(thrown instanceof IllegalStateException);
        assertEquals(10, simulation.getCurrentDay());
        assertEquals(10, dailyData.getNumberOfDays());
    }


    /**
     * Tests run all completes every remaining day once
     */
    public void testRunAllCompletesEveryRemainingDayOnce()
    {
        DailyRecord first = simulation.runNextDay();
        assertEquals(1, first.getDay());
        assertSame(first, dailyData.getDay(1));
        simulation.runNextDay();
        assertEquals(2, simulation.getCurrentDay());

        simulation.runAll();
        assertEquals(10, simulation.getCurrentDay());
        assertEquals(10, dailyData.getNumberOfDays());
        assertSame(first, dailyData.getDay(1));
        for (int day = 1; day <= 10; day++)
        {
            assertEquals(day, dailyData.getDay(day).getDay());
        }

        // running all again does nothing
        simulation.runAll();
        assertEquals(10, simulation.getCurrentDay());
        assertEquals(10, dailyData.getNumberOfDays());
    }


    /**
     * Tests seeded random is repeatable
     */
    public void testSeededRandomIsRepeatable()
    {
        simulation.runAll();
        DailyData firstRun = dailyData;
        int firstGuests = guestData.getTotalGuests();

        setUp(); // same setup, same seed
        simulation.runAll();

        assertEquals(firstGuests, guestData.getTotalGuests());
        for (int day = 1; day <= 10; day++)
        {
            DailyRecord a = firstRun.getDay(day);
            DailyRecord b = dailyData.getDay(day);
            assertEquals(a.getTotalGuests(), b.getTotalGuests());
            assertEquals(a.getCheckIns(), b.getCheckIns());
            assertEquals(a.getCheckOuts(), b.getCheckOuts());
            assertEquals(a.getUnservedGuests(), b.getUnservedGuests());
            assertEquals(a.getOccupiedRooms(), b.getOccupiedRooms());
            assertEquals(a.getDemandByType()[0], b.getDemandByType()[0]);
            assertEquals(a.getShortageByType()[1],
                b.getShortageByType()[1]);
        }
    }
}
