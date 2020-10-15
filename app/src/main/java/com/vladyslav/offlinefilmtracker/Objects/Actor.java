package com.vladyslav.offlinefilmtracker.Objects;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;
import com.vladyslav.offlinefilmtracker.R;

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
        if(born == null) born = "unknown";
        this.born = born;
        if(died == null) died = "alive";
        this.died = died;
        characters = characters.trim();
        this.characters = characters.substring(1, characters.length() - 1).trim().split("\\s*,\\s*");
        ;
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

    public Drawable getPhoto(Context context) {
        Drawable drawable =  ResourcesManager.getInstance(context).getPhotoByPersonId(person_id);
        if(drawable == null) drawable = context.getDrawable(R.drawable.noimage_photo);
        return drawable;
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
