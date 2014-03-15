package com.katbutler.clionotes.models;


import com.google.gson.annotations.SerializedName;

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
    private Date date;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;


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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}
