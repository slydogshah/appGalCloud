package io.appgal.cloud.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class Note implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(Note.class);

    protected String note;

    public Note(String note)
    {
        this.note = note;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public static Note parse(String json)
    {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String note = jsonObject.get("note").getAsString();
        return new Note(note);
    }

    public JsonObject toJson()
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("note", this.note);
        return jsonObject;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }
}
