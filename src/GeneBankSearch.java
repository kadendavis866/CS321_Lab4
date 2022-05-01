import java.io.IOException;

public class GeneBankSearch {
    public static void main(String[] args) {
        try {
            BTree bTree = new BTree(0, "test3.gbk.btree.data.6.102", BTree.MODE_READ);
            String searchString = "aaaaat";
            TreeObject o = bTree.get(DNAConversion.dnaToLong(searchString));
            System.out.println("frequency of searchString: " + (o == null ? 0 : bTree.get(DNAConversion.dnaToLong(searchString)).frequency));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
