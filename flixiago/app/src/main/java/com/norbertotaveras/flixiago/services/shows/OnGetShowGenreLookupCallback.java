package com.norbertotaveras.flixiago.services.shows;

import java.util.HashMap;

public interface OnGetShowGenreLookupCallback {
    void onSuccess(HashMap<Long, String> showGenreList);
    void onFailure(Throwable error);
}
