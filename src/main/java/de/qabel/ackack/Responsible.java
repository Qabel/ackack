package de.qabel.ackack;

import java.io.Serializable;

/**
 * Answerable is an interface which describes classes, that want to get responses
 * to their requests.
 */
public interface Responsible {
    /**
     * method which is called when a request gets answered
     * @param data
     */
    void onResponse(Serializable... data);
}
