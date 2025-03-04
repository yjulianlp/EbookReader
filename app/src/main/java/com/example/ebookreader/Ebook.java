package com.example.ebookreader;

import android.net.Uri;

import java.io.Serializable;

public class Ebook implements Serializable {
    private String title;
    private Uri ebookUri;
    private int lastScrollPos;
    private int currentChapter;


    public Ebook(String title) {
        this.title = title;
        this.ebookUri = null;
        this.lastScrollPos = 0;
        this.currentChapter = 0;
    }

    public Ebook(String title, Uri ebookUri){
        this.title = title;
        this.ebookUri = ebookUri;
        this.lastScrollPos = 0;
        this.currentChapter = 0;
    }

    public Ebook(String title, Uri ebookUri, int lastScrollPos){
        this.title = title;
        this.ebookUri = ebookUri;
        this.lastScrollPos = lastScrollPos;
        this.currentChapter = 0;
    }

    public Ebook(String title, Uri ebookUri, int lastScrollPos, int currentChapter){
        this.title = title;
        this.ebookUri = ebookUri;
        this.lastScrollPos = lastScrollPos;
        this.currentChapter = currentChapter;
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

    public int getLastScrollPos() {
        return lastScrollPos;
    }

    public void setLastScrollPos(int lastScrollPos) {
        this.lastScrollPos = lastScrollPos;
    }

    public int getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(int currentChapter) {
        this.currentChapter = currentChapter;
    }
}
