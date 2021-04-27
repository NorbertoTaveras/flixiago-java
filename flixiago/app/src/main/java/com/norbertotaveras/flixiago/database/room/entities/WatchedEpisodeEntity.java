package com.norbertotaveras.flixiago.database.room.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import java.util.HashMap;

@Entity(
    tableName = "table_watched_episodes",

    // Fast lookup by all fields
    primaryKeys = {
            "show_id",
            "episode_id",
            "season_id"
    },

    indices = {
            // Fast get all records for a given season of a given show
            @Index(name = "show_season",
                    value = {"show_id", "season_id" }),

            @Index(name = "last_modified",
                value = { "last_modified" })
    }
)
public class WatchedEpisodeEntity {
    @ColumnInfo(name = "show_id")
    private final long showId;

    @ColumnInfo(name = "episode_id")
    private final long episodeId;

    @ColumnInfo(name = "season_id")
    private final long seasonId;

    @ColumnInfo(name = "watched")
    private final boolean watched;

    @ColumnInfo(name = "last_modified")
    public long unix_ms;

    public WatchedEpisodeEntity(long showId, long episodeId, long seasonId,
                                boolean watched, long unix_ms) {
        this.showId = showId;
        this.episodeId = episodeId;
        this.seasonId = seasonId;
        this.watched = watched;
        this.unix_ms = unix_ms;
    }

    // used by fire store deserializer, do not delete
    @Ignore
    public WatchedEpisodeEntity()
    {
        this(0, 0, 0, false, 0);
    }

    public long getShowId() {
        return showId;
    }

    public long getEpisodeId() {
        return episodeId;
    }

    public long getSeasonId() {
        return seasonId;
    }

    public long getUnixMs() {
        return unix_ms;
    }

    public boolean isWatched() {
        return watched;
    }

    public HashMap<String, Object> asHashMap(final String uid) {
        HashMap<String, Object> result = new HashMap<String, Object>(5) {{
            put("uid", uid);
            put("showId", showId);
            put("episodeId", episodeId);
            put("seasonId", seasonId);
            put("watched", watched);
            put("unix_ms", unix_ms);
        }};
        return result;
    }

    public void setUnixMs(long unixMs) {
        unix_ms = unixMs;
    }
}
