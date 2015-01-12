package de.qabel.ackack.event;

import de.qabel.ackack.Actor;
import de.qabel.ackack.MessageInfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by tox on 12/6/14.
 */
public class EventActor extends Actor {
    private final EventEmitter emitter;
    public HashMap<String, Set<EventListener>> listeners = new HashMap<String, Set<EventListener>>();
    void receiveEvent(String event, MessageInfo info, Serializable... data) {
        this.post(info, event, data);
    }

    public EventActor() {
        this(EventEmitter.getDefault());
    }

    public EventActor(EventEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    protected void react(MessageInfo info, Object... data) {
        if(!"event".equals(info.getType()))
            return;
        String event = (String)data[0];
        Object[] eventData = (Object[])data[1];

        Set<EventListener> listenerSet = listeners.get(event);
        for(EventListener listener : listenerSet) {
            listener.onEvent(event, info, eventData);
        }
    }

    public void on(String event, EventListener listener) {
        Set<EventListener> listenerSet = listeners.get(event);
        if(listenerSet == null) {
            listeners.put(event, listenerSet = new HashSet<EventListener>());
        }
        listenerSet.add(listener);
        emitter.register(event, this);
    }
}
