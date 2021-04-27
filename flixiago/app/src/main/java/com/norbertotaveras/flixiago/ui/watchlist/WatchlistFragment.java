package com.norbertotaveras.flixiago.ui.watchlist;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.activities.ShowSeasonActivity;
import com.norbertotaveras.flixiago.adapters.MediaAdapter;
import com.norbertotaveras.flixiago.helpers.FormHelpers;
import com.norbertotaveras.flixiago.models.base.Media;
import com.norbertotaveras.flixiago.models.movie.Movie;
import com.norbertotaveras.flixiago.models.show.Show;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabase;
import com.norbertotaveras.flixiago.database.room.entities.MovieEntity;
import com.norbertotaveras.flixiago.database.room.entities.ShowEntity;
import com.norbertotaveras.flixiago.models.show.ShowSeasonSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WatchlistFragment
        extends
        Fragment
        implements
        AdapterView.OnItemSelectedListener,
        TabLayout.OnTabSelectedListener {

    private static final String TAG = "NotificationsFragment";

    private RecyclerView watchList;
    private MediaAdapter mediaAdapter;

    private TabLayout filter;
    private SpinnerAdapter filterAdapter;

    private FlixiagoDatabase database;
    private Context context;
    private View view;

    private List<MovieEntity> movieRecords;
    private List<ShowEntity> showRecords;

    private ArrayList<Movie> movies;
    private ArrayList<Show> shows;

    WatchlistFragment fragment;

    MediaAdapter.Mode mode = MediaAdapter.Mode.ALL;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_watchlist, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        assert activity != null;
        activity.setTitle("Watch List");
        Log.e(TAG, "Title is : " + activity.getTitle());

        filter = view.findViewById(R.id.tabLayout);
        filter.addOnTabSelectedListener(this);
        filter.post(()-> {
            Objects.requireNonNull(filter.getTabAt(0)).select();});
        setupWatchListAdapter();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden)
            setupWatchListAdapter();
    }

    @Override
    public void onStart() {
        super.onStart();
        //filter.addOnTabSelectedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupWatchListAdapter();
    }

    private void setupWatchListAdapter() {
        context = getContext();

        if (context == null)
            return;

        database = FlixiagoDatabase.getInstance(context);
        movieRecords = database.movieDao().getAllMovies();
        showRecords = database.showDao().getAllShows();

        movies = new ArrayList<>(movieRecords.size());
        shows = new ArrayList<>(showRecords.size());

        for (MovieEntity movieRecord: movieRecords)
            movies.add(new Movie(movieRecord));

        for (ShowEntity showRecord: showRecords)
            shows.add(new Show(showRecord));

        View view = getView();

        if (view == null) {
            Log.e(TAG, "No view!");
            return;
        }

        watchList = view.findViewById(R.id.watch_list);

        GridLayoutManager layoutManager;
        layoutManager = new GridLayoutManager(context, 3);

        mediaAdapter = new MediaAdapter(movies, shows,
                item -> {
                    if (item instanceof Show)
                        ShowSeasonActivity.run(context, (Show)item);
                    else
                        item.open(context);
                });
        watchList.setLayoutManager(layoutManager);
        mediaAdapter.setMode(mode);
        watchList.setAdapter(mediaAdapter);
    }

    private MediaAdapter.Mode getMovies() {
        MediaAdapter.Mode mode = mediaAdapter.setMode(MediaAdapter.Mode.MOVIES_ONLY);
        watchList.setAdapter(mediaAdapter);
        return mode;
    }

    private MediaAdapter.Mode getAll() {
        MediaAdapter.Mode mode = mediaAdapter.setMode(MediaAdapter.Mode.ALL);
        watchList.setAdapter(mediaAdapter);
        return mode;
    }

    private MediaAdapter.Mode getShows() {
        MediaAdapter.Mode mode = mediaAdapter.setMode(MediaAdapter.Mode.SHOWS_ONLY);
        watchList.setAdapter(mediaAdapter);
        return mode;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                mode = getAll();
                break;
            case 1:
                mode = getMovies();
                break;
            case 2:
                mode = getShows();
                break;

            default:
                break;
        }
        setupWatchListAdapter();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        setupWatchListAdapter();
        switch (tab.getPosition()) {
            case 0:
                mode = getAll();
                break;
            case 1:
                mode = getMovies();
                break;
            case 2:
                mode = getShows();
                break;

            default:
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) { }

    @Override
    public void onTabReselected(TabLayout.Tab tab) { }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}