package com.norbertotaveras.flixiago.models.movie;

import android.os.Parcel;
import android.os.Parcelable;

import com.norbertotaveras.flixiago.helpers.InternetImage;
import com.norbertotaveras.flixiago.helpers.TmdbUrls;

public class MovieCredit implements InternetImage, Parcelable {
    private final long id;
    private final long cast_id;
    private final String credit_id;
    private final String character;
    private final String name;
    private final int order;
    private final String profile_path;

    public static final Creator<MovieCredit> CREATOR
            = new Creator<MovieCredit>() {
        public MovieCredit createFromParcel(Parcel in) {
            return new MovieCredit(in);
        }

        public MovieCredit[] newArray(int size) {
            return new MovieCredit[size];
        }
    };

    public MovieCredit(Parcel in) {
        id = in.readLong();
        cast_id = in.readLong();
        credit_id = in.readString();
        character = in.readString();
        name = in.readString();
        order = in.readInt();
        profile_path = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeLong(cast_id);
        out.writeString(credit_id);
        out.writeString(character);
        out.writeString(name);
        out.writeInt(order);
        out.writeString(profile_path);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public long getId() {
        return id;
    }

    public long getCastId() {
        return cast_id;
    }

    public String getCreditId() {
        return credit_id;
    }

    public String getCharacter() {
        return character;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public String getProfilePath() {
        return profile_path;
    }

    @Override
    public String getThumbnailUrl() {
        return TmdbUrls.IMAGE_BASE_URL_300px + profile_path;
    }

    @Override
    public String getThumbnailCaption() {
        return name;
    }
}
