package com.norbertotaveras.flixiago.services.shows;

import com.norbertotaveras.flixiago.models.show.ShowCertificationsResponse;

public interface OnGetShowCertificationsCallback {
    void onSuccess(ShowCertificationsResponse response);
    void onFailure(Throwable error);
}
