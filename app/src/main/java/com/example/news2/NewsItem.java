package com.example.news2;

import java.io.Serializable;
import java.net.URL;

public class NewsItem implements Serializable {
    private String title;
    private String text;
    private int imageId;
    private URL imageURL;
    private String publishTime;
    private URL vedioURL;
    private String publisher;

    public NewsItem(String title,int imageId) {
        this.title = title;
        this.imageId = imageId;
    }

    public String getTitle() {
        return this.title;
    }

    public  int getImageId() {
        return this.imageId;
    }
}
