package com.norbertotaveras.flixiago.services.shows;

import com.norbertotaveras.flixiago.models.show.ShowSeasonDetail;

public interface OnShowSeasonDetailCallback {
    void onSuccess(ShowSeasonDetail season);
    void onFailure(Throwable error);
}
