package com.katbutler.clionotes.models;


import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;
import com.katbutler.clionotes.db.ClioContentProvider;

import java.util.Date;

/**
 * Note model class based on Clio's Note
 *
 */
public class Note {

/*
    Example Note JSON

    {
        "id": 26225778,
        "subject": "Sample",
        "detail": "Foo",
        "date": "2013-12-13",
        "created_at": "2013-12-13T18:14:32+00:00",
        "updated_at": "2014-01-17T00:57:12+00:00",
        "regarding": {
            "id": 1021016282,
            "type": "Matter",
            "name": "00005-Apple Inc.",
            "url": "/api/v2/1021016282"
        }
    }

*/

    private Long id;
    private String subject;
    private String detail;
    private String date;
    private Regarding regarding;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;


    public Note() {

    }

    public Note(Long id) {
        setId(id);
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Regarding getRegarding() {
        return regarding;
    }

    public void setRegarding(Regarding regarding) {
        this.regarding = regarding;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(ClioContentProvider.NotesTable.COLUMN_ID, getId());
        values.put(ClioContentProvider.NotesTable.COLUMN_SUBJECT, getSubject());
        values.put(ClioContentProvider.NotesTable.COLUMN_DETAIL, getDetail());
        values.put(ClioContentProvider.NotesTable.COLUMN_MATTER_ID_FK, getRegarding().getId());
        return values;
    }

    public class Regarding {
        private Long id;
        private String type = "Matter";
        private String name;
        private String url;

        public Regarding(Long id) {
            setId(id);
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

}
