import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Driver class
 * Reads queries from a file and searches for each query in the BTree.
 * If debug level is 0 results are printed on the standard output stream.
 * If debug level is 1 results will also be stored to a result file.
 */
@SuppressWarnings("ConstantConditions")
public class GeneBankSearch {

    /**
     * loads arguments and creates a BTree from the btree file.
     * loads query file and searches the BTree.
     *
     * @param args command line arguments <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]
     */
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        boolean useCache = false;
        String bTreeFilename = null;
        String queryFilename = null;
        int cacheSize = 0;
        int debugLevel = 0;
        if (verifyArgs(args)) {
            useCache = args[0].equals("1");
            bTreeFilename = args[1];
            queryFilename = args[2];
            if (args.length == 4) {
                if (useCache) cacheSize = Integer.parseInt(args[3]);
                else debugLevel = Integer.parseInt(args[3]);
            }
            if (args.length == 5) {
                cacheSize = Integer.parseInt(args[3]);
                debugLevel = Integer.parseInt(args[4]);
            }
        } else {
            printUsageAndExit();
        }


        // verify file compatibility
        String[] bTreeFilenameArr = bTreeFilename.split("\\.");
        File bTreeFile = new File(bTreeFilename);
        if (!bTreeFile.exists()) {
            System.err.println("Error: Unable to locate file at: " + bTreeFile.getAbsolutePath());
            printUsageAndExit();
        }
        File queryFile = new File(queryFilename);
        if (!queryFile.exists()) {
            System.err.println("Error: Unable to locate file at: " + queryFile.getAbsolutePath());
            printUsageAndExit();
        }
        String outputFilename = null;

        Scanner scan = null;
        BufferedWriter bw = null;
        try {
            scan = new Scanner(queryFile);
            String line = scan.nextLine().trim().toLowerCase();
            // verify that sequence lengths match
            try {
                if (line.length() != Integer.parseInt(bTreeFilenameArr[bTreeFilenameArr.length - 2])) {
                    System.err.println("Error: sequence length mismatch");
                    printUsageAndExit();
                }
                outputFilename = bTreeFilenameArr[bTreeFilenameArr.length - 6] + "_" + queryFile.getName() + "_result";
            } catch (Exception e) {
                System.err.println("Error: Invalid BTree file");
                printUsageAndExit();
            }

            //cache
            BTree bTree;
            if (useCache) {
                bTree = new BTree(0, bTreeFilename, cacheSize, BTree.MODE_READ);
            } else {
                bTree = new BTree(0, bTreeFilename, BTree.MODE_READ);
            }

            // perform search on queries
            if (debugLevel == 1) bw = new BufferedWriter(new FileWriter(outputFilename));
            int frequency = search(bTree, line);
            if (frequency != 0) {
                String output = String.format("%s: %d\n", line, frequency);
                if (debugLevel == 1)bw.write(output);
                System.out.println(output);
            }
            while (scan.hasNextLine()) {
                line = scan.nextLine().trim().toLowerCase();
                frequency = search(bTree, line);
                if (frequency != 0) {
                    String output = String.format("%s: %d\n", line, frequency);
                    if (debugLevel == 1) bw.write(output);
                    System.out.println(output);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close streams
            try {
                if (scan != null) scan.close();
                if (bw != null) bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        double timeSeconds = (System.nanoTime() - startTime) / Math.pow(10, 9);
        int timeMinutes = (int) timeSeconds / 60;
        timeSeconds %= 60;
        System.out.printf("Time elapsed(m:s) %d:%f", timeMinutes, timeSeconds);
    }

    /**
     * Verify that all command line args are valid
     *
     * @param args command line args
     * @return true if all args are valid false otherwise
     */
    public static boolean verifyArgs(String[] args) {
        try {

            // Verify args length
            if (args.length > 2 && args.length < 6) {
                // verify that cache arg is 0 or 1
                if (!args[0].equals("0") && !args[0].equals("1")) {
                    throw new IllegalArgumentException("Error: Invalid input for cache selection");
                }
                boolean useCache = args[0].equals("1");

                // Verify that any optional args are integers and in the correct range
                if (args.length == 4) {
                    try {
                        int arg3 = Integer.parseInt(args[3]);
                        if (useCache) {
                            if (arg3 < 2) {
                                throw new IllegalArgumentException("Error: Invalid input for cache size");
                            }
                        } else {
                            if (arg3 < 0 || arg3 > 1) {
                                throw new IllegalArgumentException("Error: Invalid input for debug level");
                            }
                        }
                    } catch (NumberFormatException e) {
                        if (useCache) throw new IllegalArgumentException("Error: Invalid input for cache size");
                        else throw new IllegalArgumentException("Error: Invalid input for debug level");
                    }
                } else if (args.length == 5) {
                    try {
                        if (Integer.parseInt(args[3]) < 1) {
                            throw new IllegalArgumentException("Error: Invalid input for cache size");
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Error: Invalid input for cache size");
                    }
                    try {
                        if (Integer.parseInt(args[4]) < 0 || Integer.parseInt(args[4]) > 1) {
                            throw new IllegalArgumentException("Error: Invalid input for debug level");
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Error: Invalid input for debug level");
                    }
                }
            } else throw new IllegalArgumentException("Error: Invalid number of arguments");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Prints a usage statement and exits the program
     */
    public static void printUsageAndExit() {
        System.out.println("java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]");
        System.exit(1);
    }

    /**
     * searches the BTree for the DNA sequence and returns the frequency of the sequence
     *
     * @param bTree BTree to search
     * @param query sequence to search for
     * @return the frequency of the sequence in the BTree
     */
    public static int search(BTree bTree, String query) throws IOException {
        TreeObject o = bTree.get(DNAConversion.dnaToLong(query));
        return (o == null ? 0 : o.frequency);
    }
}
