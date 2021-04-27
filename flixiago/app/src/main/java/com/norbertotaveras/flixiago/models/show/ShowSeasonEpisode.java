package com.norbertotaveras.flixiago.models.show;

public class ShowSeasonEpisode {
    private final long id;
    private final int episode_number;
    private final String air_date;
    private final String name;

    ShowSeasonEpisode(long id, int episode_number,
                      String air_date, String name) {
        this.id = id;
        this.episode_number = episode_number;
        this.air_date = air_date;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public int getEpisodeNumber() {
        return episode_number;
    }

    public String getAirDate() {
        return air_date;
    }

    public String getName() {
        return name;
    }
}
