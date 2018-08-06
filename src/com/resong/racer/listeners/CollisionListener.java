package com.resong.racer.listeners;

/*******************************************************************************
 *
 * CollisionListener.java
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

import java.util.EventListener;

/**
 * Listener interface for receiving collision events
 * 
 * @author Jeff Shantz
 */
public interface CollisionListener extends EventListener {

	/**
	 * Called when a collision has occurred
	 */
	public void collisionOccurred();
}
