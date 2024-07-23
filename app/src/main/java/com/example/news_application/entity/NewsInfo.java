package com.example.news_application.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;

public class NewsInfo {

    private String pageSize;
    private Integer total;
    private List<DataDTO> data;
    private String currentPage;

    // Getters and Setters
    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public static class DataDTO implements Parcelable {
        private String image;
        private String publishTime;
        private List<KeywordsDTO> keywords;
        private String language;
        private String video;
        private String title;
        private List<WhenDTO> when;
        private String content;
        private String openRels;
        private String url;
        private List<PersonsDTO> persons;
        private String newsID;
        private String crawlTime;
        private List<OrganizationsDTO> organizations;
        private String publisher;
        private List<LocationsDTO> locations;
        private List<WhereDTO> where;
        private List<WhoDTO> who;
        private boolean isViewed;

        protected DataDTO(Parcel in) {
            title = in.readString();
            url = in.readString();
            image = in.readString();
            publishTime = in.readString();
            video = in.readString();
            content = in.readString();
            publisher = in.readString();
            newsID = in.readString();
        }

        public static final Creator<DataDTO> CREATOR = new Creator<DataDTO>() {
            @Override
            public DataDTO createFromParcel(Parcel in) {
                return new DataDTO(in);
            }

            @Override
            public DataDTO[] newArray(int size) {
                return new DataDTO[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(url);
            dest.writeString(image);
            dest.writeString(publishTime);
            dest.writeString(video);
            dest.writeString(content);
            dest.writeString(publisher);
            dest.writeString(newsID);
        }

        // Getters and Setters

        public boolean isViewed() {
            return isViewed;
        }

        public void setViewed(boolean viewed) {
            isViewed = viewed;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            Log.d("setImage", "Original image string: " + image);

            if ("[]".equals(image)){
                this.image = null;
            }

            if (image != null && image.startsWith("[") && image.endsWith("]")) {
                image = image.substring(1, image.length() - 1);

                String[] urls = image.split(", ");
                this.image = null;

                for (String url : urls) {
                    url = url.replaceAll("&thumbnail=.*?(&|$)", "");
                    if (url != null && !url.isEmpty()) {
                        this.image = url;
                        Log.d("setImage", "Processed URL: " + url);
                        break;
                    }
                }

                if (this.image == null) {
                    Log.d("setImage", "No valid URLs found, setting image to null.");
                }
            } else {
                this.image = null;
                Log.d("setImage", "Invalid image string, setting image to null.");
            }
        }

        public String getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(String publishTime) {
            this.publishTime = publishTime;
        }

        public List<KeywordsDTO> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<KeywordsDTO> keywords) {
            this.keywords = keywords;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<WhenDTO> getWhen() {
            return when;
        }

        public void setWhen(List<WhenDTO> when) {
            this.when = when;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getOpenRels() {
            return openRels;
        }

        public void setOpenRels(String openRels) {
            this.openRels = openRels;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<PersonsDTO> getPersons() {
            return persons;
        }

        public void setPersons(List<PersonsDTO> persons) {
            this.persons = persons;
        }

        public String getNewsID() {
            return newsID;
        }

        public void setNewsID(String newsID) {
            this.newsID = newsID;
        }

        public String getCrawlTime() {
            return crawlTime;
        }

        public void setCrawlTime(String crawlTime) {
            this.crawlTime = crawlTime;
        }

        public List<OrganizationsDTO> getOrganizations() {
            return organizations;
        }

        public void setOrganizations(List<OrganizationsDTO> organizations) {
            this.organizations = organizations;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        public List<LocationsDTO> getLocations() {
            return locations;
        }

        public void setLocations(List<LocationsDTO> locations) {
            this.locations = locations;
        }

        public List<WhereDTO> getWhere() {
            return where;
        }

        public void setWhere(List<WhereDTO> where) {
            this.where = where;
        }

        public List<WhoDTO> getWho() {
            return who;
        }

        public void setWho(List<WhoDTO> who) {
            this.who = who;
        }

        public static class KeywordsDTO {
            private Double score;
            private String word;

            // Getters and Setters

            public Double getScore() {
                return score;
            }

            public void setScore(Double score) {
                this.score = score;
            }

            public String getWord() {
                return word;
            }

            public void setWord(String word) {
                this.word = word;
            }
        }

        public static class WhenDTO {
            private Double score;
            private String word;

            // Getters and Setters

            public Double getScore() {
                return score;
            }

            public void setScore(Double score) {
                this.score = score;
            }

            public String getWord() {
                return word;
            }

            public void setWord(String word) {
                this.word = word;
            }
        }

        public static class PersonsDTO {
            private Integer count;
            private String linkedURL;
            private String mention;

            // Getters and Setters

            public Integer getCount() {
                return count;
            }

            public void setCount(Integer count) {
                this.count = count;
            }

            public String getLinkedURL() {
                return linkedURL;
            }

            public void setLinkedURL(String linkedURL) {
                this.linkedURL = linkedURL;
            }

            public String getMention() {
                return mention;
            }

            public void setMention(String mention) {
                this.mention = mention;
            }
        }

        public static class OrganizationsDTO {
            private Integer count;
            private String linkedURL;
            private String mention;

            // Getters and Setters

            public Integer getCount() {
                return count;
            }

            public void setCount(Integer count) {
                this.count = count;
            }

            public String getLinkedURL() {
                return linkedURL;
            }

            public void setLinkedURL(String linkedURL) {
                this.linkedURL = linkedURL;
            }

            public String getMention() {
                return mention;
            }

            public void setMention(String mention) {
                this.mention = mention;
            }
        }

        public static class LocationsDTO {
            private Double lng;
            private Integer count;
            private String linkedURL;
            private Double lat;
            private String mention;

            // Getters and Setters

            public Double getLng() {
                return lng;
            }

            public void setLng(Double lng) {
                this.lng = lng;
            }

            public Integer getCount() {
                return count;
            }

            public void setCount(Integer count) {
                this.count = count;
            }

            public String getLinkedURL() {
                return linkedURL;
            }

            public void setLinkedURL(String linkedURL) {
                this.linkedURL = linkedURL;
            }

            public Double getLat() {
                return lat;
            }

            public void setLat(Double lat) {
                this.lat = lat;
            }

            public String getMention() {
                return mention;
            }

            public void setMention(String mention) {
                this.mention = mention;
            }
        }

        public static class WhereDTO {
            private Double score;
            private String word;

            // Getters and Setters

            public Double getScore() {
                return score;
            }

            public void setScore(Double score) {
                this.score = score;
            }

            public String getWord() {
                return word;
            }

            public void setWord(String word) {
                this.word = word;
            }
        }

        public static class WhoDTO {
            private Double score;
            private String word;

            // Getters and Setters

            public Double getScore() {
                return score;
            }

            public void setScore(Double score) {
                this.score = score;
            }

            public String getWord() {
                return word;
            }

            public void setWord(String word) {
                this.word = word;
            }
        }
    }
}

