
public class Driver {
	public static void main(String[] args) {
		Huffman hf = new Huffman("abacdabaaa");
		hf.compress();
		
		hf = new Huffman("this is an arbitrary string!");
		hf.compress();
	}

}
