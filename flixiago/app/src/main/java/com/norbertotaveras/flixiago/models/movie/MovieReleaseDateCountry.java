package com.norbertotaveras.flixiago.models.movie;

import java.util.ArrayList;

public class MovieReleaseDateCountry {
    private String iso_3166_1;
    private ArrayList<MovieReleaseDate> release_dates;

    public String getCountryCode() {
        return iso_3166_1;
    }

    public ArrayList<MovieReleaseDate> getReleaseDates() {
        return release_dates;
    }
}
