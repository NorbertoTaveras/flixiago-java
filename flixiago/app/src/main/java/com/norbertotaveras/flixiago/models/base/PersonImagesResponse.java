package com.norbertotaveras.flixiago.models.base;

import java.util.ArrayList;

public class PersonImagesResponse {
    private long id;
    private ArrayList<PersonImage> profiles;

    public long getId() {
        return id;
    }

    public ArrayList<PersonImage> getProfiles() {
        return profiles;
    }
}
