public class TreeObject implements Comparable<TreeObject> {

    long substring;
    int frequency;

    public TreeObject(long substring) {//Passed in substring
        frequency = 0;
        this.substring = substring;
    }

    @Override
    public int compareTo(TreeObject o) {
        long diff = substring - o.substring;
        if (diff > 0) {
            return 1;
        }
        if (diff < 0) {
            return -1;
        }
        return 0;
    }
}
