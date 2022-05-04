import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Driver class for creating a BTree and inserting data from a gbk file containing a DNA sequence
 */
public class GeneBankCreateBTree {


    private final int SEQUENCE_LENGTH;
    private final File sourceFile;
    private final BTree bTree;

    /**
     * Constructor, creates a BTree without a cache
     *
     * @param sourceFile     gbk file containing the DNA sequence
     * @param sequenceLength length of DNA sequences to store in BTree
     * @param degree         degree of BTree, i.e. minimum number of child nodes
     */
    public GeneBankCreateBTree(File sourceFile, int sequenceLength, int degree) throws IOException {
        this.sourceFile = sourceFile;
        SEQUENCE_LENGTH = sequenceLength;
        bTree = new BTree(degree, sourceFile.getName() + ".btree.data." + SEQUENCE_LENGTH + "." + degree, BTree.MODE_WRITE);
    }

    /**
     * Overloaded constructor, creates a BTree with a cache
     *
     * @param sourceFile     gbk file containing the DNA sequence
     * @param sequenceLength length of DNA sequences to store in BTree
     * @param degree         degree of BTree, i.e. minimum number of child nodes
     * @param cacheSize      number of objects that can be stored in cache
     */
    public GeneBankCreateBTree(File sourceFile, int sequenceLength, int degree, int cacheSize) throws IOException {
        this.sourceFile = sourceFile;
        SEQUENCE_LENGTH = sequenceLength;
        bTree = new BTree(degree, sourceFile.getName() + ".btree.data." + SEQUENCE_LENGTH + "." + degree, cacheSize, BTree.MODE_WRITE);
    }

    /**
     * main method
     * loads command line args, creates a BTree from the specified gbk file
     *
     * @param args <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]
     */
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        boolean useCache;
        int degree;
        String gbkFile;
        int sequenceLength;
        int cacheSize = 0;
        int debugLevel = 0;

        // read in args
        if (!verifyArgs(args)) {
            printUsageAndExit();
        }
        useCache = args[0].equals("1");
        degree = Integer.parseInt(args[1]);
        gbkFile = args[2];
        sequenceLength = Integer.parseInt(args[3]);
        if (args.length == 5) {
            if (useCache) cacheSize = Integer.parseInt(args[4]);
            else debugLevel = Integer.parseInt(args[4]);
        }
        if (args.length == 6) {
            cacheSize = Integer.parseInt(args[4]);
            debugLevel = Integer.parseInt(args[5]);
        }

        //sets optimal tree degree if degree is 0
        if (degree == 0) {
            degree = (4096 - BTreeNode.METADATA_SIZE + TreeObject.DISK_SIZE) / (2 * (TreeObject.DISK_SIZE + Long.BYTES));
        }

