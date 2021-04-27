package com.norbertotaveras.flixiago.ui.shows;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.activities.ShowSeasonEpisodesActivity;
import com.norbertotaveras.flixiago.adapters.ShowSeasonsAdapter;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabase;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabaseHelper;
import com.norbertotaveras.flixiago.helpers.FormHelpers;
import com.norbertotaveras.flixiago.models.show.Show;
import com.norbertotaveras.flixiago.models.show.ShowSeasonSummary;
import com.norbertotaveras.flixiago.services.MovieDBApi;
import com.norbertotaveras.flixiago.services.shows.OnGetShowCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowGenreLookupCallback;

import java.util.HashMap;

public class ShowSeasonFragment
        extends Fragment
        implements ShowSeasonsAdapter.OnSeasonSummarySelected, View.OnClickListener {
    private static final String TAG = "ShowSeasonFragment";

    private MovieDBApi server;

    private Show show;

    private RecyclerView seasonListView;
    private ImageView backdropView;
    private ImageView posterView;
    private TextView titleView;
    private TextView releaseDateView;
    private TextView seasonInfo;
    private TextView genresView;
    private RatingBar ratingBarView;
    private MaterialButton addToWatchList;
    private FlixiagoDatabase database;

    public ShowSeasonFragment() {

    }

    public static ShowSeasonFragment newInstance(Show show) {
        Bundle args = new Bundle();

        ShowSeasonFragment fragment = new ShowSeasonFragment();
        fragment.show = show;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_seasons, container, false);
    }

    private final OnGetShowCallback getShowCallback = new OnGetShowCallback() {
        @Override
        public void onSuccess(Show show) {
            processFullShow(show);
        }

        @Override
        public void onFailure(Throwable error) {
            Log.e(TAG, "Failed to get show", error);
        }
    };


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        server = MovieDBApi.getInstance();

        assert view != null;
        addToWatchList = view.findViewById(R.id.btn_watch_list);
        refreshList();
    }

    private void refreshList() {
        server.getShow(show.getId(), getShowCallback);
    }

    @Override
    public void onItemSelected(ShowSeasonSummary seasonSummary) {
        Context context = getContext();
        if (context == null)
            return;
        ShowSeasonEpisodesActivity.run(context, show, seasonSummary);
    }

    @Override
    public void onStart() {
        super.onStart();
        addToWatchList.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    void processFullShow(final Show show) {

        final Context context = getContext();
        final Activity activity = getActivity();
        if (context == null) {
            Log.e(TAG, "no context!");
            return;
        }

        final ShowSeasonFragment fragment = this;

        final View view = getView();

        if (view == null) {
            Log.e(TAG, "No view!");
            return;
        }

        this.show = show;

        ShowSeasonsAdapter adapter = new ShowSeasonsAdapter(context, show.getId(),
                show.getSeasons(), fragment);

        seasonListView = view.findViewById(R.id.season_list);
        backdropView = view.findViewById(R.id.backdrop);
        posterView = view.findViewById(R.id.poster);
        titleView = view.findViewById(R.id.title);
        releaseDateView = view.findViewById(R.id.release_date);
        seasonInfo = view.findViewById(R.id.season_info);
        genresView = view.findViewById(R.id.genres);
        ratingBarView = view.findViewById(R.id.rating);

        database = FlixiagoDatabase.getInstance(context);
        FlixiagoDatabaseHelper.setupWatchButton(database, addToWatchList, show);

        LinearLayoutManager layoutManager = (LinearLayoutManager) seasonListView.getLayoutManager();
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(context);
            seasonListView.setLayoutManager(layoutManager);
        }
        seasonListView.setAdapter(adapter);

        Glide.with(context)
                .load(show.getBackdropUrl())
                .into(backdropView);

        Glide.with(context)
                .load(show.getThumbnailUrl())
                .into(posterView);

        assert activity != null;
        activity.setTitle(show.getTitle());
        titleView.setText(show.getTitle());
        releaseDateView.setText(FormHelpers.formatDate(show.getReleaseDate()));
        seasonInfo.setText(show.getSeasonCount() + " seasons" + " • " + show.getEpisodeCount() + " episodes");

        server.getShowGenreList(new OnGetShowGenreLookupCallback() {
            @Override
            public void onSuccess(HashMap<Long, String> showGenreList) {
                genresView.setText(show.commaSeparatedGenres(showGenreList));
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "Failed to get genre list", error);
                genresView.setText("");
            }
        });

        ratingBarView.setRating(show.getVoteAverage() / 2);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.btn_watch_list) {
            FlixiagoDatabaseHelper.toggleWatch(database, addToWatchList, show);
        }
    }
}
