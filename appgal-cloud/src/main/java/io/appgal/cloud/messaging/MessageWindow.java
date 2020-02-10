package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;

import java.time.OffsetDateTime;

public class MessageWindow {
    private OffsetDateTime start;
    private OffsetDateTime end;
    private JsonArray messages;

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

    public JsonArray getMessages() {
        return messages;
    }

    public void setMessages(JsonArray messages) {
        this.messages = messages;
    }
}
