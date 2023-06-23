/*
  Name: Cameron Arch
  Email: cameronarch598@gmail.com
  Sources Used: Java Interface Documentation

  This file holds the test cases for the implementations of 
  MyHashtableSC and MyHashtableLP.
*/
import org.junit.*;

import static org.junit.Assert.*;
/** 
* A HashtableTester class for testing MyHashtableSC and MyHashtableLP. 
*/
public class HashtableTester {

    /** 
    * Tests contructors of MyHashtableSC.
    */
    @Test
    public void testContructorSC() {
        MyHashtableSC<String,Integer> test = new MyHashtableSC<>();
        
        assertEquals(0,test.size);
        assertEquals(11,test.data.length);
        assertEquals(0.75,test.loadFactor,0.0000001);

        MyHashtableSC<String,Integer> test2 = 
                new MyHashtableSC<>(5);

        assertEquals(0,test2.size);
        assertEquals(5,test2.data.length);
        assertEquals(0.75,test2.loadFactor,0.0000001);
        assertThrows(IllegalArgumentException.class,
                () -> new MyHashtableSC<>(-1));

        MyHashtableSC<String,Integer> test3 = 
                new MyHashtableSC<>(0,0.5);

        assertEquals(0,test3.size);
        assertEquals(0,test3.data.length);
        assertEquals(0.5,test3.loadFactor,0.0000001);
        assertThrows(IllegalArgumentException.class,
                () -> new MyHashtableSC<>(1,0));
        assertThrows(IllegalArgumentException.class,
                () -> new MyHashtableSC<>(-1,1));
    }
    /** 
    * Tests rehash() of MyHashtableSC.
    */
    @Test
    public void testRehashSC() {
        MyHashtableSC<String,Integer> test = new MyHashtableSC<>();
        test.rehash();
        assertEquals(23,test.data.length);
        assertEquals(0,test.size());

        MyHashtableSC<String,Integer> test2 = new MyHashtableSC<>();
        test2.data["one".hashCode() % 11] = new HashEntry<>("one", 1);
        test2.data["one".hashCode() % 11].setNext(new HashEntry<>("ten", 10));
        test2.data["two".hashCode() % 11] = new HashEntry<>("two", 2);
		test2.data["two".hashCode() % 11]
                .setNext(new HashEntry<>("eight", 8));
		test2.data["five".hashCode() % 11] = new HashEntry<>("five", 5);
		test2.data[10] = new HashEntry<>("five", 15);
        test2.size = 6;

        test2.rehash();

        assertEquals(23,test2.data.length);
        assertEquals(6,test2.size());
        assertEquals("one",test2.data["one".hashCode() % 23]
                .getKey());
        assertEquals(1,test2.data["one".hashCode() % 23]
                .getValue().intValue());
        assertEquals("two",test2.data["two".hashCode() % 23]
                .getKey());
        assertEquals(2,test2.data["two".hashCode() % 23]
                .getValue().intValue());
        assertEquals("ten",test2.data["ten".hashCode() % 23]
                .getKey());
        assertEquals(10,test2.data["ten".hashCode() % 23]
                .getValue().intValue());
		assertEquals("two",test2.data["eight".hashCode() % 23]
                .getKey());
        assertEquals(2,test2.data["eight".hashCode() % 23]
                .getValue().intValue());
		assertEquals("eight",test2.data["eight".hashCode() % 23]
                .getNext().getKey());
        assertEquals(8,test2.data["eight".hashCode() % 23]
                .getNext().getValue().intValue());
		assertEquals("five",test2.data["five".hashCode() % 23]
                .getKey());
        assertEquals(5,test2.data["five".hashCode() % 23]
                .getValue().intValue());
		assertEquals("five",test2.data["five".hashCode() % 23]
                .getNext().getKey());
        assertEquals(15,test2.data["five".hashCode() % 23]
                .getNext().getValue().intValue());	
    }
    /** 
    * Tests put() of MyHashtableSC.
    */
    @Test
    public void testPutSC() {
        MyHashtableSC<String,Integer> test = new MyHashtableSC<>();

        assertThrows(NullPointerException.class,
                () -> test.put(null,1));
        assertThrows(NullPointerException.class,
                () -> test.put("one",null));

        assertEquals(null,test.put("one",1));
        assertEquals(1,test.size);
        assertEquals(1,test.data["one".hashCode() % 11].getValue().intValue());
        assertEquals("one",test.data["one".hashCode() % 11].getKey());

        assertEquals(1,test.put("one",2).intValue());
        assertEquals(1,test.size);
        assertEquals(2,test.data["one".hashCode() % 11].getValue().intValue());
        assertEquals("one",test.data["one".hashCode() % 11].getKey());

        MyHashtableSC<String,Integer> test2 = new MyHashtableSC<>();
        test2.data["one".hashCode() % 11] = new HashEntry<>("ten",10);
        test2.size = 1;

        assertEquals(null,test2.put("one",1));
        assertEquals(2,test2.size);
        assertEquals(10,test2.data["one".hashCode() % 11]
                .getValue().intValue());
        assertEquals("ten",test2.data["one".hashCode() % 11]
                .getKey());
        assertEquals(1,test2.data["one".hashCode() % 11]
                .getNext().getValue().intValue());
        assertEquals("one",test2.data["one".hashCode() % 11]
                .getNext().getKey());

        assertEquals(1,test2.put("one",2).intValue());
        assertEquals(2,test2.size);
        assertEquals(2,test2.data["one".hashCode() % 11]
                .getNext().getValue().intValue());
        assertEquals("one",test2.data["one".hashCode() % 11]
                .getNext().getKey());
        assertEquals(10,test2.data["one".hashCode() % 11]
                .getValue().intValue());
        assertEquals("ten",test2.data["one".hashCode() % 11].getKey());

		MyHashtableSC<String,Integer> test3 = 
                new MyHashtableSC<>(4,0.7);

		assertEquals(null,test3.put("one",1));
		assertEquals(1,test3.size);
		assertEquals(1,test3.data["one".hashCode() % 4]
                .getValue().intValue());
        assertEquals("one",test3.data["one".hashCode() % 4].getKey());
		assertEquals(4,test3.data.length);

		assertEquals(null,test3.put("two",2));
		assertEquals(2,test3.size);
		assertEquals(2,test3.data["two".hashCode() % 4]
                .getValue().intValue());
        assertEquals("two",test3.data["two".hashCode() % 4].getKey());
		assertEquals(4,test3.data.length);

		assertEquals(null,test3.put("three",3));
		assertEquals(3,test3.size);
		assertEquals(3,test3.data["three".hashCode() % 9]
                .getValue().intValue());
        assertEquals("three",test3.data["three".hashCode() % 9].getKey());
		assertEquals(9,test3.data.length);

        MyHashtableSC<String,Integer> test4 = new MyHashtableSC<>(0);
        assertEquals(0,test4.data.length);
        assertEquals(null,test4.put("1",1));
        assertEquals(1,test4.size);
        assertEquals(1,test4.data.length);
    }
    /** 
    * Tests containsKey() of MyHashtableSC.
    */
    @Test
    public void testContainsKeySC() {
        MyHashtableSC<String,Integer> test2 = new MyHashtableSC<>();
        test2.data["one".hashCode() % 11] = new HashEntry<>("one", 1);
        test2.data["two".hashCode() % 11] = new HashEntry<>("two", 2);
		test2.data["two".hashCode() % 11]
                .setNext(new HashEntry<>("eight", 8));
		test2.data["five".hashCode() % 11] = new HashEntry<>("five", 5);
        test2.data[10] = new HashEntry<>("nine", 5);
        test2.size = 6;

        assertThrows(NullPointerException.class,
                () -> test2.containsKey(null));
        assertEquals(true,test2.containsKey("two"));
        assertEquals(true,test2.containsKey("one"));
        assertEquals(true,test2.containsKey("eight"));
        assertEquals(false,test2.containsKey("ten"));
        assertEquals(false,test2.containsKey("nine"));
        assertEquals(6,test2.size);

        MyHashtableSC<String,Integer> test = new MyHashtableSC<>(0);
        assertEquals(false,test.containsKey("1"));
    }
    /** 
    * Tests containsValue() of MyHashtableSC.
    */
    @Test
    public void testContainsValueSC() {
        MyHashtableSC<String,Integer> test2 = new MyHashtableSC<>();
        test2.data["one".hashCode() % 11] = new HashEntry<>("one", 1);
        test2.data["two".hashCode() % 11] = new HashEntry<>("two", 1);
		test2.data["two".hashCode() % 11]
                .setNext(new HashEntry<>("eight", 8));
		test2.data["five".hashCode() % 11] = new HashEntry<>("five", 5);
        test2.data[10] = new HashEntry<>("nine", 6);
        test2.size = 6;

        assertThrows(NullPointerException.class,
                () -> test2.containsValue(null));
        assertEquals(true,test2.containsValue(1));
        assertEquals(false,test2.containsValue(2));
        assertEquals(true,test2.containsValue(8));
        assertEquals(false,test2.containsValue(10));
        assertEquals(false,test2.containsValue(12));
        assertEquals(true,test2.containsValue(5));
        assertEquals(true,test2.containsValue(6));
        assertEquals(6,test2.size);

        MyHashtableSC<String,Integer> test = new MyHashtableSC<>(0);
        assertEquals(false,test.containsValue(2));
    }
    /** 
    * Tests remove() of MyHashtableSC.
    */
    @Test
    public void testRemoveSC() {
        MyHashtableSC<String,Integer> test = new MyHashtableSC<>();

        MyHashtableSC<String,Integer> test3 = new MyHashtableSC<>(0);

        assertThrows(NullPointerException.class,
                () -> test.remove(null));

        MyHashtableSC<String,Integer> test2 = new MyHashtableSC<>();
        test2.data["one".hashCode() % 11] = new HashEntry<>("one", 1);
        test2.data["two".hashCode() % 11] = new HashEntry<>("two", 2);
		test2.data["two".hashCode() % 11]
                .setNext(new HashEntry<>("eight", 8));
		test2.data["five".hashCode() % 11] = new HashEntry<>("five", 5);
        test2.data[10] = new HashEntry<>("nine", 5);
        test2.size = 6;

        assertEquals(null, test.remove("null"));
        assertEquals(null, test3.remove("null"));
        assertEquals(0, test3.size);
        assertEquals(0, test.size);

        assertEquals(1, test2.remove("one").intValue());
        assertEquals(5, test2.size);
        assertEquals(null, test2.data["one".hashCode() % 11]);
        assertEquals(null,test2.remove("nine"));
        assertEquals(5, test2.size);
        assertEquals(8,test2.remove("eight").intValue());
        assertEquals(4, test2.size);
        assertEquals("two", 
                test2.data["two".hashCode() % 11].getKey());
        assertEquals(2, 
                test2.data["two".hashCode() % 11].getValue().intValue());
        assertEquals(null, 
                test2.data["two".hashCode() % 11].getNext());
        assertEquals(null,test2.remove("eight"));
    }
    /** 
    * Tests clear() of MyHashtableSC.
    */
    @Test
    public void testClearSC() {
        MyHashtableSC<String,Integer> test2 = new MyHashtableSC<>();
        test2.data["one".hashCode() % 11] = new HashEntry<>("one", 1);
        test2.data["two".hashCode() % 11] = new HashEntry<>("two", 2);
		test2.data["two".hashCode() % 11]
                .setNext(new HashEntry<>("eight", 8));
		test2.data["five".hashCode() % 11] = new HashEntry<>("five", 5);
        test2.data[10] = new HashEntry<>("nine", 5);
        test2.size = 6;

        MyHashtableSC<String,Integer> test = new MyHashtableSC<>();

        MyHashtableSC<String,Integer> test3 = new MyHashtableSC<>(0);

        test.clear();
        assertEquals(0,test.size);

        test3.clear();
        assertEquals(0,test3.size);
        assertEquals(0,test3.data.length);

        test2.clear();
        assertEquals(0,test2.size);
        assertEquals(11,test2.data.length);
        for (int i = 0; i < test2.data.length; i++) {
            assertEquals(null,test2.data[i]);
        }
    }
    /** 
    * Tests get() of MyHashtableSC.
    */
    @Test
    public void testGetSC() {
        MyHashtableSC<String,Integer> test2 = new MyHashtableSC<>();
        test2.data["one".hashCode() % 11] = new HashEntry<>("one", 1);
        test2.data["two".hashCode() % 11] = new HashEntry<>("two", 2);
		test2.data["two".hashCode() % 11]
                .setNext(new HashEntry<>("eight", 8));
		test2.data["five".hashCode() % 11] = new HashEntry<>("five", 5);
        test2.data[10] = new HashEntry<>("nine", 5);
        test2.size = 6;

        MyHashtableSC<String,Integer> test = new MyHashtableSC<>();

        MyHashtableSC<String,Integer> test3 = new MyHashtableSC<>(0);

        assertEquals(null,test.get("one"));
        assertEquals(null,test3.get("one"));
        assertEquals(1,test2.get("one").intValue());
        assertEquals(2,test2.get("two").intValue());
        assertEquals(8,test2.get("eight").intValue());
        assertEquals(null,test2.get("nine"));
        assertThrows(NullPointerException.class,
                () -> test.get(null));
    }
}