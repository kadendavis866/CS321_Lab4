/**
 * This class represents a Cache object which uses a linked-list implementation
 * to store objects. Cache contains methods necessary to find and remove objects
 * to add/move data to the top of the cache. Additionally, Cache contains
 * information about the number of hits, accesses/references, and hit rate.
 * <p>
 * Movement of items to the top of cache will be handled by the application
 * using the cache.
 * <p>
 * Note: The end of the list is the top of the cache.
 *
 * @param <K> type of key used to reference the objects in the cache
 * @param <T> type of objects to be stored
 * @version Spring2022
 */
@SuppressWarnings("unused")
public class Cache<K, T> {
    private final int capacity;
    private CacheNode<K, T> head;
    private CacheNode<K, T> tail;
    private int count;
    private int accesses;
    private int hits;

    /**
     * Initializes a new cache of given size and sets default values for instance
     * variables
     *
     * @param capacity size of the Cache
     */
    public Cache(int capacity) {
        this.capacity = capacity;
        count = 0;
        accesses = 0;
        hits = 0;
    }

    /**
     * Adds an object to the top of the cache
     *
     * @param key  key of new object
     * @param data object to be added
     */
    public void add(K key, T data) {
        add(new CacheNode<>(key, data));
    }

    /**
     * Adds a new CacheNode to the top of the cache
     *
     * @param node CacheNode to be added
     */
    private void add(CacheNode<K, T> node) {
        // check if cache is empty
        if (isEmpty()) {
            head = node;
            node.setPrevious(null);
        } else {
            // if cache is full remove head
            if (count == capacity) {
                remove(head);
            }
            // add object to top of cache
            node.setPrevious(tail);
            tail.setNext(node);
        }
        node.setNext(null);
        tail = node;
        count++;
    }

    /**
     * Removes a CacheObject from the cache
     *
     * @param key key of object to remove
     */
    public void remove(K key) {
        remove(find(key));
    }

    /**
     * Moves a CacheNode to the top of the cache
     *
     * @param node CacheNode to be moved
     */
    private void move(CacheNode<K, T> node) {
        remove(node);
        add(node);
    }

    /**
     * Retrieves an object from the cache and moves it to the top
     *
     * @param key key of object to search for
     * @return the object with the corresponding key, null if it does not exist
     */
    public T getObject(K key) {
        accesses++;
        CacheNode<K, T> node = find(key);
        if (node == null) return null;
        hits++;
        move(node);
        return node.getElement();
    }

    /**
     * @return number of cache hits
     */
    public int getHits() {
        return hits;
    }

    /**
     * @return number of cache accesses
     */
    public int getAccesses() {
        return accesses;
    }

    /**
     * @return the hit-rate (hits/accesses)
     */
    public double getHitRate() {
        // don't divide by 0
        if (accesses != 0) {
            return (double) hits / accesses;
        } else {
            return 0;
        }
    }

    /**
     * deletes all cache contents and resets the cache variables
     */
    public void clear() {
        head = tail = null;
        count = 0;
    }

    /**
     * @return true if cache is empty, false otherwise
     */
    public boolean isEmpty() {
        return (count == 0);
    }

    /**
     * Searches the cache (from top to bottom) for the target object. If found, the
     * corresponding CacheNode is returned.
     *
     * @param key key of target object
     * @return the node containing the target object from the cache, null if the
     * target object is not in the cache
     */
    private CacheNode<K, T> find(K key) {
        // search from top of cache
        CacheNode<K, T> current = tail;
        while (current != null && !current.getKey().equals(key)) {
            current = current.previous();
        }
        return current;
    }

    /**
     * Remove the node from the cache
     *
     * @param node the node to be removed
     */
    private void remove(CacheNode<K, T> node) {
        if (node != null) {
            if (node == tail) {
                // if node is the only object in the list
                if (node == head) {
                    head = tail = null;
                } else {
                    // if node is at top of cache
                    tail = tail.previous();
                    tail.setNext(null);
                }
            } else if (node == head) {
                // if node is at bottom of cache
                head = head.next();
                head.setPrevious(null);
            } else {
                // if node is in middle of cache
                node.previous().setNext(node.next());
                node.next().setPrevious(node.previous());
            }
            count--;
        }
    }

}
