package com.example.ebookreader;

import java.io.Serializable;

public class Ebook implements Serializable {
    public String title;

    public Ebook(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
