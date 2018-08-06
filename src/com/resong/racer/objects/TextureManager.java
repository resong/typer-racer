package com.resong.racer.objects;

import java.awt.Component;
import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.media.j3d.Appearance;
import javax.media.j3d.Texture;

/*******************************************************************************
 *
 * TextureManager.java
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

import com.sun.j3d.utils.image.TextureLoader;

/**
 * Loads and retrieves game textures
 *
 * @author Jeff Shantz
 */
public class TextureManager implements Runnable {

	/***************************************************************************
	 * CONSTANT DECLARATIONS
	 **************************************************************************/

	// Whether or not to load images when requested (true), or load all at the
	// beginning of the game (false)
	private final static boolean LOAD_JUST_IN_TIME = false;

	// Path under which textures are found
	private final static String TEXTURES_PATH = "images";

	// Building texture filename prefixes and file counts
	private final static String[] BUILDING_TEXTURES = { "shop", "building" };
	private final static int[] BUILDING_COUNTS = { 26, 34 };

	// Obstacle texture filename prefixes and file counts
	private final static String[] OBSTACLE_TEXTURES = { "brick", "marble", "rock", "stucco", "tiles", "carpet",
			"leather", "fabric", "metal", "metalfloor", "metalplates", "copper", "fiberglass", "plastic", "circuit" };
	private final static int[] OBSTACLE_COUNTS = { 21, 6, 6, 4, 2, 11, 7, 5, 4, 7, 22, 2, 10, 1, 6 };

	// Ground texture filename prefixes and file counts
	private final static String[] GROUND_TEXTURES = { "road" };
	private final static int[] GROUND_COUNTS = { 3 };

	// Sky texture filename prefixes and file counts
	private final static String[] SKY_TEXTURES = { "back", "sky" };
	private final static int[] SKY_COUNTS = { 3, 8 };

	// Player avatar texture filename prefixes and file counts
	private final static String[] PLAYER_TEXTURES = { "player" };
	private final static int[] PLAYER_COUNTS = { 1 };

	/***************************************************************************
	 * ENUMERATIONS
	 **************************************************************************/

	/**
	 * Type of texture to load/retrieve
	 */
	public static enum TextureType {
		BUILDING, GROUND, SKY, PLAYER, OBSTACLE
	};

	/***************************************************************************
	 * STATIC VARIABLES
	 **************************************************************************/

	// Singleton TextureManager object
	private static TextureManager manager = null;

	// Observer component for images loaded
	private static Component component = null;

	/***************************************************************************
	 * INSTANCE VARIABLES
	 **************************************************************************/

	// Whether or not all game textures have been loaded
	private boolean texturesLoaded;

	// Maps filenames to texture objects
	private HashMap<String, Texture> textureMap;

	// Used to return random textures
	private Random random;

	// Stores all building textures
	private Texture[] building;

	// Stores all ground textures
	private Texture[] ground;

	// Stores all obstacle textures
	private Texture[] obstacle;

	// Stores all sky textures
	private Texture[] sky;

	// Stores all player avatar textures
	private Texture[] player;

	// Object to which updates will be provided as textures are being loaded
	private Notifiable notifiable;

	/***************************************************************************
	 * CONSTRUCTORS
	 **************************************************************************/

	/**
	 * Creates a new TextureManager
	 *
	 * @param notifiable Object to notify as textures are being loaded
	 */
	private TextureManager(Notifiable notifiable) {
		this.random = new Random();
		this.textureMap = new HashMap<String, Texture>();
		this.notifiable = notifiable;
		this.texturesLoaded = false;
	}

	/**
	 * Returns an appearance object using a random texture of the specified type
	 *
	 * @param type The type of texture to apply to the appearance
	 * @return An appearance object using a random texture of the specified type
	 */
	public Appearance getRandomAppearance(TextureType type) {

		Appearance app = new Appearance();
		Texture texture = this.getRandomTexture(type);
		app.setTexture(texture);
		return app;
	}

