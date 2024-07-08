package com.bilcodes.animestack.model;

public class AnimeModel {

    String title;
    String description;
    String posterImage;
    String rating;
    String subType; //tv/movie
    String ageRating;
    String status;
    String coverImage;

    public AnimeModel(String title, String description, String posterImage, String rating, String subType, String ageRating, String status, String coverImage) {
        this.title = title;
        this.description = description;
        this.posterImage = posterImage;
        this.rating = rating;
        this.subType = subType;
        this.ageRating = ageRating;
        this.status = status;
        this.coverImage = coverImage;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getSubType() {
        return subType;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public String getStatus() {
        return status;
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

    public String getPosterImage() {
        return posterImage;
    }

    public String getRating() {
        return rating;
    }

}
