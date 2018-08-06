package com.resong.racer;

/**
 * Class to construct a TyperRacer object, and is a child class of GameWindow. 
 * Uses various methods to create obstacles and remove them in the game, 
 * determine if the game is ready to be played, and determine the actions 
 * taken when the user presses certain keys on the keyboard. 
 * 
 * @author CS1027 and Rebecca Song
 */

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

import javax.swing.UIManager;

import com.resong.racer.objects.FrameEvent;
import com.resong.racer.objects.GameWindow;
import com.resong.racer.objects.HUD;
import com.resong.racer.objects.Obstacle;
import com.resong.racer.structures.ArrayIndexedList;
import com.resong.racer.structures.Trie;

public class TyperRacer extends GameWindow {

	/////////////// Attributes ///////////////

	private Trie<Obstacle> obstacles; // trie storing current obstacles in level and their words

	private ArrayIndexedList<String> words; // list storing words from dictionary

	private int fElapsed; // number of frames elapsed since last frame elapse

	private int fThreshold; // threshold number of frames

	/////////////// Constructors ///////////////

	public TyperRacer() throws Exception {
		super();
		this.obstacles = new Trie<Obstacle>();
		this.words = this.getWords();
		this.fElapsed = 0;
		this.fThreshold = 200;

	}

	////////////// Methods ///////////////

	/**
	 * Method to determine if the game is ready for the user to play.
	 */

	public void gameReady() {

		try {

			// get the HUD and set the player's health to max health (100)

			HUD display = this.getHUD();
			display.setHealth(100);

			// create a Random object, and assign the size of the list to size

			Random randomGenerator = new Random();
			int size = this.words.size();

			// loop through and add 10 random obstacles to the game

			for (int i = 0; i < 10; i++) {
				int random = randomGenerator.nextInt(size);
				this.addWord(this.words.get(random));
			}

			super.startGame(); // displays the game window and starts moving the player avatar
		}

		catch (Exception e) {
			System.out.println("Not Ready"); // print message saying game is not ready
		}

	}

	/**
	 * Helper method to read and add words from the provided dictionary file to an
	 * ArrayIndexList of strings.
	 * 
	 * @return ArrayIndexedList<String> list of strings from dictionary file
	 * @throws Exception
	 */

	private ArrayIndexedList<String> getWords() throws Exception {

		// create new ArrayIndexedList<String> called
		// dictionary, and declare a String line

		ArrayIndexedList<String> dictionary = new ArrayIndexedList<String>();
		String line;

		try {
			// create a BufferedReader object to read the dictionary file
			// line by line, and add each line to the list if it isn't null
			BufferedReader br = new BufferedReader(new FileReader("src/resources/dictionary.txt"));
			while ((line = br.readLine()) != null) {
				dictionary.addToRear(line);
			}
			return dictionary; // return the list
		}

		catch (Exception ex) {
			System.out.println("Error reading dictionary: " + ex); // print error message
			return null;
		}
	}

	/**
	 * Helper method to add an obstacle to the game with the word passed to this
	 * method associated with it
	 * 
	 * @param word String to be associated with the new object
	 * @throws Exception throws exception passed from the add methods
	 */

	private void addWord(String word) throws Exception {
		Obstacle ob = super.addObstacle(word); // super class adds an obstacle
		this.obstacles.add(word, ob); // add the word and obstacle to the trie
	}

	/**
	 * Method determines what actions to take when the user presses a key.
	 * 
	 * @param e KeyEvent
	 */

