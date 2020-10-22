package com.singularitycoder.retrofitresponseobject.model;

import java.util.Collections;
import java.util.List;

public final class NewsItem {

    public static final class NewsResponse {

        private String status;
        private Integer totalResults;
        private List<NewsArticle> articles = Collections.EMPTY_LIST;

        public List<NewsArticle> getArticles() {
            return articles;
        }
    }

    public static final class NewsArticle {

        private NewsSource source;
        private String author;
        private String title;
        private String description;
        private String urlToImage;
        private String publishedAt;

        public NewsSource getSource() {
            return source;
        }

        public String getAuthor() {
            return author;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getUrlToImage() {
            return urlToImage;
        }

        public String getPublishedAt() {
            return publishedAt;
        }
    }

    public static final class NewsSource {

        private String id;
        private String name;

        public String getName() {
            return name;
        }
    }
}
