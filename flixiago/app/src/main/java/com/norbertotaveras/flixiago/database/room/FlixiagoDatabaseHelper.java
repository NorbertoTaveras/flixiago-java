/*
    Author: Norberto Taveras
    File: MovieEntiy.java
    Purpose:
        * Assist ensuring that specific records exist within the database
        * Assist on setting up the watch button (removing and adding from/to the watch list)
        * Assist with checking if movies or show/show episodes are watched

 */
package com.norbertotaveras.flixiago.database.room;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.helpers.FormHelpers;
import com.norbertotaveras.flixiago.models.movie.Movie;
import com.norbertotaveras.flixiago.models.show.Show;
import com.norbertotaveras.flixiago.database.room.daos.WatchedEpisodeDao;
import com.norbertotaveras.flixiago.database.room.entities.MovieEntity;
import com.norbertotaveras.flixiago.database.room.entities.ShowEntity;
import com.norbertotaveras.flixiago.database.room.entities.WatchedEpisodeEntity;

import java.util.Date;

public class FlixiagoDatabaseHelper {
    private static final String TAG = "FlixiagoDatabaseHelper";

    private static boolean recordExists(FlixiagoDatabase database, Show show) {
        return database.showDao().countById(show.getId()) > 0;
    }

    private static boolean recordExists(FlixiagoDatabase database, Movie movie) {
        return database.movieDao().countById(movie.getId()) > 0;
    }

    public static void setupWatchButton(FlixiagoDatabase database,
                                        Button watchButton, Movie movie) {
        boolean watchExisted = recordExists(database, movie);
        FormHelpers.toggleSwitchButton(watchButton, watchExisted,
                R.string.remove_watch, R.string.add_watch);
    }

    public static void setupWatchButton(FlixiagoDatabase database,
                                        Button watchButton, Show show) {
        boolean watchExisted = recordExists(database, show);
        FormHelpers.toggleSwitchButton(watchButton, watchExisted,
                R.string.remove_watch, R.string.add_watch);
    }

    public static void setupWatchButton(FlixiagoDatabase database,
                                        ImageButton watchButton,
                                        Show show) {
        boolean watchExisted = recordExists(database, show);
        FormHelpers.toggleFavoriteButton(watchButton, watchExisted);
    }

    public static void setupWatchButton(FlixiagoDatabase database,
                                        ImageButton watchButton,
                                        Movie movie) {
        boolean watchExisted = recordExists(database, movie);
        FormHelpers.toggleFavoriteButton(watchButton, watchExisted);
    }

    public static void setupWatchButton(FlixiagoDatabase database,
                                        ImageView watchView,
                                        Movie movie) {
        boolean watchExisted = recordExists(database, movie);
        FormHelpers.toggleFavoriteView(watchView, watchExisted);
    }

    public static void setupWatchButton(FlixiagoDatabase database,
                                        ImageView watchView,
                                        Show show) {
        boolean watchExisted = recordExists(database, show);
        FormHelpers.toggleFavoriteView(watchView, watchExisted);
    }

    // method to toggle watch button on and off for the show
    public static void toggleWatch(FlixiagoDatabase database,
                                   Button watchButton,
                                   Show show) {
        boolean watchExisted = toggleWatchData(database, show);
        FormHelpers.toggleSwitchButton(watchButton, !watchExisted,
                R.string.remove_watch, R.string.add_watch);
    }

    public static void toggleWatch(FlixiagoDatabase database,
                                   ImageButton watchButton,
                                   Show show) {
        boolean watchExisted = toggleWatchData(database, show);
        FormHelpers.toggleFavoriteButton(watchButton, !watchExisted);
    }

    public static void toggleWatch(FlixiagoDatabase database,
                                   ImageView watchView,
                                   Show show) {
        boolean watchExisted = toggleWatchData(database, show);
        FormHelpers.toggleFavoriteView(watchView, !watchExisted);
    }

    private static boolean toggleWatchData(FlixiagoDatabase database,
                                           Show show) {
        // checks if the show record exist within the database
        boolean watchExisted = recordExists(database, show);

        // gets the current time stamp-time
        long now = new Date().getTime();

        // instantiate a new show entity
        ShowEntity entity = new ShowEntity(show, !watchExisted, now);

        // upsert the entity into the database
        database.showDao().upsert(entity);

        return watchExisted;
    }

    private static boolean toggleWatchData(FlixiagoDatabase database,
                                           Movie movie) {
        // checks if the movie record exist within the dtabase
        boolean watchExisted = recordExists(database, movie);

        // gets the current time stamp-time
        long now = new Date().getTime();

        // instantiates a new movie entity
        MovieEntity entity = new MovieEntity(movie, !watchExisted);

        // upsert the entity into the database
        database.movieDao().upsert(entity);

        return watchExisted;
    }

    // method to toggle the watch button on and of for the movie
    public static void toggleWatch(FlixiagoDatabase database,
                                   Button watchButton,
                                   Movie movie) {
        boolean watchExisted = toggleWatchData(database, movie);
        FormHelpers.toggleSwitchButton(watchButton, !watchExisted,
                R.string.remove_watch, R.string.add_watch);
    }

    // method to toggle the watch button on and of for the movie
    public static void toggleWatch(FlixiagoDatabase database,
                                   ImageButton watchButton,
                                   Movie movie) {
        boolean watchExisted = toggleWatchData(database, movie);
        FormHelpers.toggleFavoriteButton(watchButton, !watchExisted);
    }

    public static void toggleWatch(FlixiagoDatabase database,
                                   ImageView watchView,
                                   Movie movie) {
        boolean watchExisted = toggleWatchData(database, movie);
        FormHelpers.toggleFavoriteView(watchView, !watchExisted);
    }

    // method to determine if a show episode is watched
    public static boolean isShowEpisodeWatched(FlixiagoDatabase database,
                                               long showId, long episodeId) {
        return database.watchedEpisodeDao().countById(showId, episodeId) > 0;
    }

    // method to set a episode as watched
    public static void setShowEpisodeWatched(FlixiagoDatabase database,
                                             long showId, long episodeId,
                                             long seasonId, boolean watched) {

        WatchedEpisodeDao dao = database.watchedEpisodeDao();

        boolean wasWatched;
        wasWatched = isShowEpisodeWatched(database, showId, episodeId);

        if (watched == wasWatched)
            return;

        long timestamp = new Date().getTime();

        WatchedEpisodeEntity record = new WatchedEpisodeEntity(
                showId, episodeId, seasonId, watched, timestamp);

        dao.upsert(record);
    }

    public static int showSeasonWatchedCount(FlixiagoDatabase database,
                                             long showId, long seasonId) {
        WatchedEpisodeDao dao = database.watchedEpisodeDao();
        return dao.countWatchedByShowIdSeasonId(showId, seasonId);
    }
}
