/*
  Name: Cameron Arch
  Email: cameronarch598@gmail.com
  Sources Used: Java Interface Documentation

  This file holds the implementation for an entry node in a Hashtable, 
  and the methods associated to them.
*/

/** 
* A HashEntry class for implementing an entry in a Hashtable and
* associated methods for an entry. 
* 
* 
* Instance variables:
* key - Reference to the key of entry.
* value - Reference to the value of entry.
* next - Reference to next entry in Linked List for separate chaining.
*/
public class HashEntry<K,V> {
        
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