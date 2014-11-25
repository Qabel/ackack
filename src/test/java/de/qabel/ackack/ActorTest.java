package de.qabel.ackack;

import org.junit.Assert;
import org.junit.Test;

public class ActorTest {
    Actor actor1 = new Actor("actor1");
    Actor actor2 = new Actor("actor2");

    @Test
    public void isNotRunningTest() {
        Assert.assertFalse(actor1.isRunning());
    }

    @Test
    public void sendReceiveTest() {
        // Register a Reactor which will terminate the actor after its first event.
        actor1.registerDefault(String.class, new Reactor<String>() {
            public void onEvent(Event<String> event) {
                Assert.assertEquals("DATA", event.getData());
                Assert.assertEquals(actor2, event.getSender());
                // Stop this actor to terminate endless loop
                actor1.stop();
            }
        });

        // Send data to actor1 through its id.
        Assert.assertTrue(actor2.send("actor1", "event", "DATA"));

        // Start the actor to receive
        actor1.run();
    }

    @Test
    public void threadedSendReceiveTest() {
        // Register a Reactor which will terminate the actor after its first event.
        actor1.registerDefault(String.class, new Reactor<String>() {
            public void onEvent(Event<String> event) {
                Assert.assertEquals("DATA", event.getData());
                Assert.assertEquals(actor2, event.getSender());
                // Stop this actor to terminate thread
                actor1.stop();
            }
        });
        Thread actor1Thread = new Thread(actor1);
        actor1Thread.start();

        // Send data to actor1 through its id.
        Assert.assertTrue(actor2.send("actor1", "event", "DATA"));

        // Busyloop to wait for background thread to be terminated
        while(actor1.isRunning()) {
            Thread.yield();
        }
    }


}
