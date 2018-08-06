package com.resong.racer.exceptions;

/**
 * Represents the situation in which an invalid string is entered.
 * 
 * @author Rebecca Song
 *
 */

public class InvalidStringException extends Exception {

	/**
	 * Prints error message when an invalid string is entered
	 * 
	 * @param s the invalid string entered
	 */

	public InvalidStringException(String s) {
		super(s + " is an invalid string."); // calls parent method and prints out a string
	}

}
