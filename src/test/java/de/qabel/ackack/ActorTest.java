package de.qabel.ackack;

import org.junit.Assert;
import org.junit.Test;

class TestClass {
    public String data;
}

public class ActorTest {

    TestClass tc = new TestClass();
    TestClass tcc = new TestClass();
    Actor a = new Actor();
    Actor b = new Actor();
    Event e = new Event();

    @Test
    public void isNotRunningTest() {
        Assert.assertFalse(a.isRunning());
    }

    @Test
    public void sendTest() {
        a.register("New Event", TestClass.class, new Reactor<TestClass>() {
            @Override
            public void onEvent(Event<TestClass> event) {
                tc = event.getData();
            }
        });
        tcc.data = "foo";
        Assert.assertTrue(a.send(b, "New Event", tcc));
        Assert.assertEquals("foo", tcc.data);
    }
}
