package com.norbertotaveras.flixiago.services.movie;

import com.norbertotaveras.flixiago.models.movie.Movie;

public interface OnMovieClickCallback {
    void onClick(Movie movie);
    void onFavoriteClick(Movie movie);
}
