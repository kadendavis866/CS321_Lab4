public class BTree {

    private final int t;
    private BTreeNode root;

    public BTree(int t) {
        this.t = t;
    }

    public void insert(TreeObject k) {
        if (root == null) {
            root = new BTreeNode(t, true);
            root.node[0] = k;
            root.n = 1;
        } else {
            BTreeNode r = root;
            if (r.n == (2 * t - 1)) {
                BTreeNode s = new BTreeNode(t, false);
                root = s;
                s.children[0] = r;
                splitChild(s, 0);
                insertNonFull(s, k);
            } else {
                insertNonFull(r, k);
            }
        }
    }

    /**
     * @param nonFull   node containing child to be split
     * @param fullChild index of node to be split
     */
    public void splitChild(BTreeNode nonFull, int fullChild) {
        BTreeNode fullNode = nonFull.children[fullChild];

        // Create a new node
        BTreeNode newNode = new BTreeNode(t, fullNode.leaf);
        newNode.n = t - 1;

        // Copy the last (t-1) keys of fullNode to newNode
        System.arraycopy(fullNode.node, t, newNode.node, 0, t - 1);

        // Copy the last t children of fullNode to newNode
        if (!fullNode.leaf) {
            System.arraycopy(fullNode.children, t, newNode.children, 0, t);
        }

        // update fullNode.n
        fullNode.n = t - 1;

        // add newNode to parent
        System.arraycopy(nonFull.children, fullChild + 1, nonFull.children, fullChild + 2, nonFull.n - fullChild);
        nonFull.children[fullChild + 1] = newNode;

        // add middle key of fullNode to parent
        System.arraycopy(nonFull.node, fullChild, nonFull.node, fullChild + 1, nonFull.n - fullChild);
        nonFull.node[fullChild] = fullNode.node[t - 1];

        // update nonFull.n
        nonFull.n++;
    }

    public void insertNonFull(BTreeNode nonFull, TreeObject key) {

        // i = index to insert key, starts at end of node
        int i = nonFull.n - 1;

        // If this is a leaf node, insert
        if (nonFull.leaf) {
            // shift keys forward until the location to insert new key is found
            while (i >= 0 && nonFull.node[i].compareTo(key) > 0) {
                nonFull.node[i + 1] = nonFull.node[i];
                i--;
            }
            // Insert the new key at found location
            nonFull.node[i + 1] = key;
            nonFull.n = nonFull.n + 1;

            // if this is not a leaf node, continue searching
        } else {
            // Find the correct child
            while (i >= 0 && nonFull.node[i].compareTo(key) > 0) i--;

            // if child is full, split it
            if (nonFull.children[i + 1].n == 2 * t - 1) {
                splitChild(nonFull, i + 1);

                // update i if necessary
                if (nonFull.node[i + 1].compareTo(key) < 0) i++;
            }
            // calls the method recursively until a leaf node is found
            insertNonFull(nonFull.children[i + 1], key);
        }

    }
}
