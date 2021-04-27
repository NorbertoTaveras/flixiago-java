/*
    Author: Norberto Taveras
    File: MovieEntiy.java
    Purpose:
        * Assist setting columns within the table_movies
        * Assist on retrieving each column data within the table_movies

 */
package com.norbertotaveras.flixiago.database.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.norbertotaveras.flixiago.models.movie.Movie;

import java.util.Date;
import java.util.HashMap;

@Entity(tableName = "table_movies")
public class MovieEntity implements MediaEntity {

    @PrimaryKey
    private final long id;

    @ColumnInfo(name = "title")
    private final String title;

    @ColumnInfo(name = "overview")
    private final String overview;

    @ColumnInfo(name = "poster_path")
    private final String poster_path;

    @ColumnInfo(name = "backdrop_path")
    private final String backdrop_path;

    @ColumnInfo(name = "vote_average")
    private final Float vote_average;

    @ColumnInfo(name = "release_date")
    private final String release_date;

    @ColumnInfo(name = "watched")
    private final boolean watched;

    @ColumnInfo(name = "last_modified")
    private final long unix_ms;

    public MovieEntity(
            long id,
            String title, String overview,
            String poster_path, String backdrop_path,
            Float vote_average, String release_date,
            boolean watched, long unix_ms) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.poster_path = poster_path;
        this.backdrop_path = backdrop_path;
        this.vote_average = vote_average;
        this.release_date = release_date;
        this.watched = watched;
        this.unix_ms = unix_ms;
    }

    @Ignore
    public MovieEntity() {
        this(0, "", "", "", "",
                0.0f, "", false, 0);
    }

    public MovieEntity(Movie movie, boolean watched) {
        this(movie.getId(),
                movie.getTitle(),
                movie.getOverview(),
                movie.getPosterPath(),
                movie.getBackdropPath(),
                movie.getVoteAverage(),
                movie.formatReleaseDate(),
                watched,
                new Date().getTime());
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getOverview() {
        return overview;
    }

    @Override
    public String getPoster_path() {
        return poster_path;
    }

    @Override
    public String getBackdrop_path() {
        return backdrop_path;
    }

    @Override
    public Float getVote_average() {
        return vote_average;
    }

    @Override
    public String getRelease_date() {
        return release_date;
    }

    public HashMap<String, Object> asHashMap(final String uid) {
        return new HashMap<String, Object>() {{
            put("uid", uid);
            put("id", id);
            put("title", title);
            put("overview", overview);
            put("poster_path", poster_path);
            put("backdrop_path", backdrop_path);
            put("vote_average", vote_average);
            put("release_date", release_date);
            put("watched", watched);
            put("unix_ms", unix_ms);
        }};
    }

    public boolean isWatched() {
        return watched;
    }

    public long getUnix_ms() {
        return unix_ms;
    }
}
