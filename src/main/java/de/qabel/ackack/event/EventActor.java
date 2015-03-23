package de.qabel.ackack.event;

import de.qabel.ackack.Actor;
import de.qabel.ackack.MessageInfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * EventActor is an {@link de.qabel.ackack.Actor} which is used as running context for multiple
 * {@link de.qabel.ackack.event.EventListener}. EventListener can register themselves using the @{link #on}
 * method.
 *
 */
public class EventActor extends Actor {
    private final EventEmitter emitter;
    
    /**
     * Map of message ids and its listeners
     */
    protected HashMap<String, Set<EventListener>> listeners = new HashMap<String, Set<EventListener>>();
    
    /**
     * Post receive events
     * @param event Event id
     * @param info Information of the message
     * @param data Data to send
     */
    void receiveEvent(String event, MessageInfo info, Serializable... data) {
        this.post(info, event, data);
    }

    public EventActor() {
        this(EventEmitter.getDefault());
    }

    /**
     * 
     * @param emitter Object which shall emit data
     */
    public EventActor(EventEmitter emitter) {
        this.emitter = emitter;
    }

    /**
     * Function which handle incoming data
     * @param info Information of the message
     * @param data Data to send
     */
    @Override
    protected void react(MessageInfo info, Object... data) {
        if(!"event".equals(info.getType()))
            return;
        String event = (String)data[0];
        Object[] eventData = (Object[])data[1];

        Set<EventListener> listenerSet = listeners.get(event);
        if(listenerSet != null)
            for(EventListener listener : listenerSet) {
                listener.onEvent(event, info, eventData);
            }
    }

    /**
     * Add an event listener to an event id
     * @param event Event id
     * @param listener Event listener
     */
    public void on(String event, EventListener listener) {
        Set<EventListener> listenerSet = listeners.get(event);
        if(listenerSet == null) {
            listeners.put(event, listenerSet = new HashSet<EventListener>());
        }
        listenerSet.add(listener);
        emitter.register(event, this);
    }
}
