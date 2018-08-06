package com.resong.racer.objects;

import javax.media.j3d.Appearance;
import javax.media.j3d.Group;
import javax.media.j3d.Shape3D;

/*******************************************************************************
 *
 * Building.java
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
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;

/**
 * Models a building at the side of the road
 *
 * @author Jeff Shantz
 * @author Daniel Selman
 */
public class Building extends ComplexObject {

	/***************************************************************************
	 * CONSTANT DECLARATIONS
	 **************************************************************************/

	// Fixed width and length for a building
	private final float BUILDING_WIDTH = 1.0f;
	private final float BUILDING_LENGTH = 1.0f;

	// Flags used to construct a building object
	private final static int BUILDING_FLAGS = ComplexObject.GEOMETRY | ComplexObject.TEXTURE | ComplexObject.COLLISION;

	/***************************************************************************
	 * INSTANCE VARIABLES
	 **************************************************************************/

	// The shape used to represent a building
	private Box shape;

	/***************************************************************************
	 * CONSTRUCTORS
	 **************************************************************************/

	/**
	 * Creates a new Building in the specified group
	 * 
	 * @param group    Scene graph group to which the building will be added
	 * @param position Position at which the building will be placed
	 * @param scale    Scale of the building
	 */
	public Building(Group group, Vector3d position, Vector3d scale) {
		super(group, position, scale, BUILDING_FLAGS);
	}

	/***************************************************************************
	 * PUBLIC METHODS
	 **************************************************************************/

	/**
	 * Sets the appearance (texture) of the building
	 * 
	 * @param app The new appearance for the building
	 */
	public void setAppearance(Appearance app) {
		this.shape.setAppearance(app);
	}

	/***************************************************************************
	 * PROTECTED METHODS
	 **************************************************************************/

	/**
	 * Creates the scene graph object that will be used to model the building
	 * 
	 * @param position Position at which the object will be placed
	 * @param scale    Scale of the object
	 * @return A Group containing the new building object
	 */
	protected Group createGeometryGroup(Vector3d position, Vector3d scale) {

		// Create the building as a Box
		this.shape = new Box(getRandomNumber(BUILDING_WIDTH, 0.25f), (float) position.y,
				getRandomNumber(BUILDING_LENGTH, 0.15f), Primitive.GENERATE_TEXTURE_COORDS, new Appearance());

		// Add the ability to change the texture of each side of the box
		for (int i = 0; i < 6; i++) {
			this.shape.getShape(i).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		}

		return shape;
	}
}
