import java.io.File;
import java.io.IOException;

/**
 * Represents a BTree, uses a file on the disk for storage
 * Maintains BTree property
 */
public class BTree {

    public static final int MODE_WRITE = 0;
    public static final int MODE_READ = 1;
    public static final int METADATA_SIZE = Integer.BYTES + Long.BYTES;
    private final int t; // degree/min children
    private final int m; // order/max children
    private final DiskReadWrite diskrw;
    private BTreeNode root;
    private Cache<Long, BTreeNode> cache;
    private boolean useCache;

    /**
     * Creates a new BTree (without cache), can either create an empty tree or load a tree from a file.
     * Initializes a DiskReadWrite object for disk access.
     *
     * @param degree   degree of BTree, i.e. minimum number of child nodes
     * @param fileName name of file where BTree will be stored
     * @param mode     0 for creating a new tree, 1 for loading from an existing file
     */
    public BTree(int degree, String fileName, int mode) throws IOException {
        useCache = false;
        diskrw = new DiskReadWrite(new File(fileName), METADATA_SIZE);
        if (mode == MODE_WRITE) {
            t = degree;
            m = degree * 2;
            diskrw.writeMetadata(0, m);
        } else {
            t = diskrw.getDegree();
            m = t * 2;
            root = diskrw.readNode(diskrw.getRootAddress());
        }
    }

    /**
     * Creates a new BTree (with cache), can either create an empty tree or load a tree from a file.
     * Initializes a DiskReadWrite object for disk access.
     *
     * @param degree    degree of BTree, i.e. minimum number of child nodes
     * @param fileName  name of file where BTree will be stored
     * @param cacheSize number of objects that can be stored in cache
     * @param mode      0 for creating a new tree, 1 for loading from an existing file
     */
    public BTree(int degree, String fileName, int cacheSize, int mode) throws IOException {
        this(degree, fileName, mode);
        useCache = true;
        cache = new Cache<>(cacheSize);
    }

    /**
     * Inserts a TreeObject into the BTree while maintaining the BTree property.
     * Updates file on disk
     *
     * @param k TreeObject to insert
     */
    public void insert(TreeObject k) throws IOException {
        if (root == null) {
            root = new BTreeNode(t, true);
            root.keys[0] = k;
            root.n = 1;
            // write root to disk
            diskrw.writeNode(root);
            diskrw.setRoot(root.address);
        } else {
            BTreeNode existingNode = getContainingNode(k.substring);
            TreeObject object = getTreeObject(existingNode, k.substring);
            if (object == null) {
                if (root.n == (m - 1)) {
                    BTreeNode s = new BTreeNode(t, false);
                    s.children[0] = root.address;
                    diskrw.writeNode(s);
                    root = s;
                    diskrw.setRoot(s.address);
                    splitChild(root, 0);
                }
                insertNonFull(root, k);
            } else {
                object.incrementFrequency();
                diskrw.updateNode(existingNode);
            }
        }
    }

    /**
     * Splits a child node and updates the file on the disk
     *
     * @param nonFull   node containing child to be split
     * @param fullChild index of node to be split
     */
    private void splitChild(BTreeNode nonFull, int fullChild) throws IOException {
        BTreeNode fullNode = getNode(nonFull.children[fullChild]);

        // Create a new node
        BTreeNode newNode = new BTreeNode(t, fullNode.leaf);
        newNode.n = t - 1;

        // Copy the last (t-1) keys of fullNode to newNode
        System.arraycopy(fullNode.keys, t, newNode.keys, 0, t - 1);

        // Copy the last t children of fullNode to newNode
        if (!fullNode.leaf) {
            System.arraycopy(fullNode.children, t, newNode.children, 0, t);
        }

        // write newNode
        diskrw.writeNode(newNode);

        // update fullNode.n
        fullNode.n = t - 1;

        // update fullNode on disk
        diskrw.updateNode(fullNode);

        // add newNode to parent
        System.arraycopy(nonFull.children, fullChild + 1, nonFull.children, fullChild + 2, nonFull.n - fullChild);
        nonFull.children[fullChild + 1] = newNode.address;

        // add middle key of fullNode to parent
        System.arraycopy(nonFull.keys, fullChild, nonFull.keys, fullChild + 1, nonFull.n - fullChild);
        nonFull.keys[fullChild] = fullNode.keys[t - 1];

        // update nonFull.n
        nonFull.n++;

        // update nonFull on disk
        diskrw.updateNode(nonFull);
    }

