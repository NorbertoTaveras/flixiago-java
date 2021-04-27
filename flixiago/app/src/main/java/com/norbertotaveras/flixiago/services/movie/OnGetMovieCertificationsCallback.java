package com.norbertotaveras.flixiago.services.movie;

import com.norbertotaveras.flixiago.models.movie.MovieCertificationsResponse;

public interface OnGetMovieCertificationsCallback {
    void onSuccess(MovieCertificationsResponse response);
    void onFailure(Throwable error);
}
