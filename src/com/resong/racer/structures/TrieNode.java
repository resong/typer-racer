package com.resong.racer.structures;

/**
 * TrieNode class allows for construction of a TrieNode object that can be used to
 * expand a trie. Either takes nothing, or takes a Character and the TrieNode which 
 * will be its parent. Has an add, remove, findEndNode, getData, isLeaf, getCharacter 
 * and various iterative methods.
 * 
 * @author Rebecca Song
 *
 */

import java.util.Iterator;

public class TrieNode<T> implements TrieNodeADT<T> {

	///////////// Attributes ///////////

	private Character character; // character stored in node

	private T data; // data

	private TrieNode<T> parent; // reference to parent node

	private SmartArray<TrieNode<T>> children; // list of children

	private int childCount; // number of children

	///////////// Constructors ///////////

	/**
	 * Construct that initializes a trie node
	 */

	public TrieNode() {
		this.character = ' ';
		this.data = null;
		this.parent = null;
		this.children = new SmartArray<TrieNode<T>>();
		this.childCount = 0;
	}

	/**
	 * Construct that takes a Character and TrieNode as parameters and initializes
	 * the trie node using these parameters
	 * 
	 * @param c
	 * @param p
	 */

	public TrieNode(Character c, TrieNode<T> p) {
		this.character = c;
		this.data = null;
		this.parent = p;
		this.children = new SmartArray<TrieNode<T>>();
		this.childCount = 0;
	}

	///////////// Methods /////////////

	/**
	 * Method to add a string, each character of the string being assigned to the
	 * current node's child node. A new child node is created if the character
	 * doesn't exist as a child node already. If it does, then the next character in
	 * the string is added. The last character of the string gets a data element.
	 * 
	 * @param word String to be added
	 * @param data T generic object to be added to the last node
	 */

	public void add(String word, T data) {

		// if word is not null

		if (!("").equals(word)) {

			// get first character of the word, its integer value
			// and the length of the word

			char character = word.charAt(0);
			int i = (int) character;
			int length = word.length();

			// get the node at index i of the array and assign it to temp

			TrieNode<T> temp = this.children.get(i);

			// if temp is not null, pass the word excluding the
			// character stored by temp and the data element
			// to the recursive add method

			if (temp != null) {
				word = word.substring(1, length);
				temp.add(word, data);
			}

			// otherwise create and set the child node so its
			// character is the current character and its parent
			// is this, and pass the word excluding the current character
			// to the recursive add method

			else {
				TrieNode<T> child = new TrieNode<T>(character, this);
				this.children.set(i, child);
				word = word.substring(1, length);
				child.add(word, data);
				this.childCount++; // increment number of children
			}

		}

		// otherwise assign the data element to this node and return

		else {
			this.data = data;
			return;
		}

	}

	/**
	 * Method to remove a string by removing the nodes containing the characters
	 * individually and returning the data value contained in the last character.
	 * This removes the end node of each word passed to it (where each word is the
	 * previous word excluding the character) so long as the end node is a leaf
	 * node.
	 * 
	 * @param word String to be removed
	 * @return T info data object in the node of the last character
	 */

	public T remove(String word) {

		T info = null; // create and initialize info to null

		// if word is not null

		if (!("").equals(word)) {

			// create a tempData T object, a temp trie node
			// and a current trie node that is assigned the end node
			// of the current word

			T tempData;
			TrieNode<T> temp;
			TrieNode<T> current = findEndNode(word);

			// if current is not null and a leaf node

			if (current != null) {
				if (current.isLeaf()) {

					// get the integer value of current's character

					int i = (int) current.getCharacter();

					// remove the current node and store
					// its data object in a tempData variable

					tempData = current.getData();
					temp = current.parent;
					current.parent = null;
					current = temp;
					current.children.set(i, null);
					current.childCount--; // decrement the child count

					// pass the recursive remove method the current word
					// excluding the character the end node contained

					word = word.substring(0, word.length() - 1);
					this.remove(word);

					// assign tempData to info

					info = tempData;

				}

			}

		}

		return info; // return info
	}

	/**
	 * Method to find the end node containing the last character of the word pass to
	 * this method. Returns null if the word doesn't exist in the trie, else it
	 * returns the last node.
	 * 
	 * @param word String to find the end node of
	 * @return TrieNode<T> node containing the last character of the word
	 */

	public TrieNode<T> findEndNode(String word) {

		TrieNode<T> end = null; // create and initiate a node called end

		// if the word is not null

		if (!("").equals(word)) {

			// get first character, its integer value and length of the word

			char character = word.charAt(0);
			int i = (int) character;
			int length = word.length();

			// get the child node at index i and assign it to a temp node

			TrieNode<T> temp = this.children.get(i);

			// if temp is not null, pass the substring of the word
			// excluding its first character to the findEndNode method,
			// called by temp, and assign the returning node to end

			if (temp != null) {
				word = word.substring(1, length);
				end = temp.findEndNode(word);
			}
		}

		// otherwise assign the current node to end

		else {
			end = this;
		}

		return end; // return end
	}

	/**
	 * Accessor method that returns the current node's data.
	 * 
	 * @return T data
	 */

	public T getData() {
		return this.data; // return data
	}

	/**
	 * Method to determine if the current node is a leaf node
	 * 
	 * @return boolean true if it's a leaf, else false
	 */

	public boolean isLeaf() {

		boolean result = false; // initialized to false

		// if the current node has no children
		// assign result to true

		if (this.childCount == 0) {
			result = true;
		}
		return result; // return result
	}

	/**
	 * Accessor method that returns the current node's character
	 * 
	 * @return Character character
	 */

	public Character getCharacter() {
		return this.character; // return character
	}

