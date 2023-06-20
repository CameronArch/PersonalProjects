/*
  Name: Cameron Arch
  Email: cameronarch598@gmail.com
  Sources Used: Java Interface Documentation

  This file holds the implementation for an entry node in a Hashtable,
  a Hashtable with separate chaining, and the 
  methods associated to them.
*/

/** 
 * A MyHashtableSC class for implementing a Hashtable with 
 * separate chaining and associated methods for a Hashtable. 
 * 
 * 
 * Instance variables:
 * data - Reference to underlying data structure for Hashtable.
 * size - Reference to amount of elements in Hashtable.
 * loadFactor - Reference to maximum load factor for Hashtable before resize.
*/
class MyHashtableSC<K,V> {
    Object[] data;
    int size;
    double loadFactor;
    
    private static final int DEFAULT_CAPACITY = 11;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    /** 
    * A HashEntry class for implementing an entry in MyHashtable and
    * associated methods for an entry. 
    * 
    * 
    * Instance variables:
    * key - Reference to the key of entry.
    * value - Reference to the value of entry.
    * next - Reference to next entry in Linked List for separate chaining.
    */
    private class HashEntry<K,V> {
        private K key;
        private V value;
        private HashEntry<K,V> next;

        public HashEntry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public HashEntry<K,V> getNext() {
            return next;
        }
    }

    public MyHashtableSC() {
        data = new Object[DEFAULT_CAPACITY];
        loadFactor = DEFAULT_LOAD_FACTOR;
        size = 0;
    }



}