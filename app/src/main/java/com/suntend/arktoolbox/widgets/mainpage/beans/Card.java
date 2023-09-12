package com.suntend.arktoolbox.widgets.mainpage.beans;

/**
* Created By nooly
* class: Card
* package: com.suntend.arktoolbox.widgets.mainpage.beans
* 为主页IP衍生Adapter创建的Bean
* */
public class Card {
   private String title;

   private String titleEN;

   private String content;

   private String image;

    /**
     * class constructor
     * used by DataFactory
     * */
    public Card(String title, String titleEN, String content, String image) {
        this.title = title;
        this.titleEN = titleEN;
        this.content = content;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleEN() {
        return titleEN;
    }

    public void setTitleEN(String titleEN) {
        this.titleEN = titleEN;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
