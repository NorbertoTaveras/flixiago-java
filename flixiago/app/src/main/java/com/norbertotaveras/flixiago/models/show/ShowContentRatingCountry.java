package com.norbertotaveras.flixiago.models.show;

public class ShowContentRatingCountry {
    private String iso_3166_1;
    private String rating;

    public String getCountryCode() {
        return iso_3166_1;
    }

    public String getRating() {
        return rating;
    }
}
