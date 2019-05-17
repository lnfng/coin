package coin.utils;

import java.util.*;
import java.io.Serializable;

/**
 * 
 * @author Qian
 */
public class QList<E> implements Serializable {
	private static final long serialVersionUID = 6913196367394160536L;
	
	/**
	 * 所有元素的总量
	 */
	private int size;
	/**
	 * 默认初始容量
	 */
	private static final int DEFAULT_CAPACITY = 10;
	/**
	 *  Integer 类型的最大位数
	 */
	private static final int DATASTORE_MAX_SIZE = 31;
	/**
	 * 存放数链
	 */
	private List<DataStore> dataStore;
	
	
	public void add(E e) {
		size++;
		
		
	}
	
	public E remove(int index) {
		size--;
		
		return null;
	}
	
	public E get(int index) {
		
		return null;
	}
	
	public boolean contains(E e) {
		return false;
	}
	
	public int getSize() {
		return size;
	}
	
	private int getCurrIndex(){
		return 0;
	}
	
	/**
	 * load factor
	 */
	private int getNextcapacity(int currSize, int factor){
		
		return 0;
	}
	
	/**
	 * 存放数据
	 */
	private static class DataStore {
		DataStore pre;
		DataStore next;
		Object[] elements;
		/**
		 * 元素个数
		 */
		int size;
		/**
		 * 容量
		 */
		int capacity;
		/**
		 * 当前序列
		 */
		int sequence;
		
		
		DataStore(int capacity) {
			if(capacity < 0) {
				throw new IllegalArgumentException("Capacity cannot be less than zero!");
			}
			this.capacity = capacity;
			elements = new Object[capacity];
		}
		
		
		Object getElement(int index) {
			return elements[index];
		}
		
		void addElement(int index,Object obj) {
			elements[index] = obj;
		}
		
		void removeElement(int index) {
			elements[index] = null;
		}
	}

	
	
	
	
	public static void main(String[] args) {
		int n = 27;
		int capacity = 10;
		
		// 公比数未2成立
		// 等比数列的第n个长度
		int a = (int) (capacity*(Math.pow(2, n-1)));
		int an = capacity << n-1;
		
		
		// 等比数列的n项和
		int s = (int) (capacity*(Math.pow(2, n)-1));
		int sn = (capacity << n) - capacity;

		System.out.println(a);
		System.out.println(an);
		System.out.println(s);
		System.out.println(sn);
		
		// 2 的倍数
		// 31 - 当前长度 1010B
		System.out.println(10 << 27);
		
		int cap = 100;
		int position = 31;
		int cposition = 1; // 当前容量的所占的位数
		
		for ( ;cposition < position; cposition++) {
			if (cap >> cposition == 0)
				break ;
		}
		
		System.out.println(cposition);
		
		
	}
	
	
	
}













