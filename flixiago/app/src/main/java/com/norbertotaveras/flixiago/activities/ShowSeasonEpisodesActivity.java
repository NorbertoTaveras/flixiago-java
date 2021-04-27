/*
    Author: Norberto Taveras
    File: ShowSeasonEpisodes.java
    Purpose:
        * Inflates the show episodes fragment
        * Passes an entire show credit object to be displayed
        * Passes an entire show season summary object to be displayed
        * Gets the data of the show and show season summary passed in through parcelable
 */
package com.norbertotaveras.flixiago.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.models.show.Show;
import com.norbertotaveras.flixiago.models.show.ShowSeasonSummary;
import com.norbertotaveras.flixiago.ui.shows.ShowSeasonEpisodesFragment;

public class ShowSeasonEpisodesActivity extends AppCompatActivity {

    private static final String ARG_SHOW = "show";
    private static final String ARG_SEASON = "season";

    public static void run(Context context, Show show, ShowSeasonSummary season) {
        Intent intent = new Intent(context, ShowSeasonEpisodesActivity.class);
        intent.putExtra(ARG_SHOW, show);
        intent.putExtra(ARG_SEASON, season);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_season_episodes);

        // gets the intent
        Intent intent = getIntent();

        // gets the show parcelable data
        Show show = intent.getParcelableExtra(ARG_SHOW);

        // gets the show season summary parcelable data
        ShowSeasonSummary season = intent.getParcelableExtra(ARG_SEASON);

        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container,
                ShowSeasonEpisodesFragment.newInstance(show, season))
                .commit();
    }
}
