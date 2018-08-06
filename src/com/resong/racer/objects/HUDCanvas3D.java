package com.resong.racer.objects;

/*******************************************************************************
 *
 * HUDCanvas3D.java
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.J3DGraphics2D;

/**
 * Game canvas that displays a HUD at the top of the canvas
 * 
 * @author Jeff Shantz
 */
public class HUDCanvas3D extends Canvas3D {

	/***************************************************************************
	 * INSTANCE VARIABLES
	 **************************************************************************/

	// Objects used to draw the HUD
	private BufferedImage hudImage;
	private Graphics2D g2d;
	private J3DGraphics2D j3dg2d;

	// Dimensions of the canvas
	private int width;
	private int height;

	// Health bar image
	private Image healthBar;

	// Dimension of the health bar
	private int healthBarWidth;
	private int healthBarHeight;

	// Fonts used in the HUD
	private Font hudFont;
	private Font currentWordFont;

	// HUD object containing details to be displayed
	private HUD hud;

	/***************************************************************************
	 * CONSTRUCTORS
	 **************************************************************************/

	/**
	 * Creates a new canvas of the given dimensions, using the specified
	 * configuration and HUD
	 * 
	 * @param config GraphicsConfiguration for the canvas
	 * @param hud    HUD details to display
	 * @param width  Width of the canvas
	 * @param height Height of the canvas
	 * @throws Exception If the health bar image cannot be loaded
	 */
	public HUDCanvas3D(GraphicsConfiguration config, HUD hud, int width, int height) throws Exception {

		super(config);

		this.width = width;
		this.height = height;
		this.hud = hud;

		// Initialize objects needed to draw the HUD
		this.hudImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		this.g2d = this.hudImage.createGraphics();
		this.j3dg2d = this.getGraphics2D();
		this.g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);

		// Load the health bar image
		java.net.URL url = this.getClass().getClassLoader().getResource("images/healthbar.png");
		this.healthBar = ImageIO.read(url);
		this.healthBarWidth = this.healthBar.getWidth(this);
		this.healthBarHeight = this.healthBar.getHeight(this);

		// Set up fonts for the HUD
		this.hudFont = new Font("SansSerif", Font.BOLD, 16);
		this.currentWordFont = new Font("SansSerif", Font.BOLD, 14);
	}

	/***************************************************************************
	 * PUBLIC METHODS
	 **************************************************************************/

	/**
	 * Called after rendering the canvas. Draws the HUD over top of the canvas
	 */
	@Override
	public void postRender() {

		super.postRender();

		// Draw the HUD background and setup the font
		this.g2d.setColor(Color.DARK_GRAY);
		this.g2d.fillRect(0, 0, this.width, 40);
		this.g2d.setFont(hudFont);
		this.g2d.setColor(Color.WHITE);

		if ((!hud.isGameOver()) && (!hud.isPaused())) {
			this.drawNormalHUD();
		} else if (hud.isGameOver()) {
			this.drawGameOverHUD();
		} else {
			this.drawPausedHUD();
		}

		// Draw the HUD image over the canvas
		j3dg2d.drawAndFlushImage(this.hudImage, 0, 0, this);
	}

	/**
	 * Draws a "PAUSED" message in the HUD
	 */
	private void drawPausedHUD() {

		// Paused
		this.g2d.drawString("PAUSED", 350, 25);
	}

	/**
	 * Draws a "GAME OVER" message in the HUD
	 */
	private void drawGameOverHUD() {

		// Game over
		this.g2d.drawString("GAME OVER", 350, 25);
	}

	/**
	 * Draws the normal HUD as it should appear during game play, showing the
	 * player's health, the current level, the number of words successfully typed,
	 * and the current word being typed
	 */
	private void drawNormalHUD() {

		// Get the player's health as a value between 0.0f and 1.0f
		float health = hud.getHealth() / 100f;

		// Health Bar
		this.g2d.drawString("Health:", 10, 25);
		this.g2d.setStroke(new BasicStroke(3));
		this.g2d.setColor(Color.BLACK);
		this.g2d.drawRect(78, 8, this.healthBarWidth + 3, this.healthBarHeight + 3);
		this.g2d.setClip(80, 10, (int) (health * this.healthBarWidth), 20);
		this.g2d.drawImage(this.healthBar, 80, 10, this);

		// Level
		this.g2d.setClip(0, 0, width, height);
		this.g2d.setColor(Color.WHITE);
		this.g2d.drawString("Level:", 300, 25);
		this.g2d.drawString("" + hud.getLevel(), 355, 25);

		// Words
		this.g2d.drawString("Words:", 395, 25);
		this.g2d.drawString("" + hud.getWordCount(), 462, 25);

		this.g2d.drawString("Current Word:", 510, 25);
		this.g2d.setFont(this.currentWordFont);
		this.g2d.drawString(this.hud.getCurrentWord(), 645, 25);
	}
}
