/*
    Author: Norberto Taveras
    File: MovieCreditActivity.java
    Purpose:
        * Inflates the movie credit fragment
        * Passes an entire movie credit object to be displayed
        * Gets the data of the movie credit passed in through parcelable
 */
package com.norbertotaveras.flixiago.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.models.movie.MovieCredit;
import com.norbertotaveras.flixiago.ui.movies.MovieCreditFragment;


public class MovieCreditActivity extends AppCompatActivity {
    private static final String MOVIE_CREDIT = "movie_credit";

    // method to passed in an entire movie credit object
    // through an intent as an extra
    // then starting the activity based on the current activity
    public static void run(Activity activity, MovieCredit movieCredit) {
        Intent intent = new Intent(activity, MovieCreditActivity.class);
        intent.putExtra(MOVIE_CREDIT, movieCredit);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_credit);

        Intent intent = getIntent();
        MovieCredit movieCredit = intent.getParcelableExtra(MOVIE_CREDIT);

        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container,
                MovieCreditFragment.newInstance(movieCredit))
                .commit();
    }
}
