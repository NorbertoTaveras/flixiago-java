package com.norbertotaveras.flixiago.services.shows;

import com.norbertotaveras.flixiago.models.show.ShowPerson;

public interface OnGetShowPersonCallback {
    void onSuccess(ShowPerson person);
    void onFailure(Throwable error);
}
