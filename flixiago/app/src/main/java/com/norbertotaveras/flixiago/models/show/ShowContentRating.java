package com.norbertotaveras.flixiago.models.show;

public class ShowContentRating {
    private String iso_3166_1;
    private String rating;

    public String getCountry() {
        return iso_3166_1;
    }

    public String getRating(){
        return rating;
    }
}
