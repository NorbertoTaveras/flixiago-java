package com.norbertotaveras.flixiago.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabase;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabaseHelper;
import com.norbertotaveras.flixiago.helpers.FormHelpers;
import com.norbertotaveras.flixiago.models.show.Show;
import com.norbertotaveras.flixiago.models.show.ShowSeasonDetail;
import com.norbertotaveras.flixiago.models.show.ShowSeasonEpisode;

import java.util.Locale;

public class ShowSeasonEpisodesAdapter
        extends
        RecyclerView.Adapter<ShowSeasonEpisodesAdapter.ViewHolder> {
    final Show show;
    final ShowSeasonDetail season;
    FlixiagoDatabase database;

    // show season episodes adapter constructor
    // takes in a show and show season detail object
    public ShowSeasonEpisodesAdapter(Show show, ShowSeasonDetail season) {
        this.show = show;
        this.season = season;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        database = FlixiagoDatabase.getInstance(
                parent.getContext());

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.fragment_item_show_episode, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {
        holder.bind(season.getEpisodes().get(position));
    }

    @Override
    public int getItemCount() {
        return season.getEpisodes().size();
    }

    class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        ShowSeasonEpisode episode;
        TextView titleView;
        TextView descriptionView;
        CheckBox watchedView;

        ViewHolder(View view) {
            super(view);
            titleView = view.findViewById(R.id.episode_name);
            descriptionView = view.findViewById(R.id.episode_number);
            watchedView = view.findViewById(R.id.episode_checkbox);
            watchedView.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View v) {

        }

        public void bind(ShowSeasonEpisode episode) {
            this.episode = episode;
            titleView.setText(episode.getName());

            String airDate = episode.getAirDate();
            if (airDate == null)
                return;

            String descriptionText = String.format(Locale.getDefault(),
                    "Episode #%d - %s",
                    episode.getEpisodeNumber(),
                    FormHelpers.formatDate(episode.getAirDate()));
            descriptionView.setText(descriptionText);

            boolean isWatched = FlixiagoDatabaseHelper.isShowEpisodeWatched(
                    database, show.getId(), episode.getId());

            watchedView.setChecked(isWatched);
        }

        // override of on checked change method for each checkbox within each recycler view card item
        // sets the episode as watched within the local and remote database by taking
        // the instance of the database, show id, episode id, season id and is checked boolean value
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            FlixiagoDatabaseHelper.setShowEpisodeWatched(database,
                    show.getId(), episode.getId(), season.getId(), isChecked);
        }
    }
}
