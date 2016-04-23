package mytreemap;

/**
 *  �����
 * @author coming
 * �������
 * 1�����Ľڵ�ֻ���Ǻ�ɫ���ɫ
 * 2�����ĸ��ڵ�Ϊ��ɫ
 * 3��ÿ��Ҷ�ڵ㣨NIL�սڵ㣩���Ǻ�ɫ
 * 4��ÿһ��·���ϲ��ܳ����������ڵĺ�ڵ�
 * 5��������һ���ڵ㵽��ÿ��Ҷ�ӵ�����·���ϵĺ�ɫ�ڵ��������һ��
 */
public class RedBlackTree {
	
	// ��ɫ
	private final boolean RED = false;
	private final boolean BLACK = true;
	
	// ���ڵ�
	private Entry root;
	
	public RedBlackTree(int key , int value){
		root = new Entry(key, value);
		setColor(root, BLACK);
	}
	
	/**
	 * 	�����½ڵ㣬Ĭ���Ǻ�ɫ
	 * @param key
	 * @param value
	 * @return value
	 */
	public int put(int key, int value){
		
		if(root == null){ // �����������½ڵ���Ϊ���ڵ� 
			root = new Entry(key, value);
			setColor(root, BLACK);
			return value;
		}
		
		Entry e = root;
		Entry parent;
		do{ // �����������������ִ���keyֵ��ͬ�Ľڵ㣬������ֵ���Ǿ�ֵ�������ҵ��Լ��ĸ��ڵ�
			parent = e;
			if (e.key == key) {
				e.value = value;
			} else if (e.key > key){
				e = e.leftChild;
			} else if (e.key < key){
				e = e.rightChild;
			}
		}while(e != null);
		
		// �����½ڵ㣬�Ƚ�keyֵ��������Ϊ���ڵ���ӽڵ�
		Entry newEntry = new Entry(key, value, parent, null, null);
		if(parent.key > newEntry.key){
			parent.leftChild = newEntry;
		} else if (parent.key < newEntry.key){
			parent.rightChild = newEntry;
		}
		
		// �����½ڵ���¶�������ƽ������⵽�ƻ�����Ҫ���в��ֵ���
		fixAfterInsertion(newEntry);

		return value;
	}
	

	/**
	 * ɾ��keyֵ��Ӧ�Ľڵ�
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

		// �������ӽڵ�
		if(entry.leftChild != null && entry.rightChild != null){
			Entry replacement = successor(entry);
			entry.value = replacement.value;
			entry.key = replacement.key;
			entry = replacement;
		}
		
		
		// ֻ��һ���ӽڵ�
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
			} else {// �޸��ڵ�˵���Ǹ��ڵ�
				child.parent = null;
			}
			if(entry.color == BLACK){
				fixAfterDeletion(child);
			}
		} else if(entry.leftChild ==null && entry.rightChild == null){// û���ӽڵ�
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
	 * ɾ���ڵ��ͨ�������ڵ�ṹ������/����������ɫƽ�����������
	 * @param child
	 */
	private void fixAfterDeletion(Entry entry) {
		while(entry != null && entry.parent != null && entry.color != RED){
			Entry brother = findBrother(entry);
			if(isLeftOf(entry)){
				// �ֵܽڵ�Ϊ��
				if (brother.color == RED) {
					setColor(entry.parent, RED);
					setColor(brother, BLACK);
					rotateLeft(entry.parent);
				} else {// �ֵܽڵ�Ϊ��
					if (colorOf(brother.leftChild) == BLACK   					// ˫ֶΪ�� 
							&& colorOf(brother.rightChild)== BLACK) {
						setColor(brother, RED);
						if (entry.parent.color == RED) {
							setColor(entry.parent, BLACK);
							entry = root;
						} else {
							entry = entry.parent;
						}
					} else if(colorOf(brother.leftChild) == RED // ��ֶΪ�죬��ֶΪ�� 
							&& colorOf(brother.rightChild) == BLACK){ 
						setColor(brother, RED);
						setColor(brother.leftChild, BLACK);
						rotateRight(brother);
					} else if(colorOf(brother.rightChild) == RED){ // ��ֶΪ��
						setColor(brother, entry.parent.color);
						setColor(entry.parent, BLACK);
						setColor(brother.rightChild, BLACK);
						rotateLeft(entry.parent);
						entry = root;
					}
				}
			} else {
				// �ֵܽڵ�Ϊ��
				if (brother.color == RED) {
					setColor(entry.parent, RED);
					setColor(brother, BLACK);
					rotateRight(entry.parent);
				} else {// �ֵܽڵ�Ϊ��

					// ˫ֶΪ��
					if (colorOf(brother.leftChild) == BLACK 
							&& colorOf(brother.rightChild)== BLACK) {
						setColor(brother, RED);
						if (entry.parent.color == RED) {
							setColor(entry.parent, BLACK);
							entry = root;
						} else {
							entry = entry.parent;
						} 
					}else if(colorOf(brother.rightChild) == RED // ��ֶΪ�죬��ֶΪ�� 
							&& colorOf(brother.leftChild) == BLACK){ 
						setColor(brother, RED);
						setColor(brother.rightChild, BLACK);
						rotateLeft(brother);
					} else if(colorOf(brother.leftChild) == RED){ // ��ֶΪ��
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
	 * ��ȡ�ֵܽڵ�
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
	 * ����keyֵ��ȡ�ڵ�
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
	 * Ѱ�����������������
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
	 * �����ڵ��ͨ�������ڵ�ṹ������/����������ɫƽ�����������
	 * @param e
	 */
	private void fixAfterInsertion(Entry e) {
		while (e != null && e != root && e.parent.color != BLACK) {

			Entry uncle = findMyUncle(e);

			// ���һ�����ڵ㡢���ڵ�Ϊ��ɫ
			if (e.parent.color == RED && uncle != null && uncle.color == RED) {
				// ������ڵ��ڣ��游��죬���游��Ϊ�½ڵ�
				setColor(e.parent, BLACK);
				setColor(uncle, BLACK);
				setColor(e.parent.parent, RED);
				e = e.parent.parent;

				// ����������ڵ�Ϊ�죬�常�ڵ�Ϊ�ڣ�����Ϊnull��
			} else if (e.parent.color == RED
					&& ((uncle == null) || (uncle != null && uncle.color == BLACK))) {
				// ���Լ���Զ����ʱ
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
				// ���Լ��׽�����ʱ
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
	 * �ж��Ƿ���ڵ�
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
	 * �ж��Ƿ��ҽڵ�
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
	 * ��ȡ�常�ڵ�
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
	 * ��ȡ�ڵ���ɫ
	 * @param e
	 * @return
	 */
	private boolean colorOf(Entry e){
		return e != null ? e.color : BLACK; 
	}
	
	
	/**
	 * ����
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
	 * ����
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
	 * ��ӡ�����
	 */
	public void showNotes(){
		
	}
	
	
	/**
	 * ������ڵ�
	 * @author coming
	 */
	class Entry {

		int key;
		int value;
		
		// �ڵ���ɫĬ���Ǻ�ɫ
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