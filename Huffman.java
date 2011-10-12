import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.PriorityQueue;

/** This class depends on BinaryOut.java, a class I did not write myself. As far as I am
 * aware, there is no other (simple) way to write bits to a file with Java. This class
 * was created at Princeton University. For additional information on other library files
 * and BinaryOut.java, please see: http://introcs.cs.princeton.edu/java/stdlib/       */
public class Huffman {
	private int[]                         freq;       // Store frequency of each character
	private String[]                      codes;      // Array holding codes of each character
	private PriorityQueue<CharFrequency>  heap;		  // Heap for character-frequencies
	private String                        text;       // Text to compress
	private String                        file;       // Where to write compressed text
	private final static int              ALPH = 128; // Alphabet size 

	/** Huffman will run on the given string and output the result to the given file.
	 * @param file Name of output file.
	 * @param text String to compress.
	 */
	public Huffman (String file, String text) {
		this.text = text;
		this.file = file + ".hff";
		codes     = new String[ALPH];
		freq      = new int[ALPH];
		heap      = new PriorityQueue<CharFrequency>(ALPH, new CharFrequencyComparator());
	}

	/** Allow everything to be reclaimed by the garbage collector. */
	public void clean () {
		freq  = null;
		text  = null;
		codes = null;
		heap  = null;
	}

	/** Compress the string and write it to an output file. */
	public void encode () {
		compress();
		write();
	}

	/** Compress the string. */
	private void compress () {
		// Increment frequency of each character in the text.
		for (int i = 0; i < text.length(); i++)
			freq[(int) text.charAt(i)]++;

		// Build heap with one node per character.
		for (int i = 0; i < ALPH; i++) {
			if (freq[i] > 0)
				heap.add(new CharFrequency((char) i, freq[i]));
		}

		// Rebuild the heap into Huffman tree. The result is a heap with a
		// single element, but it is the root node of the Huffman tree. This
		// can be used later to access the tree.
		while (heap.size() > 1) {
			CharFrequency x = heap.poll();
			CharFrequency y = heap.poll();

			CharFrequency z = new CharFrequency((char) 0, 
					x.frequency + y.frequency);

			heap.add(z);
			z.left  = y;
			z.right = x;
		}

		// Create Huffman bit-sequence for string.
		inorder (heap.peek(), "");
	}

	/** Write the compressed text to a file. */
	private void write() {
		// Prepare a binary stream to a file.
		BinaryOut bout = new BinaryOut(file);
		
		// Get the bit-sequence for each character in the text. It's a string
		// representing bits. For each element of the string, write a '0' or
		// '1' to the output file.
		for (int i = 0; i < text.length(); i++) {
			// Get the bit-sequence for the character.
			String c = codes[text.charAt(i)];
			
			// Now write the bit to the file.
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
	private void inorder (CharFrequency node, String bits) {
		// Stop traversal when the reference is null.
		if (node != null) {
			node.bitSequence = bits;

			// External nodes hold characters and a full bit-sequence. Store this
			// bit sequence in a codes array so we can lookup the bit-sequence for
			// this character quickly later on.
			if (node.isExternal())
				codes[(int) node.character] = bits;
			
			// Left children add a '0', right children add a '1' to their bit-sequence.
			inorder(node.left, node.bitSequence + "0");
			inorder(node.right, node.bitSequence + "1");
		}
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

		/** Return true if node is external. */
		public boolean isExternal () { return (left == null) && (right == null); }
	}

	/** Comparator for CharFrequency. Need this for the heap. */
	private class CharFrequencyComparator implements Comparator<CharFrequency> {
		public int compare(CharFrequency cf1, CharFrequency cf2) {
			return cf1.frequency - cf2.frequency;
		}
	}

	/** Take arguments from command line and run Huffman on those files. */
	public static void main (String[] args) {
		// Check that the user provided some files to compress. Don't run if there
		// aren't any.
		if (args.length == 0)
			System.out.println("Usage: Provide files to compress as arguments.");
		
		// Run Huffman on each given file.
		for (String file : args) {
			String input = "";
			
			// Read the file into one big string.
			try {
				System.out.println("--- Analyzing " + file + " ---");
				
				// Prepare to read file.
				FileInputStream ifs1 = new FileInputStream(file);
				DataInputStream dis1 = new DataInputStream(ifs1);
				BufferedReader  br1  = new BufferedReader(new InputStreamReader(dis1));

				// Read file into a string.
				String tempString;
				System.out.println("Reading...");
				while ((tempString = br1.readLine()) != null)
					input += tempString;
				dis1.close();
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}

			// Run Huffman on the file.
			Huffman hf = new Huffman(file, input);
			System.out.println("Coding...");
			hf.encode();

			// Be nice, be clean.
			hf.clean();
			input = null;
		}	
	}
}