	public void keyPressed(KeyEvent e) {

		// switch between the following actions based on
		// what e.getKeyCode() returns

		switch (e.getKeyCode()) {

		// if space bar is pressed, pause the game
		// if it is not paused already, else resume the game

		case KeyEvent.VK_SPACE:
			HUD display = this.getHUD();
			if (!display.isPaused()) {
				display.setPaused(true);
			} else {
				display.setPaused(false);
			}
			this.pauseResume();
			break;

		// if the left arrow key is pressed and the game is
		// not paused, and the player's x-position is greater than
		// or equal to -2.65 (i.e. player is still on the road),
		// move the player left by 0.1 units

		case KeyEvent.VK_LEFT:
			if ((!this.getHUD().isPaused()) && this.getPlayerX() >= -2.65) {
				this.movePlayerX((float) -0.1);
			}
			break;

		// if the right arrow key is pressed and the game is
		// not paused, and the player's x-position is less than or
		// equal to 2.65 (i.e. player is still on the road), move
		// the player right by 0.1 units

		case KeyEvent.VK_RIGHT:
			if ((!this.getHUD().isPaused()) && this.getPlayerX() <= 2.65) {
				this.movePlayerX((float) +0.1);
			}
			break;

		// if any other key is pressed

		default:
			display = this.getHUD(); // get the HUD
			display.appendCharacter(e.getKeyChar()); // append the character pressed by the user to the word in the HUD

			// if the trie doesn't contain the word in the
			// HUD as a prefix, erase the word from the HUD

			if (!obstacles.containsPrefix(display.getCurrentWord())) {
				display.setCurrentWord("");
			}

			try {

				// if the trie contains the current word in the HUD,
				// remove the word from the trie, erase it from the HUD
				// and increment the word count

				if (obstacles.contains(display.getCurrentWord())) {
					(obstacles.remove(display.getCurrentWord())).remove();
					display.setCurrentWord("");
					display.incrementWordCount();

				}
			}

			catch (Exception ex) {
				return; // return
			}
		}
	}

	/**
	 * Method called when player collides with an obstacle. It decrements the
	 * player's health, and ends the game if the player's health reaches 0.
	 */

	public void collisionOccurred() {

		// get HUD and decrement health by 5 units

		HUD display = this.getHUD();
		display.decrementHealth(5);

		// if player health is 0, end the game

		if (display.getHealth() == 0) {
			this.gameOver();
		}

	}

	/**
	 * Method that increments the frames elapsed, and adds an obstacle and resets
	 * the frames elapsed to 0 if it surpasses the frames threshold. If the player's
	 * z-position is greater than 184, increment the current level, add a number of
	 * obstacles to the game equivalent to the current level, decrement the frames
	 * threshold and repaint the game objects.
	 * 
	 * @param event FrameEvent
	 */

	public void tick(FrameEvent event) {

		try {

			// increment number of frames elapsed and create a new Random object

			this.fElapsed += event.getFrameCountSinceLastEvent();
			Random randomGenerator = new Random();

			// if number of frames elapsed is equal or greater than the threshold

			if (this.fElapsed >= this.fThreshold) {

				// get a random integer within the boundaries of the word list
				// and add an obstacle with a word associated with it from the
				// corresponding index in the words list

				int random = randomGenerator.nextInt(this.words.size());
				this.addWord(this.words.get(random));
				this.fElapsed = 0; // reset frames elapsed to 0

			}

			// get z position of player

			int position = (int) event.getPosition().getZ();

			// if position is equal to or greater than 185

			if (position > 184) {

				// get the HUD and increment the level

				HUD display = this.getHUD();
				display.incrementLevel();

				// add the number of obstacles with random words
				// corresponding to the number of the current level

				for (int i = 0; i < display.getLevel(); i++) {
					int random = randomGenerator.nextInt(this.words.size());
					this.addWord(this.words.get(random));
				}

				// decrement the frames threshold so long
				// as it is not lower than 50

				if (this.fThreshold >= 60) {
					this.fThreshold -= 10;
				}

				this.repaintObjects(); // repaint objects

			}
		}

		catch (Exception e) {
			System.out.println("Invalid object added."); // error message indicating object wasn't added
		}

	}

	/**
	 * Main method creates a TyperRacer object and runs the game
	 * 
	 * @param args
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			// Ignore -- not fatal
		}

		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {

				try {
					TyperRacer game = new TyperRacer();
				} catch (Exception ex) {
					System.out.println(ex.toString());
				}
			}
		});
	}
}
