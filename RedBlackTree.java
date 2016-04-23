package mytreemap;

/**
 *  红黑树
 * @author coming
 * 五个规则：
 * 1、树的节点只能是红色或黑色
 * 2、树的根节点为黑色
 * 3、每个叶节点（NIL空节点）都是黑色 
 * 4、每一个路径上不能出现两个相邻的红节点
 * 5、从任意一个节点到其每个叶子的所有路径上的黑色节点的数量都一样
 */
public class RedBlackTree {
	
	// 颜色
	private final boolean RED = false;
	private final boolean BLACK = true;
	
	// 根节点
	private Entry root;
	
	public RedBlackTree(int key , int value){
		root = new Entry(key, value);
		setColor(root, BLACK);
	}
	
	/**
	 * 	插入新节点，默认是红色
	 * @param key
	 * @param value
	 * @return value
	 */
	public int put(int key, int value){
		
		if(root == null){ // 空树，插入新节点作为根节点 
			root = new Entry(key, value);
			setColor(root, BLACK);
			return value;
		}
		
		Entry e = root;
		Entry parent;
		do{ // 遍历二叉树，若发现存在key值相同的节点，则以新值覆盖旧值；否则找到自己的父节点
			parent = e;
			if (e.key == key) {
				e.value = value;
			} else if (e.key > key){
				e = e.leftChild;
			} else if (e.key < key){
				e = e.rightChild;
			}
		}while(e != null);
		
		// 创建新节点，比较key值，插入作为父节点的子节点
		Entry newEntry = new Entry(key, value, parent, null, null);
		if(parent.key > newEntry.key){
			parent.leftChild = newEntry;
		} else if (parent.key < newEntry.key){
			parent.rightChild = newEntry;
		}
		
		// 插入新节点后，新二叉树的平衡可能遭到破坏，需要进行部分调整
		fixAfterInsertion(newEntry);

		return value;
	}
	

	/**
	 * 删除key值对应的节点
	 * @param key
	 * @return value
	 */
	public int delete(int key){
		if(root == null){
			throw new NullPointerException();
		}
		Entry entry = findEntryByKey(key);
		if(entry == null ){
			throw new NullPointerException();
		}
		
		int value = 0;

		// 有两个子节点
		if(entry.leftChild != null && entry.rightChild != null){
			Entry replacement = successor(entry);
			entry.value = replacement.value;
			entry.key = replacement.key;
			entry = replacement;
		}
		
		
		// 只有一个子节点
		if((entry.leftChild != null && entry.rightChild == null) 
				|| (entry.leftChild == null && entry.rightChild != null)){
			
			Entry child = entry.leftChild == null ? entry.rightChild: entry.leftChild;
			if(entry.parent != null){
				if(isLeftOf(entry)){
					entry.parent.leftChild = child;
				}else{
					entry.parent.rightChild = child;
				}
				child.parent = entry.parent;
			} else {// 无父节点说明是根节点
				child.parent = null;
			}
			if(entry.color == BLACK){
				fixAfterDeletion(child);
			}
		} else if(entry.leftChild ==null && entry.rightChild == null){// 没有子节点
			if(entry.parent !=null){
				if(isLeftOf(entry)){
					entry.parent.leftChild = null;
				} else {
					entry.parent.rightChild = null;
				}
			} 
			if(entry.color == BLACK){
				fixAfterDeletion(entry.parent);
			}
		}
		
		value = entry.value;
		entry = null;
		return value;
	}

