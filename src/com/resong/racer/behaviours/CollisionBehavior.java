package com.resong.racer.behaviours;

/*******************************************************************************
 *
 * CollisionBehaviour.java
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

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.Node;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOr;

import com.resong.racer.objects.ComplexObject;

/**
 * Monitors collisions between game objects
 *
 * @author Jeff Shantz
 * @author Daniel Selman
 */
public class CollisionBehavior extends Behavior {

	/***************************************************************************
	 * INSTANCE VARIABLES
	 **************************************************************************/

	// Collision wakeup event
	private WakeupOnCollisionEntry wakeup;

	// Array of criteria on which we will wake up
	private WakeupCriterion[] criteria;

	// Condition on which we will wake up
	private WakeupCondition wakeupCondition;

	// Object to be notified when collisions occur
	private ComplexObject owner;

	/***************************************************************************
	 * CONSTRUCTORS
	 **************************************************************************/

	/**
	 * Sets up collision monitoring for the specified scene graph node, notifying
	 * the given owner when collisions are detected
	 * 
	 * @param node  The node for which collisions will be monitored
	 * @param owner The object to be notified when collisions occur
	 */
	public CollisionBehavior(Node node, ComplexObject owner) {

		this.owner = owner;

		// Set a single wakeup criterion: when a collision occurs involving
		// the given node
		this.criteria = new WakeupCriterion[1];
		this.wakeup = new WakeupOnCollisionEntry(node, WakeupOnCollisionEntry.USE_GEOMETRY);
		this.criteria[0] = wakeup;
		this.wakeupCondition = new WakeupOr(criteria);
	}

	/***************************************************************************
	 * PUBLIC METHODS
	 **************************************************************************/

	/**
	 * Sets the behaviour to wake up on a collision
	 */
	@Override
	public void initialize() {
		wakeupOn(wakeupCondition);
	}

	/**
	 * Receives collision events and notifies objects involved
	 * 
	 * @param criteria Event criteria
	 */
	@Override
	public void processStimulus(Enumeration criteria) {

		// Process all events
		while (criteria.hasMoreElements()) {

			// Get the next event
			WakeupCriterion event = (WakeupCriterion) criteria.nextElement();

			// If it was a collision event, notify the owner
			if (event instanceof WakeupOnCollisionEntry) {
				owner.onCollide(true);
			}
		}

		// Reset the wake up condition
		wakeupOn(wakeupCondition);
	}
}
