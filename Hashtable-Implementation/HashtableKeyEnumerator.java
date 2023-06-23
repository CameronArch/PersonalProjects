
import java.util.Enumeration;

public class HashtableKeyEnumerator<K,V> implements Enumeration<K> {

    private HashEntry<K,V>[] data;
    private HashEntry<K,V> nextEntry;
    private int index;

    public HashtableKeyEnumerator(HashEntry<K,V>[] data) {
        this.data = data;
        this.nextEntry = null;
        this.index = 0;
    }

    public boolean hasMoreElements() {
        while (nextEntry == null && index < data.length) {
            nextEntry = data[index++];
        }
        return nextEntry != null;
    }

    
    public K nextElement() {
        if (hasMoreElements()) {
            HashEntry<K,V> currEntry = nextEntry;
            nextEntry = ;
        }
    }
}