	/**
	 * 删除节点后，通过调整节点结构（左旋/右旋）和着色平衡调整二叉树
	 * @param child
	 */
	private void fixAfterDeletion(Entry entry) {
		while(entry != null && entry.parent != null && entry.color != RED){
			Entry brother = findBrother(entry);
			if(isLeftOf(entry)){
				// 兄弟节点为红
				if (brother.color == RED) {
					setColor(entry.parent, RED);
					setColor(brother, BLACK);
					rotateLeft(entry.parent);
				} else {// 兄弟节点为黑
					if (colorOf(brother.leftChild) == BLACK   					// 双侄为黑 
							&& colorOf(brother.rightChild)== BLACK) {
						setColor(brother, RED);
						if (entry.parent.color == RED) {
							setColor(entry.parent, BLACK);
							entry = root;
						} else {
							entry = entry.parent;
						}
					} else if(colorOf(brother.leftChild) == RED // 左侄为红，右侄为黑 
							&& colorOf(brother.rightChild) == BLACK){ 
						setColor(brother, RED);
						setColor(brother.leftChild, BLACK);
						rotateRight(brother);
					} else if(colorOf(brother.rightChild) == RED){ // 右侄为红
						setColor(brother, entry.parent.color);
						setColor(entry.parent, BLACK);
						setColor(brother.rightChild, BLACK);
						rotateLeft(entry.parent);
						entry = root;
					}
				}
			} else {
				// 兄弟节点为红
				if (brother.color == RED) {
					setColor(entry.parent, RED);
					setColor(brother, BLACK);
					rotateRight(entry.parent);
				} else {// 兄弟节点为黑

					// 双侄为黑
					if (colorOf(brother.leftChild) == BLACK 
							&& colorOf(brother.rightChild)== BLACK) {
						setColor(brother, RED);
						if (entry.parent.color == RED) {
							setColor(entry.parent, BLACK);
							entry = root;
						} else {
							entry = entry.parent;
						} 
					}else if(colorOf(brother.rightChild) == RED // 右侄为红，左侄为黑 
							&& colorOf(brother.leftChild) == BLACK){ 
						setColor(brother, RED);
						setColor(brother.rightChild, BLACK);
						rotateLeft(brother);
					} else if(colorOf(brother.leftChild) == RED){ // 左侄为红
						setColor(brother, entry.parent.color);
						setColor(entry.parent, BLACK);
						setColor(brother.leftChild, BLACK);
						rotateRight(entry.parent);
						entry = root;
					}
				}
				
			}
			
		}
		setColor(entry, BLACK);
		
	}
	
	
	/**
	 * 获取兄弟节点
	 * @param entry
	 * @return
	 */
	private Entry findBrother(Entry entry) {
		if(entry != null && entry.parent != null){
			if(isLeftOf(entry)){
				return entry.parent.rightChild;
			} else {
				return entry.parent.leftChild;
			}
		}
		return null;
	}

	/**
	 * 根据key值获取节点
	 * @param key
	 * @return Entry
	 */
	private Entry findEntryByKey(int key) {
		Entry entry = root;
		while(entry != null){
			if(key == entry.key){
				return entry;
			} else if(key > root.key){
				entry = entry.rightChild;
			} else {
				entry = entry.leftChild;
			}
		}
		return entry;
	}

	/**
	 * 寻找右子树最大左子树
	 * @param e
	 * @return Entry
	 */
	public Entry successor(Entry e){
		if(e == null || e.rightChild == null){
			return null;
		}
		Entry entry = e.rightChild;
		while(entry.leftChild != null){
			entry = entry.leftChild;
		}
		return entry;
	}
	
