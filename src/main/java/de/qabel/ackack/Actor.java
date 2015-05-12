package de.qabel.ackack;

import java.io.Serializable;

import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.lang3.SerializationUtils;

/**
 * Actor is a Message Passing class which blocks a thread and waits for incoming messages
 * from other Threads and especially other actors.
 *
 */
public class Actor implements Runnable {
    private static final Runnable EMPTY = new Runnable() {
        @Override
        public void run() {
        }
    };
    private final LinkedBlockingQueue<Runnable> inQueue = new LinkedBlockingQueue<>();
    private boolean running;

    /**
     * Shows whether the "thread" is running in background
     * @return true when object is running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Stop the background "thread" 
     */
    public void stop() {
        this.running = false;
        this.runInContext(EMPTY);
    }

    /**
     * Post data
     * @param data Data to send
     * @return True when data is send
     */
    public boolean post(final Serializable... data) {
        return post(new MessageInfo(), data);
    }

    /**
     * ask a question which can (not must) be answered by an actor.
     * @param sender the actor which asks the question
     * @param responsible the anserable object which is called by onResponse
     * @param data data
     * @return true if successfully asked, false otherwise
     */
    public boolean ask(Actor sender, Responsible responsible, final Serializable... data) {
        MessageInfo info = new MessageInfo();
        info.setSender(sender);
        info.setResponsible(responsible);
        return post(info, data);
    }

    /**
     * asks this Actor for data in a synchronous way and runs the senders eventloop
     * as long as the data isn't received
     * @param sender the actor which asks for an answer
     * @param data data send to this actor
     * @return the result from this actor
     */
    public Serializable[] askSync(Actor sender, final Serializable... data) {
        final boolean[] localrun = { true };
        final Serializable[][] result = { null };

        this.ask(sender, new Responsible() {
            @Override
            public void onResponse(Serializable... data) {
                result[0] = data;
                localrun[0] = false;
            }
        }, data);

        try {
            while(localrun[0] && sender.isRunning()) {
                Runnable action = sender.inQueue.take();
                if(action != null)
                    action.run();
            }
        } catch (InterruptedException ex) {
            sender.stop();
        }
        return result[0];
    }

    /**
     * Post data
     * @param info Information of the message
     * @param data Data to send
     * @return True when data is send
     */
    public boolean post(final MessageInfo info, final Serializable... data) {
        info.setTime(System.currentTimeMillis());
        final Object[] copies = new Object[data.length];
        for(int i = 0; i < copies.length; i++) {
            copies[i] = SerializationUtils.clone(data[i]);
        }
        return this.runInContext(new Runnable() {
            public void run() {
                // runs in context of receiver
                react(info, copies);
            }
        });
    }

    /**
     * Function which handle incoming data
     * @param info Information of the message
     * @param data Data to send
     */
    protected void react(final MessageInfo info, final Object... data) {
    }

    public boolean runInContext(Runnable action) {
        try {
            inQueue.put(action);
        } catch (InterruptedException e) {
            // TODO log error
            return false;
        }
        return true;
    }

    /**
     * Handle the background "thread"
     */
    public void run() {
        Runnable action;
        running = true;

        try {
            while(isRunning()) {
                action = inQueue.take();
                if(action != null) {
                    action.run();
                }
            }
        } catch (InterruptedException ex) {
            stop();
        }
    }
}
