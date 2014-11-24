import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by tox on 11/24/14.
 */
public class Vent {
    public class Event<T extends Object> implements Cloneable {
        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        private T data;

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        long time;
        public T as(Class<T> cls) {
            return cls.cast(getData());
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    static private Map<Object, Vent> vents = Collections.synchronizedMap(new HashMap<Object, Vent>());

    private LinkedBlockingQueue<Event<?>> inQueue = new LinkedBlockingQueue<Event<?>>();

    public boolean send(Object receiver, String event, Object data) {
        Event e = new Event();
        Vent vent = null;
        if(receiver instanceof Vent) {
            vent = ((Vent)receiver);
        }
        else {
            vent = vents.get(receiver);
        }

        if(vent == null) {
            return false;
        }

        vent.queue(e.clone());
        return true;
    }

    private void queue(Event<?> e) {
        try {
            inQueue.put(e);
        } catch (InterruptedException e1) {
            // TODO
            e1.printStackTrace();
        }
    }

    public Event nextEvent() {
        try {
            return inQueue.take();
        } catch (InterruptedException e) {
            // TODO
            return null;
        }
    }

    public void nextEvent(String event) {

    }
}
