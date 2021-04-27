package com.norbertotaveras.flixiago.models.show;

import java.util.ArrayList;

public class ShowCreditsResponse {
    private long id;
    private ArrayList<ShowCredit> cast;

    public long getId() {
        return id;
    }

    public ArrayList<ShowCredit> getCast() {
        return cast;
    }
}
