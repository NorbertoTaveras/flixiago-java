package com.norbertotaveras.flixiago.models.show;

import java.util.ArrayList;

public class ShowContentRatingsResponse {
    private long id;
    private ArrayList<ShowContentRatingCountry> results;

    public long getId() {
        return id;
    }

    public ArrayList<ShowContentRatingCountry> getResults() {
        return results;
    }
}
