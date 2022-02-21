package com.github.thedeathlycow.tnsparkour.events;

import java.util.LinkedList;
import java.util.List;

public class NoParamEventDelegate {

    private final List<Listener> listeners = new LinkedList<>();

    public void execute() {
        listeners.forEach(Listener::run);
    }

    public void register(Listener listener) {
        listeners.add(listener);
    }

    public void remove(Listener listener) {
        listeners.remove(listener);
    }

    public interface Listener {
        void run();
    }
}
