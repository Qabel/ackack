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

    private LinkedBlockingQueue<Event<?>> inQueue = new LinkedBlockingQueue<Event<?>>();

    public Registration<?> getDefaultReactor() {
        return defaultReactor;
    }

    public void setDefaultReactor(Registration<?> defaultReactor) {
        this.defaultReactor = defaultReactor;
    }

    private Registration<?> defaultReactor = null;

    static class Registration<T> {
        public Class<T> dataClass;
        public Reactor<T> reactor;
    }

    private Map<String, Registration<?>> reactors = new HashMap<String, Registration<?>>();

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        this.running = false;
    }

    private boolean running;

    public <T> boolean send(Object receiver, String event, T data) {
        Event<T> e = new Event<T>();
        e.setData(data);
        e.setTime(System.currentTimeMillis());
        Actor actor = null;
        if(receiver instanceof Actor) {
            actor = ((Actor)receiver);
        }
        else {
            actor = actors.get(receiver);
        }

        if(actor == null) {
            return false;
        }

        actor.queue(e.clone());
        return true;
    }

    private void queue(Event<?> e) {
        try {
            inQueue.put(e);
        } catch (InterruptedException e1) {
            // TODO Log exception
            e1.printStackTrace();
        }
    }

    public void run() {
        Registration<?> registration;
        Event<?> e;

        try {
            while(isRunning()) {
                e = inQueue.take();
                registration = reactors.getOrDefault(e.getName(), defaultReactor);
                if(registration == null)
                    continue;
                if(registration.dataClass.isAssignableFrom(e.getData().getClass())) {
                    // TODO Fix Warning
                    registration.reactor.onEvent((Event)e);
                }
                // TODO Throw error if wrong datatype is given.
            }
        } catch (InterruptedException ex) {
            // TODO Log exception
            stop();
        }
    }

    public <T> void register(String event, Class<T> dataClass, Reactor<T> reactor) {
        Registration<T> registration = new Registration<T>();
        registration.dataClass = dataClass;
        registration.reactor = reactor;
        reactors.put(event, registration);
    }

    public <T> void registerDefault(Class<T> dataClass, Reactor<T> reactor) {
        Registration<T> registration = new Registration<T>();
        registration.dataClass = dataClass;
        registration.reactor = reactor;
        defaultReactor = registration;
    }
}
