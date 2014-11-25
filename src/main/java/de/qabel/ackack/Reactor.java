package de.qabel.ackack;

/**
 * Created by tox on 11/25/14.
 */
public interface Reactor<T> {
    void onEvent(Event<T> event);
}
