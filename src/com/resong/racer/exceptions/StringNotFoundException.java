package com.resong.racer.exceptions;

/**
 * Represents the situation in which a string is not found.
 * 
 * @author Rebecca Song
 *
 */

public class StringNotFoundException extends Exception {

	/**
	 * Prints error message when the string entered is not found
	 * 
	 * @param s the string which could not be found
	 */

	public StringNotFoundException(String s) {
		super(s + " was not found."); // calls parent method and prints out a string
	}

}
