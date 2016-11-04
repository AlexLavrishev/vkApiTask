package com.example.alexlovr.httpclient.ItemClasses;

import android.graphics.Bitmap;

/**
 * Created by shadow on 28/10/16.
 */
public class MessageItem {
    private Bitmap ava;
    private String name;
    private String text;
    private String time;


    private int owner;

    public MessageItem(Bitmap ava, String name, String text, String time, int owner) {
        this.ava = ava;
        this.name = name;
        this.text = text;
        this.time = time;
        this.owner = owner;
    }


    public Bitmap getAva() {
        return ava;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public int getOwner() {
        return owner;
    }


    public void setText(String text) {
        this.text = text;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAva(Bitmap ava) {
        this.ava = ava;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

}
