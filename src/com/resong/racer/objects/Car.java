package com.resong.racer.objects;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.Group;

/*******************************************************************************
 *
 * Car.java
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
 ******************************************************************************/
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;

/**
 * Models a car used as the player avatar
 *
 * @author Jeff Shantz
 * @author Daniel Selman
 */
public class Car extends ComplexObject {

	/***************************************************************************
	 * CONSTANT DECLARATIONS
	 **************************************************************************/

	// Fixed width, height, and length for the car
	public static final float CAR_WIDTH = 0.2f;
	public static final float CAR_HEIGHT = 0.2f;
	public static final float CAR_LENGTH = 0.6f;

	// Flags used to construct a car object
	private static int CAR_FLAGS = ComplexObject.GEOMETRY | ComplexObject.TEXTURE | ComplexObject.COLLISION;

	/***************************************************************************
	 * INSTANCE VARIABLES
	 **************************************************************************/

	// The shape used to represent a building
	private Box shape;

	/***************************************************************************
	 * CONSTRUCTORS
	 **************************************************************************/

	/**
	 * Creates a new Car in the specified group
	 * 
	 * @param group    Scene graph group to which the car will be added
	 * @param position Position at which the car will be placed
	 * @param scale    Scale of the car
	 */
	public Car(Group group, Vector3d position, Vector3d scale) {
		super(group, position, scale, CAR_FLAGS);
	}

	/***************************************************************************
	 * PUBLIC METHODS
	 **************************************************************************/

	/**
	 * Sets the appearance (texture) of the car
	 * 
	 * @param app The new appearance for the car
	 */
	public void setAppearance(Appearance app) {
		this.shape.setAppearance(app);
	}

	/**
	 * Called when the car collides with a game object
	 * 
	 * @param collide Whether or not the car is currently colliding with an object
	 */
	@Override
	public void onCollide(boolean collide) {

		// If we're colliding, fire a collision event to notify all observers
		if (collide) {
			this.fireCollisionEvent();
		}
	}

	/***************************************************************************
	 * PROTECTED METHODS
	 **************************************************************************/

	/**
	 * Returns the bounds of the car
	 * 
	 * @return The bounds of the car
	 */
	@Override
	protected Bounds getGeometryBounds() {
		return new BoundingSphere(new Point3d(0, 0, 0), 0.2);
	}

	/**
	 * Creates the scene graph object that will be used to model the car
	 * 
	 * @param position Position at which the object will be placed
	 * @param scale    Scale of the object
	 * @return A Group containing the new car object
	 */
	protected Group createGeometryGroup(Vector3d position, Vector3d scale) {

		this.shape = new Box(CAR_WIDTH, (float) position.y, getRandomNumber(CAR_LENGTH, 0.01f),
				Primitive.GENERATE_TEXTURE_COORDS, new Appearance());
		return this.shape;
	}
}
