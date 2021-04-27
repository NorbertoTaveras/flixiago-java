/*
    Author: Norberto Taveras
    File: OnGetMovieGenreLookupCallback.java
    Purpose:
        * Callback to the genres of a movie
 */
package com.norbertotaveras.flixiago.services.movie;

import java.util.HashMap;

public interface OnGetMovieGenreLookupCallback {
    void onSuccess(HashMap<Long, String> movieGenreList);
    void onFailure(Throwable error);
}
