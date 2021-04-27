package com.norbertotaveras.flixiago.models.movie;

import com.norbertotaveras.flixiago.helpers.InternetImage;

public class MovieTrailer implements InternetImage {
    private static final String VIDEO_THUMBNAIL_BASE_URL = "http://img.youtube.com/vi/%s/0.jpg";

    private String key;

    public String getKey() {
        return key;
    }

    @Override
    public String getThumbnailUrl() {
        return String.format(VIDEO_THUMBNAIL_BASE_URL, getKey());
    }

    @Override
    public String getThumbnailCaption() {
        return null;
    }
}
