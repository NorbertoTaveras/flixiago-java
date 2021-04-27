package com.norbertotaveras.flixiago.services.shows;

import com.norbertotaveras.flixiago.models.show.ShowCreditsResponse;

public interface OnGetShowCreditsCallback {
    void onSuccess(ShowCreditsResponse creditsResponse);
    void onFailure(Throwable error);
}
