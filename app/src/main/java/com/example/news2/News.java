package com.example.news2;

import java.io.Serializable;

public class News implements Serializable {
    private String publishTime;
    private String title;
    private String content;
    private String image;
    private String video;
    private String newsID;
    private String publisher;
    private String category;
    private String[] images;
    private boolean visited = false;
    private boolean stared = false;

    public void setImages() {
        String str = this.image;
        str = str.replaceAll("[\\[\\]]", "");
        this.images = str.split(",");
    }

    public String[] getImages() {
        return images;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVideo() {
        return video;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }

    public String getNewsID() {
        return newsID;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setVisited() {
        this.visited = true;
    }

    public boolean getVisited() {
        return this.visited;
    }

    public boolean Stared() {
        boolean t = stared;
        stared = !stared;
        return t;
    }

    public boolean getStared() {
        return this.stared;
    }
}