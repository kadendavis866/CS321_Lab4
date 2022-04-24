public class TreeObject implements Comparable<TreeObject> {

    private final long substring;
    private int frequency;

    public TreeObject(long substring) {//Passed in substring
        frequency = 1;
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

    public long getSubstring() {
        return substring;
    }

    public int getFrequency() {
        return frequency;
    }

    public void incrementFrequency() {
        frequency++;
    }
}
