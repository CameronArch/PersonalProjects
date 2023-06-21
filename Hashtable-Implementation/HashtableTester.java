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
    public void contructorSCTest() {
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
}