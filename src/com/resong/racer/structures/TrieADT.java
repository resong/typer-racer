package com.resong.racer.structures;

import java.util.Iterator;

import com.resong.racer.exceptions.InvalidStringException;
import com.resong.racer.exceptions.StringExistsException;
import com.resong.racer.exceptions.StringNotFoundException;

/**
 * Public interface for a standard trie data structure.
 * 
 * @author Jeff Shantz <x@y> x = jshantz4, y = csd.uwo.ca
 * @param <T> Type of object that will be stored in the leaves of the trie
 */
public interface TrieADT<T> {

	public void add(String word, T data) throws StringExistsException, InvalidStringException;

	public T remove(String word) throws InvalidStringException, StringNotFoundException;

	public TrieNodeADT<T> getRoot();

	public void clear();

	public boolean contains(String word);

	public boolean containsPrefix(String prefix);

	public T find(String word) throws StringNotFoundException;

	public int size();

	public boolean isEmpty();

	public Iterator<String> ascendingStringIterator();

	public Iterator<String> descendingStringIterator();
}
