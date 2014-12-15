package de.qabel.ackack.event;

import de.qabel.ackack.MessageInfo;

/**
 * Created by tox on 12/6/14.
 */
public interface EventListener {
    void onEvent(String event, MessageInfo info, Object... data);
}
