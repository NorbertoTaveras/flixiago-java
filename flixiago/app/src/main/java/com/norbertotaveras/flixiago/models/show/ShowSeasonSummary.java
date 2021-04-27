package com.norbertotaveras.flixiago.models.show;

import android.os.Parcel;
import android.os.Parcelable;

public class ShowSeasonSummary implements Parcelable {
    private final Long id;
    private final String air_date;
    private final String name;
    private final String overview;
    private final String poster_path;
    private final int season_number;
    private final int episode_count;

    public static final Creator<ShowSeasonSummary> CREATOR
            = new Creator<ShowSeasonSummary>() {
        public ShowSeasonSummary createFromParcel(Parcel in) {
            return new ShowSeasonSummary(in);
        }

        public ShowSeasonSummary[] newArray(int size) {
            return new ShowSeasonSummary[size];
        }
    };

    public ShowSeasonSummary(Long id,
                             String air_date, String name,
                             String overview, String poster_path,
                             int season_number, int episode_count) {
        this.id = id;
        this.air_date = air_date;
        this.name = name;
        this.overview = overview;
        this.poster_path = poster_path;
        this.season_number = season_number;
        this.episode_count = episode_count;
    }

    public ShowSeasonSummary(Parcel in) {
        id = in.readLong();
        air_date = in.readString();
        name = in.readString();
        overview = in.readString();
        poster_path = in.readString();
        season_number = in.readInt();
        episode_count = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(air_date);
        out.writeString(name);
        out.writeString(overview);
        out.writeString(poster_path);
        out.writeInt(season_number);
        out.writeInt(episode_count);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public Long getId() {
        return id;
    }

    public String getAirDate() {
        return air_date;
    }

    public String getName() {
        return name;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public int getSeasonNumber() {
        return season_number;
    }

    public String getFormattedSeason() {
        return "Season " + getSeasonNumber();
    }
    public int getEpisodeCount() {
        return episode_count;
    }
}
