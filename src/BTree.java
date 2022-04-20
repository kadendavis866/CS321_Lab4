public class BTree {

    private BTreeNode root;
    private int t;

    public BTree() {
        root = allocateNode();
    }

    public BTreeNode allocateNode() {//To be changed
        return new BTreeNode(t, true);
    }

    public void insert(TreeObject k) {
        BTreeNode r = root;
        if (r.n == (2 * t - 1)) {
            BTreeNode s = allocateNode();
            root = s;
            s.leaf = false;
            s.n = 0;
            s.children[0] = r;
            splitChild(s, 1);
            insertNonFull(s, k);
        } else {
            insertNonFull(r, k);
        }
    }

    public void splitChild(BTreeNode nonFull, int fullChild) {
    }

    public void insertNonFull(BTreeNode nonFull, TreeObject key) {
    }
}
