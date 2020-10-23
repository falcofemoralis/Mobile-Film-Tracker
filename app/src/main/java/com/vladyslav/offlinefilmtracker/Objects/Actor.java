package com.vladyslav.offlinefilmtracker.Objects;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

public class Actor implements Serializable {
    private String person_id, name, born, died, category;
    private String[] characters;

    public Actor(String person_id, String name, String born, String died, String characters, String category) {
        this.person_id = person_id;
        this.name = name;
        this.category = category;

        //в случае если неизвестна дата рождения
        if (born == null) born = "unknown";
        this.born = born;

        //если актер еще жив
        if (died == null) died = "alive";
        this.died = died;

        //т.к данные приходят в формате ["",""], то их нужно разделить
        characters = characters.trim();
        this.characters = characters.substring(1, characters.length() - 1).trim().split("\\s*,\\s*");
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

    public BitmapDrawable getPhoto(Context context) {
        try {
            return ResourcesManager.getInstance(context).getDrawableById(person_id, false);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "Actor{" +
                "person_id='" + person_id + '\'' +
                ", name='" + name + '\'' +
                ", born='" + born + '\'' +
                ", died='" + died + '\'' +
                ", characters=" + Arrays.toString(characters) +
                ", category='" + category + '\'' +
                '}';
    }
}
