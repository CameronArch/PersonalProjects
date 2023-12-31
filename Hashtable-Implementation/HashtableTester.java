/*
  Name: Cameron Arch
  Email: cameronarch598@gmail.com
  Sources Used: Java Interface Documentation

  This file holds the test cases for the implementations of 
  MyHashtableSC and MyHashtableLP.
*/
import org.junit.*;

import static org.junit.Assert.*;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.NoSuchElementException;
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
        test2.data["one".hashCode() % 11] = new LinkedList<>(); 
        test2.data["one".hashCode() % 11].add(test2.entry("one",1));
        test2.data["one".hashCode() % 11].add(test2.entry("ten", 10));
        test2.data["two".hashCode() % 11] = new LinkedList<>();
		test2.data["two".hashCode() % 11].add(test2.entry("two", 2));
        test2.data["two".hashCode() % 11].add(test2.entry("eight", 8));
        test2.data["five".hashCode() % 11] = new LinkedList<>();
		test2.data["five".hashCode() % 11].add(test2.entry("five", 5));
        test2.data[10] = new LinkedList<>();
        test2.data[10].add(test2.entry("five", 15));
        test2.size = 6;

        test2.rehash();

        assertEquals(23,test2.data.length);
        assertEquals(6,test2.size());
        assertEquals("one",test2.data["one".hashCode() % 23]
                .get(0).getKey());
        assertEquals(1,test2.data["one".hashCode() % 23]
                .get(0).getValue().intValue());
        assertEquals("two",test2.data["two".hashCode() % 23]
                .get(0).getKey());
        assertEquals(2,test2.data["two".hashCode() % 23]
                .get(0).getValue().intValue());
        assertEquals("ten",test2.data["ten".hashCode() % 23]
                .get(0).getKey());
        assertEquals(10,test2.data["ten".hashCode() % 23]
                .get(0).getValue().intValue());
		assertEquals("two",test2.data["eight".hashCode() % 23]
                .get(0).getKey());
        assertEquals(2,test2.data["eight".hashCode() % 23]
                .get(0).getValue().intValue());
		assertEquals("eight",test2.data["eight".hashCode() % 23]
                .get(1).getKey());
        assertEquals(8,test2.data["eight".hashCode() % 23]
                .get(1).getValue().intValue());
		assertEquals("five",test2.data["five".hashCode() % 23]
                .get(0).getKey());
        assertEquals(5,test2.data["five".hashCode() % 23]
                .get(0).getValue().intValue());
		assertEquals("five",test2.data["five".hashCode() % 23]
                .get(1).getKey());
        assertEquals(15,test2.data["five".hashCode() % 23]
                .get(1).getValue().intValue());	
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
        assertEquals(1,test.data["one".hashCode() % 11]
                .get(0).getValue().intValue());
        assertEquals("one",test.data["one".hashCode() % 11].get(0).getKey());

        assertEquals(1,test.put("one",2).intValue());
        assertEquals(1,test.size);
        assertEquals(2,test.data["one".hashCode() % 11]
                .get(0).getValue().intValue());
        assertEquals("one",test.data["one".hashCode() % 11].get(0).getKey());

        MyHashtableSC<String,Integer> test2 = new MyHashtableSC<>();
        test2.data["one".hashCode() % 11] = new LinkedList<>();
        test2.data["one".hashCode() % 11].add(test2.entry("ten",10));
        test2.size = 1;

        assertEquals(null,test2.put("one",1));
        assertEquals(2,test2.size);
        assertEquals(10,test2.data["one".hashCode() % 11]
                .get(0).getValue().intValue());
        assertEquals("ten",test2.data["one".hashCode() % 11]
                .get(0).getKey());
        assertEquals(1,test2.data["one".hashCode() % 11]
                .get(1).getValue().intValue());
        assertEquals("one",test2.data["one".hashCode() % 11]
                .get(1).getKey());

        assertEquals(1,test2.put("one",2).intValue());
        assertEquals(2,test2.size);
        assertEquals(2,test2.data["one".hashCode() % 11]
                .get(1).getValue().intValue());
        assertEquals("one",test2.data["one".hashCode() % 11]
                .get(1).getKey());
        assertEquals(10,test2.data["one".hashCode() % 11]
                .get(0).getValue().intValue());
        assertEquals("ten",test2.data["one".hashCode() % 11].get(0).getKey());

		MyHashtableSC<String,Integer> test3 = 
                new MyHashtableSC<>(4,0.7);

		assertEquals(null,test3.put("one",1));
		assertEquals(1,test3.size);
		assertEquals(1,test3.data["one".hashCode() % 4]
                .get(0).getValue().intValue());
        assertEquals("one",test3.data["one".hashCode() % 4].get(0).getKey());
		assertEquals(4,test3.data.length);

		assertEquals(null,test3.put("two",2));
		assertEquals(2,test3.size);
		assertEquals(2,test3.data["two".hashCode() % 4]
                .get(0).getValue().intValue());
        assertEquals("two",test3.data["two".hashCode() % 4].get(0).getKey());
		assertEquals(4,test3.data.length);

		assertEquals(null,test3.put("three",3));
		assertEquals(3,test3.size);
		assertEquals(3,test3.data["three".hashCode() % 9]
                .get(0).getValue().intValue());
        assertEquals("three",test3.data["three".hashCode() % 9]
                .get(0).getKey());
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
        test2.data["one".hashCode() % 11] = new LinkedList<>(); 
        test2.data["one".hashCode() % 11].add(test2.entry("one",1));
        test2.data["one".hashCode() % 11].add(test2.entry("ten", 10));
        test2.data["two".hashCode() % 11] = new LinkedList<>();
		test2.data["two".hashCode() % 11].add(test2.entry("two", 2));
        test2.data["two".hashCode() % 11].add(test2.entry("eight", 8));
        test2.data["five".hashCode() % 11] = new LinkedList<>();
		test2.data["five".hashCode() % 11].add(test2.entry("five", 5));
        test2.data[10] = new LinkedList<>();
        test2.data[10].add(test2.entry("nine", 5));
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
        test2.data["one".hashCode() % 11] = new LinkedList<>(); 
        test2.data["one".hashCode() % 11].add(test2.entry("one",1));
        test2.data["two".hashCode() % 11] = new LinkedList<>();
		test2.data["two".hashCode() % 11].add(test2.entry("two", 1));
        test2.data["two".hashCode() % 11].add(test2.entry("eight", 8));
        test2.data["five".hashCode() % 11] = new LinkedList<>();
		test2.data["five".hashCode() % 11].add(test2.entry("five", 5));
        test2.data[10] = new LinkedList<>();
        test2.data[10].add(test2.entry("nine", 6));
        test2.size = 5;

        assertThrows(NullPointerException.class,
                () -> test2.containsValue(null));
        assertEquals(true,test2.containsValue(1));
        assertEquals(false,test2.containsValue(2));
        assertEquals(true,test2.containsValue(8));
        assertEquals(false,test2.containsValue(10));
        assertEquals(false,test2.containsValue(12));
        assertEquals(true,test2.containsValue(5));
        assertEquals(true,test2.containsValue(6));
        assertEquals(5,test2.size);

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
        test2.data["one".hashCode() % 11] = new LinkedList<>(); 
        test2.data["one".hashCode() % 11].add(test2.entry("one",1));
        test2.data["two".hashCode() % 11] = new LinkedList<>();
		test2.data["two".hashCode() % 11].add(test2.entry("two", 2));
        test2.data["two".hashCode() % 11].add(test2.entry("eight", 8));
        test2.data["five".hashCode() % 11] = new LinkedList<>();
		test2.data["five".hashCode() % 11].add(test2.entry("five", 5));
        test2.data[10] = new LinkedList<>();
        test2.data[10].add(test2.entry("nine", 5));
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
                test2.data["two".hashCode() % 11].get(0).getKey());
        assertEquals(2, 
                test2.data["two".hashCode() % 11].get(0).getValue().intValue());
        assertThrows(IndexOutOfBoundsException.class, 
                () -> test2.data["two".hashCode() % 11].get(1));
        assertEquals(null,test2.remove("eight"));
    }
    /** 
    * Tests clear() of MyHashtableSC.
    */
    @Test
    public void testClearSC() {
        MyHashtableSC<String,Integer> test2 = new MyHashtableSC<>();
        test2.data["one".hashCode() % 11] = new LinkedList<>(); 
        test2.data["one".hashCode() % 11].add(test2.entry("one",1));
        test2.data["one".hashCode() % 11].add(test2.entry("ten", 10));
        test2.data["two".hashCode() % 11] = new LinkedList<>();
		test2.data["two".hashCode() % 11].add(test2.entry("two", 2));
        test2.data["two".hashCode() % 11].add(test2.entry("eight", 8));
        test2.data["five".hashCode() % 11] = new LinkedList<>();
		test2.data["five".hashCode() % 11].add(test2.entry("five", 5));
        test2.data[10] = new LinkedList<>();
        test2.data[10].add(test2.entry("nine", 5));
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
        test2.data["one".hashCode() % 11] = new LinkedList<>(); 
        test2.data["one".hashCode() % 11].add(test2.entry("one",1));
        test2.data["one".hashCode() % 11].add(test2.entry("ten", 10));
        test2.data["two".hashCode() % 11] = new LinkedList<>();
		test2.data["two".hashCode() % 11].add(test2.entry("two", 2));
        test2.data["two".hashCode() % 11].add(test2.entry("eight", 8));
        test2.data["five".hashCode() % 11] = new LinkedList<>();
		test2.data["five".hashCode() % 11].add(test2.entry("five", 5));
        test2.data[10] = new LinkedList<>();
        test2.data[10].add(test2.entry("nine", 5));
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
    /** 
    * Tests keys() of MyHashtableSC.
    */
    @Test
    public void testKeysSC() {
        MyHashtableSC<String,Integer> test2 = new MyHashtableSC<>();
        test2.data["one".hashCode() % 11] = new LinkedList<>(); 
        test2.data["one".hashCode() % 11].add(test2.entry("one",1));
        test2.data["two".hashCode() % 11] = new LinkedList<>();
		test2.data["two".hashCode() % 11].add(test2.entry("two", 2));
        test2.data["two".hashCode() % 11].add(test2.entry("eight", 8));
        test2.data["five".hashCode() % 11] = new LinkedList<>();
		test2.data["five".hashCode() % 11].add(test2.entry("five", 5));
        test2.data[10] = new LinkedList<>();
        test2.data[10].add(test2.entry("nine", 5));
        test2.size = 5;

        MyHashtableSC<String,Integer> test = new MyHashtableSC<>();

        MyHashtableSC<String,Integer> test3 = new MyHashtableSC<>(0);

        Enumeration<String> l = test.keys();
        assertThrows(NoSuchElementException.class, 
                () -> l.nextElement());
        assertEquals(false, l.hasMoreElements());

        Enumeration<String> v = test3.keys();
        assertThrows(NoSuchElementException.class, 
                () -> v.nextElement());
        assertEquals(false, v.hasMoreElements());

        Enumeration<String> x = test2.keys();
        assertEquals(true, x.hasMoreElements());
		assertEquals("one", x.nextElement());
		assertEquals(true, x.hasMoreElements());
		assertEquals("two", x.nextElement());
		assertEquals(true, x.hasMoreElements());
		assertEquals("eight", x.nextElement());
		assertEquals(true, x.hasMoreElements());
		assertEquals("five", x.nextElement());
		assertEquals(true, x.hasMoreElements());
		assertEquals("nine", x.nextElement());
		assertEquals(false, x.hasMoreElements());
		assertThrows(NoSuchElementException.class, 
                () -> x.nextElement());
    }
    /** 
    * Tests elements() of MyHashtableSC.
    */
    @Test
    public void testElementsSC() {
        MyHashtableSC<String,Integer> test2 = new MyHashtableSC<>();
        test2.data["one".hashCode() % 11] = new LinkedList<>(); 
        test2.data["one".hashCode() % 11].add(test2.entry("one",1));
        test2.data["two".hashCode() % 11] = new LinkedList<>();
		test2.data["two".hashCode() % 11].add(test2.entry("two", 2));
        test2.data["two".hashCode() % 11].add(test2.entry("eight", 8));
        test2.data["five".hashCode() % 11] = new LinkedList<>();
		test2.data["five".hashCode() % 11].add(test2.entry("five", 5));
        test2.data[9] = new LinkedList<>();
        test2.data[9].add(test2.entry("nine", 9));
        test2.size = 5;

        MyHashtableSC<String,Integer> test = new MyHashtableSC<>();

        MyHashtableSC<String,Integer> test3 = new MyHashtableSC<>(0);

        Enumeration<Integer> l = test.elements();
        assertThrows(NoSuchElementException.class, 
                () -> l.nextElement());
        assertEquals(false, l.hasMoreElements());

        Enumeration<Integer> v = test3.elements();
        assertThrows(NoSuchElementException.class, 
                () -> v.nextElement());
        assertEquals(false, v.hasMoreElements());

        Enumeration<Integer> x = test2.elements();
        assertEquals(true, x.hasMoreElements());
		assertEquals(1, x.nextElement().intValue());
		assertEquals(true, x.hasMoreElements());
		assertEquals(2, x.nextElement().intValue());
		assertEquals(true, x.hasMoreElements());
		assertEquals(8, x.nextElement().intValue());
		assertEquals(true, x.hasMoreElements());
		assertEquals(5, x.nextElement().intValue());
		assertEquals(9, x.nextElement().intValue());
		assertEquals(false, x.hasMoreElements());
		assertThrows(NoSuchElementException.class, 
                () -> x.nextElement());
    }
    /** 
    * Tests contructors of MyHashtableLP.
    */
    @Test
    public void testContructorLP() {
        MyHashtableLP<String,Integer> test = new MyHashtableLP<>();
        
        assertEquals(0,test.size);
        assertEquals(11,test.data.length);
        assertEquals(0.75,test.loadFactor,0.0000001);

        MyHashtableLP<String,Integer> test2 = 
                new MyHashtableLP<>(5);

        assertEquals(0,test2.size);
        assertEquals(5,test2.data.length);
        assertEquals(0.75,test2.loadFactor,0.0000001);
        assertThrows(IllegalArgumentException.class,
                () -> new MyHashtableLP<>(-1));

        MyHashtableLP<String,Integer> test3 = 
                new MyHashtableLP<>(0,0.5);

        assertEquals(0,test3.size);
        assertEquals(0,test3.data.length);
        assertEquals(0.5,test3.loadFactor,0.0000001);
        assertThrows(IllegalArgumentException.class,
                () -> new MyHashtableLP<>(1,0));
        assertThrows(IllegalArgumentException.class,
                () -> new MyHashtableLP<>(-1,1));
		assertThrows(IllegalArgumentException.class,
                () -> new MyHashtableLP<>(0,1.01));
    }
    /** 
    * Tests rehash() of MyHashtableLP.
    */
    @Test
    public void testRehashLP() {
        MyHashtableLP<String,Integer> test = new MyHashtableLP<>();
        test.rehash();
        assertEquals(23,test.data.length);
        assertEquals(0,test.size());

        MyHashtableLP<String,Integer> test2 = new MyHashtableLP<>(); 
        test2.data["one".hashCode() % 11] = (test2.entry("one",1));
        test2.data["ten".hashCode() % 11] = test2.entry("ten", 10);
		test2.data["two".hashCode() % 11] = test2.entry("two", 2);
        test2.data[0] = (test2.entry("eight", 8));
		test2.data["five".hashCode() % 11] = test2.entry("five", 5);
        test2.data[10] = test2.entry("five", 15);
		test2.data[4] = test2.entry("zero",0);
		test2.data[4].setState(true);
        test2.size = 6;
		
        test2.rehash();
		
        assertEquals(23,test2.data.length);
        assertEquals(6,test2.size());
        assertEquals("one",test2.data["one".hashCode() % 23]
                .getKey());
        assertEquals(1,test2.data["one".hashCode() % 23]
                .getValue().intValue());
        assertEquals("eight",test2.data["two".hashCode() % 23]
                .getKey());
        assertEquals(8,test2.data["two".hashCode() % 23]
                .getValue().intValue());
        assertEquals("ten",test2.data["ten".hashCode() % 23]
                .getKey());
        assertEquals(10,test2.data["ten".hashCode() % 23]
                .getValue().intValue());
		assertEquals("two",test2.data[1]
                .getKey());
        assertEquals(2,test2.data[1]
                .getValue().intValue());
		assertEquals(null,test2.data["zero".hashCode() % 23]);
		assertEquals("five",test2.data["five".hashCode() % 23]
                .getKey());
        assertEquals(5,test2.data["five".hashCode() % 23]
                .getValue().intValue());
		assertEquals("five",test2.data[6]
                .getKey());
        assertEquals(15,test2.data[6]
                .getValue().intValue());	
    }
    /** 
    * Tests put() of MyHashtableLP.
    */
    @Test
    public void testPutLP() {
        MyHashtableLP<String,Integer> test = new MyHashtableLP<>();

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

        MyHashtableLP<String,Integer> test2 = new MyHashtableLP<>();
        test2.data["two".hashCode() % 11] = test2.entry("eight",8);
        test2.size = 1;

        assertEquals(null,test2.put("two",2));
        assertEquals(2,test2.size);
        assertEquals(8,test2.data["two".hashCode() % 11]
                .getValue().intValue());
        assertEquals("eight",test2.data["two".hashCode() % 11]
                .getKey());
        assertEquals(2,test2.data["two".hashCode() % 11 + 1]
                .getValue().intValue());
        assertEquals("two",test2.data["two".hashCode() % 11 + 1]
                .getKey());

        assertEquals(2,test2.put("two",3).intValue());
        assertEquals(2,test2.size);
        assertEquals(3,test2.data["two".hashCode() % 11 + 1]
                .getValue().intValue());
        assertEquals("two",test2.data["two".hashCode() % 11 + 1]
                .getKey());
        assertEquals(8,test2.data["two".hashCode() % 11]
                .getValue().intValue());
        assertEquals("eight",test2.data["two".hashCode() % 11].getKey());

		MyHashtableLP<String,Integer> test3 = 
                new MyHashtableLP<>(4,0.7);

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

        MyHashtableLP<String,Integer> test4 = new MyHashtableLP<>(0);
        assertEquals(0,test4.data.length);
        assertEquals(null,test4.put("1",1));
        assertEquals(1,test4.size);
        assertEquals(1,test4.data.length);

		MyHashtableLP<String,Integer> test5 = 
                new MyHashtableLP<>(3,1.0);
		test5.data["one".hashCode() % 3] = test5.entry("one",1);
        test5.data["ten".hashCode() % 3] = test5.entry("ten", 10);
		test5.data["three".hashCode() % 3] = test5.entry("three", 3);
		test5.data["three".hashCode() % 3].setState(true);
		test5.size = 2;

		assertEquals(null,test5.put("two",2));
		assertEquals(3,test5.size);
		assertEquals(3,test5.data.length);

		MyHashtableLP<String,Integer> test6 = 
                new MyHashtableLP<>(5,1.0);
		test6.data["one".hashCode() % 5] = test6.entry("one",1);
        test6.data[3] = test6.entry("ten", 10);
		test6.data[4] = test6.entry("three", 3);
		test6.data[4].setState(true);
		test6.data["three".hashCode() % 5] = test6.entry("five", 5);
		test6.size = 3;
		
		assertEquals(null,test6.put("3",20));
		assertEquals(4,test6.size);
		assertEquals(5,test6.data.length);
		assertEquals(20,test6.data[4].getValue().intValue());
		assertEquals("3",test6.data[4].getKey());
    }
    /** 
    * Tests containsKey() of MyHashtableLP.
    */
    @Test
    public void testContainsKeyLP() {
        MyHashtableLP<String,Integer> test2 = new MyHashtableLP<>(); 
        test2.data["one".hashCode() % 11] = (test2.entry("one",1));
        test2.data["ten".hashCode() % 11] = (test2.entry("ten", 10));
		test2.data["two".hashCode() % 11] = (test2.entry("two", 2));
		test2.data[10] = (test2.entry("3", 2));
		test2.data[10].setState(true);
        test2.data[0] = (test2.entry("eight", 8));
		test2.data["five".hashCode() % 11] = (test2.entry("five", 5));
        test2.data[5] = (test2.entry("nine", 5));
        test2.size = 6;
        
        assertThrows(NullPointerException.class,
                () -> test2.containsKey(null));
        assertEquals(true,test2.containsKey("two"));
        assertEquals(true,test2.containsKey("one"));
        assertEquals(true,test2.containsKey("eight"));
        assertEquals(false,test2.containsKey("tennn"));
        assertEquals(false,test2.containsKey("nine"));
        assertEquals(6,test2.size);

        MyHashtableLP<String,Integer> test = new MyHashtableLP<>(0);
        assertEquals(false,test.containsKey("1"));
    }
    /** 
    * Tests containsValue() of MyHashtableLP.
    */
    @Test
    public void testContainsValueLP() {
        MyHashtableLP<String,Integer> test2 = new MyHashtableLP<>(); 
        test2.data["one".hashCode() % 11] = (test2.entry("one",1));
        test2.data["ten".hashCode() % 11] = (test2.entry("ten", 11));
		test2.data["two".hashCode() % 11] = (test2.entry("two", 2));
		test2.data[10] = (test2.entry("3", 2));
		test2.data[10].setState(true);
        test2.data[0] = (test2.entry("eight", 8));
		test2.data["five".hashCode() % 11] = (test2.entry("five", 5));
        test2.data[5] = (test2.entry("nine", 6));
        test2.size = 6;

        assertThrows(NullPointerException.class,
                () -> test2.containsValue(null));
        assertEquals(true,test2.containsValue(1));
        assertEquals(true,test2.containsValue(2));
        assertEquals(true,test2.containsValue(8));
        assertEquals(false,test2.containsValue(10));
        assertEquals(false,test2.containsValue(12));
        assertEquals(true,test2.containsValue(5));
        assertEquals(true,test2.containsValue(6));
        assertEquals(6,test2.size);

        MyHashtableLP<String,Integer> test = new MyHashtableLP<>(0);
        assertEquals(false,test.containsValue(2));
    }
    /** 
    * Tests clear() of MyHashtableLP.
    */
    @Test
    public void testClearLP() {
        MyHashtableLP<String,Integer> test2 = new MyHashtableLP<>(); 
        test2.data["one".hashCode() % 11] = (test2.entry("one",1));
        test2.data["ten".hashCode() % 11] = (test2.entry("ten", 11));
		test2.data["two".hashCode() % 11] = (test2.entry("two", 2));
		test2.data[10] = (test2.entry("3", 2));
		test2.data[10].setState(true);
        test2.data[0] = (test2.entry("eight", 8));
		test2.data["five".hashCode() % 11] = (test2.entry("five", 5));
        test2.data[5] = (test2.entry("nine", 6));
        test2.size = 6;

        MyHashtableLP<String,Integer> test = new MyHashtableLP<>();

        MyHashtableLP<String,Integer> test3 = new MyHashtableLP<>(0);

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
    * Tests remove() of MyHashtableLP.
    */
    @Test
    public void testRemoveLP() {
        MyHashtableLP<String,Integer> test = new MyHashtableLP<>();

        MyHashtableLP<String,Integer> test3 = new MyHashtableLP<>(0);

        assertThrows(NullPointerException.class,
                () -> test.remove(null));

        MyHashtableLP<String,Integer> test2 = new MyHashtableLP<>(); 
        test2.data["one".hashCode() % 11] = (test2.entry("one",1));
        test2.data["ten".hashCode() % 11] = (test2.entry("ten", 11));
		test2.data["two".hashCode() % 11] = (test2.entry("two", 2));
		test2.data[10] = (test2.entry("3", 2));
		test2.data[10].setState(true);
        test2.data[0] = (test2.entry("eight", 8));
		test2.data["five".hashCode() % 11] = (test2.entry("five", 5));
        test2.data[5] = (test2.entry("nine", 6));
        test2.size = 6;

        assertEquals(null, test.remove("null"));
        assertEquals(null, test3.remove("null"));
        assertEquals(0, test3.size);
        assertEquals(0, test.size);

        assertEquals(1, test2.remove("one").intValue());
        assertEquals(5, test2.size);
        assertEquals(true, test2.data["one".hashCode() % 11].getState());
        assertEquals(null,test2.remove("nine"));
        assertEquals(5, test2.size);
        assertEquals(8,test2.remove("eight").intValue());
        assertEquals(4, test2.size);
		assertEquals(true, test2.data[0].getState());
        assertEquals(null,test2.remove("eight"));
		assertEquals(null,test2.remove("3"));
		assertEquals(4, test2.size);
    }
    /** 
    * Tests get() of MyHashtableLP.
    */
    @Test
    public void testGetLP() {
        MyHashtableLP<String,Integer> test2 = new MyHashtableLP<>(); 
        test2.data["one".hashCode() % 11] = (test2.entry("one",1));
        test2.data["ten".hashCode() % 11] = (test2.entry("ten", 11));
		test2.data["two".hashCode() % 11] = (test2.entry("two", 2));
		test2.data[10] = (test2.entry("3", 2));
		test2.data[10].setState(true);
        test2.data[0] = (test2.entry("eight", 8));
		test2.data["five".hashCode() % 11] = (test2.entry("five", 5));
        test2.data[5] = (test2.entry("nine", 6));
        test2.size = 6;

        MyHashtableLP<String,Integer> test = new MyHashtableLP<>();

        MyHashtableLP<String,Integer> test3 = new MyHashtableLP<>(0);

        assertEquals(null,test.get("one"));
        assertEquals(null,test3.get("one"));
        assertEquals(1,test2.get("one").intValue());
        assertEquals(2,test2.get("two").intValue());
        assertEquals(8,test2.get("eight").intValue());
        assertEquals(null,test2.get("nine"));
		assertEquals(null,test2.get("3"));
        assertThrows(NullPointerException.class,
                () -> test.get(null));
    }
    /** 
    * Tests keys() of MyHashtableLP.
    */
    @Test
    public void testKeysLP() {
        MyHashtableLP<String,Integer> test2 = new MyHashtableLP<>(); 
        test2.data["one".hashCode() % 11] = (test2.entry("one",1));
        test2.data["ten".hashCode() % 11] = (test2.entry("ten", 11));
		test2.data["two".hashCode() % 11] = (test2.entry("two", 2));
		test2.data[10] = (test2.entry("3", 2));
		test2.data[10].setState(false);
        test2.data[0] = (test2.entry("eight", 8));
		test2.data["five".hashCode() % 11] = (test2.entry("five", 5));
        test2.data[5] = (test2.entry("nine", 6));
        test2.size = 6;
		
        MyHashtableLP<String,Integer> test = new MyHashtableLP<>();

        MyHashtableLP<String,Integer> test3 = new MyHashtableLP<>(0);

        Enumeration<String> l = test.keys();
        assertThrows(NoSuchElementException.class, () -> l.nextElement());
        assertEquals(false, l.hasMoreElements());

        Enumeration<String> v = test3.keys();
        assertThrows(NoSuchElementException.class, () -> v.nextElement());
        assertEquals(false, v.hasMoreElements());

        Enumeration<String> x = test2.keys();
        assertEquals(true, x.hasMoreElements());
		assertEquals("eight", x.nextElement());
		assertEquals(true, x.hasMoreElements());
		assertEquals("nine", x.nextElement());
		assertEquals(true, x.hasMoreElements());
		assertEquals("one", x.nextElement());
		assertEquals(true, x.hasMoreElements());
		assertEquals("two", x.nextElement());
		assertEquals(true, x.hasMoreElements());
		assertEquals("five", x.nextElement());
		assertEquals(true, x.hasMoreElements());
		assertEquals("ten", x.nextElement());
		assertEquals(true, x.hasMoreElements());
		assertEquals("3", x.nextElement());
		assertEquals(false, x.hasMoreElements());
		assertThrows(NoSuchElementException.class, 
                () -> x.nextElement());
    }
    /** 
    * Tests elements() of MyHashtableLP.
    */
    @Test
    public void testElementsLP() {
        MyHashtableLP<String,Integer> test2 = new MyHashtableLP<>(); 
        test2.data["one".hashCode() % 11] = (test2.entry("one",1));
        test2.data["ten".hashCode() % 11] = (test2.entry("ten", 11));
		test2.data["two".hashCode() % 11] = (test2.entry("two", 2));
		test2.data[10] = (test2.entry("3", 3));
		test2.data[10].setState(true);
        test2.data[0] = (test2.entry("eight", 8));
		test2.data["five".hashCode() % 11] = (test2.entry("five", 5));
        test2.data[5] = (test2.entry("nine", 6));
        test2.size = 6;

        MyHashtableLP<String,Integer> test = new MyHashtableLP<>();

        MyHashtableLP<String,Integer> test3 = new MyHashtableLP<>(0);

        Enumeration<Integer> l = test.elements();
        assertThrows(NoSuchElementException.class, () -> l.nextElement());
        assertEquals(false, l.hasMoreElements());

        Enumeration<Integer> v = test3.elements();
        assertThrows(NoSuchElementException.class, () -> v.nextElement());
        assertEquals(false, v.hasMoreElements());

        Enumeration<Integer> x = test2.elements();
        assertEquals(true, x.hasMoreElements());
		assertEquals(8, x.nextElement().intValue());
		assertEquals(true, x.hasMoreElements());
		assertEquals(6, x.nextElement().intValue());
		assertEquals(true, x.hasMoreElements());
		assertEquals(1, x.nextElement().intValue());
		assertEquals(true, x.hasMoreElements());
		assertEquals(2, x.nextElement().intValue());
		assertEquals(true, x.hasMoreElements());
		assertEquals(5, x.nextElement().intValue());
		assertEquals(true, x.hasMoreElements());
		assertEquals(11, x.nextElement().intValue());
		assertEquals(false, x.hasMoreElements());
		assertThrows(NoSuchElementException.class, 
                () -> x.nextElement());
    }
}