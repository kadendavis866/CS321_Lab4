import java.io.File;
import java.io.IOException;

public class BTree {

    public static final int MODE_WRITE = 0;
    public static final int MODE_READ = 1;
    public static final int METADATA_SIZE = Integer.BYTES + Long.BYTES;
    private final int t; // degree/min children
    private final int m; // order/max children
    private final DiskReadWrite diskrw;
    private BTreeNode root;

    public BTree(int degree, String fileName, int mode) throws IOException {
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
                    insertNonFull(root, k);
                } else {
                    insertNonFull(root, k);
                }
            } else {
                object.incrementFrequency();
                diskrw.updateNode(existingNode);
            }
        }
    }

    /**
     * @param nonFull   node containing child to be split
     * @param fullChild index of node to be split
     */
    private void splitChild(BTreeNode nonFull, int fullChild) throws IOException {
        BTreeNode fullNode = diskrw.readNode(nonFull.children[fullChild]);

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
            BTreeNode child = diskrw.readNode(nonFull.children[i + 1]);

            // if child is full, split it
            if (child.n == m - 1) {
                splitChild(nonFull, i + 1);

                // go to next child if necessary
                if (nonFull.keys[i + 1].compareTo(key) < 0) {
                    child = diskrw.readNode(nonFull.children[i + 2]);
                } else {
                    child = diskrw.readNode(nonFull.children[i + 1]);
                }
            }
            // calls the method recursively until a leaf node is found
            insertNonFull(child, key);
        }

    }

    public TreeObject get(long key) throws IOException {
        return getTreeObject(getContainingNode(key), key);
    }


    /**
     * @param key the key to search for
     * @return the BTreeNode containing the key, null if not found
     * @throws IOException if an error occurs when reading/writing to the file
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
            node = diskrw.readNode(node.children[i]);
        }
        return null;
    }

    /**
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

    public void dump(String filename, int sequenceLength) {
        diskrw.dump(filename, sequenceLength);
    }
}
 