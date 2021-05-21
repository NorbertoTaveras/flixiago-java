package com.norbertotaveras.flixiago.models.movie;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.norbertotaveras.flixiago.activities.MovieActivity;
import com.norbertotaveras.flixiago.database.room.entities.MovieEntity;
import com.norbertotaveras.flixiago.helpers.InternetImage;
import com.norbertotaveras.flixiago.models.base.Media;
import com.norbertotaveras.flixiago.services.MovieDBApi;
import com.norbertotaveras.flixiago.services.base.OnGetMediaCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieCallback;

public class Movie extends Media implements Parcelable, InternetImage {

    private final String release_date;
    private final String title;
    private int runtime;

    protected Movie(Parcel in) {
        super(in);
        release_date = in.readString();
        title = in.readString();
    }

    public Movie(MovieEntity movieRecord) {
        super(movieRecord);
        title = movieRecord.getTitle();
        release_date = movieRecord.getRelease_date();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(release_date);
        dest.writeString(title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    @Override
    public void open(Context context) {
        MovieActivity.show(context, this);
    }

    @Override
    public String getReleaseDate() {
        return release_date;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void reload(OnGetMediaCallback callback) {
        MovieDBApi api = MovieDBApi.getInstance();

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
    }

    public String formatDuration() {
        return runtime + " Minutes";
    }
}
