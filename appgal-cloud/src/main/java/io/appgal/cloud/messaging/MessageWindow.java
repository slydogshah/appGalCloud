package io.appgal.cloud.messaging;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.OffsetDateTime;

public class MessageWindow {
    private OffsetDateTime start;
    private OffsetDateTime end;
    private JsonArray messages;

    public MessageWindow(OffsetDateTime start, OffsetDateTime end)
    {
        this.start = start;
        this.end = end;
        this.messages = new JsonArray();
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

    public synchronized JsonArray getCopyOfMessages()
    {
        JsonArray copy = this.messages;
        this.messages = null;
        return copy;
    }

    public synchronized void addMessage(JsonObject jsonObject)
    {
        if(this.messages == null)
        {
            return;
        }
        this.messages.add(jsonObject);
    }
}
