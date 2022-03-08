package com.KReader.app;

public class Article {
    public String title;
    public String link;
    public String description;
    public String pubdate;
    public String guid;
    public String imageUrl;
    public int id;

    public Article(String title, String link, String description, String pubdate,String guid, int id, String imageUrl ) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubdate = pubdate;
        this.guid = guid;
        this.imageUrl = imageUrl;
        this.id = id;
    }
}