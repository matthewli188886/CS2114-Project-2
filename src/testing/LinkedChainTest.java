package testing;

import hotelhelper.*;

/**
 * Tests the LinkedChain class.
 *
 * @author Matthew Li
 * @version October 2026
 */
public class LinkedChainTest
    extends student.TestCase
{

    private LinkedChain<String> chain;

    /**
     * Creates a new LinkedChain before each test
     */
    public void setUp()
    {
        chain = new LinkedChain<String>();
    }


    /**
     * Tests add to empty chain
     */
    public void testAddToEmptyChain()
    {
        assertTrue(chain.isEmpty());
        assertEquals(0, chain.size());
        assertEquals("[]", chain.toString());

        assertTrue(chain.add("a"));
        assertFalse(chain.isEmpty());
        assertEquals(1, chain.size());
        assertEquals("a", chain.get(0));

        chain.clear();
        assertTrue(chain.addToEnd("b"));
        assertEquals(1, chain.size());
        assertEquals("b", chain.get(0));
    }


    /**
     * Tests add to single element chain
     */
    public void testAddToSingleElementChain()
    {
        chain.add("a");
        chain.add("b");
        assertEquals(2, chain.size());
        assertEquals("b", chain.get(0));
        assertEquals("a", chain.get(1));

        chain.clear();
        chain.add("a");
        chain.addToEnd("b");
        assertEquals(2, chain.size());
        assertEquals("a", chain.get(0));
        assertEquals("b", chain.get(1));
    }


    /**
     * Tests add to multi element chain
     */
    public void testAddToMultiElementChain()
    {
        chain.add("a");
        chain.add("b");
        chain.add("c");
        chain.add("d");
        assertEquals(4, chain.size());
        assertEquals("[d, c, b, a]", chain.toString());

        chain.addToEnd("e");
        assertEquals(5, chain.size());
        assertEquals("[d, c, b, a, e]", chain.toString());

        chain.add("f");
        assertEquals("[f, d, c, b, a, e]", chain.toString());
    }


    /**
     * Tests size is empty get and contains
     */
    public void testSizeIsEmptyGetAndContains()
    {
        assertTrue(chain.isEmpty());
        assertFalse(chain.contains("a"));
        assertEquals(0, chain.count("a"));

        chain.addToEnd("a");
        chain.addToEnd("b");
        chain.addToEnd("a");

        assertFalse(chain.isEmpty());
        assertEquals(3, chain.size());
        assertEquals("a", chain.get(0));
        assertEquals("b", chain.get(1));
        assertEquals("a", chain.get(2));

        assertTrue(chain.contains("a"));
        assertTrue(chain.contains("b"));
        assertFalse(chain.contains("z"));
        assertFalse(chain.contains(null));

        assertEquals(2, chain.count("a"));
        assertEquals(1, chain.count("b"));
        assertEquals(0, chain.count("z"));
        assertEquals(0, chain.count(null));

        chain.clear();
        assertTrue(chain.isEmpty());
        assertEquals(0, chain.size());
        assertFalse(chain.contains("a"));
    }


    /**
     * Tests keeps insertion order
     */
    public void testKeepsInsertionOrder()
    {
        chain.addToEnd("a");
        chain.addToEnd("b");
        chain.addToEnd("c");

        assertEquals("[a, b, c]", chain.toString());

        String[] result = chain.toArray(new String[3]);
        assertEquals(3, result.length);
        assertEquals("a", result[0]);
        assertEquals("b", result[1]);
        assertEquals("c", result[2]);

        // A larger array is allowed; extra slots are untouched.
        String[] larger = chain.toArray(new String[5]);
        assertEquals("c", larger[2]);
        assertNull(larger[3]);
        assertNull(larger[4]);

        // add() reverses the order
        LinkedChain<String> reversed = new LinkedChain<String>();
        reversed.add("a");
        reversed.add("b");
        reversed.add("c");
        String[] rev = reversed.toArray(new String[3]);
        assertEquals("c", rev[0]);
        assertEquals("b", rev[1]);
        assertEquals("a", rev[2]);

        // An empty chain fills nothing
        assertEquals(0, new LinkedChain<String>().toArray(new String[0]).length);
    }


    /**
     * Tests remove first
     */
    public void testRemoveFirst()
    {
        chain.addToEnd("a");
        chain.addToEnd("b");
        chain.addToEnd("c");

        assertEquals("a", chain.remove());
        assertEquals(2, chain.size());
        assertEquals("[b, c]", chain.toString());

        assertTrue(chain.remove("b"));
        assertEquals(1, chain.size());
        assertEquals("c", chain.get(0));
    }


    /**
     * Tests remove middle
     */
    public void testRemoveMiddle()
    {
        chain.addToEnd("a");
        chain.addToEnd("b");
        chain.addToEnd("c");

        assertTrue(chain.remove("b"));
        assertEquals(2, chain.size());
        assertFalse(chain.contains("b"));
        assertEquals("[a, c]", chain.toString());
    }


    /**
     * Tests remove last
     */
    public void testRemoveLast()
    {
        chain.addToEnd("a");
        chain.addToEnd("b");
        chain.addToEnd("c");

        assertTrue(chain.remove("c"));
        assertEquals(2, chain.size());
        assertEquals("[a, b]", chain.toString());

        // addToEnd must still append correctly after removing the last
        chain.addToEnd("d");
        assertEquals("[a, b, d]", chain.toString());
    }


    /**
     * Tests remove only element
     */
    public void testRemoveOnlyElement()
    {
        chain.add("a");
        assertTrue(chain.remove("a"));
        assertTrue(chain.isEmpty());
        assertEquals(0, chain.size());
        assertEquals("[]", chain.toString());

        chain.add("b");
        assertEquals("b", chain.remove());
        assertTrue(chain.isEmpty());

        // Removing from an empty chain
        assertNull(chain.remove());
        assertFalse(chain.remove("b"));
        assertEquals(0, chain.size());
    }


    /**
     * Tests remove missing element returns false
     */
    public void testRemoveMissingElementReturnsFalse()
    {
        assertFalse(chain.remove("a"));

        chain.addToEnd("a");
        chain.addToEnd("b");

        assertFalse(chain.remove("z"));
        assertFalse(chain.remove(null));
        assertEquals(2, chain.size());
        assertEquals("[a, b]", chain.toString());
    }


    /**
     * Tests remove updates head tail and size
     */
    public void testRemoveUpdatesHeadTailAndSize()
    {
        chain.addToEnd("a");
        chain.addToEnd("b");
        chain.addToEnd("c");
        chain.addToEnd("a");

        // Only the first occurrence is removed
        assertTrue(chain.remove("a"));
        assertEquals(3, chain.size());
        assertEquals("b", chain.get(0));
        assertEquals("a", chain.get(2));
        assertEquals(1, chain.count("a"));

        assertTrue(chain.remove("a"));
        assertEquals(2, chain.size());
        assertEquals("b", chain.get(0));
        assertEquals("c", chain.get(1));

        assertEquals("b", chain.remove());
        assertEquals("c", chain.get(0));
        assertEquals(1, chain.size());

        assertEquals("c", chain.remove());
        assertEquals(0, chain.size());
        assertNull(chain.remove());
    }


    /**
     * Tests add null throws
     */
    public void testAddNullThrows()
    {
        try
        {
            chain.add(null);
            fail("add(null) should throw");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }

        try
        {
            chain.addToEnd(null);
            fail("addToEnd(null) should throw");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }

        assertEquals(0, chain.size());
        assertTrue(chain.isEmpty());

        // toArray argument validation
        try
        {
            chain.toArray(null);
            fail("toArray(null) should throw");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }

        chain.add("a");
        try
        {
            chain.toArray(new String[0]);
            fail("toArray with too small an array should throw");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e.getMessage());
        }
    }


    /**
     * Tests invalid index throws
     */
    public void testInvalidIndexThrows()
    {
        try
        {
            chain.get(0);
            fail("get on empty chain should throw");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertNotNull(e.getMessage());
        }

        chain.add("a");
        chain.add("b");

        try
        {
            chain.get(-1);
            fail("negative index should throw");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertNotNull(e.getMessage());
        }

        try
        {
            chain.get(2);
            fail("index equal to size should throw");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertNotNull(e.getMessage());
        }

        // Valid boundaries still work
        assertEquals("b", chain.get(0));
        assertEquals("a", chain.get(1));
    }


    /**
     * Tests generic type safety
     */
    public void testGenericTypeSafety()
    {
        LinkedChain<Integer> numbers = new LinkedChain<Integer>();
        numbers.addToEnd(1);
        numbers.addToEnd(2);
        numbers.addToEnd(3);

        int first = numbers.get(0);
        assertEquals(1, first);
        assertTrue(numbers.contains(2));
        assertFalse(numbers.contains(4));
        assertTrue(numbers.remove(Integer.valueOf(2)));
        assertEquals("[1, 3]", numbers.toString());

        Integer[] array = numbers.toArray(new Integer[2]);
        assertTrue(array[0].equals(1));
        assertTrue(array[1].equals(3));

        // Chains of different types are independent
        chain.add("x");
        assertEquals(1, chain.size());
        assertEquals(2, numbers.size());
    }
}
