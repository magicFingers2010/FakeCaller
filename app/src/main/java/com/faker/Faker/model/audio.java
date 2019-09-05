package com.faker.Faker.model;

public class audio {

    private String name;
    private Boolean isPaused;
    private Boolean inProgress;


    public audio() {
    }

    public audio(String name, Boolean isPaused, Boolean inProgress) {
        this.name = name;
        this.isPaused = isPaused;
        this.inProgress = inProgress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPaused() {
        return isPaused;
    }

    public void setPaused(Boolean paused) {
        isPaused = paused;
    }

    public Boolean getInProgress() {
        return inProgress;
    }

    public void setInProgress(Boolean inProgress) {
        this.inProgress = inProgress;
    }
}
