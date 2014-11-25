package de.qabel.ackack;

/**
* Created by tox on 11/25/14.
*/
public class Event<T> implements Cloneable {
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private T data;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private long time;
    public T as(Class<T> cls) {
        return cls.cast(getData());
    }

    public Actor getSender() {
        return sender;
    }

    public void setSender(Actor sender) {
        this.sender = sender;
    }

    private Actor sender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    @Override
    public Event<T> clone() {
        Event<T> event = new Event<T>();
        // TODO Clone Data Object
        event.setData(data);
        event.setSender(sender);
        event.setTime(time);
        return event;
    }
}
