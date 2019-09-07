package com.example.news2;

import java.io.Serializable;
import java.util.ArrayList;

class Keywords implements Comparable, Serializable{
    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    private String score;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    private String word;

    @Override
    public int compareTo(Object o) {
        Keywords k = (Keywords) o;
        if(this.score.compareTo(k.score) <= 0)
            return 1;
        else
            return -1;
    }
}

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
    private ArrayList<Keywords> keywords;
    private boolean visited = false;
    private boolean stared = false;

    public ArrayList<Keywords> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<Keywords> keywords) {
        this.keywords = keywords;
    }

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

    public void setStared(boolean t) {
        this.stared = t;
    }

    public boolean getStared() {
        return this.stared;
    }
}