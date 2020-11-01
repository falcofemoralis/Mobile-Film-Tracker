package com.vladyslav.offlinefilmtracker.Objects;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;

import java.io.Serializable;
import java.util.Arrays;

public class Actor implements Serializable {
    private String person_id, name, born, died, category;
    private String[] characters;

    public Actor(String person_id, String name, String born, String died, String characters, String category) {
        this.person_id = person_id;
        this.name = name;
        this.category = category;
        this.born = born;
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

    public void getPhoto(final Context context, final BitmapDrawable[] photo, final Runnable runnable) {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                photo[0] = ResourcesManager.getInstance(context).getPhotoDrawableById(person_id);
                runnable.run();
            }
        })).start();
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
