public class TreeObject implements Comparable<TreeObject> {

    public static final int DISK_SIZE = Long.BYTES + Integer.BYTES;

    protected final long substring;
    protected int frequency;

    public TreeObject(long substring) {//Passed in substring
        frequency = 1;
        this.substring = substring;
    }

    public TreeObject(long substring, int frequency) {
        this.substring = substring;
        this.frequency = frequency;
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

    public void incrementFrequency() {
        frequency++;
    }
}
