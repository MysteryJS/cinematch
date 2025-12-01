package com.project.cinematch.Model;

public class Actor {

    private String id;          // εδώ θα χρησιμοποιούμε το όνομα ως id (OMDb limitation)
    private String name;
    private Integer age;        // optional, μπορεί να μείνει null
    private Double popularity;  // δικό μας metric ή από άλλο API

    public Actor() {
    }

    public Actor(String id, String name, Integer age, Double popularity) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.popularity = popularity;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }
}