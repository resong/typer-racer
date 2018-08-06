package com.resong.racer.objects;

/*******************************************************************************
 *
 * GameWindow.java
 *
 * Modified (heavily) by Jeff Shantz
 *
 * This code is based on a racing example taken from the book
 * "Java 3D Programming" by Daniel Selman and published by Manning Publications.
 * (Copyright (C) 2001 Daniel Selman) http://manning.com/selman
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
 * Authors can be contacted at:
 * Daniel Selman: daniel@selman.org
 * If you make changes you think others would like, please
 * contact one of the authors or someone at the
 * www.j3d.org web site.
 *
 ******************************************************************************/
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.WindowConstants;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.resong.racer.behaviours.FrameBehaviour;
import com.resong.racer.behaviours.KeyBehaviour;
import com.resong.racer.listeners.CollisionListener;
import com.resong.racer.listeners.FrameListener;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewerAvatar;
import com.sun.j3d.utils.universe.ViewingPlatform;

/**
 * Main TyperRacer game window
 *
 * @author Jeff Shantz
 * @author Daniel Selman
 */
public abstract class GameWindow extends javax.swing.JFrame
		implements KeyListener, CollisionListener, FrameListener, TextureManager.Notifiable {

	/***************************************************************************
	 * CONSTANT DECLARATIONS
	 **************************************************************************/

	// Dimensions of the window
	private final static int WINDOW_WIDTH = 800;
	private final static int WINDOW_HEIGHT = 600;

	/***************************************************************************
	 * INSTANCE VARIABLES
	 **************************************************************************/

	// Canvas on which the game will be drawn
	private HUDCanvas3D gameCanvas;

	// Used to load and retrieve textures for game objects
	private TextureManager textureMgr;

	// Root branch of the scene graph
	private BranchGroup bgRoot;

	// Transform group for the root branch
	private TransformGroup tgRoot;

	// Transform group for the player
	private TransformGroup playerGroup;

	// Alpha used to control driving speed
	private Alpha playerAlpha;

	// Used to move the player
	private PositionInterpolator posInt;

	// "Heads-up display"
	private HUD hud;

	// The player's avatar
	private Car playerCar;

	// Monitors user keypresses
	private KeyBehaviour keys;

	// Monitors elapsed frames
	private FrameBehaviour frames;

	// Scene background
	private Sphere sky;

	// Road on which the user drives
	private Road road;

	// List of buildings in the scene
	private java.util.ArrayList<Building> buildings;

	// Game splash screen
	private GameSplashScreen splash;

	/***************************************************************************
	 * CONSTRUCTORS
	 **************************************************************************/

	/**
	 * Creates a new TyperRacer game window and loads all game textures
	 * 
	 * @throws Exception If loading textures fails in any way
	 */
	public GameWindow() throws Exception {

		this.buildings = new java.util.ArrayList<Building>();
		this.hud = new HUD();

		// Create and show the splash screen
		splash = new GameSplashScreen(this);
		splash.setVisible(true);

		// Initialize the TextureManager
		TextureManager.initialize(this, this);
		this.textureMgr = TextureManager.getManager();

		// Load all textures on a separate thread
		Thread thrTextures = new Thread(textureMgr);
		thrTextures.start();
	}

	/***************************************************************************
	 * PUBLIC METHODS
	 **************************************************************************/

	/**
	 * Returns the game HUD (heads-up display) used to convey game information to
	 * the user
	 * 
	 * @return The game HUD
	 */
	public HUD getHUD() {
		return this.hud;
	}

	/**
	 * Adds a new obstacle to the game with a random texture, and having the
	 * specified word above it
	 * 
	 * @param word The word to draw above the obstacle
	 * @return The newly created Obstacle
	 */
	public Obstacle addObstacle(String word) {

		// New scenegraph branch for the obstacle and its text
		BranchGroup bg = new BranchGroup();

		// Randomly place the obstacle at a position on the road somewhere
		Vector3d position = new Vector3d(getRandomNumber(-2f, 4f), getRandomNumber(0.3f, 0.5f),
				getRandomNumber(Road.ROAD_LENGTH + 30, 160f));

		// Create the obstacle, add the word above it, and set its texture
		Vector3d scale = new Vector3d(1, 1, 1);
		Obstacle obstacle = new Obstacle(bg, position, scale);
		obstacle.setWord(word);
		obstacle.setAppearance(this.textureMgr.getRandomAppearance(TextureManager.TextureType.OBSTACLE));

		// Add the obstacle to the root transform group and return it
		this.tgRoot.addChild(bg);
		return obstacle;
	}

	/**
	 * Displays the game window and starts moving the player avatar
	 */
	public void startGame() {

		this.setVisible(true);
		this.playerAlpha.resume();
	}

	/**
	 * Called by the TextureManager to provide progress reports. Should not be
	 * called by subclasses
	 * 
	 * @param status The current texture file being loaded
	 */
	public void notify(String status) {
		splash.notify(status);
	}

	/**
	 * Called by the TextureManager when all textures are loaded
	 */
	public void done() {

		// Close the splash screen
		splash.dispose();

		try {

			// Initialize the game and notify child classes that we are ready
			// to roll
			this.setupGame();
			this.gameReady();

		} catch (Exception ex) {

			// If an error occurs setting up the game, exit
			System.out.println(ex.toString());
			ex.printStackTrace();
			System.exit(-1);
		}

	}

	/**
	 * Returns the current x-coordinate of the player avatar
	 * 
	 * @return The x-coordinate of the player avatar
	 */
	public float getPlayerX() {

		// Used to store the user's current translation
		Vector3f translate = new Vector3f();
		Transform3D t3d = new Transform3D();

		// Retrieve the user's current translation
		this.playerGroup.getTransform(t3d);
		t3d.get(translate);

		return translate.x;
	}

	/**
	 * Adds the specified amount to the player's x-coordinate, allowing the player
	 * avatar to be moved left and right. Negative values will move the player to
	 * the left, while positive values will move the player to the right
	 * 
	 * @param amount Amount by which to move the player
	 */
	public void movePlayerX(float amount) {

		// Used to store the user's current translation
		Vector3f translate = new Vector3f();
		Transform3D t3d = new Transform3D();

		// Get the user's current translation
		this.playerGroup.getTransform(t3d);
		t3d.get(translate);

		// Translate the user by the specified amount
		translate.x += amount;
		translate.y = 0.5f;
		t3d.setTranslation(translate);
		this.playerGroup.setTransform(t3d);
	}

	/**
	 * Stops the game and displays "GAME OVER" in the HUD
	 */
	public void gameOver() {

		// Stop moving
		this.posInt.setEnable(false);
		this.playerAlpha.pause();

		// Stop monitoring collisions, keypresses, and elapsed frames
		this.playerCar.removeCollisionListener(this);
		this.keys.removeKeyListener(this);
		this.frames.removeFrameListener(this);

		// Notify the HUD
		this.hud.setGameOver();
	}

	/***************************************************************************
	 * PROTECTED METHODS
	 **************************************************************************/

	/**
	 * Called after all textures have been loaded and the scene has been
	 * initialized. Subclasses should load any obstacles into the scene, set the HUD
	 * display as needed, and then call startGame()
	 * 
	 * @see GameWindow#startGame()
	 */
	protected abstract void gameReady();

	/**
	 * Used to repaint all static objects in the scene -- the sky, road, and all
	 * buildings. Should be called at the end of the level to make it appear as
	 * though the user is entering a new level (even though we're just looping
	 * around again and using the same objects on the screen)
	 */
	protected void repaintObjects() {

		this.paintSky();
		this.paintRoad();
		this.paintBuildings();
	}

	/***************************************************************************
	 * PRIVATE METHODS
	 **************************************************************************/

	/**
	 * Sets up the scene graph and initializes all game objects
	 * 
	 * @throws Exception If 3D setup fails for any reason
	 */
	private void setupGame() throws Exception {

		// Initialize the game canvas and add it to the window
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		gameCanvas = new HUDCanvas3D(config, hud, WINDOW_WIDTH, WINDOW_HEIGHT);

		// Initialize the game universe
		SimpleUniverse u = new SimpleUniverse(gameCanvas);

		ViewingPlatform platform = u.getViewer().getViewingPlatform();

		// Setup the player avatar
		this.playerGroup = platform.getViewPlatformTransform();

		Transform3D t3d = new Transform3D();
		t3d.set(new Vector3f(0, 0.5f, 0));
		playerGroup.setTransform(t3d);

		keys = new KeyBehaviour(this.playerGroup);
		keys.addKeyListener(this);
		keys.setSchedulingBounds(this.getBoundingSphere());
		u.getViewer().setAvatar(this.createPlayer());

		// Create the scene graph, apply textures, and start monitoring key presses
		this.createSceneGraph();
		this.repaintObjects();
		this.bgRoot.addChild(keys);
		u.addBranchGraph(this.bgRoot);
		u.getViewingPlatform().getViewPlatform().setActivationRadius(2);

		// Close the program when the window is closed
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// Set the window dimensions and center the window in the screen
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - WINDOW_WIDTH) / 2;
		int y = (screen.height - WINDOW_HEIGHT) / 2;
		this.setBounds(x, y, WINDOW_WIDTH, WINDOW_HEIGHT);

		this.setLayout(new BorderLayout());
		this.add(gameCanvas, BorderLayout.CENTER);
	}

	/**
	 * Applies a new random sky texture to the game background
	 */
	private void paintSky() {

		Appearance app = textureMgr.getRandomAppearance(TextureManager.TextureType.SKY);
		sky.setAppearance(app);
	}

	/**
	 * Applies a new road texture to the road
	 */
	private void paintRoad() {

		Appearance app = textureMgr.getRandomAppearance(TextureManager.TextureType.GROUND);
		road.setAppearance(app);
	}

	/**
	 * Applies new building/shop textures to the buildings in the game
	 */
	private void paintBuildings() {

		for (Building b : this.buildings) {
			Appearance app = textureMgr.getRandomAppearance(TextureManager.TextureType.BUILDING);
			b.setAppearance(app);
		}
	}

	/**
	 * Creates the game scene graph, adding all game objects to the scene
	 */
	private void createSceneGraph() {

		// Initialize the scene graph
		this.bgRoot = new BranchGroup();
		bgRoot.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		bgRoot.setCapability(Group.ALLOW_CHILDREN_WRITE);

		// Initialize the main transform group
		this.tgRoot = addBehaviors(bgRoot);

		// Create all game objects
		createBuildings(tgRoot);
		createRoad(tgRoot);
		createBackground(bgRoot);
		createNextLevelText(tgRoot);

		// Apply textures to all game objects
		this.repaintObjects();
	}

	/**
	 * Creates the scene background
	 * 
	 * @param bg Scene graph branch to which the background will be added
	 */
	private void createBackground(Group bg) {

		// Create the background and add it to the scene graph
		Background back = new Background();
		back.setApplicationBounds(getBoundingSphere());
		bg.addChild(back);

		// Display the background on a sphere
		sky = new Sphere(1.0f, Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS_INWARD, new Appearance());
		sky.getShape().setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

		// Add the sphere to the background branch
		BranchGroup bgGeometry = new BranchGroup();
		bgGeometry.addChild(sky);
		back.setGeometry(bgGeometry);
	}

	/**
	 * Creates the game road
	 * 
	 * @param g Group to which the road will be added
	 * @return The newly created group representing the road
	 */
	private Group createRoad(Group g) {
		this.road = new Road(g, new Vector3d(0, 0, 0), new Vector3d(1, 1, 1));
		// return road.createObject(,
		// this.textureMgr.getRandomTexture(TextureManager.TextureType.GROUND), this);
		return road;
	}

	/**
	 * Helper method that returns a random floating point number between basis and
	 * basis + random
	 * 
	 * @param basis  The minimum value
	 * @param random The "dynamic" value
	 * @return A random floating point number between basis and basis + random
	 */
	private float getRandomNumber(float basis, float random) {
		return basis + ((float) Math.random() * random);
	}

	/**
	 * Creates a new building and adds it to the scene using the specified x- and
	 * z-coordinate values as bases for the random position selected
	 * 
	 * @param bg     Group to which the building will be added
	 * @param xBasis Minimum x-coordinate at which the building will be placed
	 * @param zBasis Minimum z-coordinate at which the building will be placed
	 */
	private void createBuilding(BranchGroup bg, float xBasis, float zBasis) {

		// Generate a random position for the building, and set its scale to 100%
		Vector3d position = new Vector3d(getRandomNumber(xBasis, 0.25f), getRandomNumber(1.0f, 0.5f),
				getRandomNumber(zBasis, 0.5f));
		Vector3d scale = new Vector3d(1, 1, 1);

		// Create the building and add it to the building list
		Building building = new Building(bg, position, scale);
		this.buildings.add(building);
	}

	/**
	 * Creates all buildings along the side of the road
	 * 
	 * @param g Group to which the buildings will be added
	 * @return The newly-created group containing all buildings
	 */
	private Group createBuildings(Group g) {

		// Group to store the buildings
		BranchGroup bg = new BranchGroup();

		// Iterate over the entire road, adding buildings on the left
		// and right side. This loop leaves a 30 unit buffer at the
		// end of level, as the user drives out into the "country"
		for (int n = (int) Road.ROAD_LENGTH + 30; n < 0; n = n + 10) {

			this.createBuilding(bg, -4.0f, n);
			this.createBuilding(bg, 4.0f, n);
		}

		g.addChild(bg);
		return bg;
	}

	/**
	 * Creates the "GET READY!" text that the user sees as he/she drives out into
	 * the "country" at the end of a level. This is intended to prepare the user for
	 * the next level
	 * 
	 * @param g Group to which the text will be added
	 * @return The newly-created group containing the next level text
	 */
	private Group createNextLevelText(Group g) {

		// Used to rotate and position the text
		TransformGroup tg = new TransformGroup();
		Transform3D t3d = new Transform3D();

		// Rotate the text, and set it at z-coordinate -190
		t3d.rotX(0.6f);
		t3d.setTranslation(new Vector3d(0, 1, -190));
		t3d.setScale(0.4f);
		tg.setTransform(t3d);

		// Create the text itself
		Text3D d = new Text3D(new Font3D(new Font("Arial", Font.PLAIN, 1), new FontExtrusion()), "GET READY!");
		d.setAlignment(Text3D.ALIGN_CENTER);
		Appearance a = new Appearance();
		Material m = new Material();
		m.setShininess(100f);

		// Create the text shape and add it to the scene
		Shape3D textShape = new Shape3D(d, a);
		tg.setBounds(g.getBounds());
		tg.addChild(textShape);
		g.addChild(tg);

		return tg;
	}

	/**
	 * Creates the PositionInterpolator used to move the player avatar, and begins
	 * monitoring user keypresses
	 * 
	 * @param bgRoot Root to which the avatar transform group will be added
	 * @return The newly-created avatar transform group
	 */
	private TransformGroup addBehaviors(Group bgRoot) {

		// Group used to move the player avatar
		TransformGroup objTrans = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTrans.setCapability(Group.ALLOW_CHILDREN_WRITE);
		objTrans.setCapability(Group.ALLOW_CHILDREN_EXTEND);

		// Movement will occur along the z-axis
		Transform3D zAxis = new Transform3D();
		zAxis.rotY(Math.toRadians(90.0));

		// Setup the alpha used to drive moment
		playerAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, 35000, 0, 0, 0, 0, 0);

		playerAlpha.pause();

		// Create the position interpolator which will actually move the avatar
		this.posInt = new PositionInterpolator(playerAlpha, objTrans, zAxis, 0, -200);
		posInt.setSchedulingBounds(getBoundingSphere());
		objTrans.addChild(posInt);

		// Start monitoring user keypresses
		frames = new FrameBehaviour(objTrans);
		frames.addFrameListener(this);
		frames.setSchedulingBounds(getBoundingSphere());

		bgRoot.addChild(objTrans);
		bgRoot.addChild(frames);

		return objTrans;
	}

	/**
	 * Returns the bounding sphere for the scene
	 * 
	 * @return The bounding sphere for the scene
	 */
	private BoundingSphere getBoundingSphere() {
		return new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 400.0);
	}

	/**
	 * Creates the player avatar
	 * 
	 * @return The newly-created ViewerAvatar
	 */
	private ViewerAvatar createPlayer() {

		// Create the avatar and its transform group
		ViewerAvatar va = new ViewerAvatar();
		TransformGroup tg = new TransformGroup();

		// Set its initial position and scale
		Vector3d position = new Vector3d(0, -0.3, -0.3);
		Vector3d scale = new Vector3d(0.3, 0.3, 1);

		// Create the avatar object and paint it
		playerCar = new Car(tg, position, scale);
		playerCar.setAppearance(this.textureMgr.getRandomAppearance(TextureManager.TextureType.PLAYER));

		// Wire up collision notification events
		playerCar.addCollisionListener(this);

		tg.addChild(playerCar);
		va.addChild(tg);

		return va;
	}

	public void pauseResume() {

		if (!this.playerAlpha.isPaused()) {
			this.playerAlpha.pause();
			this.posInt.setEnable(false);
		} else {
			this.posInt.setEnable(true);
			this.playerAlpha.resume();
		}
	}

	/***************************************************************************
	 * INTERFACE METHODS -- NOT USED
	 **************************************************************************/

	/**
	 * Required to implement KeyListener -- not used
	 * 
	 * @param e The event that occurred
	 */
	public void keyTyped(KeyEvent e) {
		// Ignore this
	}

	/**
	 * Required to implement KeyListener -- not used
	 * 
	 * @param e The event that occurred
	 */
	public void keyReleased(KeyEvent e) {
		// Ignore this
	}
}
