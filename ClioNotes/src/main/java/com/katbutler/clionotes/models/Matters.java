package com.katbutler.clionotes.models;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class Matters {

    private List<Matter> matters;

    public List<Matter> getMatters() {
        return matters;
    }

    public void setMatters(List<Matter> matters) {
        this.matters = matters;
    }

    public List<ContentValues> getContentValues() {
        List<ContentValues> values = new ArrayList<ContentValues>();
        for (Matter m : getMatters()) {
            values.add(m.getContentValues());
        }
        return values;
    }

}
