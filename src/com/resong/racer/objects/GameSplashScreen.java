package com.resong.racer.objects;

/*******************************************************************************
 *
 * GameSplashScreen.java
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 * Splash screen displayed while the game is loading
 * 
 * @author Jeff Shantz
 */
public class GameSplashScreen extends JWindow {

	/***************************************************************************
	 * CONSTANT DECLARATIONS
	 **************************************************************************/

	// Dimensions of the splash screen
	private int SPLASH_WIDTH = 600;
	private int SPLASH_HEIGHT = 400;

	/***************************************************************************
	 * INSTANCE VARIABLES
	 **************************************************************************/

	// Background image panel
	private ImagePanel pnlBackground;

	// Logo image panel
	private ImagePanel pnlLogo;

	// Panel in which the current loading status is displayed
	private JPanel pnlStatus;

	// Displays the current loading status
	private JLabel lblStatus;

	/***************************************************************************
	 * CONSTRUCTORS
	 **************************************************************************/

	/**
	 * Creates a new splash screen
	 * 
	 * @param parent Parent component
	 * @throws Exception If the splash screen images cannot be loaded
	 */
	public GameSplashScreen(Frame parent) throws Exception {

		super(parent);
		this.initComponents();

		// Set the size of the splash screen, and center it on the screen
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - SPLASH_WIDTH) / 2;
		int y = (screen.height - SPLASH_HEIGHT) / 2;
		this.setBounds(x, y, SPLASH_WIDTH, SPLASH_HEIGHT);
	}

	/***************************************************************************
	 * PUBLIC METHODS
	 **************************************************************************/

	/**
	 * Set the status message to display
	 * 
	 * @param status The status message to display
	 */
	public void notify(String status) {
		this.lblStatus.setText(status);
	}

	/***************************************************************************
	 * PRIVATE METHODS
	 **************************************************************************/

	/**
	 * Initializes the components of the splash screen
	 * 
	 * @throws Exception If the splash screen images cannot be loaded
	 */
	private void initComponents() throws Exception {

		// Create and add the background panel
		this.pnlBackground = new ImagePanel(TextureManager.loadImage(this.getClass(), "bg.jpg"));
		pnlBackground.setLayout(new BorderLayout());
		this.add(pnlBackground);

		// Create and add the logo panel
		this.pnlLogo = new ImagePanel(TextureManager.loadImage(this.getClass(), "logo.png"), true);
		this.pnlLogo.setMinimumSize(new Dimension(544, 122));
		this.pnlLogo.setPreferredSize(new Dimension(544, 122));

		// Create the status panel and label
		this.pnlStatus = new JPanel();
		this.lblStatus = new JLabel();

		// Set the appearance of the status label
		this.lblStatus.setFont(new java.awt.Font("SansSerif", Font.BOLD, 24));
		this.lblStatus.setForeground(Color.WHITE);

		// Center the status label horizontally and vertically (GridBagLayout)
		this.pnlStatus.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		this.pnlStatus.setLayout(new GridBagLayout());
		this.pnlStatus.setOpaque(false);
		this.pnlStatus.add(this.lblStatus);

		pnlBackground.add(pnlLogo, BorderLayout.NORTH);
		pnlBackground.add(pnlStatus, BorderLayout.CENTER);
	}

	/***************************************************************************
	 * INNER CLASSES
	 **************************************************************************/

	/**
	 * Panel that displays a background image
	 * 
	 * @author Jeff Shantz
	 * @see JPanel
	 */
	private class ImagePanel extends JPanel {

		/***********************************************************************
		 * INSTANCE VARIABLES
		 **********************************************************************/

		// Background image
		private Image img;

		// Width of the background image
		private int imageWidth;

		// X-coordinate of the left side of the image
		private int imageX;

		// Whether or not to center the image horizontally within the panel
		private boolean centerImage;

		/***********************************************************************
		 * CONSTRUCTORS
		 **********************************************************************/

		/**
		 * Creates a new ImagePanel with the specified image displayed on its background
		 * 
		 * @param img Background image
		 */
		public ImagePanel(Image img) {

			this.img = img;
			this.imageX = 0;
			this.imageWidth = img.getWidth(this);
			this.centerImage = false;
			this.setOpaque(false);
		}

		/**
		 * Creates a new ImagePanel with the specified image displayed on its
		 * background, optionally centering the image horizontally within the panel
		 * 
		 * @param img         Background image
		 * @param centerImage Whether or not to center the image horizontally
		 */
		public ImagePanel(Image img, boolean centerImage) {

			this(img);
			this.centerImage = centerImage;
			this.setSize(this.img.getWidth(this), this.img.getHeight(this));
		}

		/***********************************************************************
		 * PUBLIC METHODS
		 **********************************************************************/

		/**
		 * Called when the panel is laid out. Ensures that a centered image remains so
		 */
		@Override
		public void doLayout() {
			super.doLayout();
			doCenter();
		}

		/***********************************************************************
		 * PROTECTED METHODS
		 **********************************************************************/

		/**
		 * Called to paint the panel. Draws the background image and then paints all
		 * children
		 * 
		 * @param g Graphics context used to draw the panel
		 */
		@Override
		protected void paintChildren(Graphics g) {
			g.drawImage(img, imageX, 0, this.getParent());
			super.paintChildren(g);
		}

		/***********************************************************************
		 * PRIVATE METHODS
		 **********************************************************************/

		/**
		 * Ensures that an image remains horizontally centered within the panel, if this
		 * was requested
		 */
		private void doCenter() {

			if (this.centerImage) {

				if (this.getWidth() <= imageWidth) {
					return;
				}

				this.imageX = (this.getWidth() - imageWidth) / 2;
			}
		}
	}
}
