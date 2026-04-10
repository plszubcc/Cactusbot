package io.github.hello09x.fakeplayer.core.manager.naming;

import java.util.Collections;
import java.util.LinkedList;

public class NameSource {

    private final LinkedList<Integer> names;
    private int capacity;

    public NameSource(int initializeCapacity) {
        super();
        this.capacity = initializeCapacity;
        this.names = new LinkedList();

        for (int i = 0; i < initializeCapacity; ++i) {
            this.names.add(i);
        }

    }

    public NameSource() {
        this(0);
    }

    public int pop() {
        if (this.names.isEmpty()) {
            int newCapacity = this.capacity * 2;

            for (int i = this.capacity; i < newCapacity; ++i) {
                this.names.add(i);
            }

            this.capacity = newCapacity;
        }

        return (Integer) this.names.pop();
    }

    public void push(int i) {
        if (i < this.capacity) {
            if (!this.names.contains(i)) {
                this.names.push(i);
                this.sort();
            }
        }
    }

    public void sort() {
        Collections.sort(this.names);
    }
}