	/**
	 * 新增节点后，通过调整节点结构（左旋/右旋）和着色平衡调整二叉树
	 * @param e
	 */
	private void fixAfterInsertion(Entry e) {
		while (e != null && e != root && e.parent.color != BLACK) {

			Entry uncle = findMyUncle(e);

			// 情况一：父节点、叔夫节点为红色
			if (e.parent.color == RED && uncle != null && uncle.color == RED) {
				// 父与叔节点标黑，祖父标红，以祖父作为新节点
				setColor(e.parent, BLACK);
				setColor(uncle, BLACK);
				setColor(e.parent.parent, RED);
				e = e.parent.parent;

				// 情况二：父节点为红，叔父节点为黑（包括为null）
			} else if (e.parent.color == RED
					&& ((uncle == null) || (uncle != null && uncle.color == BLACK))) {
				// 当自己疏远叔叔时
				if (isLeftOf(e) && isLeftOf(e.parent)) {
					setColor(e.parent, BLACK);
					setColor(e.parent.parent, RED);
					Entry t = e.parent; 
					rotateRight(e.parent.parent);
					e = t;
				} else if (isRightOf(e) && isRightOf(e.parent)) {
					setColor(e.parent, BLACK);
					setColor (e.parent.parent, RED);
					Entry t = e.parent; 
					rotateLeft(e.parent.parent);
					e = t;
				// 当自己亲近叔叔时
				} else if (isRightOf(e) && isLeftOf(e.parent)) {
					Entry t = e.parent; 
					rotateLeft(e.parent);
					e = t;
				} else if (isLeftOf(e) && isRightOf(e.parent) ) {
					Entry t = e.parent; 
					rotateRight(e.parent);
					e = t;
				}
			}

		}
		setColor(root, BLACK);
	}

	/**
	 * 判断是否左节点
	 * @param e
	 * @return boolean
	 */
	private boolean isLeftOf(Entry e){
		if(e.parent != null){
			return e.parent.leftChild == e;
		}
		return false;
	}
	
	/**
	 * 判断是否右节点
	 * @param e
	 * @return boolean
	 */
	private boolean isRightOf(Entry e){
		if(e.parent != null){
			return e.parent.rightChild == e;
		}
		return false;
	}
	
	/**
	 * 获取叔父节点
	 * @param e
	 * @return Entry
	 */
	private Entry findMyUncle(Entry e){
		Entry parent = e.parent;
		if(parent != null && parent.parent !=null){
			if(isLeftOf(parent)) return parent.parent.rightChild;
			if(isRightOf(parent)) return parent.parent.leftChild;
		}
		return null;
	}
	
	/**
	 * 获取节点颜色
	 * @param e
	 * @return
	 */
	private boolean colorOf(Entry e){
		return e != null ? e.color : BLACK; 
	}
	
	
	/**
	 * 左旋
	 * @param e
	 */
	private void rotateLeft(Entry e){
		if(e.parent != null){
			e.parent.leftChild = e.rightChild;
			e.rightChild.parent = e.parent;
		} else {
			e.rightChild.parent = null;
			root = e.rightChild;
		}
		e.parent = e.rightChild;
		e.rightChild = e.rightChild.leftChild == null ? null : e.rightChild.leftChild;
		e.parent.leftChild = e;
		if(e.rightChild != null) e.rightChild.parent = e; 
	}
	
	/**
	 * 右旋
	 * @param e
	 */
	private void rotateRight(Entry e){
		if(e.parent != null){
			if(isLeftOf(e)){
				e.parent.leftChild = e.leftChild;
			} else {
				e.parent.rightChild = e.leftChild;
			}
			e.leftChild.parent = e.parent;
		} else {
			e.leftChild.parent = null;
			root = e.leftChild;
		}
		e.parent = e.leftChild;
		e.leftChild = e.leftChild.rightChild == null ? null : e.leftChild.rightChild;
		e.parent.rightChild = e;
		if(e.leftChild != null) e.leftChild.parent = e;
		
	}
	
	public void setColor(Entry e, Boolean color){
		if(e != null)
			e.color = color;
	}
	
	/**
	 * 打印红黑树
	 */
	public void showNotes(){
		
	}
	
	
	/**
	 * 红黑树节点
	 * @author coming
	 */
	class Entry {

		int key;
		int value;
		
		// 节点颜色默认是红色
		Boolean color = RED;
		
		Entry parent;
		Entry leftChild;
		Entry rightChild;
		
		public Entry(int key, int value){
			this.key = key;
			this.value = value;
		}
		
		public Entry(int key, int value, Entry parent, Entry leftChild, Entry rightChild){
			this.key = key;
			this.value = value;
			this.parent = parent;
			this.leftChild = leftChild;
			this.rightChild = rightChild;
		}
		
	}
}
