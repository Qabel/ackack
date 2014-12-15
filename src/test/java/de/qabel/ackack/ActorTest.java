package de.qabel.ackack;

import org.junit.Assert;
import org.junit.Test;

public class ActorTest {
    Actor actor = new Actor();

    @Test
    public void isNotRunningTest() {
        Assert.assertFalse(actor.isRunning());
    }

    @Test
    public void sendReceiveTest() {
        Actor actor = new Actor() {
            @Override
            protected void react(MessageInfo info, Object... data) {
                Assert.assertEquals(data[0], "Hello World");
                stop();
            }
        };
        actor.post("Hello World");
        actor.run();
    }

    @Test
    public void threadedSendReceiveTest() throws InterruptedException {
        final Object result[] = { null };
        Actor actor = new Actor() {
            @Override
            protected void react(MessageInfo info, Object... data) {
                result[0] = data[0];
                stop();
            }
        };
        Thread bg = new Thread(actor);
        bg.start();
        actor.post("Hello World");
        bg.join();
        Assert.assertEquals(result[0], "Hello World");
    }


}
