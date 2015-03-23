package de.qabel.ackack.event;

import de.qabel.ackack.MessageInfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by tox on 12/6/14.
 */
public class EventEmitter {

    private static EventEmitter defaultEmitter = null;
    
    /**
     * Get default event emitter. If no one exist it generate one
     * @return Default event emitter
     */
    public static EventEmitter getDefault() {
        if(defaultEmitter == null)
            defaultEmitter = new EventEmitter();
        return defaultEmitter;
    }

    private HashMap<String, Set<EventActor>> actors = new HashMap<String, Set<EventActor>>();

    /**
     * Register an actor to an event id
     * @param event Event id
     * @param actor Actor to register
     */
    public synchronized void register(String event, EventActor actor) {
        Set<EventActor> actorSet = actors.get(event);
        if(actorSet == null) {
            actors.put(event, actorSet = new HashSet<EventActor>());
        }
        actorSet.add(actor);
    }

    /**
     * Emit event
     * @param event Event id
     * @param data Data to emit
     * @return Number of actor which want the data
     */
    public int emit(String event, Serializable... data) {
        return emit(event, new MessageInfo(), data);
    }

    /**
     * Emit event
     * @param event Event id
     * @param info Information of the message
     * @param data Data to emit
     * @return Number of actor which want the data
     */
    synchronized public int emit(String event, MessageInfo info, Serializable... data) {
        Set<EventActor> actorSet = actors.get(event);
        info.setType("event");
        if(actorSet == null)
            return 0;
        for(EventActor actor : actorSet) {
            actor.receiveEvent(event, info, data);
        }
        return actorSet.size();
    }
}
