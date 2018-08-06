package com.resong.racer.objects;

import java.awt.Font;

import javax.media.j3d.Appearance;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

/*******************************************************************************
 *
 * Obstacle.java
 *
 * Based on Building.java, which was originally written by Daniel Selman and
 * modified (heavily) by Jeff Shantz
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
 ******************************************************************************/
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;

/**
 * Models an obstacle on the road
 *
 * @author Jeff Shantz
 */
public class Obstacle extends ComplexObject {

	/***************************************************************************
	 * CONSTANT DECLARATIONS
	 **************************************************************************/

	// Minimum dimensions of an obstacle
	public static final float MIN_WIDTH = 0.3f;
	public static final float MIN_HEIGHT = 0.2f;
	public static final float MIN_LENGTH = 0.2f;

	// Flags used to construct a car object
	private static final int OBSTACLE_FLAGS = ComplexObject.GEOMETRY | ComplexObject.TEXTURE;

	/***************************************************************************
	 * INSTANCE VARIABLES
	 **************************************************************************/

	// Dimensions of the obstacle
	private float width;
	private float length;

	// Shape used to model the obstacle
	private Box box;

	// Group containing the obstacle shape and the text above it
	private Group grp;

	// Position of the obstacle
	protected Vector3d position;

	/***************************************************************************
	 * CONSTRUCTORS
	 **************************************************************************/

	/**
	 * Creates a new Obstacle in the specified group
	 * 
	 * @param group    Scene graph group to which the obstacle will be added
	 * @param position Position at which the obstacle will be placed
	 * @param scale    Scale of the obstacle
	 */
	public Obstacle(BranchGroup group, Vector3d position, Vector3d scale) {
		super(group, position, scale, OBSTACLE_FLAGS);
		this.position = position;
		group.setCapability(BranchGroup.ALLOW_DETACH);
	}

	/***************************************************************************
	 * PUBLIC METHODS
	 **************************************************************************/

	/**
	 * Removes the obstacle from the scene
	 */
	public void remove() {
		((BranchGroup) super.parentGroup).detach();
	}

	/**
	 * Sets the word displayed above the obstacle
	 * 
	 * @param word Word displayed above the obstacle
	 */
	public void setWord(String word) {

		// Define the position of the text
		TransformGroup tg = new TransformGroup();
		Transform3D t3d = new Transform3D();
		t3d.setScale(0.15f);
		t3d.setTranslation(new Vector3d(0, position.y + 0.1f, 0));
		tg.setTransform(t3d);

		// Create the text itself
		Text3D d = new Text3D(new Font3D(new Font("Arial", Font.BOLD, 1), new FontExtrusion()), word);
		d.setAlignment(Text3D.ALIGN_CENTER);
		Appearance a = new Appearance();
		Material m = new Material();
		m.setShininess(100f);

		// Create the text scale
		Shape3D textShape = new Shape3D(d, a);

		tg.setBounds(transformGroup.getBounds());
		tg.addChild(textShape);

		BranchGroup bg = new BranchGroup();
		bg.setCapability(BranchGroup.ALLOW_DETACH);
		bg.setBounds(transformGroup.getBounds());
		bg.addChild(tg);

		grp.addChild(bg);
	}

	/**
	 * Sets the appearance (texture) of the obstacle
	 * 
	 * @param app The new appearance for the obstacle
	 */
	public void setAppearance(Appearance app) {
		this.box.setAppearance(app);
	}

	/***************************************************************************
	 * PROTECTED METHODS
	 **************************************************************************/

	/**
	 * Returns the bounds of the obstacle
	 * 
	 * @return The bounds of the obstacle
	 */
	protected Bounds getGeometryBounds() {
		return this.box.getCollisionBounds();
	}

	/**
	 * Creates the scene graph object that will be used to model the obstacle
	 * 
	 * @param position Position at which the object will be placed
	 * @param scale    Scale of the object
	 * @return A Group containing the new obstacle object
	 */
	protected Group createGeometryGroup(Vector3d position, Vector3d scale) {

		grp = new Group();
		grp.setCapability(Group.ALLOW_CHILDREN_WRITE);
		this.width = this.getRandomNumber(MIN_WIDTH, 0.2f);
		this.length = this.getRandomNumber(MIN_LENGTH, 0.5f);
		this.box = new Box(this.width, (float) position.y, this.length, Primitive.GENERATE_TEXTURE_COORDS,
				new Appearance());
		grp.addChild(box);

		return grp;
	}
}
