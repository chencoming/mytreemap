package mytreemap;

public class TestRedBlackTree {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RedBlackTree tree = new RedBlackTree(12, 12);
		int [] nums = {1,9,2,0};
		for(int n : nums){
			tree.put(n, n);
		}
		System.out.println("done!");
		
	}

}
