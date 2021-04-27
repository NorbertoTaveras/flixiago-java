package com.norbertotaveras.flixiago.models.base;

import android.content.Context;

import com.norbertotaveras.flixiago.activities.MovieActivity;
import com.norbertotaveras.flixiago.helpers.InternetImage;
import com.norbertotaveras.flixiago.helpers.TmdbUrls;
import com.norbertotaveras.flixiago.models.movie.Movie;
import com.norbertotaveras.flixiago.services.MovieDBApi;
import com.norbertotaveras.flixiago.services.base.OnGetMediaCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieCallback;


import java.util.ArrayList;

public class MediaCredit implements InternetImage, Openable {
    long id;
    String original_language;
    int episode_count;
    String overview;
    ArrayList<Long> genre_ids;
    String name;
    String media_type;
    String poster_path;
    String first_air_date;
    float vote_average;
    int vote_count;
    String character;
    String backdrop_path;
    float popularity;
    String credit_id;
    String release_date;
    String title;

    void getMedia(final OnGetMediaCallback callback) {
        MovieDBApi api = MovieDBApi.getInstance();

        switch (media_type) {
            case "tv":
                /*api.getShow(id, new OnGetShowCallback() {
                    @Override
                    public void onSuccess(Show show) {
                        callback.onSuccess(show);
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        callback.onFailure(error);
                    }
                });*/
                break;

            case "movie":
                api.getMovie(id, new OnGetMovieCallback() {
                    @Override
                    public void onSuccess(Movie movie) {
                        callback.onSuccess(movie);
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        callback.onFailure(error);
                    }
                });
                break;

            default:
                callback.onFailure(new Exception("Unknown media type"));
                return;
        }
    }

    String getTitle() {
        switch (media_type) {
            case "tv":
                return name;

            case "movie":
                return title;

            default:
                return null;
        }
    }

    @Override
    public String getThumbnailUrl() {
        return TmdbUrls.IMAGE_BASE_URL_342px + poster_path;
    }

    @Override
    public String getThumbnailCaption() {
        return getTitle();
    }

    @Override
    public void open(final Context context) {
        MovieDBApi api = MovieDBApi.getInstance();

        switch (media_type) {
            case "tv":
                /*api.getShow(id, new OnGetShowCallback() {
                    @Override
                    public void onSuccess(Show show) {
                        ShowActivity.show(context, show);
                    }

                    @Override
                    public void onFailure(Throwable error) {

                    }
                });*/
            case "movie":
                api.getMovie(id, new OnGetMovieCallback() {
                    @Override
                    public void onSuccess(Movie movie) {
                        MovieActivity.show(context, movie);
                    }

                    @Override
                    public void onFailure(Throwable error) {

                    }
                });

        }
    }
}
