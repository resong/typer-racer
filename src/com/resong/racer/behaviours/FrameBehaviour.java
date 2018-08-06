package com.resong.racer.behaviours;

/*******************************************************************************
 *
 * FrameBehaviour.java
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

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.media.j3d.WakeupOr;
import javax.swing.event.EventListenerList;
import javax.vecmath.Vector3f;

import com.resong.racer.listeners.FrameListener;
import com.resong.racer.objects.FrameEvent;

/**
 * Monitors elapsed frames in the game
 *
 * @author Jeff Shantz
 * @author Daniel Selman
 */
public class FrameBehaviour extends Behavior {

	/***************************************************************************
	 * CONSTANT DECLARATIONS
	 **************************************************************************/

	// Number of frames to wait before next notifying listeners
	private int ELAPSED_FRAMES = 10;

	/***************************************************************************
	 * INSTANCE VARIABLES
	 **************************************************************************/

	// Elapsed frames wakeup event
	private WakeupOnElapsedFrames wakeup;

	// Array of criteria on which we will wake up
	private WakeupCriterion[] criteria;

	// Condition on which we will wake up
	private WakeupCondition wakeupCondition;

	// Transform group for the player avatar
	private TransformGroup transformGroup;

	// Elapsed frame listeners
	private EventListenerList listenerList;

	/***************************************************************************
	 * CONSTRUCTORS
	 **************************************************************************/

	/**
	 * Sets up elapsed frame monitoring, firing notifications every ELAPSED_FRAMES
	 * frames. Each time a notification is fired, the coordinates of the specified
	 * transform group are passed to all listeners
	 * 
	 * @param tg Transform group whose coordinates will be included in notifications
	 */
	public FrameBehaviour(TransformGroup tg) {

		criteria = new WakeupCriterion[1];
		transformGroup = tg;
		wakeup = new WakeupOnElapsedFrames(ELAPSED_FRAMES);
		criteria[0] = wakeup;
		wakeupCondition = new WakeupOr(criteria);
		listenerList = new EventListenerList();
	}

	/***************************************************************************
	 * PUBLIC METHODS
	 **************************************************************************/

	/**
	 * Subscribes the specified listener to elapsed frame notifications
	 * 
	 * @param listener The listener to subscribe
	 */
	public void addFrameListener(FrameListener listener) {
		listenerList.add(FrameListener.class, listener);
	}

	/**
	 * Unsubscribes the specified listener from elapsed frame notifications
	 * 
	 * @param listener The listener to unsubscribe
	 */
	public void removeFrameListener(FrameListener listener) {
		listenerList.remove(FrameListener.class, listener);
	}

	/**
	 * Sets the behaviour to wake up after ELAPSED_FRAMES frames
	 */
	@Override
	public void initialize() {
		wakeupOn(wakeupCondition);
	}

	/**
	 * Receives elapsed frame events and notifies listeners
	 * 
	 * @param criteria Event criteria
	 */
	@Override
	public void processStimulus(Enumeration criteria) {

		// Iterate over all events
		while (criteria.hasMoreElements()) {

			criteria.nextElement();

			// Get the current translation of the transform group
			Vector3f translate = new Vector3f();
			Transform3D t3d = new Transform3D();
			transformGroup.getTransform(t3d);
			t3d.get(translate);

			// Create a new FrameEvent to pass to listeners, passing in the
			// current translation of the transform group, as well as the
			// number of elapsed frames since the last notification
			FrameEvent evt = new FrameEvent(translate, ELAPSED_FRAMES);

			// Notify all listeners
			this.fireTickEvent(evt);
		}

		// Set wakeup criteria for next time
		wakeupOn(wakeupCondition);
	}

	/***************************************************************************
	 * PROTECTED METHODS
	 **************************************************************************/

	/**
	 * Notifies all listeners that ELAPSED_FRAMES frames have elapsed
	 * 
	 * @param evt The FrameEvent to pass to all listeners
	 */
	protected void fireTickEvent(FrameEvent evt) {

		// Get the listeners
		Object[] listeners = listenerList.getListenerList();

		// Iterate over the listeners, notifying each one
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == FrameListener.class) {
				((FrameListener) listeners[i + 1]).tick(evt);
			}
		}
	}
}
