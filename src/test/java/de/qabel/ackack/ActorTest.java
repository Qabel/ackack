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
    	MessageInfo messageInfo;
        Actor actor, actor2;
        final Object result[] = { null };
        Thread actorThread, actor2Thread;

        actor = new Actor() {
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
                Assert.assertEquals(data[0], "Answer");
                
                stop();
            }
        };
        
        messageInfo = new MessageInfo();
        messageInfo.setSender(actor2);
        
        actor2Thread = new Thread(actor2);
        actor2Thread.start();

        actorThread = new Thread(actor);
        actorThread.start();
        actor.post(messageInfo, "Hello World");
        actorThread.join();

        actor2Thread.join();
        Assert.assertEquals(result[0], "Hello World");
    }



}
