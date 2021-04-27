package com.norbertotaveras.flixiago.services.movie;

import com.norbertotaveras.flixiago.models.movie.MovieReleaseDatesResponse;

public interface OnGetMovieReleaseDatesCallback {
    void onSuccess(MovieReleaseDatesResponse response);
    void onFailure(Throwable error);
}
