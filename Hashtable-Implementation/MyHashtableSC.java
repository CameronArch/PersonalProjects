/*
  Name: Cameron Arch
  Email: cameronarch598@gmail.com
  Sources Used: Java Interface Documentation

  This file holds the implementation a Hashtable with separate chaining, 
  and the methods associated to them.
*/

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

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
class MyHashtableSC<K,V> extends Dictionary<K,V> {
    
    LinkedList<HashEntry>[] data;
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
    */
    protected class HashEntry {
        
        private K key;
        private V value;
        
        /** 
        * Contructor to create a HashEntry.
        * 
        * @param key the key for this HashEntry
        * @param value the value for this HashEntry
        */
        public HashEntry(K key, V value) {
            this.key = key;
            this.value = value;
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
    * list - Reference to an iterator for the Linked List nextEntry is
    * contained in.
    */
    private class HashtableEnumerator<T> implements Enumeration<T> {

        private LinkedList<HashEntry>[] data;
        private HashEntry nextEntry;
        private int index;
        private boolean keys;
        private ListIterator<HashEntry> list;
        /** 
        * Contructor to create a HashtableEnumerator.
        * 
        * @param data the underlying array for Hashtable
        * @param keys boolean value to determine if enumerator
        * will be for the keys or values
        */
        public HashtableEnumerator(LinkedList<HashEntry>[] data, boolean keys) {
            this.data = data;
            this.keys = keys;
            for (int i = 0; i < data.length; i++) {
                if (data[i] instanceof Object) {
                    this.nextEntry = data[i].get(0);
                    this.index = i;
                    this.list = data[i].listIterator(1);
                    break;
                }
                this.nextEntry = null;
                this.index = i;
                this.list = null;
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
                if (list.hasNext()) {
                    nextEntry = list.next();
                }
                else if (index == data.length - 1) {
                    nextEntry = null;
                }
                else {
                    for (int i = index + 1; i < data.length; i++) {
                        index = i;
                        if (data[i] instanceof Object) {
                            nextEntry = data[i].get(0);
                            list = data[i].listIterator(1);
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
    public MyHashtableSC() {
        data = (LinkedList<HashEntry>[]) new LinkedList[DEFAULT_CAPACITY];
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
        
        data = (LinkedList<HashEntry>[]) new LinkedList[initialCapacity];
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
        if (initialCapacity < 0 || loadFactor <= 0.0) {
            throw new IllegalArgumentException();
        }
        
        data = (LinkedList<HashEntry>[]) new LinkedList[initialCapacity];
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

        ListIterator<HashEntry> list = data[hash].listIterator(1);
        HashEntry entry = data[hash].get(0);

        while (list.hasNext() && !key.equals(entry.getKey())) {
            entry = list.next();
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

        int hash = key.hashCode() % data.length;
        if (hash < 0) {
            hash += data.length;
        }

        if (data[hash] == null) {
            return false;
        }

        ListIterator<HashEntry> list = data[hash].listIterator(1);
        HashEntry entry = data[hash].get(0);
        while (list.hasNext() && !key.equals(entry.getKey())) {
            entry = list.next();
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

        int hash = key.hashCode() % data.length;
        if (hash < 0) {
            hash += data.length;
        }

        if (data[hash] == null) {
            data[hash] = new LinkedList<>();
            data[hash].add(new HashEntry(key,value));
            size++;
            return null;
        }

        ListIterator<HashEntry> list = data[hash].listIterator(1);
        HashEntry entry = data[hash].get(0);

        while (list.hasNext() && !key.equals(entry.getKey())) {
            entry = list.next();
        }

        if (key.equals(entry.getKey())) {
            V replacedValue = entry.getValue();
            entry.setValue(value);
            return replacedValue;
        }

        list.add(new HashEntry(key,value)); 
        size++;
        return null;
    }
    /** 
    * Method to expand the capacity of the Hashtable and rehash
    * entries into larger array.
    */
    public void rehash() {
        LinkedList<HashEntry>[] newData = (LinkedList<HashEntry>[]) 
                new LinkedList[(data.length << 1) + 1];

        for (LinkedList<HashEntry> s: data) {
            if (s!= null){
                ListIterator<HashEntry> list = s.listIterator(0);
                while (list.hasNext()) {
                    HashEntry entry = list.next();
                    int hash = entry.getKey().hashCode() % newData.length;
                    if (hash < 0) {
                        hash += newData.length;
                    }

                    if (newData[hash] == null) {
                        newData[hash] = new LinkedList<>();
                        newData[hash].add(entry);
                    }

                    else {
                        newData[hash].add(entry);
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

        ListIterator<HashEntry> list = data[hash].listIterator(0);
        HashEntry entry = list.next();

        while (list.hasNext() && !key.equals(entry.getKey())) {
            entry = list.next();
        }

        if (key.equals(entry.getKey())) {
            V removedValue = entry.getValue();
            list.remove();
            size--;
            if (data[hash].size() == 0) {
                data[hash] = null;
            }
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
                ListIterator<HashEntry> list = data[i].listIterator(1);
                HashEntry entry = data[i].get(0);
                while (list.hasNext() && !value.equals(entry.getValue())) {
                    System.out.println(entry.getValue());
                    entry = list.next();
                }
                if (value.equals(entry.getValue())) {
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