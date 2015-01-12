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
