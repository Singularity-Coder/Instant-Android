package com.singularitycoder.roomnews.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.singularitycoder.roomnews.helper.room.NewsArticleConverter;

import java.util.Collections;
import java.util.List;

public final class NewsItem {

    @Entity(tableName = "table_news_response")
    public static final class NewsResponse {

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "roomIdNewsResponse")
        private int roomIdNewsResponse;

        @ColumnInfo(name = "status")
        @SerializedName("status")
        @Expose
        private String status;

        @ColumnInfo(name = "totalResults")
        @SerializedName("totalResults")
        @Expose
        private Integer totalResults;

        @TypeConverters(NewsArticleConverter.class)
        @SerializedName("articles")
        @Expose
        private List<NewsArticle> articles = Collections.EMPTY_LIST;

        public int getRoomIdNewsResponse() {
            return roomIdNewsResponse;
        }

        public void setRoomIdNewsResponse(int roomIdNewsResponse) {
            this.roomIdNewsResponse = roomIdNewsResponse;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getTotalResults() {
            return totalResults;
        }

        public void setTotalResults(Integer totalResults) {
            this.totalResults = totalResults;
        }

        public List<NewsArticle> getArticles() {
            return articles;
        }

        public void setArticles(List<NewsArticle> articles) {
            this.articles = articles;
        }
    }

    @Entity(tableName = "table_news_article")
    public static final class NewsArticle {

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "roomIdNewsArticle")
        private int roomIdNewsArticle;

        @Embedded(prefix = "source")
        @SerializedName("source")
        @Expose
        private NewsSource source;

        @ColumnInfo(name = "author")
        @SerializedName("author")
        @Expose
        private String author;

        @ColumnInfo(name = "title")
        @SerializedName("title")
        @Expose
        private String title;

        @ColumnInfo(name = "description")
        @SerializedName("description")
        @Expose
        private String description;

        @ColumnInfo(name = "content")
        @SerializedName("content")
        @Expose
        private String content;

        @ColumnInfo(name = "urlToImage")
        @SerializedName("urlToImage")
        @Expose
        private String urlToImage;

        @ColumnInfo(name = "publishedAt")
        @SerializedName("publishedAt")
        @Expose
        private String publishedAt;

        public int getRoomIdNewsArticle() {
            return roomIdNewsArticle;
        }

        public void setRoomIdNewsArticle(int roomIdNewsArticle) {
            this.roomIdNewsArticle = roomIdNewsArticle;
        }

        public NewsSource getSource() {
            return source;
        }

        public void setSource(NewsSource source) {
            this.source = source;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUrlToImage() {
            return urlToImage;
        }

        public void setUrlToImage(String urlToImage) {
            this.urlToImage = urlToImage;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }
    }

    @Entity(tableName = "table_news_source")
    public static final class NewsSource {

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "roomIdNewsSource")
        private int roomIdNewsSource;

        @ColumnInfo(name = "sourceId")
        @SerializedName("id")
        @Expose
        private String id;

        @ColumnInfo(name = "sourceName")
        @SerializedName("name")
        @Expose
        private String name;

        public int getRoomIdNewsSource() {
            return roomIdNewsSource;
        }

        public void setRoomIdNewsSource(int roomIdNewsSource) {
            this.roomIdNewsSource = roomIdNewsSource;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}