	/**
	 * Implements the Runnable interface. Loads all textures and then notifies the
	 * Notifiable object when finished
	 */
	public void run() {
		this.loadTextures();
		this.notifiable.done();
	}

	/**
	 * Returns a random texture of the specified type
	 *
	 * @param type The type of texture to retrieve
	 * @return A random texture of the specified type
	 */
	public Texture getRandomTexture(TextureType type) {

		// The texture array to use
		Texture[] textures = null;

		// If we are using JIT loading, then we need to load the texture
		if (LOAD_JUST_IN_TIME)
			return loadRandomTexture(type);

		// Choose the texture array based on the specified type
		switch (type) {
		case BUILDING:
			textures = building;
			break;
		case OBSTACLE:
			textures = obstacle;
			break;
		case GROUND:
			textures = ground;
			break;
		case SKY:
			textures = sky;
			break;
		case PLAYER:
			textures = player;
			break;
		default:
			return null;
		}

		// Return a random texture from the texture array
		return textures[random.nextInt(textures.length)];
	}

	/**
	 * Loads and returns a random texture object of the specified type
	 *
	 * @param type The type of texture to retrieve
	 * @return A random texture object of the specified type
	 */
	private Texture loadRandomTexture(TextureType type) {

		// The prefix and count arrays to use
		String[] prefixArray = null;
		int[] countArray = null;

		// Choose the prefix/count arrays based on the specified type
		switch (type) {
		case BUILDING:
			prefixArray = BUILDING_TEXTURES;
			countArray = BUILDING_COUNTS;
			break;
		case GROUND:
			prefixArray = GROUND_TEXTURES;
			countArray = GROUND_COUNTS;
			break;
		case OBSTACLE:
			prefixArray = OBSTACLE_TEXTURES;
			countArray = OBSTACLE_COUNTS;
			break;
		case SKY:
			prefixArray = SKY_TEXTURES;
			countArray = SKY_COUNTS;
			break;
		case PLAYER:
			prefixArray = PLAYER_TEXTURES;
			countArray = PLAYER_COUNTS;
			break;
		default:
			return null;
		}

		// Select a random prefix
		int i = random.nextInt(prefixArray.length);

		// Select a random texture having the chosen prefix
		String filename = prefixArray[i] + (1 + random.nextInt(countArray[i])) + ".jpg";

		// Check if it's already been loaded. If so, just return it.
		if (this.textureMap.containsKey(filename))
			return this.textureMap.get(filename);
		else
			// Otherwise, load and return it
			return this.loadTexture(filename);
	}

	/**
	 * Creates a new singleton TextureManager using the specified observer component
	 * and notifiable object
	 *
	 * @param cmp        The observer component used for loading imges
	 * @param notifiable The object to which loading updates will be provided
	 */
	public static void initialize(Component cmp, Notifiable notifiable) {

		// If the singleton object has not yet been created
		if (manager == null) {

			// Store the observer component, and create the singleton
			component = cmp;
			manager = new TextureManager(notifiable);
		}
	}

	/**
	 * Loads the specified image
	 *
	 * @param klazz    Class used to obtain the image resource
	 * @param filename Name of the image file
	 * @return An Image object loaded from the specified file
	 * @throws Exception If the image cannot be loaded
	 */
	public static Image loadImage(Class klazz, String filename) throws Exception {

		URL url = klazz.getClassLoader().getResource(TEXTURES_PATH + "/" + filename);
		return ImageIO.read(url);
	}

	/**
	 * Returns the singleton TextureManager object. Note that initialize() must be
	 * called before this method.
	 *
	 * @return The TextureManager
	 * @throws Exception If initialize() has not been called
	 * @see TextureManager#initialize(java.awt.Component, TextureManager.Notifiable)
	 */
	public static TextureManager getManager() throws Exception {

		if (manager == null)
			throw new Exception("Must call static 'initialize' method before calling getManager()");
		else
			return manager;

	}

