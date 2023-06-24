/*
  Name: Cameron Arch
  Email: cameronarch598@gmail.com
  Sources Used: Java Interface Documentation

  This file holds the implementation a Hashtable with linear probing, 
  and the methods associated to them.
*/

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.NoSuchElementException;

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
class MyHashtableLP<K,V> extends Dictionary<K,V> {
    
    HashEntry[] data;
    int size;
    double loadFactor;
    
    private static final int DEFAULT_CAPACITY = 11;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    /** 
    * A HashEntry class for implementing an entry in a Hashtable and
    * associated methods for an entry. 
    * 
    * 
    * Instance variables:
    * key - Reference to the key of entry.
    * value - Reference to the value of entry.
    * removed - Reference to if entry has been removed.
    */
    protected class HashEntry {
        
        private K key;
        private V value;
        private boolean removed;
        
        /** 
        * Contructor to create a HashEntry.
        * 
        * @param key the key for this HashEntry
        * @param value the value for this HashEntry
        */
        public HashEntry(K key, V value) {
            this.key = key;
            this.value = value;
            this.removed = false;
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
        * Method to set a new key for this HashEntry.
        * 
        * @param value the new key to replace previous key 
        */
        public void setKey(K key) {
            this.key = key;
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
        * Method to see if this HashEntry is removed. 
        * 
        * @return the state of the entry
        */
        public boolean getState() {
            return removed;
        }
        /** 
        * Method to set this HashEntry as removed or not.
        * 
        * @param removed the new state of this HashEntry 
        */
        public void setState(boolean removed) {
            this.removed = removed;
        }
    }
    /** 
    * A HashtableEnumerator class for implementing an enumeration of
    * the keys and values in a Hashtable. 
    * 
    * 
    * Instance variables:
    * data - Reference to Hashtable data.
    * nextEntry - Reference to the next entry in Hashtable.
    * index - Reference to the index in data where nextEntry is located.
    * keys - Determines whether to create an enumeration of keys or values.
    */
    private class HashtableEnumerator<T> implements Enumeration<T> {

        private HashEntry[] data;
        private HashEntry nextEntry;
        private int index;
        private boolean keys;

        /** 
        * Contructor to create a HashtableEnumerator.
        * 
        * @param data the underlying array for Hashtable
        * @param keys boolean value to determine if enumerator
        * will be for the keys or values
        */
        public HashtableEnumerator(HashEntry[] data, boolean keys) {
            this.data = data;
            this.keys = keys;
            for (int i = 0; i < data.length; i++) {
                if (data[i] != null && !data[i].getState()) {
                    this.nextEntry = data[i];
                    this.index = i;
                    break;
                }
                this.nextEntry = null;
                this.index = i;
            } 
        }
        /** 
        * Method to determine if there are more elements in HashtableEnumerator.
        * 
        * @return true if there are more elements, false otherwise
        */
        public boolean hasMoreElements() {
            return nextEntry != null;
        }
        /** 
        * Method to get element in HashtableEnumerator.
        * 
        * @return the next element or throws NoSuchElementException if there 
        * are no more elements
        */
        public T nextElement() {
            if (hasMoreElements()) {
                HashEntry currEntry = nextEntry;
                if (index == data.length - 1) {
                    nextEntry = null;
                }
                else {
                    for (int i = index + 1; i < data.length; i++) {
                        index = i;
                        if (data[i] != null && !data[i].getState()) {
                            nextEntry = data[i];
                            break;
                        }
                        nextEntry = null;
                    }
                }
                return keys ? (T) currEntry.getKey() : (T) currEntry.getValue();
            }
            throw new NoSuchElementException();
        }
        
    }
    /** 
    * Default contructor to create MyHashtableSC. 
    */
    public MyHashtableLP() {
        data = (HashEntry[]) new MyHashtableLP.HashEntry[DEFAULT_CAPACITY];
        loadFactor = DEFAULT_LOAD_FACTOR;
        size = 0;
    }
    /** 
    * Contructor to create MyHashtableSC with a specified
    * initial capacity. The inital capacity cannot be less than 0.
    * 
    * @param initialCapacity the initial capacity for this MyHashtableSC
    */
    public MyHashtableLP(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException();
        }
        
        data = (HashEntry[]) new MyHashtableLP.HashEntry[initialCapacity];
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
    public MyHashtableLP(int initialCapacity, double loadFactor) {
        if (initialCapacity < 0 || loadFactor <= 0.0 || loadFactor > 1.0) {
            throw new IllegalArgumentException();
        }
        
        data = (HashEntry[]) new MyHashtableLP.HashEntry[initialCapacity];
        this.loadFactor = loadFactor;
        size = 0;
    }
    /**
    * Method to create a HashEntry object.
    * 
    * @param key the key for the entry
    * @param value the value for the entry
    * 
    * @return a HashEntry object
    */
    public HashEntry entry(K key, V value) {
        return new HashEntry(key, value);
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
    public V get(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (data.length == 0) {
            return null;
        }
        
        int hash = key.hashCode() % data.length;
        if (hash < 0) {
            hash += data.length;
        }
        
        if (data[hash] == null) {
            return null;
        }

        HashEntry entry = data[hash];
        int index = hash;
        int count = 0;
        while (count < data.length && entry != null) {
            if (key.equals(entry.getKey()) && !entry.getState()) {
                return entry.getValue();
            }
            index = (index + 1) % data.length;
            entry = data[index];
            count++;
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

        int hash = key.hashCode() % data.length;
        if (hash < 0) {
            hash += data.length;
        }

        if (data[hash] == null) {
            return false;
        }

        HashEntry entry = data[hash];
        int index = hash;
        int count = 0;
        while (count < data.length && entry != null) {
            if (key.equals(entry.getKey()) && !entry.getState()) {
                return true;
            }
            
            index = (index + 1) % data.length;
            entry = data[index];
            count++;
        }
        
        return false;
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

        int hash = key.hashCode() % data.length;
        if (hash < 0) {
            hash += data.length;
        }

        if (data[hash] == null) {
            data[hash] = new HashEntry(key,value);
            size++;
            return null;
        }

        HashEntry entry = data[hash];
        int index = hash;
        int count = 0;
        while (count < data.length && entry != null) {
            if (key.equals(entry.getKey()) && !entry.getState()) {
                V replacedValue = entry.getValue();
                entry.setValue(value);
                return replacedValue;
            }
            index = (index + 1) % data.length;
            entry = data[index];
            count++;
        }

        for (int i = hash; i < data.length + hash; i++) {
            if (i >= data.length) {
                i = i % data.length;
            }
            if (data[i] == null) {
                break;
            }
            if (data[i].getState()) {
                data[i].setKey(key);
                data[i].setValue(value);
                data[i].setState(false);
                size++;
                return null;
            }
        }
        
        data[index] = new HashEntry(key,value);
        size++;
        return null;
    }
    /** 
    * Method to expand the capacity of the Hashtable and rehash
    * entries into larger array.
    */
    public void rehash() {
        HashEntry[] newData = (HashEntry[]) 
                new MyHashtableLP.HashEntry[(data.length << 1) + 1];
        for (HashEntry s: data) {
            if (s != null && !s.getState()){
                int hash = s.getKey().hashCode() % newData.length;
                if (hash < 0) {
                    hash += newData.length;
                }

                if (newData[hash] == null) {
                    newData[hash] = s;
                }

                else {
                    for (int i = hash + 1; i < newData.length + hash; i++) {
                        if (i >= newData.length) {
                            i = i % newData.length;
                        }
                        if (newData[i] == null) {
                            newData[i] = s;
                            break;
                        }
                    }
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
    public V remove(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (data.length == 0) {
            return null;
        }

        int hash = key.hashCode() % data.length;
        if (hash < 0) {
            hash += data.length;
        }

        if (data[hash] == null) {
            return null;
        }

        HashEntry entry = data[hash];
        int index = hash;
        int count = 0;
        while (count < data.length && entry != null) {
            if (key.equals(entry.getKey()) && !entry.getState()) {
                V removedValue = entry.getValue();
                entry.setState(true);
                size--;
                return removedValue;
            }
            index = (index + 1) % data.length;
            entry = data[index];
            count++;
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
                if (value.equals(data[i].getValue())) {
                    return true;
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
                if (data[i] instanceof Object) {
                    data[i] = null;
                }
            }
            size = 0;
        }
    }
    /** 
    * Method to get an enumeration of the keys in Hashtable.
    *
    * @return an enumeration of the keys
    */
    public Enumeration<K> keys() {
        return (Enumeration<K>) new HashtableEnumerator(data,true);
    }
    /** 
    * Method to get an enumeration of the values in Hashtable.
    *
    * @return an enumeration of the values
    */
    public Enumeration<V> elements() {
        return (Enumeration<V>) new HashtableEnumerator(data,false);
    }
}