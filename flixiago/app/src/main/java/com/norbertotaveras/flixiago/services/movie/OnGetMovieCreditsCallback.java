/*
    Author: Norberto Taveras
    File: OnGetMovieCreditsCallback.java
    Purpose:
        * Callback to get the movie credits of a movie
 */
package com.norbertotaveras.flixiago.services.movie;

import com.norbertotaveras.flixiago.models.movie.MovieCreditsResponse;

public interface OnGetMovieCreditsCallback {
    void onSuccess(MovieCreditsResponse creditsResponse);
    void onFailure(Throwable error);
}
