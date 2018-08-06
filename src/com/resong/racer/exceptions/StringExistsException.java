package com.resong.racer.exceptions;

/**
 * Represents the situation in which a string already exists.
 * 
 * @author Rebecca Song
 *
 */

public class StringExistsException extends Exception {

	/**
	 * Prints error message when the entered string already exists
	 * 
	 * @param s the string that already exists
	 */

	public StringExistsException(String s) {
		super(s + " already exists."); // calls parent method and prints out a string
	}

}
