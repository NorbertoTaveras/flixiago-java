package com.norbertotaveras.flixiago.services.shows;

import com.norbertotaveras.flixiago.models.show.Show;

public interface OnGetShowCallback {
    void onSuccess(Show show);
    void onFailure(Throwable error);
}
