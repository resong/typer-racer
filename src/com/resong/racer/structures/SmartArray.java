package com.resong.racer.structures;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A "smart" array that will automatically expand itself when an index is used
 * that is greater than its current length.
 * 
 * @author Jeff Shantz <x@y> x = jshantz4, y = csd.uwo.ca
 * @param <T> Type of object to store in the array
 */
public class SmartArray<T> implements Iterable<T> {

	/***************************************************************************
	 * CONSTANT DECLARATIONS
	 **************************************************************************/
	// Default initial capacity of a smart array
	private final int DEFAULT_CAPACITY = 10;
	/***************************************************************************
	 * INSTANCE VARIABLES
	 **************************************************************************/
	// Stores the elements of the smart array
	private T[] list;

	/**
	 * Initializes a new SmartArray with a default initial capacity of 10.
	 */
	public SmartArray() {
		this.list = (T[]) new Object[DEFAULT_CAPACITY];
	}

	/**
	 * Initializes a new SmartArray with the specified initial capacity
	 * 
	 * @param capacity Initial capacity of the array
	 */
	public SmartArray(int capacity) {
		this.list = (T[]) new Object[capacity];
	}

	/**
	 * Sets the element at the specified index. If the index is greater than the
	 * current length of the SmartArray, its capacity is automatically expanded.
	 * 
	 * @param index   Index at which to set data
	 * @param element Data to stored at the specified index
	 */
	public void set(int index, T element) {

		// If the index is out of bounds, expand the array
		if (index >= this.list.length) {
			expandCapacity((int) (index * 1.5));
		}

		this.list[index] = element;
	}

	/**
	 * Returns the element stored at the specified index. If the index has not been
	 * initialized, returns null.
	 * 
	 * @param index Index at which to retrieve data
	 * @return Data stored at the given index, or null if the index is invalid
	 */
	public T get(int index) {
		if (index >= this.list.length) {
			return null;
		} else {
			return this.list[index];
		}
	}

	/**
	 * Returns the length (capacity) of the SmartArray
	 * 
	 * @return The length of the SmartArray
	 */
	public int length() {
		return this.list.length;
	}

	/**
	 * Expands the capacity of the SmartArray to match the specified new capacity.
	 * 
	 * @param newCapacity New capacity of the SmartArray
	 */
	private void expandCapacity(int newCapacity) {

		T[] newList = (T[]) new Object[newCapacity];
		System.arraycopy(this.list, 0, newList, 0, this.list.length);
		this.list = newList;
	}

	/**
	 * Returns an iterator over all non-null elements in the SmartArray.
	 * 
	 * @return An iterator over all non-null elements in the array
	 */
	public Iterator<T> iterator() {
		ArrayList<T> tempList = new ArrayList<T>();

		for (int i = 0; i < list.length; i++) {
			if (list[i] != null) {
				tempList.add(list[i]);
			}
		}

		return tempList.iterator();
	}

	public String toString() {

		String s = "";
		Iterator<T> it = this.iterator();

		while (it.hasNext())
			s += it.next() + ", ";

		if (!s.isEmpty())
			return "[" + s.substring(0, s.length() - 2) + "]";
		else
			return "[]";
	}
}
