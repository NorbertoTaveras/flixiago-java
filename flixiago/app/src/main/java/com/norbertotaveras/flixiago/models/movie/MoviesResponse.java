/*
    Author: Norberto Taveras
    File: MoviesResponse.java
    Purpose:
        * Provides a model for the movies response.
        * Retrieves the movie page
        & Retrieves all movies
 */
package com.norbertotaveras.flixiago.models.movie;

import com.norbertotaveras.flixiago.models.base.Dates;

import java.util.ArrayList;

public class MoviesResponse {

    private int page;
    private int total_results;
    private int total_pages;
    private ArrayList<Movie> results;
    private Dates dates;

    public int getPage() {
        return page;
    }

    public int getTotalResults() {
        return total_results;
    }

    public int getTotalPages() {
        return total_pages;
    }

    public ArrayList<Movie> getMovies() {
        return results;
    }

    public Dates getDates() {return dates; }
}
