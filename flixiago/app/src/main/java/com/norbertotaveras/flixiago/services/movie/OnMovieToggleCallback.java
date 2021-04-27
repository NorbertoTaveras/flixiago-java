package com.norbertotaveras.flixiago.services.movie;

import com.norbertotaveras.flixiago.models.movie.Movie;

public interface OnMovieToggleCallback {
    void onToggle(Movie movie);
}
