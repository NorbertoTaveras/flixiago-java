/*
    Author: Norberto Taveras
    File: OnGetMovieCallback.java
    Purpose:
        * Callback to the specific details of a movie
 */
package com.norbertotaveras.flixiago.services.movie;

import com.norbertotaveras.flixiago.models.movie.Movie;

public interface OnGetMovieCallback {
    void onSuccess(Movie movie);
    void onFailure(Throwable error);
}
