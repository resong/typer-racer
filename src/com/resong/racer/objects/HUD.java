package com.resong.racer.objects;

/*******************************************************************************
 *
 * HUD.java
 *
 * Written by Jeff Shantz
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * The license can be found on the WWW at:
 * http://www.fsf.org/copyleft/gpl.html
 *
 * Or by writing to:
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 ******************************************************************************/

/**
 * Stores data to be displayed on the game HUD (heads-up display)
 * 
 * @author Jeff Shantz
 */
public class HUD {

	/***************************************************************************
	 * CONSTANT DECLARATIONS
	 **************************************************************************/

	// Allowable range for the player's health
	private final int MIN_HEALTH = 0;
	private final int MAX_HEALTH = 100;

	// Starting level
	private final int INITIAL_LEVEL = 1;

	/***************************************************************************
	 * INSTANCE VARIABLES
	 **************************************************************************/

	// Health of the player
	private int health;

	// Current level
	private int level;

	// Number of words successfully typed
	private int wordCount;

	// Current word being typed
	private String currentWord;

	// Whether or not the game is over
	private boolean gameOver;

	// Whether or not the game is paused
	private boolean isPaused;

	/***************************************************************************
	 * CONSTRUCTORS
	 **************************************************************************/

	/**
	 * Creates a new HUD
	 */
	public HUD() {
		this.reset();
	}

	/***************************************************************************
	 * PUBLIC METHODS
	 **************************************************************************/

	/**
	 * Resets the HUD
	 */
	public void reset() {
		this.level = INITIAL_LEVEL;
		this.wordCount = 0;
		this.health = MIN_HEALTH;
		this.gameOver = false;
		this.isPaused = false;
		this.currentWord = "";
	}

	/**
	 * Returns whether or not the game is over
	 * 
	 * @return True, if the game is over; false otherwise
	 */
	public boolean isGameOver() {
		return this.gameOver;
	}

	/**
	 * Indicates to the HUD that the game is over
	 */
	public void setGameOver() {
		this.gameOver = true;
	}

	/**
	 * Returns a Boolean value indicating whether or not the game is currently
	 * paused
	 * 
	 * @return True, if the game is paused; false otherwise
	 */
	public boolean isPaused() {
		return this.isPaused;
	}

	/**
	 * Sets whether or not the HUD should display a "PAUSED" message
	 * 
	 * @param paused Whether or not the game is paused
	 */
	public void setPaused(boolean paused) {
		this.isPaused = paused;
	}

	/**
	 * Returns the health of the player as an integer between 0 and 100
	 * 
	 * @return Health of the player (0 - 100)
	 */
	public int getHealth() {
		return this.health;
	}

	/**
	 * Sets the health of the player
	 * 
	 * @param health The new health value
	 * @throws IllegalArgumentException If the value specified is outside of the
	 *                                  range [0, 100]
	 */
	public void setHealth(int health) throws IllegalArgumentException {

		if ((this.health < MIN_HEALTH) || (this.health > MAX_HEALTH)) {
			throw new IllegalArgumentException("Invalid health value specified -- range is limited between 0 and 100");
		}
		this.health = health;
	}

	/**
	 * Decrements the player's health by the specified amount
	 * 
	 * @param amount The amount by which to decrement the player's health
	 */
	public void decrementHealth(int amount) {

		this.health -= amount;

		if (this.health < MIN_HEALTH) {
			this.health = MIN_HEALTH;
		} else if (this.health > MAX_HEALTH) {
			this.health = MAX_HEALTH;
		}

	}

	/**
	 * Appends the specified character to the current word being typed
	 * 
	 * @param c The character to append
	 */
	public void appendCharacter(char c) {
		this.currentWord += c;
	}

	/**
	 * Sets the current word being typed
	 * 
	 * @param word The word being typed
	 */
	public void setCurrentWord(String word) {
		this.currentWord = word;
	}

	/**
	 * Returns the current word being typed
	 * 
	 * @return The word being typed
	 */
	public String getCurrentWord() {
		return this.currentWord;
	}

	/**
	 * Returns the current level
	 * 
	 * @return The current level
	 */
	public int getLevel() {
		return this.level;
	}

	/**
	 * Sets the current level
	 * 
	 * @param level The current level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Increments the current level
	 */
	public void incrementLevel() {
		this.level++;
	}

	/**
	 * Returns the number of words successfully typed by the user
	 * 
	 * @return The number of words successfully typed by the user
	 */
	public int getWordCount() {
		return this.wordCount;
	}

	/**
	 * Sets the number of words successfully typed by the user
	 * 
	 * @param count The number of words typed by the user
	 */
	public void setWordCount(int count) {
		this.wordCount = count;
	}

	/**
	 * Increments the number of words successfully typed by the user
	 */
	public void incrementWordCount() {
		this.wordCount++;
	}
}
