package com.resong.racer.objects;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Group;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;

/*******************************************************************************
 *
 * Road.java
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

/**
 * Models the road in the game
 */
public class Road extends ComplexObject {

	/***************************************************************************
	 * CONSTANT DECLARATIONS
	 **************************************************************************/

	// Fixed width, height, and length for the road
	public static final float ROAD_WIDTH = 3.0f;
	public static final float ROAD_HEIGHT = 0.01f;
	public static final float ROAD_LENGTH = -200.0f;

	// Flags used to construct a road object
	private static final int ROAD_FLAGS = ComplexObject.GEOMETRY | ComplexObject.TEXTURE;

	/***************************************************************************
	 * INSTANCE VARIABLES
	 **************************************************************************/

	// Shape used to model the road
	private Shape3D shape;

	/***************************************************************************
	 * CONSTRUCTORS
	 **************************************************************************/

	/**
	 * Creates a new Road in the specified group
	 * 
	 * @param group    Scene graph group to which the road will be added
	 * @param position Position at which the road will be placed
	 * @param scale    Scale of the road
	 */
	public Road(Group group, Vector3d position, Vector3d scale) {
		super(group, position, scale, ROAD_FLAGS);
		this.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
	}

	/***************************************************************************
	 * PUBLIC METHODS
	 **************************************************************************/

	/**
	 * Sets the appearance (texture) of the road
	 * 
	 * @param app The new appearance for the road
	 */
	public void setAppearance(Appearance app) {
		this.shape.setAppearance(app);
	}

	/***************************************************************************
	 * PROTECTED METHODS
	 **************************************************************************/

	/**
	 * Creates the scene graph object that will be used to model the road
	 * 
	 * @param position Position at which the object will be placed
	 * @param scale    Scale of the object
	 * @return A Group containing the new road object
	 */
	protected Group createGeometryGroup(Vector3d position, Vector3d scale) {

		QuadArray quadArray = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);

		float[] coordArray = { -ROAD_WIDTH, ROAD_HEIGHT, 0, ROAD_WIDTH, ROAD_HEIGHT, 0, ROAD_WIDTH, ROAD_HEIGHT,
				ROAD_LENGTH, -ROAD_WIDTH, ROAD_HEIGHT, ROAD_LENGTH };

		float[] texArray = { 0, 0, 1, 0, 1, 1, 0, 1 };

		quadArray.setCoordinates(0, coordArray, 0, 4);
		quadArray.setTextureCoordinates(0, 0, texArray, 0, 4);

		this.shape = new Shape3D(quadArray);
		this.shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

		BranchGroup bg = new BranchGroup();
		bg.addChild(shape);
		return bg;
	}
}
