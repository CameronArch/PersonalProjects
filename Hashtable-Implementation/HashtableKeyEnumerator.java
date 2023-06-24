
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class HashtableKeyEnumerator<K,V> implements Enumeration<K> {

    private HashEntry<K,V>[] data;
    private HashEntry<K,V> nextEntry;
    private int index;

    public HashtableKeyEnumerator(HashEntry<K,V>[] data) {
        this.data = data;
        for (int i = 0; i < data.length; i++) {
            this.nextEntry = data[i];
            this.index = i;
            if (data[i] instanceof HashEntry<K,V>) {
                break;
            }
        }
    }

    public boolean hasMoreElements() {
        return nextEntry != null;
    }
    
    public K nextElement() {
        if (hasMoreElements()) {
            HashEntry<K,V> currEntry = nextEntry;
            if (currEntry.getNext() != null) {
                nextEntry = currEntry.getNext();
            }
            else if (index == data.length - 1) {
                nextEntry = null;
            }
            else {
                for (int i = index + 1; i < data.length; i++) {
                    nextEntry = data[i];
                    index = i;
                    if (data[i] instanceof HashEntry<K,V>) {
                        break;
                    }
                }
            }
            return currEntry.getKey();
        }
        throw new NoSuchElementException();
    }
}