package de.qabel.ackack;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for class Actor
 *
 */
public class ActorTest {

    /**
     * Actor is not running an a "thread"
     */
    @Test
    public void isNotRunningTest() {
        Actor actor = new Actor();
        Assert.assertFalse(actor.isRunning());
    }

    /**
     * Send data and check received message
     */
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

    /**
     * Send data and check received message in a "threaded" environment
     * @throws InterruptedException
     */
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

    /**
     * Send data and check received message in a "threaded" environment with an 
     * answer to an actor
     * @throws InterruptedException
     */
    @Test
    public void threadedSendReceiveTestMessageInfo() throws InterruptedException {
    	MessageInfo messageInfo2;
        Actor actor1, actor2;
        final Object result[] = { null };
        Thread actor1Thread, actor2Thread;

        actor1 = new Actor() {
            @Override
            protected void react(MessageInfo info, Object... data) {
                result[0] = data[0];
                info.answer("Answer");
                stop();
            }
        };

        actor2 = new Actor() {
            @Override
            protected void react(MessageInfo info, Object... data) {
                Assert.assertEquals(data[0].toString(), "Answer");
                
                stop();
            }
        };
        
        messageInfo2 = new MessageInfo();
        messageInfo2.setSender(actor2);
        
        actor2Thread = new Thread(actor2);
        actor2Thread.start();

        actor1Thread = new Thread(actor1);
        actor1Thread.start();
        actor1.post(messageInfo2, "Hello World");
        actor1Thread.join();

        actor2Thread.join();
        Assert.assertEquals(result[0], "Hello World");
    }
}
