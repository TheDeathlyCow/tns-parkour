package com.github.thedeathlycow.tnsparkour.events;

import java.util.LinkedList;
import java.util.List;

public class OneParamEventDelegate<T> {

    private final List<Listener<T>> listeners = new LinkedList<>();

    public void execute(T param) {
        listeners.forEach((listener) -> listener.run(param));
    }

    public void register(Listener<T> listener) {
        listeners.add(listener);
    }

    public void remove(Listener<T> listener) {
        listeners.remove(listener);
    }

    public interface Listener<T> {
        void run(T param);
    }

}
