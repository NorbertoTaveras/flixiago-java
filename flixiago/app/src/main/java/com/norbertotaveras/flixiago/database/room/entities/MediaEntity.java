/*
    Author: Norberto Taveras
    File: MediaEntity.java
    Purpose:
        * Base Entity Interface
 */
package com.norbertotaveras.flixiago.database.room.entities;

public interface MediaEntity {
    long getId();
    String getTitle();
    String getOverview();
    String getPoster_path();
    String getBackdrop_path();
    Float getVote_average();
    String getRelease_date();
}
