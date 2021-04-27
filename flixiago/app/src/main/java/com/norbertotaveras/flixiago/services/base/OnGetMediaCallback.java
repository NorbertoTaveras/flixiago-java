package com.norbertotaveras.flixiago.services.base;

import com.norbertotaveras.flixiago.models.base.Media;

public interface OnGetMediaCallback {
    void onSuccess(Media media);
    void onFailure(Throwable error);
}
