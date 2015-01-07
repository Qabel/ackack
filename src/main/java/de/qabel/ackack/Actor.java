package de.qabel.ackack;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by tox on 11/24/14.
 */
public class Actor implements Runnable {

    private final LinkedBlockingQueue<Runnable> inQueue = new LinkedBlockingQueue<Runnable>();
    private boolean running;

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        this.running = false;
    }

    public boolean post(final Object... data) {
        return post(new MessageInfo(), data);
    }

    public boolean post(final MessageInfo info, final Object... data) {
        info.setTime(System.currentTimeMillis());
        return this.runInContext(new Runnable() {
            public void run() {
                // runs in context of receiver
                react(info, data);
            }
        });
    }

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
