package com.katbutler.clionotes.models;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;
import com.katbutler.clionotes.db.ClioContentProvider;

/**
 * Matter model class based on Clio's Matters
 *
 */
public class Matter {
    private Long id;

    @SerializedName("display_number")
    private String displayNumber;

    private String description;

    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayNumber() {
        return displayNumber;
    }

    public void setDisplayNumber(String displayNumber) {
        this.displayNumber = displayNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(ClioContentProvider.MattersTable.COLUMN_ID, getId());
        values.put(ClioContentProvider.MattersTable.COLUMN_DISPLAY_NUMBER, getDisplayNumber());
        values.put(ClioContentProvider.MattersTable.COLUMN_STATUS, getStatus());
        values.put(ClioContentProvider.MattersTable.COLUMN_DESCRIPTION, getDescription());
        return values;
    }

}
