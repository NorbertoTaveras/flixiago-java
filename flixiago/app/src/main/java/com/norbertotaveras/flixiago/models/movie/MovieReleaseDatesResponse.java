package com.norbertotaveras.flixiago.models.movie;

import java.util.ArrayList;

public class MovieReleaseDatesResponse {
    private long id;
    private ArrayList<MovieReleaseDateCountry> results;

    public long getId() {
        return id;
    }

    public ArrayList<MovieReleaseDateCountry> getResults() {
        return results;
    }
}
