/*
    Author: Norberto Taveras
    File: OnGetMovieTrailersCallback.java
    Purpose:
        * Callback to retrieve all trailers of a movie
 */
package com.norbertotaveras.flixiago.services.movie;

import com.norbertotaveras.flixiago.models.movie.MovieTrailer;

import java.util.ArrayList;

public interface OnGetMovieTrailersCallback {
    void onSuccess(ArrayList<MovieTrailer> movieTrailers);
    void onFailure(Throwable error);
}
