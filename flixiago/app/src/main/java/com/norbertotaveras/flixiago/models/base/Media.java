package com.norbertotaveras.flixiago.models.base;

import android.os.Parcel;
import android.text.TextUtils;

import com.norbertotaveras.flixiago.database.room.entities.MediaEntity;
import com.norbertotaveras.flixiago.helpers.FormHelpers;
import com.norbertotaveras.flixiago.helpers.TmdbUrls;
import com.norbertotaveras.flixiago.services.base.OnGetMediaCallback;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Media implements Openable {
    private static final String BACKDROP_BASE_URL = TmdbUrls.IMAGE_BASE_URL_780px;

    protected final long id;
    protected final String backdrop_path;
    protected ArrayList<Long> genre_ids;
    protected ArrayList<Genre> genres;
    protected final String overview;
    protected String popularity;
    protected final String poster_path;
    protected final float vote_average;

    public abstract String getReleaseDate();
    public abstract String getTitle();
    public abstract void reload(OnGetMediaCallback callback);

    @SuppressWarnings("unchecked")
    protected Media(Parcel in) {
        id = in.readLong();
        backdrop_path = in.readString();
        genre_ids = in.readArrayList(Integer.class.getClassLoader());
        overview = in.readString();
        popularity = in.readString();
        poster_path = in.readString();
        vote_average = in.readFloat();
    }

    public Media(MediaEntity mediaEntity) {
        id = mediaEntity.getId();
        backdrop_path = mediaEntity.getBackdrop_path();
        overview = mediaEntity.getOverview();
        poster_path = mediaEntity.getPoster_path();
        vote_average = mediaEntity.getVote_average();
    }

    protected void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(backdrop_path);
        out.writeList(genre_ids);
        out.writeString(overview);
        out.writeString(popularity);
        out.writeString(poster_path);
        out.writeFloat(vote_average);
    }

    public int describeContents() {
        return 0;
    }

    public long getId() {
        return id;
    }

    public String getBackdropPath() {
        return backdrop_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public float getVoteAverage() {
        return vote_average;
    }

    public String getThumbnailUrl() {
        return TmdbUrls.IMAGE_BASE_URL_342px + poster_path;
    }

    public String getThumbnailCaption() {
        return getTitle();
    }

    public String getBackdropUrl() {
        return TmdbUrls.IMAGE_BASE_URL_780px + getBackdropPath();
    }

    public String formatReleaseDate() {
        String releaseDate = getReleaseDate();
        return FormHelpers.formatDate(releaseDate);
    }

    private ArrayList<Long> getGenreIds() {
        if (genre_ids == null) {
            genre_ids = new ArrayList<>(genres.size());
            for (Genre genre: genres)
                genre_ids.add(genre.getId());
        }
        return genre_ids;
    }

    public boolean isGenre(long genreId) {
        return getGenreIds().contains(genreId);
    }

    public String commaSeparatedGenres(HashMap<Long, String> genreList) {
        ArrayList<Long> genreIds = getGenreIds();
        if (genreIds.isEmpty())
            return null;

        String[] genreNames = new String[genreIds.size()];
        for (int i = 0; i < genreIds.size(); ++i)
            genreNames[i] = genreList.get(genreIds.get(i));

        return TextUtils.join(", ", genreNames);
    }
}
