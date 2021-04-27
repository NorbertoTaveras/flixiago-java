package com.norbertotaveras.flixiago.database.room.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.norbertotaveras.flixiago.database.room.entities.ShowEntity;

import java.util.List;


@Dao
public interface ShowDao {

    // gets all shows
    @Query("SELECT *" +
            " FROM table_shows" +
            " WHERE watched")
    List<ShowEntity> getAllShows();

    // gets all records modified since a specific time stamp
    @Query("SELECT *" +
            " FROM table_shows" +
            " WHERE watched" +
            " AND last_modified > :unixMs")
    List<ShowEntity> recordsModifiedSince(long unixMs);

    // gets a specific show by id
    @Query("SELECT * FROM table_shows" +
            " WHERE id = :id" +
            " AND watched" +
            " LIMIT 1")
    ShowEntity findShowById(long id);

    @Query("UPDATE table_shows" +
            " SET watched=0" +
            " WHERE id = :id")
    void deleteShowById(long id);

    // insert a show entity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(ShowEntity show);

    @Query("SELECT COUNT(*)" +
            " FROM table_shows" +
            " WHERE id = :id" +
            " AND watched")
    int countById(long id);
}
