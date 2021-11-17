package com.nagarro.driven.core.util;

public enum SleepReasons implements SleepReason {
    // wait because ...
    UI("ui has to render"),
    SERVER("otherwise to many requests would be sent"),
    TIME("some event should happen after sleeping");

    private final String reason;

    SleepReasons(String reason) {
        this.reason = reason;
    }

    @Override
    public String getReason() {
        return reason;
    }
}

