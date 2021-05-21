package com.norbertotaveras.flixiago.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabase;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabaseHelper;
import com.norbertotaveras.flixiago.helpers.FormHelpers;
import com.norbertotaveras.flixiago.models.base.Media;
import com.norbertotaveras.flixiago.models.movie.Movie;
import com.norbertotaveras.flixiago.models.show.Show;

import java.util.ArrayList;

public class MediaAdapter
        extends RecyclerView.Adapter<MediaAdapter.MediaHolder> {

    private final FormHelpers.ThumbnailClickListener<Media> clickListener;

    // enum to reference the mode of the watch list
    public enum Mode {
        ALL,
        MOVIES_ONLY,
        SHOWS_ONLY
    }

    // containers for movies, shows, all
    private ArrayList<Movie> movieWatchList = new ArrayList<>();
    private final ArrayList<Show> showWatchList = new ArrayList<>();
    private final ArrayList<Media> watchList = new ArrayList<>();

    // media adapter constructor
    // takes in an array list of movies and shows
    // sets the mode to all by default
    public MediaAdapter(ArrayList<Movie> movies, ArrayList<Show> shows,
                        FormHelpers.ThumbnailClickListener<Media> clickListener) {
        this.clickListener = clickListener;
        movieWatchList.addAll(movies);
        showWatchList.addAll(shows);
        setMode(Mode.ALL);
    }

    // method to set the watch list mode
    // takes in a mode type
    public Mode setMode(Mode mode) {

        // clearing the watch list before setting the mode
        watchList.clear();

        // checking if the mode equals all or movies only
        // adds all movies into the watch list
        if (mode == Mode.ALL || mode == Mode.MOVIES_ONLY)
            watchList.addAll(movieWatchList);

        // checking if the mode equals all or shows only
        // adds all shows into the watch list
        if (mode == Mode.ALL || mode == Mode.SHOWS_ONLY)
            watchList.addAll(showWatchList);

        // returns the mode
        return mode;
    }

    public void updateMovies(ArrayList<Movie> movies) {
        this.movieWatchList = movies;
        notifyDataSetChanged();
    }

    public void update() {
        watchList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MediaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.fragment_item_watchlist, parent, false);
        return new MediaHolder(view);
    }

    @Override
    public int getItemCount() {
        return watchList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MediaHolder holder, int position) {
        holder.bind(watchList.get(position));
    }

    public class MediaHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Media media;
        private final ImageView poster;
        private final ImageView favorite;

        public MediaHolder(View view) {
            super(view);
            poster = view.findViewById(R.id.item_watch_list_poster);
            favorite = view.findViewById(R.id.favorite);

            favorite.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void bind(Media media) {
            this.media = media;

            Glide.with(itemView)
                    .load(media.getThumbnailUrl())
                    //.apply(RequestOptions.centerCropTransform())
                    .into(poster);

            FlixiagoDatabase database = FlixiagoDatabase.getInstance(itemView.getContext());
            if (media instanceof Movie)
                FlixiagoDatabaseHelper.setupWatchButton(database, favorite, (Movie)media);
            else if (media instanceof Show)
                FlixiagoDatabaseHelper.setupWatchButton(database, favorite, (Show)media);
        }

        // on click handles opening any type of media object (show or movie)
        // based on the item view's context
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            int id = v.getId();

            int position = getAdapterPosition();

            switch (id) {
                case R.id.favorite:
                    FlixiagoDatabase database = FlixiagoDatabase.getInstance(v.getContext());

                    if (media instanceof Movie)
                        FlixiagoDatabaseHelper.toggleWatch(database, favorite, (Movie)media);
                    else if (media instanceof Show)
                        FlixiagoDatabaseHelper.toggleWatch(database, favorite, (Show)media);

                    watchList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, watchList.size());
                    break;

                default:
                    if (clickListener != null)
                        clickListener.onClick(media);
                    else
                        media.open(itemView.getContext());
                    break;
            }
        }
    }
}
