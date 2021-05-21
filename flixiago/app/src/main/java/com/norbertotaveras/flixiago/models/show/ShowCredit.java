package com.norbertotaveras.flixiago.models.show;

import android.os.Parcel;
import android.os.Parcelable;

import com.norbertotaveras.flixiago.helpers.InternetImage;
import com.norbertotaveras.flixiago.helpers.TmdbUrls;

public class ShowCredit implements InternetImage, Parcelable {
    private final long id;
    private final long cast_id;
    private final String credit_id;
    private final String character;
    private final String name;
    private final int order;
    private final String profile_path;

    public static final Creator<ShowCredit> CREATOR
            = new Creator<ShowCredit>() {
        public com.norbertotaveras.flixiago.models.show.ShowCredit createFromParcel(Parcel in) {
            return new com.norbertotaveras.flixiago.models.show.ShowCredit(in);
        }

        public com.norbertotaveras.flixiago.models.show.ShowCredit[] newArray(int size) {
            return new com.norbertotaveras.flixiago.models.show.ShowCredit[size];
        }
    };

    public ShowCredit(Parcel in) {
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
        return TmdbUrls.IMAGE_BASE_URL_200px + profile_path;
    }

    @Override
    public String getThumbnailCaption() {
        return name;
    }
}
