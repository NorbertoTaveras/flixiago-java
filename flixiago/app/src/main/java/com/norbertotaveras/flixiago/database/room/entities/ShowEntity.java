package com.norbertotaveras.flixiago.database.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.norbertotaveras.flixiago.models.show.Show;

import java.util.HashMap;

@Entity(tableName = "table_shows")
public class ShowEntity implements MediaEntity {

    @PrimaryKey
    private final long id;

    @ColumnInfo(name = "name")
    private final String name;

    @ColumnInfo(name = "overview")
    private final String overview;

    @ColumnInfo(name = "poster_path")
    private final String poster_path;

    @ColumnInfo(name = "backdrop_path")
    private final String backdrop_path;

    @ColumnInfo(name = "vote_average")
    private final Float vote_average;

    @ColumnInfo(name = "first_air_date")
    private final String first_air_date;

    @ColumnInfo(name = "watched")
    private final boolean watched;

    @ColumnInfo(name = "last_modified")
    private final long unix_ms;

    public ShowEntity(
            long id,
            String name, String overview,
            String poster_path, String backdrop_path,
            Float vote_average, String first_air_date,
            boolean watched, long unix_ms) {
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.poster_path = poster_path;
        this.backdrop_path = backdrop_path;
        this.vote_average = vote_average;
        this.first_air_date = first_air_date;
        this.watched = watched;
        this.unix_ms = unix_ms;
    }

    public ShowEntity(Show show, boolean watched, long unix_ms) {
        this(show.getId(),
                show.getTitle(),
                show.getOverview(),
                show.getPosterPath(),
                show.getBackdropPath(),
                show.getVoteAverage(),
                show.getReleaseDate(),
                watched,
                unix_ms);
    }

    // Needed to make firebase deserializer happy
    @Ignore
    public ShowEntity() {
        this(0, "", "", "",
                "", 0.0f, "",
                false, 0);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return getName();
    }

    public String getName() {
        return name;
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
        return getFirst_air_date();
    }

    public String getFirst_air_date() {
        return first_air_date;
    }

    public boolean isWatched() {
        return watched;
    }

    public long getUnix_ms() {
        return unix_ms;
    }

    public HashMap<String, Object> asHashMap(final String userId) {
        return new HashMap<String, Object>() {{
            put("uid", userId);
            put("id", id);
            put("name", name);
            put("overview", overview);
            put("poster_path", poster_path);
            put("backdrop_path", backdrop_path);
            put("vote_average", vote_average);
            put("first_air_date", first_air_date);
            put("watched", watched);
            put("unix_ms", unix_ms);
        }};
    }
}
