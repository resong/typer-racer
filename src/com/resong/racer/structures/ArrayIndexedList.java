package com.resong.racer.structures;

public class ArrayIndexedList<T> extends ArrayUnorderedList<T> implements IndexedListADT<T> {

	private final static int NOT_FOUND = -1;

	public void add(int index, T element) {

		if (index >= this.list.length - 1)
			this.addToRear(element);
		else
			this.addAfter(element, this.list[index - 1]);
	}

	public void set(int index, T element) {
		if (index >= this.list.length)
			return;

		this.list[index] = element;
	}

	public void add(T element) {
		this.addToRear(element);
	}

	public T get(int index) {
		return this.list[index];
	}

	public int indexOf(T element) {
		for (int i = 0; i < this.list.length; i++)
			if (this.list[i].equals(element))
				return i;

		return NOT_FOUND;
	}

	public T remove(int index) {
		return this.remove(this.list[index]);
	}

}
