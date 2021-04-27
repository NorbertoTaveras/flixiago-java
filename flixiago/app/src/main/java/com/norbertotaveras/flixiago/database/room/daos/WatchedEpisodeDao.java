package com.norbertotaveras.flixiago.database.room.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.norbertotaveras.flixiago.database.room.entities.WatchedEpisodeEntity;

import java.util.List;

@Dao
public interface WatchedEpisodeDao {

    // get all watched shows by id
    @Query("SELECT * FROM table_watched_episodes" +
            " WHERE show_id = :showId")
    List<WatchedEpisodeEntity> getWatchedByShowId(long showId);

    // delete or update show episodes by id
    @Query("UPDATE table_watched_episodes" +
            " SET watched=0" +
            " WHERE show_id = :showId" +
            " AND episode_id = :episodeId" +
            " AND season_id = :seasonId")
    void deleteByShowEpisodeSeason(long showId, long episodeId, long seasonId);

    // get all records modified since a specific time stamp
    @Query("SELECT * from table_watched_episodes WHERE last_modified > :unixMs")
    List<WatchedEpisodeEntity> recordsModifiedSince(long unixMs);

    // upsert watched episode entiy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(WatchedEpisodeEntity entity);

    // insert watch episode entity
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(WatchedEpisodeEntity entity);

    // get the count by id
    @Query("SELECT COUNT(*) from table_watched_episodes" +
            " WHERE show_id = :showId" +
            " AND episode_id = :episodeId" +
            " AND watched")
    int countById(long showId, long episodeId);

    @Query("SELECT COUNT(*) from table_watched_episodes" +
            " WHERE show_id = :showId" +
            " AND watched")
    int countWatchedByShowId(long showId);

    @Query("SELECT COUNT(*) from table_watched_episodes" +
            " WHERE show_id = :showId" +
            " AND season_id = :seasonId" +
            " AND watched")
    int countWatchedByShowIdSeasonId(long showId, long seasonId);

    @Query("SELECT * from table_watched_episodes" +
            " WHERE show_id = :showId" +
            " AND episode_id = :episodeId")
    WatchedEpisodeEntity getById(long showId, long episodeId);
}
