package com.example.ebookreader;

import android.net.Uri;

import java.io.Serializable;

public class Ebook implements Serializable {
    private String title;
    private Uri ebookUri;

    public Ebook(String title) {
        this.title = title;
        this.ebookUri = null;
    }

    public Ebook(String title, Uri ebookUri){
        this.title = title;
        this.ebookUri = ebookUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getEbookUri() {
        return ebookUri;
    }

    public void setEbookUri(Uri ebookUri) {
        this.ebookUri = ebookUri;
    }
}
