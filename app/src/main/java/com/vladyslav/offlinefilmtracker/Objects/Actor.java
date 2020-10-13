package com.vladyslav.offlinefilmtracker.Objects;

public class Actor {
    private String person_id;
    private String name;
    private String born;
    private String died;

    public Actor(String person_id, String name, String born, String died) {
        this.person_id = person_id;
        this.name = name;
        this.born = born;
        this.died = died;
    }
}
