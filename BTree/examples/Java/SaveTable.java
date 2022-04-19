
/**
  Example of freeze-drying (or serializing) a hash table to disk.
*/

// Use java -source 1.4 to avoid warnings
import java.io.*;
import java.util.*;
import java.awt.*;

public class SaveTable {
	public static void main (String [] args) 
	{
		Hashtable h = new Hashtable(); 
		// Hashtable implements the serializable interface
		// so we can try to  freeze dry it
		h.put("string","Cyrano De Bergerac");
		h.put("int",new Integer("1600"));
		h.put("double", new Double(Math.PI));
		h.put("color", new Color(255,0,0));

		try {
			FileOutputStream fileout = new FileOutputStream("hash.serial");
			ObjectOutputStream out = new ObjectOutputStream(fileout);
			out.writeObject(h);
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
}	



