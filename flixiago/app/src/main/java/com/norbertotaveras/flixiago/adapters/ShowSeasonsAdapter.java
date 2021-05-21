/*
    Author: Norberto Taveras
    File: ShowSeasonsAdapter
    Purpose:
        * Adapter for all seasons of a show to be displayed within a recycler view
        * Populates the recycler view of seasons from a show with card items representing a show season
        * Attach a click listen for each season card item
        * Sets a season completed when all episodes have set as watched by the user
 */
package com.norbertotaveras.flixiago.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabase;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabaseHelper;
import com.norbertotaveras.flixiago.helpers.FormHelpers;
import com.norbertotaveras.flixiago.models.show.ShowSeasonSummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ShowSeasonsAdapter
        extends
        RecyclerView.Adapter<ShowSeasonsAdapter.ShowSeasonsViewHolder> {

    public interface OnSeasonSummarySelected {
        void onItemSelected(ShowSeasonSummary seasonSummary);
    }

    private final long showId;
    private final ArrayList<ShowSeasonSummary> showSeasons;
    private final OnSeasonSummarySelected listener;
    private final HashMap<Long, Integer> watchCountCache = new HashMap<>();
    final FlixiagoDatabase database;

    public ShowSeasonsAdapter(Context context,
                              long showId, ArrayList<ShowSeasonSummary> showSeasons,
                              OnSeasonSummarySelected listener) {
        this.showId = showId;
        this.showSeasons = showSeasons;
        this.listener = listener;
        database = FlixiagoDatabase.getInstance(context);
    }

    @NonNull
    @Override
    public ShowSeasonsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.fragment_item_show_season, parent, false);

        return new ShowSeasonsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowSeasonsViewHolder holder,
                                 int position) {
        ShowSeasonSummary season = showSeasons.get(position);
        int watchCount = lookupWatchCount(showId, season.getId());
        holder.bind(season, watchCount);
    }

    @Override
    public int getItemCount() {
        return showSeasons.size();
    }

    int lookupWatchCount(long showId, long seasonId) {
        Integer watchCount = watchCountCache.get(seasonId);
        if (watchCount != null)
            return watchCount;

        watchCount = FlixiagoDatabaseHelper.showSeasonWatchedCount(
                database, showId, seasonId);

        watchCountCache.put(seasonId, watchCount);

        return watchCount;
    }

    public class ShowSeasonsViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ShowSeasonSummary season;

        private final TextView seasonNumber;
        private final TextView seasonAirDate;
        private final View seasonCompleted;
        private final TextView seasonEpisodeCount;

        public ShowSeasonsViewHolder(@NonNull View view) {
            super(view);
            seasonNumber = view.findViewById(R.id.season);
            seasonAirDate = view.findViewById(R.id.release_date);
            seasonEpisodeCount = view.findViewById(R.id.episode_count);
            seasonCompleted = view.findViewById(R.id.completed);

            view.setOnClickListener(this);
        }

        public void bind(ShowSeasonSummary season, int watchCount) {
            this.season = season;
            seasonNumber.setText(season.getFormattedSeason());

            String airDate = season.getAirDate();

             if (airDate == null)
                 return;

            seasonAirDate.setText(FormHelpers.formatDate(season.getAirDate()));
            seasonEpisodeCount.setText(String.format(Locale.getDefault(),
                    "%d/%d", watchCount, season.getEpisodeCount()));

            // sets a season as completed when the watch count has the same value
            // as season episode count
            // makes the view visible or gone depending on the return value
            seasonCompleted.setVisibility(watchCount == season.getEpisodeCount()
                ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            listener.onItemSelected(season);
        }
    }
}
