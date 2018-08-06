package com.resong.racer.behaviours;

import java.awt.AWTEvent;
/*******************************************************************************
 *
 * KeyBehaviour.java
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
import javax.swing.event.EventListenerList;

/**
 * Monitors keypress events in the game and notifies listeners
 * 
 * @author Jeff Shantz
 * @author Daniel Selman
 */
public class KeyBehaviour extends Behavior {

	/***************************************************************************
	 * INSTANCE VARIABLES
	 **************************************************************************/

	// Keypress wakeup event
	private WakeupOnAWTEvent wakeupEvent;

	// Array of criteria on which we will wake up
	private WakeupCriterion[] criteria;

	// Condition on which we will wake up
	private WakeupCondition wakeupCondition;

	// Elapsed frame listeners
	private EventListenerList listenerList;

	/***************************************************************************
	 * CONSTRUCTORS
	 **************************************************************************/

	/**
	 * Sets up keypress monitoring, firing notifications when the user presses a
	 * key.
	 */
	public KeyBehaviour(TransformGroup grp) {

		criteria = new WakeupCriterion[1];
		criteria[0] = wakeupEvent = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
		wakeupCondition = new WakeupOr(criteria);
		listenerList = new EventListenerList();
	}

	/***************************************************************************
	 * PUBLIC METHODS
	 **************************************************************************/

	/**
	 * Subscribes the specified listener to keypress notifications
	 * 
	 * @param listener The listener to subscribe
	 */
	public void addKeyListener(KeyListener listener) {
		listenerList.add(KeyListener.class, listener);
	}

	/**
	 * Unsubscribes the specified listener from keypress notifications
	 * 
	 * @param listener The listener to unsubscribe
	 */
	public void removeKeyListener(KeyListener listener) {
		listenerList.remove(KeyListener.class, listener);
	}

	/**
	 * Sets the behaviour to wake up on a keypress event
	 */
	public void initialize() {
		wakeupOn(wakeupCondition);
	}

	/**
	 * Receives keypress events and notifies all listeners
	 * 
	 * @param criteria Event criteria
	 */
	public void processStimulus(Enumeration criteria) {

		// Next AWT event
		WakeupOnAWTEvent event;

		// Criterion of the next event
		WakeupCriterion criterion;

		// Array of AWT events received
		AWTEvent[] events;

		// Iterate over all notifications received
		while (criteria.hasMoreElements()) {

			// Get the next wakeup criterion
			criterion = (WakeupCriterion) criteria.nextElement();

			// If it wasn't for an AWT event, move on
			if (!(criterion instanceof WakeupOnAWTEvent)) {
				return;
			}

			// Otherwise, get all AWT events received
			event = (WakeupOnAWTEvent) criterion;
			events = event.getAWTEvent();

			// Iterate over each one
			for (AWTEvent awtEvent : events) {

				// If it wasn't for a key event, move on
				if (!(awtEvent instanceof KeyEvent)) {
					continue;
				}

				// Otherwise, grab the event, and if it was a keypress event,
				// then forward the event to all listeners
				KeyEvent keyEvent = (KeyEvent) awtEvent;

				if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
					this.fireKeyEvent(keyEvent);
				}
			}

		}

		// Set wakeup criteria for next time
		wakeupOn(wakeupCondition);
	}

	/***************************************************************************
	 * PROTECTED METHODS
	 **************************************************************************/

	/**
	 * Notifies all listeners of the specified key press event
	 * 
	 * @param keyEvent Details of the key press event
	 */
	protected void fireKeyEvent(KeyEvent keyEvent) {

		// Get the listeners
		Object[] listeners = listenerList.getListenerList();

		// Iterate over the listeners, notifying each one
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == KeyListener.class) {
				((KeyListener) listeners[i + 1]).keyPressed(keyEvent);
			}
		}
	}
}
