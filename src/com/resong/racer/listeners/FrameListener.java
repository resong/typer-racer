package com.resong.racer.listeners;

/*******************************************************************************
 *
 * FrameListener.java
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

import com.resong.racer.objects.FrameEvent;

/**
 * Listener interface for receiving elapsed frame events
 * 
 * @author Jeff Shantz
 */
public interface FrameListener extends EventListener {

	/**
	 * Called to notify listeners when a given number of frames have elapsed
	 * 
	 * @param event Details of current position of the player, and the number of
	 *              frames that elapsed since the last notification
	 */
	public void tick(FrameEvent event);
}
