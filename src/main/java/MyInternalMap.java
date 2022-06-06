import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MyInternalMap<K, V> implements Map<K, V> {

    //https://stackoverflow.com/questions/16266459/implementing-a-remove-method-in-a-java-hashmap

    private final int initialCapacity;
    private MyMapEntry<K, V>[] mapEntries;
    private int size;

    public MyInternalMap() {
        this(16);
    }

    public MyInternalMap(int initialCapacity) {
        this.initialCapacity = initialCapacity;
        mapEntries = new MyMapEntry[initialCapacity];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i < mapEntries.length; i++) {
            MyMapEntry<K, V> mapEntry = mapEntries[i];
            if (containsValue(value, mapEntry)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsValue(Object value, MyMapEntry<K, V> mapEntry) {
        if (mapEntry == null) {
            return false;
        }
        if (value == mapEntry.getValue() || mapEntry.getValue().equals(value)) {
            return true;
        }
        return containsValue(value, mapEntry.next);
    }

    @Override
    public V get(Object key) {
        if (key == null) {
            return null;
        }
        MyMapEntry<K, V> entry = mapEntries[getHashCode(key)];
        while (entry != null) {
            if (key.equals(entry.key)) {
                return entry.value;
            }
            entry = entry.next;
        }
        if (entry == null) {
            return null;
        }
        return entry.value;
    }

    @Override
    public V put(K key, V value) {
        int keyBucket = getHashCode(key);

        MyMapEntry<K, V> temp = mapEntries[keyBucket];
        if (temp == null) {
            //create new head node in this bucket
            mapEntries[keyBucket] = new MyMapEntry<>(key, value);
            size++;
            return null;
        }
        while (temp != null) {
            if ((temp.key == null && key == null)
                    || (temp.key != null && temp.key.equals(key))) {
                V returnValue = temp.value;
                temp.value = value;
                return returnValue;
            }
            temp = temp.next;
        }

        //create new node in this bucket
        mapEntries[keyBucket].next = new MyMapEntry<>(key, value);
        size++;
        return null;
    }

    @Override
    public V remove(Object key) {
        /**
         * Using the same logic that you do in get(), locate the correct bucket and, within that bucket, the correct MapEntry (let's call it e). Then simply remove e from the bucket—basically,
         * this is removing a node from a single-linked list. If e is the first element in the bucket, set the corresponding element of Hash to e.next;
         * otherwise set the next field of the element just before e to e.next. Note that you need one more variable (updated as you're finding e) to keep track of the previous entry in the bucket
         */
        int keyBucket = getHashCode(key);
        MyMapEntry<K, V> temp = mapEntries[keyBucket];
        if (temp == null)
            return null;
        MyMapEntry<K, V> prev = temp;
        while (temp != null) {
            if (temp.key != null && temp.key.equals(key)) {
                V valueReturn = temp.value;
                if (prev == temp) { //first element?
                    mapEntries[keyBucket] = temp.next;
                } else {
                    prev.next = temp.next;
                }
                size--;
                return valueReturn;
            }
            prev = temp;
            temp = temp.next;
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        //TODO impl
    }

    @Override
    public void clear() {
        mapEntries = new MyMapEntry[initialCapacity];
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        //TODO impl
        return null;
    }

    @Override
    public Collection<V> values() {
        //TODO impl
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        //TODO impl
        return null;
    }

    private int getHashCode(Object key) {
        if (key == null)
            return 0;
        int bucketIndex = Math.abs(key.hashCode()) % initialCapacity;
        return bucketIndex;
    }


    class MyMapEntry<K, V> implements Map.Entry<K, V> {
        private K key;
        private V value;
        private MyMapEntry<K, V> next;

        public MyMapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }

}
