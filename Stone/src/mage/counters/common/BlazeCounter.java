package mage.counters.common;

import mage.counters.Counter;

public class BlazeCounter extends Counter {
    public BlazeCounter() {
        this(1);
    }

    public BlazeCounter(int amount) {
        super("Blaze");
        this.count = amount;
    }
}