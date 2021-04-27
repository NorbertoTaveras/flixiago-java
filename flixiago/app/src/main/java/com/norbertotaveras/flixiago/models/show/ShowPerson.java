package com.norbertotaveras.flixiago.models.show;

import com.norbertotaveras.flixiago.helpers.FormHelpers;
import com.norbertotaveras.flixiago.helpers.InternetImage;
import com.norbertotaveras.flixiago.helpers.TmdbUrls;

public class ShowPerson implements InternetImage {
    private long id;
    private String name;
    private String biography;
    private String profile_path;
    private String known_for_department;
    private String birthday;
    private String place_of_birth;

    @Override
    public String getThumbnailUrl() {
        return TmdbUrls.IMAGE_BASE_URL_300px + profile_path;
    }

    @Override
    public String getThumbnailCaption() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBiography() {
        return biography;
    }

    public String getProfilePath() {
        return profile_path;
    }

    public String getKnownFor() {
        return known_for_department;
    }

    public String formatBirthDate() {
        return FormHelpers.formatDate(birthday);
    }

    public String getBirthplace() {
        return place_of_birth;
    }
}
