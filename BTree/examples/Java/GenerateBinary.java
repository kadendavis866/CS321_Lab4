
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/**
  Illusrates how to write a binary file efficiently.
  @author Amit Jain

*/

public class GenerateBinary
{
	final static int MAX_KEY = 1000000;
	final static int MAX_DOUBLE = 2148;

    private static void generateFile(int n, long seed, FileChannel fout)
	throws IOException
    {
		int i;
		int key;
		double value1;
		double value2;
		double value3;
		Random generator = new Random(seed);
		// make buffer be a multiple of the record size
		ByteBuffer buff = ByteBuffer.allocateDirect(28*146);
	
		for (i=0; i<n; i++) {
			/*key = generator.nextInt(MAX_KEY); */
			key = 2 * i;
			value1 = generator.nextDouble() * MAX_DOUBLE;
			value2 = generator.nextDouble() * MAX_DOUBLE;
			value3 = generator.nextDouble() * MAX_DOUBLE;
			buff.putInt(key);
			buff.putDouble(value1);
			buff.putDouble(value2);
			buff.putDouble(value3);
			if (!buff.hasRemaining()) {
				buff.flip();
				fout.write(buff);
				buff.clear();
			}
		}
		if (buff.position() > 0) {
			buff.flip();
			fout.write(buff);
			buff.clear();
		}
	}

	public static void main(String argv[])
	{
		int n;
		long seed = 0;

		if (argv.length < 1) {
			System.err.println("Usage: java GenerateBinary <n> [<seed>]\n");
			System.exit(1);
		}

		n = Integer.parseInt(argv[0]);
	    if (argv.length == 2) {
			seed = Long.parseLong(argv[1]);
		} 


		try {
			RandomAccessFile out = new RandomAccessFile("data.bin", "rw");
			FileChannel fout = out.getChannel();
			fout.truncate(0); // truncate the file
			generateFile(n ,seed, fout);
			fout.close();
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);

		}
		System.exit(0);
	}

}
