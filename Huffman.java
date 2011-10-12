import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.PriorityQueue;

/** This class depends on BinaryOut.java, a class I did not write myself. As far as I am
 * aware, there is no other (simple) way to write bits to a file with Java. This class
 * was created by Princeton University. For additional information on other library files
 * and BinaryOut.java, please see:
 * http://introcs.cs.princeton.edu/java/stdlib/
 */
public class Huffman {
	private int[]                         freq;       // Store frequency of each character
	private String[]                      codes;      // Array holding codes of each character
	private PriorityQueue<CharFrequency>  heap;		  // Heap for character/frequencies
	private String                        text;       // Text to compress
	private String                        outputFile; // Where to write compressed text
	private final static int              ALPH = 128; // Alphabet size 

	/** Set up Huffman to work with the given string. By default,
	 * use a 128-character alphabet, the ASCII character set.
	 * 
	 * @param s String to compress.
	 */
	public Huffman (String file, String text) {
		freq = new int[ALPH];
		for (int i = 0; i < ALPH; i++)
			freq[i] = 0;

		codes      = new String[ALPH];
		this.text  = text;
		outputFile = file + ".hff";
		heap  = new PriorityQueue<CharFrequency>(ALPH, new CharFrequencyComparator());
	}

	/** Allow everything to be reclaimbed by the garbage collector. */
	public void clean () {
		freq  = null;
		text  = null;
		codes = null;
		heap  = null;
	}

	/** Return a bit-sequence for the string given to Huffman. */
	public void encode () {
		compress();
		write();

		/*
		String retVal = "";
		for (int i = 0; i < text.length(); i++)
			retVal += codes[text.charAt(i)];
		return retVal;
		 */
	}

	/** Compress the given string. */
	private void compress () {
		// Increment frequency of each character in text.
		for (int i = 0; i < text.length(); i++)
			freq[(int) text.charAt(i)]++;

		// Build a heap with each character.
		for (int i = 0; i < ALPH; i++) {
			if (freq[i] > 0)
				heap.add(new CharFrequency((char) i, freq[i]));
		}

		// Rebuild the heap.
		while (heap.size() > 1) {
			CharFrequency x = heap.poll();
			CharFrequency y = heap.poll();

			CharFrequency z = new CharFrequency((char) 0, x.getFreq() + y.getFreq());

			heap.add(z);
			z.setLeft(y);
			z.setRight(x);
		}

		// Create Huffman bit-sequence for string.
		inorder (heap.peek(), "");
	}

	/** Write the compressed text to a file. */
	private void write() {
		BinaryOut bout = new BinaryOut(outputFile);
		for (int i = 0; i < text.length(); i++) {
			String c = codes[text.charAt(i)];
			for (int j = 0; j < c.length(); j++) {
				if (c.charAt(j) == '0')
					bout.write(true);
				else
					bout.write(false);
			}
		}
		bout.flush();
	}

	/** In-order traversal of Huffman tree to set bit-sequence for each node. */
	private void inorder (CharFrequency parent, String bit) {
		if (parent != null) {
			parent.setBitSequence(bit);

			if (parent.isExternal())
				codes[(int) parent.getChar()] = bit;
			inorder(parent.getLeft(), parent.getBitSequence() + "0");
			inorder(parent.getRight(), parent.getBitSequence() + "1");
		}
	}

	/** Print each character and its frequency in the string. */
	public String getCharacterFrequencies () {
		String retVal = "";
		for (int i = 0; i < ALPH; i++) {
			if (codes[i] != null)
				retVal += (char) i + " = " + codes[i] + "\n";
		}
		return retVal;
	}

	/** Nested class representing a key-value pair. Here the key is
	 * an ASCII character and the value is its frequency in the text. */
	private class CharFrequency {
		private char character;      // Character this node represents
		private int  frequency;      // Frequency of this node's character
		private CharFrequency left;  // Left child of this node
		private CharFrequency right; // Right child of this node
		private String bitSequence;  // Huffman bit coding for this character

		/** Set the character and its frequency. */
		public CharFrequency (char c, int f) {
			character = c;
			frequency = f;
			left = right = null;
			bitSequence  = null;
		}

		/** Get character. */
		public char getChar () { return character; } 

		/** Get frequency. */
		public int getFreq () { return frequency; }

		/** Set bitsequence. */
		public void setBitSequence (String bs) { bitSequence = bs; }

		/** Get bitsequence. */
		public String getBitSequence () { return bitSequence; }

		/** Get right child. */
		public CharFrequency getRight () { return right; }

		/** Get left child. */
		public CharFrequency getLeft () { return left; }

		/** Set right child. */
		public CharFrequency setRight (CharFrequency child) {
			right = child;
			return right;
		}

		/** Set left child. */
		public CharFrequency setLeft (CharFrequency child) {
			left = child;
			return left;
		}

		/** Return true if node is external. */
		public boolean isExternal () {
			return (left == null) && (right == null);
		}

		/** Return a string representation of a CharFrequency. */
		public String toString () {
			String retVal = "(char = " + character + ", freq = "
					+ frequency + ", bit-sequence: " + bitSequence + ")\n";
			return retVal;
		}
	}

	/** Comparator for CharFrequency. Need this for the heap. */
	private class CharFrequencyComparator implements Comparator<CharFrequency> {
		public int compare(CharFrequency cf1, CharFrequency cf2) {
			return cf1.getFreq() - cf2.getFreq();
		}
	}

	/** Take arguments from command line and run Huffman on those files. */
	public static void main (String[] args) {
		if (args.length < 2)
			System.out.println("Usage: Provide files to compress as arguments.");
		
		// Run Huffman on each given file.
		for (String file : args) {
			String input = "";
			try {
				System.out.println("--- Analyzing " + file + " ---");
				// Prepare to read file.
				FileInputStream ifs1 = new FileInputStream(file);
				DataInputStream dis1 = new DataInputStream(ifs1);
				BufferedReader  br1  = new BufferedReader(new InputStreamReader(dis1));

				// Read first file into a string.
				String tempString;
				System.out.println("Reading...");
				while ((tempString = br1.readLine()) != null) {
					input += tempString;
				}
				dis1.close();

			} 
			// Catch and print any errors during reading.
			catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}

			// Get the Huffman coding length and compare to expected ASCII bit length.
			Huffman hf = new Huffman(file, input);
			System.out.println("Coding...");
			hf.encode();

			// Be nice, be clean.
			hf.clean();
			input = null;
		}	
	}
}
