package de.qabel.ackack;

import java.io.Serializable;

import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.lang3.SerializationUtils;

/**
 * Actor which want to send a message
 *
 */
public class Actor implements Runnable {

    private final LinkedBlockingQueue<Runnable> inQueue = new LinkedBlockingQueue<Runnable>();
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
     * Post data
     * @param info Information of the message
     * @param data Data to send
     * @return True when data is send
     */
    public boolean post(final MessageInfo info, final Serializable... data) {
        info.setTime(System.currentTimeMillis());
        Object[] copies = new Object[data.length];
        for(int i = 0; i < copies.length; i++) {
            copies[i] = SerializationUtils.clone(data[i]);
        }
        return this.runInContext(new Runnable() {
            public void run() {
                // runs in context of receiver
                react(info, data);
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

    private boolean runInContext(Runnable action) {
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
                if(action != null)
                    action.run();
            }
        } catch (InterruptedException ex) {
            stop();
        }
    }
}
