package com.suntend.arktoolbox.widgets.mainpage.beans;

import android.graphics.Color;

public class Theme {

    private int color;

    private String text;

    private String link;


    public Theme(int color, String text, String link) {
        this.color = color;
        this.text = text;
        this.link = link;
    }


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
