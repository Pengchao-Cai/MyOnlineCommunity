package com.spring.community.dto;

public class GithubUser {
    String name;
    Long id;
    String biolo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBiolo() {
        return biolo;
    }

    // Json.parseObject matches setAttribute with attribute in JSON
    public void setBio(String bio) {
        this.biolo = bio;
    }
}
