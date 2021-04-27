package com.norbertotaveras.flixiago.services.shows;

import com.norbertotaveras.flixiago.models.show.ShowTrailer;

import java.util.ArrayList;

public interface OnGetShowTrailersCallback {
    void onSuccess(ArrayList<ShowTrailer> showTrailers);
    void onFailure(Throwable error);
}
