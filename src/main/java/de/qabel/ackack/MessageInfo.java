package de.qabel.ackack;

import java.io.Serializable;

/**
 * MessageInfo stores meta data of a Message.
 *
 */
public class MessageInfo implements Cloneable {
    /**
     * Sender of the message
     */
    private Actor sender;
    private long time;
    private String type;
    private Responsible responsible;

    /**
     * Get the sender of the message
     * @return Sender of the message or null if not set
     */
    public Actor getSender() {
        return sender;
    }

    /**
     * Get sending time of the message
     * @return Sending time or 0 if not set
     */
    public long getTime() {
        return time;
    }

    /**
     * Get type of the message
     * @return Type of message or null if empty
     */
    public String getType() {
        return type;
    }

    /**
     * Set sender of the message
     * @param sender Sender of the message
     */
    public void setSender(Actor sender) {
        this.sender = sender;
    }

    /**
     * Set sending time of the message
     * @param time Sending time
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Set type of the message
     * @param type Type of the message
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Answer to the received message
     * @param data Data to send
     */
    public void response(final Serializable... data) {
        if (this.sender == null) {
            return;
        } else if (getResponsible() != null) {
            this.sender.runInContext(new Runnable() {
                public void run() {
                    MessageInfo.this.getResponsible().onResponse(data);
                }
            });
        } else {
            this.sender.post(this, data);
        }
    }

    /**
     * gets the Answerable
     * @return the Answerable
     */
    public Responsible getResponsible() {
        return responsible;
    }

    /**
     * sets the answerable
     * @param responsible the answerable to set
     */
    public void setResponsible(Responsible responsible) {
        this.responsible = responsible;
    }
}
