package de.qabel.ackack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by tox on 11/24/14.
 */
public class Actor implements Runnable {

    static private Map<Object, Actor> actors = Collections.synchronizedMap(new HashMap<Object, Actor>());

    public Object getId() {
        return id;
    }

    private final Object id;
    private LinkedBlockingQueue<Action> inQueue = new LinkedBlockingQueue<Action>();
    private Map<String, Reactor<?>> reactors = new HashMap<String, Reactor<?>>();
    private Reactor<?> defaultReactor = null;
    private boolean running;

    public Reactor<?> getDefaultReactor() {
        return defaultReactor;
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        this.running = false;
    }

    public Actor(Object id) {
        actors.put(id, this);
        this.id = id;
    }
    public <T> boolean send(Object receiver, String event, T data) {
        final Event<T> e = new Event<T>();
        final Actor receiverActor = receiver instanceof Actor ? (Actor) receiver : actors.get(receiver);
        e.setData(data);
        e.setName(event);
        e.setTime(System.currentTimeMillis());
        e.setSender(this);

        return receiverActor.post(new Action() {
            public void action() {
                // runs in context of receiver
                Reactor reactor = receiverActor.reactors.get(e.getName());
                if(reactor == null)
                    reactor = receiverActor.getDefaultReactor();
                if(reactor != null)
                    reactor.onEvent(e);
                // TODO: If no reactor is found, send an errormessage back.
            }
        });
    }

    private boolean post(Action action) {
        try {
            inQueue.put(action);
        } catch (InterruptedException e) {
            // TODO log error
            return false;
        }
        return true;
    }

    public void run() {
        Action action;
        running = true;

        try {
            while(isRunning()) {
                action = inQueue.take();
                if(action != null)
                    action.action();
            }
        } catch (InterruptedException ex) {
            stop();
        }
    }

    public <T> void register(String event, Class<T> dataClass, Reactor<T> reactor) {
        reactors.put(event, reactor);
    }

    public <T> void registerDefault(Class<T> dataClass, Reactor<T> reactor) {
        defaultReactor = reactor;
    }
}
