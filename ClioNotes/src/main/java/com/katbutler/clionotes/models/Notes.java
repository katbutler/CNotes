package com.katbutler.clionotes.models;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class Notes {

    private List<Note> notes;

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public List<ContentValues> getContentValues() {
        List<ContentValues> values = new ArrayList<ContentValues>();
        for (Note n : getNotes()) {
            values.add(n.getContentValues());
        }
        return values;
    }

}