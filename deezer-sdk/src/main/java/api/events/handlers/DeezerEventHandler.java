package api.events.handlers;

import api.events.DeezerEvent;

import java.util.ArrayList;
import java.util.List;

public class DeezerEventHandler<T extends DeezerEvent> {
    private List<DeezerListener<T>> listeners;

    public DeezerEventHandler() {
        listeners = new ArrayList<>();
    }

    public void addListener(DeezerListener<T> newListener) {
        listeners.add(newListener);
    }

    public void removeListener(DeezerListener<T> removedListener) {
        listeners.remove(removedListener);
    }

    public void invoke(T event) {
        for (DeezerListener<T> listener : listeners)
            listener.invoke(event);
    }

    public void clear() {

    }
}
