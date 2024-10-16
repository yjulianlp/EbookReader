package com.example.ebookreader;

import java.io.Serializable;

public class Ebook implements Serializable {
    public String title;
    public String content;

    public Ebook(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
