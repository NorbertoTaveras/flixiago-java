package com.norbertotaveras.flixiago.adapters;

import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabase;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabaseHelper;
import com.norbertotaveras.flixiago.helpers.FormHelpers;
import com.norbertotaveras.flixiago.helpers.TmdbUrls;
import com.norbertotaveras.flixiago.models.show.Show;
import com.norbertotaveras.flixiago.models.show.ShowContentRatingCountry;
import com.norbertotaveras.flixiago.models.show.ShowContentRatingsResponse;
import com.norbertotaveras.flixiago.services.MovieDBApi;
import com.norbertotaveras.flixiago.services.shows.OnGetShowContentRatingsCallback;
import com.norbertotaveras.flixiago.services.shows.OnShowClickCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import at.grabner.circleprogress.CircleProgressView;

public class ShowsAdapter extends RecyclerView.Adapter<ShowsAdapter.ShowViewHolder> {

    private final ArrayList<Show> shows;
    private final HashMap<Long, String> genreLookup;
    private final OnShowClickCallback onShowClickCallback;
    private final Map<String, Integer> certificationLookup;
    private final LongSparseArray<Integer> certificationCache;

    public ShowsAdapter(ArrayList<Show> shows,
                        @NonNull HashMap<Long, String> genreLookup,
                        @NonNull LongSparseArray<Integer> certificationCache,
                        @NonNull final Map<String, Integer> certificationLookup,
                        @NonNull OnShowClickCallback onShowClickCallback) {
        this.onShowClickCallback = onShowClickCallback;
        this.certificationCache = certificationCache;
        this.certificationLookup = certificationLookup;
        this.shows = shows;
        this.genreLookup = genreLookup;
    }
    @NonNull
    @Override
    public ShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.fragment_item_show, parent, false);
        return new ShowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowViewHolder holder, int position) {
        holder.bind(shows.get(position));
    }

    private interface Completion {
        void complete();
    }

    // method to append all shows into our own array list of shows
    // notifies the data set change to the adapter & recycler view
    public void appendShows(@NonNull final ArrayList<Show> shows,
                            final long currentGenreId,
                            final int certificationLimit) {

        final LongSparseArray<Integer> lookup = new LongSparseArray<>(shows.size());
        final FormHelpers.AsyncCount state = new FormHelpers.AsyncCount(shows.size());
        final int[] certifications = new int[shows.size()];

        for (int i = 0; i < shows.size(); ++i)
            lookup.put(shows.get(i).getId(), i);

        final MovieDBApi api = MovieDBApi.getInstance();

        final ShowsAdapter self = this;

        final Completion completionHandler = new Completion() {
            @Override
            public void complete() {
                if (++state.completed < state.pending)
                    return;

                int index = 0;
                for (Show show: shows) {
                    int certification = certifications[index];

                    if ((currentGenreId < 0 || show.isGenre(currentGenreId)) &&
                            (certification > 0 ||
                                    certificationLimit == certificationCache.size() - 1) &&
                            certification <= certificationLimit) {
                        self.shows.add(show);
                    }
                    ++index;
                }
                notifyDataSetChanged();
            }
        };

        for (final Show show: shows) {
            final long showId = show.getId();

            Integer cachedCertification = certificationCache.get(showId);

            if (cachedCertification != null) {
                int showIndex = lookup.get(showId);
                certifications[showIndex] = cachedCertification;
                completionHandler.complete();
                continue;
            }

            api.getShowContentRatings(showId, new OnGetShowContentRatingsCallback() {
                @Override
                public void onSuccess(ShowContentRatingsResponse response) {
                    int showIndex = lookup.get(response.getId());

                    int certificationOrder = -1;
                    certifications[showIndex] = certificationOrder;

                    for (ShowContentRatingCountry mrdc : response.getResults()) {
                        if (!mrdc.getCountryCode().equals("US"))
                            continue;

                        String certification = mrdc.getRating();

                        if (certification == null || certification.length() == 0)
                            continue;

                        Integer optionalCertificationOrder = certificationLookup.get(certification);

                        certificationOrder = optionalCertificationOrder != null
                                ? optionalCertificationOrder
                                : -1;
                        certificationCache.put(showId, certificationOrder);

                        if (certificationOrder >= 0)
                            break;
                    }

                    certifications[showIndex] = certificationOrder;

                    completionHandler.complete();
                }

                @Override
                public void onFailure(Throwable error) {

                }
            });
        }
    }

    // method to clear our array list of shows
    // notifies the data set change to the adapter & recycler view
    public void clearShows() {
        shows.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return shows.size();
    }

    public class ShowViewHolder extends RecyclerView.ViewHolder {

        private final TextView releaseDate;
        private final TextView title;
        private final TextView certification;
        private final TextView genres;
        private final ImageView poster;
        private final ImageView favorite;
        private final CircleProgressView ratingCircle;

        Show show;

        public ShowViewHolder(@NonNull View view) {
            super(view);
            releaseDate = view.findViewById(R.id.show_release_date);
            title = view.findViewById(R.id.show_title);
            certification = view.findViewById(R.id.certification);
            genres = view.findViewById(R.id.show_genres);
            poster = view.findViewById(R.id.show_poster);
            ratingCircle = view.findViewById(R.id.rating_circle);
            favorite = view.findViewById(R.id.favorite);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onShowClickCallback.onClick(show);
                }
            });

            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FlixiagoDatabase database = FlixiagoDatabase.getInstance(view.getContext());
                    FlixiagoDatabaseHelper.toggleWatch(database, favorite, show);
                }
            });
        }

        public void bind(Show show) {
             this.show = show;

             String releaseDateText = show.formatReleaseDate();
             if (releaseDateText != null)
                 releaseDate.setText(releaseDateText);
             else
                 releaseDate.setText(R.string.release_date_missing);

             title.setText(show.getTitle());
             float voteAverage = show.getVoteAverage();
           // rating.setText(String.format(Locale.US, "%3.1f", voteAverage));

            ratingCircle.setValueAnimated((100.0f / 10.0f) * voteAverage);

            FlixiagoDatabase database = FlixiagoDatabase.getInstance(itemView.getContext());
            FlixiagoDatabaseHelper.setupWatchButton(database, favorite, show);

            String genreText = show.commaSeparatedGenres(genreLookup);

            if (genreText != null)
                genres.setText(genreText);
            else
                genres.setText(R.string.genres_unknown);

            String posterPath = show.getPosterPath();

            // Reverse calculate certification
            int certOrder = certificationCache.get(show.getId());
            for (Map.Entry<String, Integer> cert: certificationLookup.entrySet()) {
                if (cert.getValue() == certOrder) {
                    certification.setText(cert.getKey());
                    break;
                }
            }

            if (posterPath != null) {
                String urlText = TmdbUrls.IMAGE_BASE_URL_200px + posterPath;

                Glide.with(itemView)
                        .load(urlText)
                        .error(R.drawable.ic_round_tv_24)
                        .into(poster);
            } else {
                Glide.with(itemView)
                        .load(R.drawable.ic_round_tv_24)
                        .into(poster);
            }
        }
    }

}
