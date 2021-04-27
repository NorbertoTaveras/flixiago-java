package com.norbertotaveras.flixiago.services.shows;

import com.norbertotaveras.flixiago.models.show.ShowContentRatingsResponse;

public interface OnGetShowContentRatingsCallback {
    void onSuccess(ShowContentRatingsResponse response);
    void onFailure(Throwable error);
}
