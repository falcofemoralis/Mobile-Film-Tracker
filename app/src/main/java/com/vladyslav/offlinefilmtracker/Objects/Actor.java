package com.vladyslav.offlinefilmtracker.Objects;

public class Actor {
    private String person_id;
    private String name;
    private String born;
    private String died;
    private String[] characters;
    private String category;

    public Actor(String person_id, String name, String born, String died, String characters, String category) {
        this.person_id = person_id;
        this.name = name;
        this.born = born;
        this.died = died;
        characters = characters.trim();
        this.characters = characters.substring(1, characters.length() - 1).trim().split("\\s*,\\s*");;
        this.category = category;
    }

    public String getPerson_id() {
        return person_id;
    }

    public String getName() {
        return name;
    }

    public String getBorn() {
        return born;
    }

    public String getDied() {
        return died;
    }

    public String[] getCharacters() {
        return characters;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Actor{" +
                "person_id='" + person_id + '\'' +
                ", name='" + name + '\'' +
                ", born='" + born + '\'' +
                ", died='" + died + '\'' +
                ", characters='" + characters + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
