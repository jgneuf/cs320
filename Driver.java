/** Student Name: Jonathan Neufeld
 *  Student ID  : 30671093
 *  CS ID       : p9d8
 *   
 * Test Huffman coding on four different files. The first two are
 * given in the assignment pdf. I chose the King Jame's version of Ecclesiastes
 * (source: http://ebible.org/kjv/) and pi to one million digits
 * (source: http://www.cecm.sfu.ca/organics/papers/borwein/paper/html/local/bdigits.html).
 */
import java.io.*;

public class Driver {
	private static String bigString;

	public static void main(String[] args) {
		// Do Huffman on each file and compare Huffman length with ASCII bit length.
		for (String str: args) {
			try {
				System.out.println("--- Analyzing " + str + " ---");
				// Prepare to read file.
				FileInputStream ifs1 = new FileInputStream(str);
				DataInputStream dis1 = new DataInputStream(ifs1);
				BufferedReader  br1  = new BufferedReader(new InputStreamReader(dis1));

				// Read first file into a string.
				String tempString;
				System.out.println("Reading...");
				while ((tempString = br1.readLine()) != null) {
					bigString += tempString;
				}
				dis1.close();

			} 
			// Catch and print any errors during reading.
			catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}

			// Get the Huffman coding length and compare to expected ASCII bit length.
			Huffman hf = new Huffman(bigString);
			System.out.println("Coding...");
			int huffmanLength = hf.encode().length();
			int asciiLength   = bigString.length() * 8;
			System.out.println("Expected ASCII bits: " + asciiLength);
			System.out.println("Huffman coding bits: " + huffmanLength);
			System.out.println();
			
			// Be nice, be clean.
			hf.clean();
		}
	}

}
