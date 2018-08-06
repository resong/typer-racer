package com.resong.racer.structures;

/**
 * Trie class allows for construction of a Trie object initializes a root and count of
 * the words stored in it. Has getRoot, add, remove, contains, containsPrefix, find, size,
 * isEmpty, and ascending and descending string iterator methods.
 * 
 * @author Rebecca Song
 *
 */

import java.util.Iterator;

import com.resong.racer.exceptions.InvalidStringException;
import com.resong.racer.exceptions.StringExistsException;
import com.resong.racer.exceptions.StringNotFoundException;

public class Trie<T> implements TrieADT<T> {

	//////////// Attributes /////////////

	private TrieNodeADT<T> root; // root note of trie

	private int count; // number of words in trie

	/////////// Constructors ///////////

	/**
	 * Construct that initializes a trie
	 */

	public Trie() {
		this.root = new TrieNode<T>();
		this.count = 0;
	}

	//////////// Methods //////////////

	/**
	 * Accessor method to get the root of the trie
	 * 
	 * @return TrieNodeADT<T> root node
	 */

	public TrieNodeADT<T> getRoot() {
		return this.root; // return root
	}

	/**
	 * Method to clear the trie of all strings, leaving just the root node.
	 */

	public void clear() {

		// call the preorderIterator on the root and assign the
		// returned iterator of strings to children

		Iterator<String> children = root.preorderIterator();

		// while children has a next String

		while (children.hasNext()) {
			String child = children.next(); // assign the next String to child

			try {
				this.remove(child); // remove child
			} catch (InvalidStringException e) {
				System.out.println(e); // print out error message
			} catch (StringNotFoundException e) {
				System.out.println(e); // print out error message
			}

		}
		this.count = 0; // reset count to 0
	}

	/**
	 * Method to add a string with an associated data object to the trie. Calls on
	 * the TrieNode add method if the word passed to the method is valid, not null,
	 * and not already in the trie.
	 * 
	 * @param word String to be added to the trie
	 * @param data T object to be assigned to the last node of the word
	 * @throws StringExistsException  throw exception if string already exists
	 * @throws InvalidStringException throw exception if invalid string entered
	 */

	public void add(String word, T data) throws StringExistsException, InvalidStringException {

		// assign false to invalid, 0 to ascii

		boolean invalid = false;
		int ascii = 0;

		// loop through the word and assign true to invalid
		// if the integer value is greater than 255

		for (int i = 0; i < word.length(); i++) {
			ascii = (int) word.charAt(i);
			if (ascii > 255) {
				invalid = true;
			}
		}

		// if the word is null or invalid is true, throw
		// an InvalidStringException

		if (word == "" || invalid) {
			throw new InvalidStringException(word);
		}

		// if the trie already contains the word as
		// a prefix then throw a StringExistsException

		if (containsPrefix(word)) {
			throw new StringExistsException(word);
		}

		root.add(word, data); // add the word and its data to the root
		this.count++; // increment the count
	}

	/**
	 * Method to remove a string from the trie. Calls on the TrieNode remove method
	 * if the word passed to the method is valid, not null, and in the trie. Returns
	 * the data object associated with the node containing the last character.
	 * 
	 * @param word String to be removed from the trie
	 * @return T data object of the node containing the last character
	 * @throws InvalidStringException  throw exception if invalid string is entered
	 * @throws StringNotFoundException throw exception if string is not found
	 */

	public T remove(String word) throws InvalidStringException, StringNotFoundException {

		// assign false to invalid, 0 to ascii

		boolean invalid = false;
		int ascii = 0;

		// loop through the word and assign true to invalid
		// if the integer value is greater than 255

		for (int i = 0; i < word.length(); i++) {
			ascii = (int) word.charAt(i);
			if (ascii > 255) {
				invalid = true;
			}
		}

		// if the word is null or invalid is true, throw
		// an InvalidStringException

		if (word == "" || invalid) {
			throw new InvalidStringException(word);
		}

		// if the trie doesn't contain the word,
		// throw a StringNotFoundException

		if (!contains(word)) {
			throw new StringNotFoundException(word);
		}

		count--; // decrement count
		return root.remove(word); // return data from the recursive remove call on the root

	}

	/**
	 * Method that determines if the word passed to it is in the trie as a word.
	 * Returns true if is in the trie, else it returns false (including if the word
	 * is in the trie as a prefix).
	 * 
	 * @return boolean true if word is in the trie as a word, else it returns false
	 */

	public boolean contains(String word) {

		// call findEndNode method on the root and assign the
		// returned node to temp

		TrieNodeADT<T> temp = root.findEndNode(word);

		// if temp is not null

		if (temp != null) {

			// if temp is a leaf node, return true

			if (temp.isLeaf()) {
				return true;
			}

			// else return false

			else {
				return false;
			}
		}

		// else return false

		else {
			return false;
		}

	}

	/**
	 * Method to determine if the trie contains the word passed to this method as a
	 * prefix. Returns true if the word is in the trie as a word or prefix, else it
	 * returns false.
	 * 
	 * @param prefix String that is passed to see if it's in the trie
	 * @return boolean true if in the trie, else false
	 */

	public boolean containsPrefix(String prefix) {

		// call findEndNode method on the root and assign the
		// returned node to temp

		TrieNodeADT<T> temp = root.findEndNode(prefix);

		// if temp is not null, return true

		if (temp != null) {
			return true;
		}

		// else return false

		else {
			return false;
		}
	}

	/**
	 * Method to find a word in the trie. Throws an exception if not found. Returns
	 * the data element in the end node of the word, if it is found.
	 * 
	 * @param word String to be searched for in the trie
	 * @return T data object from the end node
	 * @throws StringNotFoundException throws exception if string is not found
	 */

	public T find(String word) throws StringNotFoundException {

		// call findEndNode method on the root and assign the
		// returned node to temp

		TrieNodeADT<T> temp = root.findEndNode(word);

		// if temp is not null, return the data of temp

		if (temp != null) {
			return temp.getData();
		}

		// else throw StringNotFoundException

		else {
			throw new StringNotFoundException(word);
		}

	}

	/**
	 * Method that returns an integer value representing the number of words stored
	 * in the trie.
	 * 
	 * @return int number of words
	 */

	public int size() {
		return this.count; // return count
	}

	/**
	 * Method that determines if the trie is empty or not.
	 * 
	 * @return true if empty, else false
	 */

	public boolean isEmpty() {
		return (this.count == 0); // return true if no words, else false
	}

	/**
	 * Method that returns a string iterator, which iterates through the tree in
	 * ascending order
	 * 
	 * @return Iterator<String> ascending string iterator
	 */

	public Iterator<String> ascendingStringIterator() {
		return root.preorderIterator(); // return preorderIterator called by the root
	}

	/**
	 * Method that returns a string iterator, which iterates through the tree in
	 * descending order
	 * 
	 * @return Iterator<String> descending string iterator
	 */

	public Iterator<String> descendingStringIterator() {
		return root.reversePreorderIterator(); // return reversePreorderIteratore called by the root
	}

}
