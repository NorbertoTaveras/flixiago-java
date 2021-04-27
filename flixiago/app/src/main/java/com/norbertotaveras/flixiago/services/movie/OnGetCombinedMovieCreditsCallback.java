/*
    Author: Norberto Taveras
    File: MovieCombinedCreditsCallback.java
    Purpose:
        * Callback to retrieve movie combined credits
 */
package com.norbertotaveras.flixiago.services.movie;

import com.norbertotaveras.flixiago.models.movie.MovieCombinedCredits;

public interface OnGetCombinedMovieCreditsCallback {
    void onSuccess(MovieCombinedCredits combinedCredits);
    void onFailure(Throwable error);
}
