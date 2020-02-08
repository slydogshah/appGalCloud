package io.appgal.cloud.messaging;

import java.time.OffsetDateTime;

public class MessageWindow {
    private OffsetDateTime start;
    private OffsetDateTime end;

    public MessageWindow(OffsetDateTime start, OffsetDateTime end)
    {
        this.start = start;
        this.end = end;
    }

    public OffsetDateTime getStart() {
        return start;
    }

    public OffsetDateTime getEnd() {
        return end;
    }
}
