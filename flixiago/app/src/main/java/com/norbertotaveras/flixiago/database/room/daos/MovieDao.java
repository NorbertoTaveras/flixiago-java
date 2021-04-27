package com.norbertotaveras.flixiago.database.room.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.norbertotaveras.flixiago.database.room.entities.MovieEntity;
import java.util.List;

@Dao
public interface MovieDao {

    // gets all movies
    @Query("SELECT *" +
            " FROM table_movies" +
        " WHERE watched")
    List<MovieEntity> getAllMovies();

    // gets all records modified since a specific time stamp
    @Query("SELECT *" +
            " FROM table_movies" +
            " WHERE last_modified > :unixMs")
    List<MovieEntity> recordsModifiedSince(long unixMs);

    // gets a movie by id
    @Query("SELECT *" +
            " FROM table_movies" +
            " WHERE id = :id" +
            " LIMIT 1")
    MovieEntity findMovieById(long id);

    @Query("UPDATE table_movies" +
            " SET watched=0" +
            ",last_modified=:unixMs" +
            " WHERE id = :id")
    void deleteMovieById(long id, long unixMs);

    // insert a movie entity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(MovieEntity movie);

    // gets the count from the table movies
    // where id's matched
    @Query("SELECT COUNT(*)" +
            " FROM table_movies" +
            " WHERE id = :id" +
            " AND watched")
    int countById(long id);
}
