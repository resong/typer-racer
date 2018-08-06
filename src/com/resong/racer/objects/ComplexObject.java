package com.resong.racer.objects;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.event.EventListenerList;

/*******************************************************************************
 *
 * ComplexObject.java
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
 *
 ******************************************************************************/
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.resong.racer.behaviours.CollisionBehavior;
import com.resong.racer.listeners.CollisionListener;

/**
 * Abstract class providing routines to create and initialize a scene graph
 * object
 * 
 * @author Jeff Shantz
 */
public abstract class ComplexObject extends BranchGroup {

	/***************************************************************************
	 * CONSTANT DECLARATIONS
	 **************************************************************************/

	// Specifies that the given object has geometry
	public static final int GEOMETRY = 0x002;

	// Specifies that the given object should have a texture applied to it
	public static final int TEXTURE = 0x004;

	// Specifies that collision detection should be enabled for the objet
	public static final int COLLISION = 0x008;

	/***************************************************************************
	 * INSTANCE VARIABLES
	 **************************************************************************/

	// Group to which the object belong
	protected Group parentGroup;

	// Flags governing the creation of the object
	protected int flags;

	// Transform group to which the object belongs
	protected TransformGroup transformGroup;

	// Behaviour transform group to which the object belongs
	protected TransformGroup behaviourTransformGroup;

	// Collision listeners
	private EventListenerList listenerList;

	/***************************************************************************
	 * CONSTRUCTORS
	 **************************************************************************/

	/**
	 * Creates a new scene graph object in the specified group
	 * 
	 * @param group    Scene graph group to which the object will be added
	 * @param position Position at which the object will be placed
	 * @param scale    Scale of the object
	 * @param flags    Flags governing the creation of the object
	 */
	public ComplexObject(Group group, Vector3d position, Vector3d scale, int flags) {

		parentGroup = group;
		this.flags = flags;
		listenerList = new EventListenerList();

		this.initializeObject(position, scale);
	}

	/***************************************************************************
	 * PUBLIC METHODS
	 **************************************************************************/

	/**
	 * Subscribes the specified listener to collision notifications involving the
	 * object
	 * 
	 * @param listener The listener to subscribe
	 */
	public void addCollisionListener(CollisionListener listener) {
		listenerList.add(CollisionListener.class, listener);
	}

	/**
	 * Unsubscribes the specified listener from collision notifications
	 * 
	 * @param listener The listener to unsubscribe
	 */
	public void removeCollisionListener(CollisionListener listener) {
		listenerList.remove(CollisionListener.class, listener);
	}

	/**
	 * Called when a collision occurs involving the object
	 * 
	 * @param collide True if the object has just "entered" a collision; false if
	 *                the object is "exiting" a collision
	 */
	public void onCollide(boolean collide) {
		// Ignore collisions unless overridden
	}

	/***************************************************************************
	 * PROTECTED METHODS
	 **************************************************************************/

	/**
	 * Notifies all listeners that a collision occurred
	 */
	protected void fireCollisionEvent() {

		// Get the listeners
		Object[] listeners = listenerList.getListenerList();

		// Iterate over the listeners, notifying each one
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == CollisionListener.class) {
				((CollisionListener) listeners[i + 1]).collisionOccurred();
			}
		}
	}

	/**
	 * Returns the bounds of the object
	 * 
	 * @return The bounds of the object
	 */
	protected Bounds getGeometryBounds() {
		return new BoundingSphere(new Point3d(0, 0, 0), 100);
	}

	/**
	 * Returns a random number using the specified basis
	 * 
	 * @param basis  The basis for the random number
	 * @param random The "dynamic" value to add to the number
	 * @return A random number using the specified basis
	 */
	protected float getRandomNumber(float basis, float random) {
		return basis + ((float) Math.random() * random * 2) - (random);
	}

	/**
	 * Creates the geometry for the object. Must be overridden to create the
	 * appropriate geometry depending on the object type
	 * 
	 * @param position Position at which the object will be placed
	 * @param scale    Scale of the object
	 * @return Group containing the newly-created geometry for the object
	 */
	abstract protected Group createGeometryGroup(Vector3d position, Vector3d scale);

	/***************************************************************************
	 * PRIVATE METHODS
	 **************************************************************************/

	/**
	 * Initializes the new object, creating it geometry, and hooking up collision
	 * monitoring, if required
	 * 
	 * @param position Position at which the object will be placed
	 * @param scale    Scale of the object
	 */
	private void initializeObject(Vector3d position, Vector3d scale) {

		// Set the scale and position of the object
		transformGroup = new TransformGroup();
		Transform3D t3d = new Transform3D();
		t3d.setScale(scale);
		t3d.setTranslation(position);
		transformGroup.setTransform(t3d);

		behaviourTransformGroup = new TransformGroup();

		// Create the object's geometry, if required
		if ((flags & GEOMETRY) == GEOMETRY) {
			behaviourTransformGroup.addChild(createGeometryGroup(position, scale));
		}

		// Hook up collision monitoring, if required
		if ((flags & COLLISION) == COLLISION) {
			behaviourTransformGroup.setCapability(Node.ENABLE_COLLISION_REPORTING);
			behaviourTransformGroup.setCollidable(true);
			behaviourTransformGroup.setCollisionBounds(getGeometryBounds());

			CollisionBehavior collision = new CollisionBehavior(behaviourTransformGroup, this);
			collision.setSchedulingBounds(getGeometryBounds());

			behaviourTransformGroup.addChild(collision);
		}

		transformGroup.addChild(behaviourTransformGroup);
		parentGroup.addChild(transformGroup);
	}
}