	/**
	 * Helper method used for a preorder iterator. Takes a string and an unordered
	 * list of strings implemented by an array to store the strings, passed to it by
	 * the iterator.
	 * 
	 * @param prefix String
	 * @param words  list that will store the words
	 */

	private void preorder(String prefix, ArrayUnorderedList<String> words) {
		// get current character, assign it to letter
		// and create a temp String

		char letter = this.getCharacter();
		String temp;

		// if letter is null as a character, assign
		// temp to be a null string

		if (letter == ' ') {
			temp = "";
		}

		// else assign temp the String type of letter

		else {
			temp = Character.toString(letter);
		}

		// concatenate temp to prefix, and assign the new string
		// to prefix and create a node called child

		prefix = prefix + temp;
		TrieNode<T> child;

		// if the current node's data is not null

		if (this.data != null) {
			words.addToRear(prefix); // add the prefix to the list

			// if the current node is not a leaf

			if (!isLeaf()) {

				// loop through the children array (L to R), and call the recursive
				// method preorder on the child if it is not null

				for (int i = 0; i < this.children.length(); i++) {
					child = this.children.get(i);
					if (child != null) {
						child.preorder(prefix, words);
					}
				}
			}
		}

		// else loop through the children array (L to R), call the recursive method
		// preorder on the child if it is not null

		else {
			for (int i = 0; i < this.children.length(); i++) {
				child = this.children.get(i);
				if (child != null) {
					child.preorder(prefix, words);
				}
			}

		}
	}

	/**
	 * Iterator method that traverses the nodes in a preorder fashion. Returns an
	 * iterator of strings.
	 * 
	 * @return Iterator<String> iterator of strings
	 */

	public Iterator<String> preorderIterator() {

		// create new ArrayUnorderedList of strings called tempList
		// and a temp String and get the current character

		ArrayUnorderedList<String> tempList = new ArrayUnorderedList<String>();
		String temp;
		char letter = this.getCharacter();

		// if letter is null as a character, make temp null

		if (letter == ' ') {
			temp = "";
		}

		// else assign temp the String of letter

		else {
			temp = Character.toString(letter);
		}

		// call the preorder method, passing temp and tempList

		preorder(temp, tempList);

		// return the iterator, which iterates through the tempList

		return tempList.iterator();
	}

	/**
	 * Helper method used for a reversePreorder iterator. Takes a string and an
	 * unordered list of strings implemented by an array to store the strings,
	 * passed to it by the iterator.
	 * 
	 * @param prefix String
	 * @param words  list that will store the words
	 */

	public void reversePreorder(String prefix, ArrayUnorderedList<String> words) {
		// get current character, assign it to letter
		// and create a temp String

		char letter = this.getCharacter();
		String temp;

		// if letter is null as a character, assign
		// temp to be a null string

		if (letter == ' ') {
			temp = "";
		}

		// else assign temp the String type of letter

		else {
			temp = Character.toString(letter);
		}

		// concatenate temp to prefix, and assign the new string
		// to prefix and create a node called child

		prefix = prefix + temp;
		TrieNode<T> child;

		// if the current node's data is not null

		if (this.data != null) {
			words.addToRear(prefix);// add the prefix to the list

			// if the current node is not a leaf

			if (!isLeaf()) {

				// loop through the children array (R to L), and call the recursive
				// method reversePreorder on the child if it is not null

				for (int i = this.children.length() - 1; i >= 0; i--) {
					child = this.children.get(i);
					if (child != null) {
						child.preorder(prefix, words);
					}
				}
			}
		}

		// else loop through the children array (R to L) and call the recursive
		// method reversePreorder on the child if it isn't null

		else {
			for (int i = this.children.length() - 1; i >= 0; i--) {
				child = this.children.get(i);
				if (child != null) {
					child.preorder(prefix, words);
				}
			}
		}

	}

	/**
	 * Iterator method that traverses the nodes in a reverse preorder fashion.
	 * Returns an iterator of strings.
	 * 
	 * @return Iterator<String> iterator of strings
	 */

	public Iterator<String> reversePreorderIterator() {
		// create new ArrayUnorderedList of strings called tempList
		// and a temp String and get the current character

		ArrayUnorderedList<String> tempList = new ArrayUnorderedList<String>();
		String temp;
		char letter = this.getCharacter();

		// if letter is null as a character, make temp null

		if (letter == ' ') {
			temp = "";
		}

		// else assign temp the String of letter

		else {
			temp = Character.toString(letter);
		}

		// call the preorder method, passing temp and tempList

		reversePreorder(temp, tempList);

		// return the iterator, which iterates through the tempList

		return tempList.iterator();
	}

	/**
	 * Iterator method that iterates through the children of the current node and
	 * adds them to a temporary list. It returns an iterator of the list of children
	 * of the current node.
	 * 
	 * @return Iterator<TrieNodeADT<T>> iterator of trie nodes
	 */

	public Iterator<TrieNodeADT<T>> childNodeIterator() {
		ArrayUnorderedList<TrieNodeADT<T>> tempList = new ArrayUnorderedList<TrieNodeADT<T>>();

		TrieNodeADT<T> child; // declare child node

		// loop through the array of children, and if the child is
		// not null, add it to the rear of the tempList

		for (int i = 0; i < this.children.length(); i++) {
			child = this.children.get(i);
			if (child != null) {
				tempList.addToRear(child);
			}
		}

		// return this iterator of the list of children

		return tempList.iterator();
	}

	/**
	 * toString method of a trie node. Returns a String representation of the
	 * character in the current node.
	 * 
	 * @return String character
	 */

	public String toString() {

		String c = Character.toString(this.getCharacter()); // get String of current character and assign it to c

		// return c

		return c;

	}

}
