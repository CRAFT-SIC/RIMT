package com.suntend.arktoolbox.widgets.mainpage.beans;

import android.graphics.Color;

public class Theme {

    private int color;

    private String text;

    private boolean selected;

    private String link;


    public Theme(int color, String text, boolean selected, String link) {
        this.color = color;
        this.text = text;
        this.selected = selected;
        this.link = link;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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
