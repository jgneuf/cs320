import java.util.Comparator;
import java.util.PriorityQueue;

public class Huffman {
	private int[]						  freq;       // Store frequency of each character
	private PriorityQueue<CharFrequency>  heap;		  // Heap for character/frequencies
	private String		       		      text;       // Text to compress
	private final static int   		      ALPH = 128; // Alphabet size 

	/** Set up Huffman to work with the given string. By default,
	 * use a 128-character alphabet, the ASCII character set.
	 * 
	 * @param s String to compress.
	 */
	public Huffman (String s) {
		freq = new int[ALPH];
		for (int i = 0; i < ALPH; i++)
			freq[i] = 0;

		text = s;
		heap = new PriorityQueue<CharFrequency>(ALPH, new CharFrequencyComparator());
	}

	/** Compress the given string. */
	public void compress () {
		// Increment frequency of each character in text.
		for (int i = 0; i < text.length(); i++)
			freq[(int) text.charAt(i)]++;

		// Build a heap with each character.
		for (int i = 0; i < ALPH; i++)
			heap.add(new CharFrequency((char) i, freq[(char) i]));

		// Rebuild the heap.
		for (int i = 0; i < ALPH - 1; i++) {
			CharFrequency x = heap.poll();
			System.out.print("Removed x: " + x);
			CharFrequency y = heap.poll();
			System.out.print("Removed y: " + y);
			CharFrequency z = new CharFrequency(' ', x.getFreq() + y.getFreq());
			z.setLeft(y);
			z.setRight(x);
			heap.add(z);
		}
		System.out.println(heap.peek());
	}

	/** Print each character and its frequency in the string. */
	public String getCharacterFrequencies () {
		String retVal = "";
		for (CharFrequency cf : heap) {
			retVal += cf;
		}
		return retVal;
	}

	/** Nested class representing a key-value pair. Here the key is
	 * an ASCII character and the value is its frequency in the text. */
	private class CharFrequency {
		private char character;
		private int  frequency;
		private CharFrequency left;
		private CharFrequency right;

		/** Set the character and its frequency. */
		public CharFrequency (char c, int f) {
			character = c;
			frequency = f;
			left = right = null;
		}

		/** Get character. */
		public char getChar () { return character; } 

		/** Get frequency. */
		public int getFreq () { return frequency; }

		/** Set character. */
		public void setChar (char c) { character = c; }

		/** Set frequency. */
		public void setFreq (int f) { frequency = f; }

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
		
		/** Return a string representation of a CharFrequency. */
		public String toString () {
			String retVal = "(char = " + character + ", freq = "
					+ frequency + ")\n";
			return retVal;
		}
	}

	/** Comparator for CharFrequency. Need this for the heap. */
	private class CharFrequencyComparator implements Comparator<CharFrequency> {
		public int compare(CharFrequency o1, CharFrequency o2) {
			return o1.getFreq() - o2.getFreq();
		}
	}
}
