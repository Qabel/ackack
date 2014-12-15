package de.qabel.ackack.event;

import de.qabel.ackack.MessageInfo;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Created by tox on 12/14/14.
 */
public class EventActorTest {
    @Test
    public void sendEventTest() {
        final EventActor actor = new EventActor();
        final String[] result = { null };
        actor.on("test", new EventListener() {
            public void onEvent(String event, MessageInfo info, Object... data) {
                result[0] = (String) data[0];
                actor.stop();
            }
        });
        EventEmitter.getDefault().emit("test", "Hello");
        actor.run();
        assertEquals("Hello", result[0]);
    }

    @Test
    public void sendEventTestThreaded() throws InterruptedException {
        EventEmitter emitter = new EventEmitter();
        final EventActor actor = new EventActor(emitter);
        final Object[] result = { null };
        Thread bg = new Thread(actor);
        Object testObject = new Object();
        actor.on("test", new EventListener() {
            public void onEvent(String event, MessageInfo info, Object... data) {
                result[0] = data[0];
                actor.stop();
            }
        });
        bg.start();
        emitter.emit("test", testObject);
        bg.join();
        assertEquals(testObject, result[0]);
    }

    @Test
    public void sendMultipleThreaded() throws InterruptedException {
        EventEmitter emitter = new EventEmitter();
        final EventActor actor1 = new EventActor(emitter);
        final EventActor actor2 = new EventActor(emitter);
        final Object[] result = new Object[2];
        final Object testObject = new Object();
        actor1.on("test", new EventListener() {
            public void onEvent(String event, MessageInfo info, Object... data) {
                result[0] = data[0];
                actor1.stop();
            }
        });
        actor2.on("test", new EventListener() {
            public void onEvent(String event, MessageInfo info, Object... data) {
                result[1] = data[0];
                actor2.stop();
            }
        });
        Thread bg1 = new Thread(actor1);
        Thread bg2 = new Thread(actor2);
        bg1.start();
        bg2.start();

        emitter.emit("test", testObject);
        bg1.join();
        bg2.join();

        assertEquals(testObject, result[0]);
        assertEquals(testObject, result[1]);
    }
}
