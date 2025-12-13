package com.project.cinematch.Model;

public class Movie {

    private String id;
    private String title;
    private String year;
    private String director;
    private double imdbRating;
    private String imdbVotes;
    private String poster;
    private String awards;

    public Movie() {
    }

    public Movie(String id,
                 String title,
                 String year,
                 String director,
                 double imdbRating,
                 String imdbVotes,
                 String poster,
                 String awards) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.imdbRating = imdbRating;
        this.imdbVotes = imdbVotes;
        this.poster = poster;
        this.awards = awards;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(double imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getImdbVotes() {
        return imdbVotes;
    }

    public void setImdbVotes(String imdbVotes) {
        this.imdbVotes = imdbVotes;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }
}