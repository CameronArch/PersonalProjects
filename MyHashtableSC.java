

class MyHashtableSC<K,V> {
    Object[] data;
    int size;
    double loadFactor;
    
    private static final int DEFAULT_CAPACITY = 11;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    private class HashEntry<k,V> {

    }

    public MyHashtableSC() {
        data = new Object[DEFAULT_CAPACITY];
        loadFactor = DEFAULT_LOAD_FACTOR;
        size = 0;
    }



}