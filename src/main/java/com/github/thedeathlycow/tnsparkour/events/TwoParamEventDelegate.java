package com.github.thedeathlycow.tnsparkour.events;

import java.util.LinkedList;
import java.util.List;

public class TwoParamEventDelegate<A, B> {

    private final List<Listener<A, B>> listeners = new LinkedList<>();

    public void execute(A param1, B param2) {
        listeners.forEach((listener) -> listener.run(param1, param2));
    }

    public void register(Listener<A, B> listener) {
        listeners.add(listener);
    }

    public void remove(Listener<A, B> listener) {
        listeners.remove(listener);
    }

    public interface Listener<A, B> {
        void run(A param1, B param2);
    }

}
