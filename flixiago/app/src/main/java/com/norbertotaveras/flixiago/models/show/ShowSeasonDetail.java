package com.norbertotaveras.flixiago.models.show;

import androidx.annotation.Nullable;

import com.norbertotaveras.flixiago.helpers.InternetImage;
import com.norbertotaveras.flixiago.helpers.TmdbUrls;

import java.util.ArrayList;

public class ShowSeasonDetail implements InternetImage {
    private final long id;
    private final String name;
    private final String overview;
    private final String poster_path;
    private final ArrayList<ShowSeasonEpisode> episodes;

    public ShowSeasonDetail(long id,
                            String name,
                            String overview,
                            String poster_path,
                            ArrayList<ShowSeasonEpisode> episodes) {
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.poster_path = poster_path;
        this.episodes = episodes;
    }

    @Override
    @Nullable
    public String getThumbnailUrl() {
        return poster_path.isEmpty() ? null : TmdbUrls.IMAGE_BASE_URL_300px + poster_path;
    }

    @Override
    public String getThumbnailCaption() {
        return name;
    }

    public ArrayList<ShowSeasonEpisode> getEpisodes() {
        return episodes;
    }

    public long getId() {
        return id;
    }

    public String getOverview() {
        return overview;
    }
}
