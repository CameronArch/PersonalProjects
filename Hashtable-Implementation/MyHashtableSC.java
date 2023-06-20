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
         * Method to set a new value for this HashEntry.
         * 
         * @param value the new value to replace previous value 
         */
        public void setValue(V value) {
            this.value = value;
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
        /** 
         * Method to set next reference for this HashEntry
         * to given HashEntry. 
         * 
         * @param entry HashEntry to be referenced by next
         */
        public void setNext(HashEntry<K,V> entry) {
            next = entry;
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
    /** 
    * Method to get size of Hashtable.
    * 
    * @return the size
    */
    public int size() {
        return size;
    }
    /** 
    * Method to tell if Hashtable is empty.
    * 
    * @return true or false depending on if Hashtable is empty
    */
    public boolean isEmpty() {
        return size == 0;
    }
    /** 
    * Method to get the value of an entry given a key. The 
    * key cannot be null. The method returns null if no such key
    * exists in Hashtable.
    * 
    * @param key the key of entry to get
    *
    * @return the value of the entry with matching key or null otherwise
    */
    public V get(K key) {
        if (key == null) {
            throw new NullPointerException();
        }

        if (data[key.hashCode() % data.length] == null) {
            return null;
        }

        HashEntry<K,V> entry = (HashEntry<K,V>) data[key.hashCode() % data.length];

        while (entry.getNext() != null && !key.equals(entry.getKey())) {
            entry = entry.getNext();
        }

        if (key.equals(entry.getKey())) {
            return entry.getValue();
        }

        return null;
    }
    /** 
    * Method to see if entry with given key exists. The 
    * key cannot be null. The method returns false if no such key
    * exists in Hashtable and true otherwise.
    * 
    * @param key the key to check for
    *
    * @return true or false depending on if key matches existing entry
    */
    public boolean containsKey(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        
        if (data[key.hashCode() % data.length] == null) {
            return false;
        }

        HashEntry<K,V> entry = (HashEntry<K,V>) data[key.hashCode() % data.length];

        while (entry.getNext() != null && !key.equals(entry.getKey())) {
            entry = entry.getNext();
        }

        return key.equals(entry.getKey());
    }
    /** 
    * Method to add an entry to the Hashtable. The arguments cannot be null.
    * Method adds a new HashEntry if none exist currently in Hashtable with 
    * the same key, otherwise the value of entry with the same key is replace
    * by given value.
    * 
    * @param key the key of the entry to add
    * @param value the value of the entry to add
    *
    * @return null if new HashEntry was created or the replaced value if there
    * was a matching key.
    */
    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }

        if ((double) (size + 1) / (double) data.length > loadFactor) {
            rehash();
        }

        if (data[key.hashCode() % data.length] == null) {
            data[key.hashCode() % data.length] = new HashEntry<>(key, value);
            size++;
            return null;
        }

        HashEntry<K,V> entry = (HashEntry<K,V>) data[key.hashCode() % data.length];

        while (entry.getNext() != null && !key.equals(entry.getKey())) {
            entry = entry.getNext();
        }

        if (key.equals(entry.getKey())) {
            V replacedValue = entry.getValue();
            entry.setValue(value);
            return replacedValue;
        }

        entry.setNext(new HashEntry<>(key,value)); 
        size++;
        return null;
    }
    /** 
    * Method to expand the capacity of the Hashtable and rehash
    * entries into larger array.
    */
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