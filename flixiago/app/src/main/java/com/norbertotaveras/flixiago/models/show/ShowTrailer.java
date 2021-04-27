package com.norbertotaveras.flixiago.models.show;

import com.norbertotaveras.flixiago.helpers.InternetImage;

public class ShowTrailer implements InternetImage {
    private static final String VIDEO_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi/%s/0.jpg";

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
