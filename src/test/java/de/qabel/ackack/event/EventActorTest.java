package de.qabel.ackack.event;

import de.qabel.ackack.Actor;
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
        String testObject = "Test String";
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
        final String[] result = new String[2];
        final String testObject = "Hello World";
        actor1.on("test", new EventListener() {
            public void onEvent(String event, MessageInfo info, Object... data) {
                result[0] = data[0].toString();
                actor1.stop();
            }
        });
        actor2.on("test", new EventListener() {
            public void onEvent(String event, MessageInfo info, Object... data) {
                result[1] = data[0].toString();
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

    @Test
    public void sendMultipleThreadedMessageInfo() throws InterruptedException {
        EventEmitter emitter = new EventEmitter();
        final EventActor actor1 = new EventActor(emitter);
        final EventActor actor2 = new EventActor(emitter);
        Actor actor3;
    	MessageInfo messageInfo3;
        final String[] result = new String[2];
        final String testObject = "Hello World";
        Thread actor1Thread, actor2Thread, actor3Thread;

        actor1.on("test", new EventListener() {
            public void onEvent(String event, MessageInfo info, Object... data) {
                result[0] = data[0].toString();
                info.answer("Answer");
                actor1.stop();
            }
        });
        actor2.on("test", new EventListener() {
            public void onEvent(String event, MessageInfo info, Object... data) {
                result[1] = data[0].toString();
                info.answer("Answer");
                actor2.stop();
            }
        });

        actor3 = new Actor() {
        	private int counter = 0;

            @Override
            protected void react(MessageInfo info, Object... data) {
                assertEquals(data[0], "Answer");
                
                this.counter++;
                if (this.counter == result.length) {
                	stop();
                }
            }
        };

        messageInfo3 = new MessageInfo();
        messageInfo3.setSender(actor3);
        
        actor3Thread = new Thread(actor3);
        actor3Thread.start();
        
        actor1Thread = new Thread(actor1);
        actor1Thread.start();
        actor2Thread = new Thread(actor2);
        actor2Thread.start();

        emitter.emit("test", messageInfo3, testObject);
        actor1Thread.join();
        actor2Thread.join();
        actor3Thread.join();

        assertEquals(testObject, result[0]);
        assertEquals(testObject, result[1]);
    }
}
