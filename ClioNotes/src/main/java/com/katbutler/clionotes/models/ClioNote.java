package com.katbutler.clionotes.models;

/**
 * Note wrapper class for posting to Clio
 */
public class ClioNote {
    private Note note;

    public ClioNote(Note n) {
        setNote(n);
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }
}
