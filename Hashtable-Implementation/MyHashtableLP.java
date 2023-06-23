/*
  Name: Cameron Arch
  Email: cameronarch598@gmail.com
  Sources Used: Java Interface Documentation

  This file holds the implementation a Hashtable with linear probing, 
  and the methods associated to them.
*/

/** 
 * A MyHashtableLP class for implementing a Hashtable with 
 * linear probing and associated methods for a Hashtable. 
 * 
 * 
 * Instance variables:
 * data - Reference to underlying data structure for Hashtable.
 * size - Reference to amount of elements in Hashtable.
 * loadFactor - Reference to maximum load factor for Hashtable before resize.
*/
class MyHashtableLP<K,V> {
    
    HashEntry<K,V>[] data;
    int size;
    double loadFactor;
    
    private static final int DEFAULT_CAPACITY = 11;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    /** 
    * Default contructor to create MyHashtableLP. 
    */
    public MyHashtableLP() {
        data = (HashEntry<K,V>[]) new HashEntry[DEFAULT_CAPACITY];
        loadFactor = DEFAULT_LOAD_FACTOR;
        size = 0;
    }
    /** 
    * Contructor to create MyHashtableLP with a specified
    * initial capacity. The inital capacity cannot be less than 0.
    * 
    * @param initialCapacity the initial capacity for this MyHashtableLP
    */
    public MyHashtableLP(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException();
        }
        
        data = (HashEntry<K,V>[]) new HashEntry[initialCapacity];
        loadFactor = DEFAULT_LOAD_FACTOR;
        size = 0;
    }
    /** 
    * Contructor to create MyHashtableLP with a specified
    * initial capacity and maximum load factor. The inital capacity 
    * cannot be less than 0 and the load factor cannot be less than
    * or equal to zero.
    * 
    * @param initialCapacity the initial capacity for this MyHashtableLP
    * @param loadFactor the maximum load factor for this MyHashtableLP
    */
    public MyHashtableLP(int initialCapacity, double loadFactor) {
        if (initialCapacity < 0 || loadFactor <= 0) {
            throw new IllegalArgumentException();
        }
        
        data = (HashEntry<K,V>[]) new HashEntry[initialCapacity];
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
        if (data.length == 0) {
            return null;
        }
        if (data[key.hashCode() % data.length] == null) {
            return null;
        }

        HashEntry<K,V> entry = data[key.hashCode() % data.length];

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
    * @return true if key matches existing entry, false otherwise
    */
    public boolean containsKey(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (data.length == 0) {
            return false;
        }
        if (data[key.hashCode() % data.length] == null) {
            return false;
        }

        HashEntry<K,V> entry = data[key.hashCode() % data.length];

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

        HashEntry<K,V> entry = data[key.hashCode() % data.length];

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
    public void rehash() {
        HashEntry<K,V>[] newData = (HashEntry<K,V>[]) 
                new HashEntry[(data.length << 1) + 1];

        for (int i = 0; i < data.length; i++) {
            HashEntry<K,V> entry = data[i];
            while (entry != null) {
                if (newData[entry.getKey().hashCode() % newData.length] 
                        == null) {
                    newData[entry.getKey().hashCode() % newData.length] = entry;
                    
                    HashEntry<K,V> temp = entry;
                    entry = entry.getNext();
                    temp.setNext(null);
                }

                else {
                    HashEntry<K,V> setEntry = 
                            newData[entry.getKey().hashCode() % newData.length];
                    while (setEntry.getNext() != null) {
                        setEntry = setEntry.getNext();
                    }

                    setEntry.setNext(entry);
                    HashEntry<K,V> temp = entry;
                    entry = entry.getNext();
                    temp.setNext(null);

                }
            }
        }
        data = newData;
    }
    /** 
    * Method to remove an entry from the Hashtable. The argument cannot be null.
    * Method returns null if no entry exists with given key.
    * 
    * @param key the key of the entry to remove
    *
    * @return the value of the removed entry or null if no entry exists 
    * with given key.
    */
    public V remove(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (data.length == 0) {
            return null;
        }
        if (data[key.hashCode() % data.length] == null) {
            return null;
        }

        HashEntry<K,V> entry = data[key.hashCode() % data.length];

        if (key.equals(entry.getKey())) {
            V removedValue = entry.getValue();
            data[key.hashCode() % data.length] = entry.getNext();
            size--;
            return removedValue;
        }

        while (entry.getNext() != null && 
                !key.equals(entry.getNext().getKey())) {
            entry = entry.getNext();
        }

        if (entry.getNext() != null) {
            V removedValue = entry.getNext().getValue();
            entry.setNext(entry.getNext().getNext());
            size--;
            return removedValue;
        }

        return null;
    }
    /** 
    * Method to see given value exists one or more times in Hashtable. 
    * The value cannot be null. The method returns false if no such value
    * exists in Hashtable and true otherwise.
    * 
    * @param value the value to check for
    *
    * @return true if one or more of value exists in the Hashtable,
    * false otherwise
    */
    public boolean containsValue(V value) {
        if (value == null) {
            throw new NullPointerException();
        }

        for (int i = 0; i < data.length; i++) {
            if (data[i] instanceof Object) {
                HashEntry<K,V> entry = data[i];
                while (entry != null) {
                    if (value.equals(entry.getValue())) {
                        return true;
                    }

                    entry = entry.getNext();
                }
            }
        }

        return false;
    }
    /** 
    * Method to clear Hashtable of all entries.
    */
    public void clear() {
        if (size > 0) {
            for (int i = 0; i < data.length; i++) {
                if (data[i] instanceof HashEntry) {
                    data[i] = null;
                }
            }
            size = 0;
        }
    }
}