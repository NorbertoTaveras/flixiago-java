package com.norbertotaveras.flixiago.models.show;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.norbertotaveras.flixiago.activities.ShowActivity;
import com.norbertotaveras.flixiago.database.room.entities.MediaEntity;
import com.norbertotaveras.flixiago.helpers.InternetImage;
import com.norbertotaveras.flixiago.models.base.Media;
import com.norbertotaveras.flixiago.services.MovieDBApi;
import com.norbertotaveras.flixiago.services.base.OnGetMediaCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowCallback;

import java.util.ArrayList;

public class Show extends Media implements Parcelable, InternetImage {

    private String name;

    private String first_air_date;
    private int number_of_episodes;
    private int number_of_seasons;

    private ArrayList<ShowSeasonSummary> seasons;

    public static final Creator<Show> CREATOR
            = new Creator<Show>() {
        public Show createFromParcel(Parcel in) {
            return new Show(in);
        }

        public Show[] newArray(int size) {
            return new Show[size];
        }
    };

    public Show(Parcel in) {
        super(in);
        name = in.readString();
        first_air_date = in.readString();
        number_of_episodes = in.readInt();
        number_of_seasons = in.readInt();
        seasons = in.readArrayList(ShowSeasonSummary.class.getClassLoader());
    }

    public Show(MediaEntity showRecord) {
        super(showRecord);
        name = showRecord.getTitle();
        first_air_date = showRecord.getRelease_date();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(name);
        out.writeString(first_air_date);
        out.writeInt(number_of_episodes);
        out.writeInt(number_of_seasons);
        out.writeList(seasons);
    }

    @Override
    public String getReleaseDate() {
        return first_air_date;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public void open(Context context) {
        ShowActivity.show(context, this);
    }

    @Override
    public void reload(final OnGetMediaCallback callback) {
        MovieDBApi api = MovieDBApi.getInstance();

        api.getShow(id, new OnGetShowCallback() {
            @Override
            public void onSuccess(Show show) {
                callback.onSuccess(show);
            }

            @Override
            public void onFailure(Throwable error) {
                callback.onFailure(error);
            }
        });
    }

    public ArrayList<ShowSeasonSummary> getSeasons() {
        return seasons;
    }

    public int getEpisodeCount() {
        return number_of_episodes;
    }

    public int getSeasonCount() {
        return number_of_seasons;
    }
}
