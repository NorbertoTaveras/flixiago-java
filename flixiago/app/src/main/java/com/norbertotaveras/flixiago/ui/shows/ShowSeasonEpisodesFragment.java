/*
    Author: Norberto Taveras
    File: ShowSeasonEpisodesFragment.java
    Purpose:
        * Fragment for all show season episodes to be displayed
 */
package com.norbertotaveras.flixiago.ui.shows;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.adapters.ShowSeasonEpisodesAdapter;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabase;
import com.norbertotaveras.flixiago.helpers.FormHelpers;
import com.norbertotaveras.flixiago.models.show.Show;
import com.norbertotaveras.flixiago.models.show.ShowSeasonDetail;
import com.norbertotaveras.flixiago.models.show.ShowSeasonSummary;
import com.norbertotaveras.flixiago.services.MovieDBApi;
import com.norbertotaveras.flixiago.services.shows.OnGetShowGenreLookupCallback;
import com.norbertotaveras.flixiago.services.shows.OnShowSeasonDetailCallback;

import java.util.HashMap;
import java.util.Locale;

public class ShowSeasonEpisodesFragment extends Fragment {

    private Show show;
    private ShowSeasonSummary seasonSummary;
    private RecyclerView episodesView;

    OnShowSeasonDetailCallback showSeasonDetailCallback;
    private ImageView backdropView;
    private ImageView posterView;
    private TextView seasonNumberView;
    private TextView genresView;
    private TextView releaseDateView;
    private TextView seasonInfo;
    private TextView seasonTitleView;
    private RatingBar seasonRatingView;
    private FlixiagoDatabase database;

    public static ShowSeasonEpisodesFragment newInstance(
            Show show, ShowSeasonSummary seasonSummary) {

        Bundle args = new Bundle();
        ShowSeasonEpisodesFragment fragment = new ShowSeasonEpisodesFragment();
        fragment.show = show;
        fragment.seasonSummary = seasonSummary;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_episodes,
                container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private OnShowSeasonDetailCallback makeShowSeasonDetailCallback(final View view) {

        final Activity activity = getActivity();
        final Context context = getContext();

        assert context != null;

        return new OnShowSeasonDetailCallback() {
            @Override
            public void onSuccess(ShowSeasonDetail seasonDetail) {
                ShowSeasonEpisodesAdapter adapter = new ShowSeasonEpisodesAdapter(
                        show, seasonDetail);

                RecyclerView.LayoutManager layoutManager = episodesView.getLayoutManager();
                if (layoutManager == null) {
                    layoutManager = new LinearLayoutManager(view.getContext());
                    episodesView.setLayoutManager(layoutManager);
                }

                episodesView.setAdapter(adapter);

                assert activity != null;
                activity.setTitle(show.getTitle() + " - " + seasonSummary.getName());
                seasonTitleView.setText(seasonSummary.getName());
                seasonRatingView.setRating(show.getVoteAverage() / 2);
                seasonNumberView.setText(String.format(Locale.getDefault(), "Season %d",
                        seasonSummary.getSeasonNumber()));

                String airDate = seasonSummary.getAirDate();
                if (airDate == null)
                    return;

                releaseDateView.setText(FormHelpers.formatDate(seasonSummary.getAirDate()));
                seasonInfo.setText(show.getSeasonCount() + " seasons" + " • " + show.getEpisodeCount() + " episodes");

                MovieDBApi server = MovieDBApi.getInstance();

                server.getShowGenreList(new OnGetShowGenreLookupCallback() {
                    @Override
                    public void onSuccess(HashMap<Long, String> showGenreList) {
                        genresView.setText(show.commaSeparatedGenres(showGenreList));
                    }

                    @Override
                    public void onFailure(Throwable error) { }
                });

                Glide.with(view)
                        .load(show.getThumbnailUrl())
                        .into(posterView);

                Glide.with(view)
                        .load(show.getBackdropUrl())
                        .into(backdropView);
            }

            @Override
            public void onFailure(Throwable error) { }
        };
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final View view = getView();

        if (view == null)
            return;

        seasonTitleView = view.findViewById(R.id.title);
        seasonRatingView = view.findViewById(R.id.rating);
        backdropView = view.findViewById(R.id.backdrop);
        posterView = view.findViewById(R.id.poster);
        seasonNumberView = view.findViewById(R.id.seasons_header);
        genresView = view.findViewById(R.id.genres);
        releaseDateView = view.findViewById(R.id.release_date);
        seasonInfo = view.findViewById(R.id.season_info);
        episodesView = view.findViewById(R.id.episodes_list);

        showSeasonDetailCallback = makeShowSeasonDetailCallback(view);

        refreshList();
    }

    private void refreshList() {
        MovieDBApi server = MovieDBApi.getInstance();
        server.getShowSeason(show.getId(), seasonSummary.getSeasonNumber(),
                showSeasonDetailCallback);
    }
}
