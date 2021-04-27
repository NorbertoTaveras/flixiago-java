/*
    Author: Norberto Taveras
    File: MovieActivity.java
    Purpose:
        * Inflates the movie detail fragment
        * Passes an entire movie object to be displayed
        * Gets the data of the movie passed in through parcelable
 */
package com.norbertotaveras.flixiago.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.models.movie.Movie;
import com.norbertotaveras.flixiago.ui.movies.MovieFragment;


public class MovieActivity
        extends
        AppCompatActivity
        implements
        MovieFragment.MovieActivityInterface{

    private static final String MOVIE = "movie";
    private static final String TAG = "MovieActivity";

    // method to passed in an entire movie object
    // through an intent as an extra
    // then starting the activity based on the current context
    public static void show(Context context, Movie movie) {
        Intent movieIntent = new Intent(context, MovieActivity.class);
        movieIntent.putExtra(MovieActivity.MOVIE, movie);
        context.startActivity(movieIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        // getting the intent
        Intent intent = getIntent();

        // getting the parcelable extra of the movie passed in
        Movie movie = intent.getParcelableExtra(MOVIE);

        if (movie == null) {
            Log.e(TAG, "Missing movie extra!");
            return;
        }

        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container,
                MovieFragment.newInstance(this,this, movie))
                .commit();
    }

    @Override
    public void movieFragmentFailed(MovieFragment fragment) {
        finish();
    }

    @Override
    public void setActivityTitle() {

    }

}
