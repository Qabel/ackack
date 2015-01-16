package de.qabel.ackack.event;

import de.qabel.ackack.MessageInfo;

/**
 * Interface for the listener which want to receive events
 *
 */
public interface EventListener {
	
	/**
	 * An event is send to the listener
	 * @param event Id of the event 
	 * @param info Information about the message
	 * @param data Object to send
	 */
    void onEvent(String event, MessageInfo info, Object... data);
}
