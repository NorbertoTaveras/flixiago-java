package com.norbertotaveras.flixiago.services.base;

import com.norbertotaveras.flixiago.models.base.PersonImage;

import java.util.ArrayList;

public interface OnGetPersonImagesCallback {
    void onSuccess(long id, ArrayList<PersonImage> images);
    void onFailure(Throwable error);
}
