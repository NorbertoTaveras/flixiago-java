/*
    Author: Norberto Taveras
    File: OnGetMoviePersonCallback.java
    Purpose:
        * Callback to get the cast of a movie
 */
package com.norbertotaveras.flixiago.services.movie;

import com.norbertotaveras.flixiago.models.movie.MoviePerson;

public interface OnGetMoviePersonCallback {
    void onSuccess(MoviePerson person);
    void onFailure(Throwable error);
}
