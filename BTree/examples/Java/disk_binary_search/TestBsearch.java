

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class TestBsearch
{

	private final int MAX_KEY = 1000000;
	private int DEBUG = 0;

	private void conductRandomSearches(FileChannel dataFile, int n)
	throws IOException
	{
		int i;
		int key;
	    Record record;
		Random generator = new Random();
		ExternalBinarySearch searcher = new ExternalBinarySearch();

		for (i=0; i<n; i++) {
			key = generator.nextInt(MAX_KEY);
			if (DEBUG >= 1) {
				System.out.println("\nSearching for key = "+key+" \n");
			}
			record = searcher.search(dataFile, key);
			if (DEBUG >= 1) {
				if (record != null) {
					System.out.println(record);
					System.out.println("found: record with key "+key);
				} else {
					System.out.println("not found!: record with key "+key);
				}
			}
		}
	}//conductRandomSearches

	public static void main (String [] argv)
	{
		int n;
		long count;
		TestBsearch tester = new TestBsearch();
		Record record = new Record();

		if (argv.length != 2) {
			System.err.println("Usage: java TestBsearch <data file name> <number of searches>");
			System.exit(1);
		}
		n = Integer.parseInt(argv[1]);
		try {
			FileChannel dataFile = 
						new RandomAccessFile(argv[0],"rw").getChannel();

			count = dataFile.size()/(long ) record.getDiskSize();
			if (tester.DEBUG >= 1) {
		   		System.err.println("data file has "+count+"  records");
			}

			float startTime = NativeTiming.report_cpu_time();
			tester.conductRandomSearches(dataFile, n);
			float totalTime = NativeTiming.report_cpu_time() - startTime;
			System.out.println("Elapsed time for "+n+" searches = "+totalTime+" seconds"); 
			dataFile.close();
			System.exit(0);
		} catch (IOException e) {
		   	System.err.println(e);
			System.exit(1);
		}
	}
}

// vim: set ts=4;
