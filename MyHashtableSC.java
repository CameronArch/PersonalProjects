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
        
        /** 
         * Contructor to create a HashEntry.
         * 
         * @param key the key for this HashEntry
         * @param value the value for this HashEntry
         */
        public HashEntry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
        /** 
         * Method to get the key of this HashEntry. 
         * 
         * @return the key
         */
        public K getKey() {
            return key;
        }
        /** 
         * Method to get the value of this HashEntry. 
         * 
         * @return the value
         */
        public V getValue() {
            return value;
        }
        /** 
         * Method to get the entry after this HashEntry
         * if there is separate chaining. Returns null if 
         * there is no next entry.
         * 
         * @return the next entry or null
         */
        public HashEntry<K,V> getNext() {
            return next;
        }
    }
    
    /** 
    * Default contructor to create MyHashtableSC. 
    */
    public MyHashtableSC() {
        data = new Object[DEFAULT_CAPACITY];
        loadFactor = DEFAULT_LOAD_FACTOR;
        size = 0;
    }
    /** 
    * Contructor to create MyHashtableSC with a specified
    * initial capacity. The inital capacity cannot be less than 0.
    * 
    * @param initialCapacity the initial capacity for this MyHashtableSC
    */
    public MyHashtableSC(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException();
        }
        
        data = new Object[initialCapacity];
        loadFactor = DEFAULT_LOAD_FACTOR;
        size = 0;
    }
    /** 
    * Contructor to create MyHashtableSC with a specified
    * initial capacity and maximum load factor. The inital capacity 
    * cannot be less than 0 and the load factor cannot be less than
    * or equal to zero.
    * 
    * @param initialCapacity the initial capacity for this MyHashtableSC
    * @param loadFactor the maximum load factor for this MyHashtableSC
    */
    public MyHashtableSC(int initialCapacity, double loadFactor) {
        if (initialCapacity < 0 || loadFactor <= 0) {
            throw new IllegalArgumentException();
        }
        
        data = new Object[initialCapacity];
        this.loadFactor = loadFactor;
        size = 0;
    }
    
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public double getCurrentLoadFactor() {
        return size / data.length;
    }

    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }


    }

    protected void rehash() {
        Object[] newData = new Object[(data.length << 1) + 1];

        for (int i = 0; i < data.length; i++) {
            if (data[i] != null) {
                HashEntry<K,V> entry = (HashEntry<K,V>) data[i];
                newData[entry.getKey().hashCode() % newData.length] = entry;
            }
        }
    }

    
}