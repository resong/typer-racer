package com.resong.racer.objects;

/*******************************************************************************
 *
 * FrameEvent.java
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

import javax.vecmath.Vector3f;

/*******************************************************************************
 *
 * Passed to objects that subscribe to elapsed frame events. Contains the
 * current position of the player avatar, as well as the number of frames that
 * have elapsed since the last notification
 * 
 * @author Jeff Shantz
 *
 ******************************************************************************/

public class FrameEvent {

	/***************************************************************************
	 * INSTANCE VARIABLES
	 **************************************************************************/

	// Current position of the player avatar
	private Vector3f position;

	// Number of frames elapsed since the last notification
	private int frameCountSinceLastEvent;

	/***************************************************************************
	 * CONSTRUCTORS
	 **************************************************************************/

	/**
	 * Creates a new FrameEvent
	 * 
	 * @param position                 Current position of the player avatar
	 * @param frameCountSinceLastEvent Number of frames elapsed since the last
	 *                                 notification
	 */
	public FrameEvent(Vector3f position, int frameCountSinceLastEvent) {
		this.position = position;
		this.frameCountSinceLastEvent = frameCountSinceLastEvent;
	}

	/***************************************************************************
	 * PUBLIC METHODS
	 **************************************************************************/

	/**
	 * Returns the current position of the player avatar
	 * 
	 * @return The current position of the player
	 */
	public Vector3f getPosition() {
		return this.position;
	}

	/**
	 * Returns the number of frames elapsed since the last notification
	 * 
	 * @return The number of frames elapsed since the last notification
	 */
	public int getFrameCountSinceLastEvent() {
		return frameCountSinceLastEvent;
	}
}
