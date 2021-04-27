package com.norbertotaveras.flixiago.models.base;

import com.norbertotaveras.flixiago.helpers.InternetImage;
import com.norbertotaveras.flixiago.helpers.TmdbUrls;

public class PersonImage implements InternetImage {
    private String file_path;

    @Override
    public String getThumbnailUrl() {
        return TmdbUrls.IMAGE_BASE_URL_342px + file_path;
    }

    @Override
    public String getThumbnailCaption() {
        return null;
    }
}
