package de.qabel.ackack;

import java.io.Serializable;

/**
* Created by tox on 11/25/14.
*/
public class MessageInfo implements Cloneable {
    private Actor sender;
    private long time;
    private String type;

    public Actor getSender() {
        return sender;
    }

    public long getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public void setSender(Actor sender) {
        this.sender = sender;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void answer(final Serializable... data) {
    	if (this.sender != null) {
            this.sender.post(this, data);
    	}
    	
    }
}
