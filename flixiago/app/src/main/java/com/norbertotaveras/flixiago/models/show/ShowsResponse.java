package com.norbertotaveras.flixiago.models.show;

import java.util.ArrayList;

public class ShowsResponse {

    private int page;
    private int total_results;
    private int total_pages;
    private ArrayList<Show> results;

    public int getPage() {
        return page;
    }

    public int getTotalResults() {
        return total_results;
    }

    public int getTotalPages() {
        return total_pages;
    }

    public ArrayList<Show> getShows() {
        return results;
    }
}
