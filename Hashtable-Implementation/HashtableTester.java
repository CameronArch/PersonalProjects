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

    @Test
    public void testContructorSC() {
        MyHashtableSC<String,Integer> test = new MyHashtableSC<>();
        
        assertEquals(0,test.size);
        assertEquals(11,test.data.length);
        assertEquals(0.75,test.loadFactor,0.0000001);

        MyHashtableSC<String,Integer> test2 = new MyHashtableSC<>(5);

        assertEquals(0,test2.size);
        assertEquals(5,test2.data.length);
        assertEquals(0.75,test2.loadFactor,0.0000001);
        assertThrows(IllegalArgumentException.class,
                () -> new MyHashtableSC<>(-1));

        MyHashtableSC<String,Integer> test3 = new MyHashtableSC<>(0,0.5);

        assertEquals(0,test3.size);
        assertEquals(0,test3.data.length);
        assertEquals(0.5,test3.loadFactor,0.0000001);
        assertThrows(IllegalArgumentException.class,
                () -> new MyHashtableSC<>(1,0));
        assertThrows(IllegalArgumentException.class,
                () -> new MyHashtableSC<>(-1,1));
    }

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
		test2.data["two".hashCode() % 11].setNext(new HashEntry<>("eight", 8));
		test2.data["five".hashCode() % 11] = new HashEntry<>("five", 5);
		test2.data[10] = new HashEntry<>("five", 15);
        test2.size = 6;

        test2.rehash();

        assertEquals(23,test2.data.length);
        assertEquals(6,test2.size());
        assertEquals("one",test2.data["one".hashCode() % 23].getKey());
        assertEquals(1,test2.data["one".hashCode() % 23].getValue().intValue());
        assertEquals("two",test2.data["two".hashCode() % 23].getKey());
        assertEquals(2,test2.data["two".hashCode() % 23].getValue().intValue());
        assertEquals("ten",test2.data["ten".hashCode() % 23].getKey());
        assertEquals(10,test2.data["ten".hashCode() % 23].getValue().intValue());
		assertEquals("two",test2.data["eight".hashCode() % 23].getKey());
        assertEquals(2,test2.data["eight".hashCode() % 23].getValue().intValue());
		assertEquals("eight",test2.data["eight".hashCode() % 23].getNext().getKey());
        assertEquals(8,test2.data["eight".hashCode() % 23].getNext().getValue().intValue());
		assertEquals("five",test2.data["five".hashCode() % 23].getKey());
        assertEquals(5,test2.data["five".hashCode() % 23].getValue().intValue());
		assertEquals("five",test2.data["five".hashCode() % 23].getNext().getKey());
        assertEquals(15,test2.data["five".hashCode() % 23].getNext().getValue().intValue());
		
		
    }

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
        assertEquals(10,test2.data["one".hashCode() % 11].getValue().intValue());
        assertEquals("ten",test2.data["one".hashCode() % 11].getKey());
        assertEquals(1,test2.data["one".hashCode() % 11].getNext().getValue().intValue());
        assertEquals("one",test2.data["one".hashCode() % 11].getNext().getKey());

        assertEquals(1,test2.put("one",2).intValue());
        assertEquals(2,test2.size);
        assertEquals(2,test2.data["one".hashCode() % 11].getNext().getValue().intValue());
        assertEquals("one",test2.data["one".hashCode() % 11].getNext().getKey());
        assertEquals(10,test2.data["one".hashCode() % 11].getValue().intValue());
        assertEquals("ten",test2.data["one".hashCode() % 11].getKey());

    }
}

