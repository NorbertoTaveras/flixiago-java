/*
    Author: Norberto Taveras
    File: OnGetMovieReviewsCallback.java
    Purpose:
        * Callback to get the reviews of a movie
 */
package com.norbertotaveras.flixiago.services.movie;

import com.norbertotaveras.flixiago.models.movie.MovieReview;

import java.util.ArrayList;

public interface OnGetMovieReviewsCallback {
    void onSuccess(ArrayList<MovieReview> movieReviews);
    void onFailure(Throwable error);
}