    /**
     * Inserts a TreeObject into the proper leaf node of the BTree.
     * Uses a recursive strategy to split nodes as necessary.
     *
     * @param nonFull non-full node
     * @param key     TreeObject to be added
     */
    private void insertNonFull(BTreeNode nonFull, TreeObject key) throws IOException {

        // i = index to insert key, starts at end of node
        int i = nonFull.n - 1;

        // If this is a leaf node, insert
        if (nonFull.leaf) {
            // shift keys forward until the location to insert new key is found
            while (i >= 0 && nonFull.keys[i].compareTo(key) > 0) {
                nonFull.keys[i + 1] = nonFull.keys[i];
                i--;
            }
            // Insert the new key at found location
            nonFull.keys[i + 1] = key;
            nonFull.n = nonFull.n + 1;

            // update disk
            diskrw.updateNode(nonFull);

            // if this is not a leaf node, continue searching
        } else {
            // Find the correct child
            while (i >= 0 && nonFull.keys[i].compareTo(key) > 0) i--;
            BTreeNode child = getNode(nonFull.children[i + 1]);

            // if child is full, split it
            if (child.n == m - 1) {
                splitChild(nonFull, i + 1);

                // go to next child if necessary
                if (nonFull.keys[i + 1].compareTo(key) < 0) {
                    child = getNode(nonFull.children[i + 2]);
                } else {
                    child = getNode(nonFull.children[i + 1]);
                }
            }
            // calls the method recursively until a leaf node is found
            insertNonFull(child, key);
        }

    }

    /**
     * @param key long representation of DNA string to search for
     * @return the TreeObject that contains the target long value, null if not in tree
     */
    public TreeObject get(long key) throws IOException {
        return getTreeObject(getContainingNode(key), key);
    }


    /**
     * Finds and returns the BTreeNode containing the target key value
     *
     * @param key the key (sequence) to search for
     * @return the BTreeNode containing the key, null if not found
     */
    private BTreeNode getContainingNode(long key) throws IOException {
        BTreeNode node = root;
        while (node != null) {
            int i = 0;
            for (; i < node.n; i++) {
                long k = node.keys[i].substring;
                if (key == k) return node;
                if (key < k) {
                    break;
                }
            }
            node = getNode(node.children[i]);
        }
        return null;
    }

    /**
     * returns the TreeObject from the node if it exists
     *
     * @param node node to search in
     * @param key  value to search for
     * @return the TreeObject in the node which contains the key
     */
    private TreeObject getTreeObject(BTreeNode node, long key) {
        if (node == null) return null;
        for (int i = 0; i < node.n; i++) {
            if (node.keys[i].substring == key) return node.keys[i];
        }
        return null;
    }

    /**
     * @param filename       name/location of file to save the dump file at
     * @param sequenceLength length of DNA sequence, important for converting from long to string
     */
    public void dump(String filename, int sequenceLength) {
        diskrw.dump(filename, sequenceLength);
    }

    /**
     * Gets the node at the given address.
     * If cache is enabled, the cache will be searched before the file on the disk.
     *
     * @param address address of node to retrieve
     * @return the node at the given address
     */
    private BTreeNode getNode(long address) throws IOException {
        if (useCache) {
            BTreeNode node = cache.getObject(address);
            if (node == null) {
                node = diskrw.readNode(address);
                cache.add(address, node);
                return node;
            }
            return node;
        } else {
            return diskrw.readNode(address);
        }
    }
}
 