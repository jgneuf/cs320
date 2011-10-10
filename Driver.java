
public class Driver {
	public static void main(String[] args) {
		Huffman hf = new Huffman("this is an example of a huffman tree");
		//Huffman hf = new Huffman("aebffaabeeeceaaadfefeafebaaf");
		System.out.println(hf.encode().length());
	}

}