	/**
	 * Loads all textures, if just-in-time loading is disabled or the textures have
	 * not yet been loaded
	 */
	public void loadTextures() {

		// If the textures were already loaded, or we're using just-in-time
		// loading, then do not load the textures
		if ((texturesLoaded) || (LOAD_JUST_IN_TIME)) {
			return;
		}

		// Load all textures
		loadBuildings();
		loadObstacles();
		loadGround();
		loadSky();
		loadPlayer();

		texturesLoaded = true;
	}

	/**
	 * Gets the total number of textures of a given type
	 *
	 * @param textureCounts Texture count array
	 * @return The total number of textures of a given type
	 */
	private int getTotalCount(int[] textureCounts) {

		int count = 0;

		// Sum all values in the count array
		for (int i = 0; i < textureCounts.length; i++) {
			count += textureCounts[i];
		}

		return count;
	}

	/**
	 * Loads all building textures
	 */
	private void loadBuildings() {

		building = new Texture[getTotalCount(BUILDING_COUNTS)];
		loadTextures(BUILDING_TEXTURES, BUILDING_COUNTS, building);
	}

	/**
	 * Loads all obstacle textures
	 */
	private void loadObstacles() {

		obstacle = new Texture[getTotalCount(OBSTACLE_COUNTS)];
		loadTextures(OBSTACLE_TEXTURES, OBSTACLE_COUNTS, obstacle);
	}

	/**
	 * Loads all ground textures
	 */
	private void loadGround() {

		ground = new Texture[getTotalCount(GROUND_COUNTS)];
		loadTextures(GROUND_TEXTURES, GROUND_COUNTS, ground);

	}

	/**
	 * Loads all sky textures
	 */
	private void loadSky() {

		sky = new Texture[getTotalCount(SKY_COUNTS)];
		loadTextures(SKY_TEXTURES, SKY_COUNTS, sky);

	}

	/**
	 * Loads all player textures
	 */
	private void loadPlayer() {

		player = new Texture[getTotalCount(PLAYER_COUNTS)];
		loadTextures(PLAYER_TEXTURES, PLAYER_COUNTS, player);

	}

	/**
	 * Loads all textures specified in the filename prefix array and stores them in
	 * the specified texture array
	 *
	 * @param filenameArray Filename prefix array
	 * @param countArray    Texture count array
	 * @param textureArray  Array in which loaded textures should be stored
	 */
	private void loadTextures(String[] filenameArray, int[] countArray, Texture[] textureArray) {

		int idx = 0;

		// Iterate over all prefixes
		for (int i = 0; i < filenameArray.length; i++) {

			// Get the next prefix
			String prefix = filenameArray[i];

			// Load all images for the specified prefix, storing the results
			// in the texture array
			for (int j = 1; j <= countArray[i]; j++) {
				textureArray[idx++] = loadTexture(prefix + j + ".jpg");
			}
		}
	}

	/**
	 * Loads and returns a single texture
	 *
	 * @param filename Filename of the texture to load
	 * @return A texture loaded from the specified file
	 */
	private Texture loadTexture(String filename) {

		// Notify the observer that we're loading this texture
		if (notifiable != null)
			notifiable.notify("Loading " + filename + "...");

		// Load the texture
		java.net.URL url = this.getClass().getClassLoader().getResource(TEXTURES_PATH + "/" + filename);
		Texture texture = new TextureLoader(url, component).getTexture();

		// Store it in the texture map and return it
		this.textureMap.put(filename, texture);
		return texture;
	}

	/**
	 * Interface to be implemented by classes wishing to be received progress
	 * updates as textures are loaded
	 *
	 * @author Jeff Shantz
	 */
	public interface Notifiable {

		/**
		 * Called when a new texture is being loaded
		 *
		 * @param status Message describing the texture being loaded
		 */
		public void notify(String status);

		/**
		 * Called when loading of textures is complete
		 */
		public void done();
	}
}
