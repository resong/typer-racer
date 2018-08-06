package com.resong.racer.structures;

import java.util.Iterator;

/**
 * Public interface for a node in a trie.
 * 
 * @author Jeff Shantz <x@y> x = jshantz4, y = csd.uwo.ca
 * @param <T> Type of object that will be stored in the leaves of the trie
 */
public interface TrieNodeADT<T> {

	public void add(String word, T data);

	public T remove(String word);

	public TrieNodeADT<T> findEndNode(String word);

	public T getData();

	public boolean isLeaf();

	public Character getCharacter();

	public Iterator<TrieNodeADT<T>> childNodeIterator();

	public Iterator<String> preorderIterator();

	public Iterator<String> reversePreorderIterator();

	public String toString();
}