        GeneBankCreateBTree treeCreator;
        try {
            File sourceFile = new File(gbkFile);

            // check if file exists
            if (!(sourceFile.exists() && sourceFile.isFile())) {
                System.err.println("Unable to locate file at: " + sourceFile.getAbsolutePath());
                printUsageAndExit();
            }

            // creates BTree and prepares to insert sequences
            if (useCache) {
                treeCreator = new GeneBankCreateBTree(sourceFile, sequenceLength, degree, cacheSize);
            } else {
                treeCreator = new GeneBankCreateBTree(sourceFile, sequenceLength, degree);
            }

            // insert sequences into BTree
            treeCreator.readFile();

            if (debugLevel == 1) {
                treeCreator.createDumpFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // calculate time
        double timeSeconds = (System.nanoTime() - startTime) / Math.pow(10, 9);
        int timeMinutes = (int) timeSeconds / 60;
        timeSeconds %= 60;
        System.out.printf("Time elapsed(m:s) %d:%f", timeMinutes, timeSeconds);
    }

    /**
     * Verifies that the user-given arguments are valid.
     * Prints a message to the standard error stream if an invalid argument is encountered
     *
     * @param args command line arguments
     * @return true if arguments are valid, false otherwise
     */
    private static boolean verifyArgs(String[] args) {
        if (args.length > 3 && args.length < 7) {
            try {
                // verify cache = 0 or 1
                if (!args[0].equals("0") && !args[0].equals("1")) {
                    throw new IllegalArgumentException("Error: Invalid input for cache selection");
                }
                boolean useCache = args[0].equals("1");

                // verify degree is either 0 or is greater than 2
                try {
                    int treeDegree = Integer.parseInt(args[1]);
                    if (treeDegree != 0 && treeDegree < 2) {
                        throw new IllegalArgumentException("Error: Invalid input for tree degree");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Error: Invalid input for tree degree");
                }

                // verify sequence length is between 1 and 31 (inclusive)
                try {
                    int sequenceLength = Integer.parseInt(args[3]);
                    if (sequenceLength < 1 || sequenceLength > 31) {
                        throw new IllegalArgumentException("Error: Invalid input for sequence length");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Error: Invalid input for sequence length");
                }

                //////check for optional arguments//////

                if (args.length == 5) {
                    try {
                        if(useCache){
                            if(Integer.parseInt(args[4]) < 1){
                                throw new IllegalArgumentException("Error: Invalid input for cache size");
                            }
                        }
                        else{
                            int debugLevel = Integer.parseInt(args[4]);
                            if (debugLevel > 1 || debugLevel < 0) {
                                throw new IllegalArgumentException("Error: Invalid input for debug level");
                            }
                        }
                    } catch (NumberFormatException e) {
                        if (useCache) throw new IllegalArgumentException("Error: Invalid input for cache size");
                        else throw new IllegalArgumentException("Error: Invalid input for debug level");
                    }
                }
                if (args.length == 6) {
                    try {
                        if(Integer.parseInt(args[4]) < 1){
                            throw new IllegalArgumentException("Error: Invalid input for cache size");
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Error: Invalid input for cache size");
                    }
                    int debugLevel;
                    try {
                        debugLevel = Integer.parseInt(args[5]);
                        if (debugLevel > 1 || debugLevel < 0) {
                            throw new IllegalArgumentException("Error: Invalid input for debug level");
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Error: Invalid input for debug level");
                    }
                }
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                return false;
            }
        } else {
            System.err.println("Error: Invalid number of arguments");
            return false;
        }
        return true;
    }

    /**
     * Prints a usage statement and exits with a code of 1
     */
    private static void printUsageAndExit() {
        System.out.println("Usage: java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]");
        System.exit(1);
    }

    /**
     * Loads the gbk file.
     * Starting at ORIGIN and ending at //, DNA sequences(length set in constructor) are converted to long values and added to the BTree.
     * Any sequences containing 'n' are not added to the BTree.
     */
    public void readFile() throws IOException {
        try (Scanner scanner = new Scanner(sourceFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                //find and parse the next DNA sequence in the file
                if (line.trim().equals("ORIGIN")) {
                    StringBuilder sb = new StringBuilder();
                    line = scanner.nextLine().trim();
                    while (!line.equals("//")) {
                        String[] dnaLine = line.split(" ");
                        for (int i = 1; i < dnaLine.length; i++) { //start at 1 because the first index always contains a number
                            sb.append(dnaLine[i]);
                        }
                        line = scanner.nextLine().trim();
                    }
                    String dnaSequence = sb.toString();

                    //insert the sequence into the BTree
                    int index = 0; //ending index of the window
                    int count = 0;
                    while (count != SEQUENCE_LENGTH && index < dnaSequence.length()) {
                        //find a window to start with that doesn't contain an 'n'
                        if (dnaSequence.charAt(index) == 'n') {
                            count = 0;
                        } else {
                            count++;
                        }
                        index++;
                    }
                    String substring = dnaSequence.substring(index - SEQUENCE_LENGTH, index);
                    long binarySequence = DNAConversion.dnaToLong(substring);
                    bTree.insert(new TreeObject(binarySequence)); //insert the starting window
                    while (index < dnaSequence.length()) {
                        if (dnaSequence.charAt(index) == 'n') { //an 'n' is found
                            while (dnaSequence.charAt(index) == 'n') {
                                //find next index that is not an 'n'
                                index++;
                            }
                            count = 0;
                            while (count != SEQUENCE_LENGTH && index < dnaSequence.length()) {
                                //again find a valid window to continue with
                                if (dnaSequence.charAt(index) == 'n') {
                                    count = 0;
                                } else {
                                    count++;
                                }
                                index++;
                            }
                            if (index == dnaSequence.length()) { //no valid windows after the last 'n'
                                break;
                            }
                        } else { //not an 'n'; continue as normal
                            index++;
                        }
                        //index will always make a valid window at this point
                        substring = dnaSequence.substring(index - SEQUENCE_LENGTH, index);
                        binarySequence = DNAConversion.dnaToLong(substring);
                        bTree.insert(new TreeObject(binarySequence));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //end
    }

    /**
     * Creates a dump file of the contents of the BTree (in-order traversal)
     */
    public void createDumpFile() {
        bTree.dump(sourceFile.getName() + ".btree.dump." + SEQUENCE_LENGTH, SEQUENCE_LENGTH);
    }

}
