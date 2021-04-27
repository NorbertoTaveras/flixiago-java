package com.norbertotaveras.flixiago.services.shows;

import com.norbertotaveras.flixiago.models.show.Show;

import java.util.ArrayList;

public interface OnGetShowsCallback {
    void onSuccess(int page, int totalPages, ArrayList<Show> shows);
    void onFailure(Throwable error);
}
