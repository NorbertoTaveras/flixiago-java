package com.norbertotaveras.flixiago.services.movie;

import com.norbertotaveras.flixiago.models.movie.Movie;

import java.util.ArrayList;

public interface OnGetMoviesCallback {
    void onSuccess(int page, int totalPages, ArrayList<Movie> movies);
    void onFailure(Throwable error);
}
