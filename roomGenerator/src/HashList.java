import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Random;

public class HashList<T> {
    private final ArrayList<T> list = new ArrayList<>();
    private final LinkedHashSet<T> set = new LinkedHashSet<>();

    public boolean add(T item) {
        if (set.add(item)) {
            return list.add(item);
        }
        return false;
    }

    public T remove(int index) {
        T item = list.remove(index);
        set.remove(item);
        return item;
    }
    
    public T removeFast(int index) {
        int last = list.size() - 1;
        T item = list.get(index);
        T lastItem = list.get(last);

        list.set(index, lastItem);
        list.remove(last);

        set.remove(item);
        return item;
    }

    public T get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public void clear() {
        list.clear();
        set.clear();
    }

    public void shuffle(Random rng) {
        Collections.shuffle(list, rng);
    }
}
