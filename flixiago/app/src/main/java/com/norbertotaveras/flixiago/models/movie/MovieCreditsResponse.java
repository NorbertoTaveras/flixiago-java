package com.norbertotaveras.flixiago.models.movie;

import java.util.ArrayList;

public class MovieCreditsResponse {

    private long id;
    private ArrayList<MovieCredit> cast;

    public long getId() {
        return id;
    }

    public ArrayList<MovieCredit> getCast() {
        return cast;
    }
}
