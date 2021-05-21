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
import com.norbertotaveras.flixiago.models.movie.Movie;
import com.norbertotaveras.flixiago.models.movie.MovieReleaseDate;
import com.norbertotaveras.flixiago.models.movie.MovieReleaseDateCountry;
import com.norbertotaveras.flixiago.models.movie.MovieReleaseDatesResponse;
import com.norbertotaveras.flixiago.services.MovieDBApi;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieReleaseDatesCallback;
import com.norbertotaveras.flixiago.services.movie.OnMovieClickCallback;
import com.norbertotaveras.flixiago.services.movie.OnMovieToggleCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import at.grabner.circleprogress.CircleProgressView;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private final ArrayList<Movie> movies;
    private final HashMap<Long, String> genreLookup;
    private final OnMovieClickCallback onMovieClickCallback;
    private final OnMovieToggleCallback onMovieToggleCallback;

    private final LongSparseArray<Integer> certificationCache;
    private final Map<String, Integer> certificationLookup;
    public MoviesAdapter(ArrayList<Movie> movies,
                         @NonNull HashMap<Long, String> genreLookup,
                         @NonNull LongSparseArray<Integer> certificationCache,
                         @NonNull final Map<String, Integer> certificationLookup,
                         @NonNull OnMovieClickCallback onMovieClickCallback,
                         @NonNull OnMovieToggleCallback onMovieToggleCallback) {
        this.onMovieClickCallback = onMovieClickCallback;
        this.onMovieToggleCallback = onMovieToggleCallback;
        this.certificationCache = certificationCache;
        this.certificationLookup = certificationLookup;
        this.movies = movies;
        this.genreLookup = genreLookup;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.fragment_item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    private interface Completion {
        void complete();
    }

    public void appendMovies(@NonNull final ArrayList<Movie> movies,
                             final long currentGenreId,
                             final int certificationLimit) {

        final LongSparseArray<Integer> lookup = new LongSparseArray<>(movies.size());
        final FormHelpers.AsyncCount state = new FormHelpers.AsyncCount(movies.size());
        final int[] certifications = new int[movies.size()];

        for (int i = 0; i < movies.size(); ++i)
            lookup.put(movies.get(i).getId(), i);

        final MovieDBApi api = MovieDBApi.getInstance();

        final MoviesAdapter self = this;

        final Completion completionHandler = () -> {
            if (++state.completed == state.pending) {
                int index = 0;
                for (Movie movie: movies) {
                    int certification = certifications[index];

                    if ((currentGenreId < 0 || movie.isGenre(currentGenreId)) &&
                            (certification > 0 || certificationLimit ==
                                    (certificationCache.size() - 1)) &&
                            certification <= certificationLimit) {
                        self.movies.add(movie);
                    }
                    ++index;
                }
                notifyDataSetChanged();
            }
        };

        for (Movie movie: movies) {
            final long movieId = movie.getId();

            Integer cachedCertification = certificationCache.get(movieId);

            if (cachedCertification != null) {
                int movieIndex = lookup.get(movieId);
                certifications[movieIndex] = cachedCertification;
                completionHandler.complete();
                continue;
            }

            api.getMovieReleaseDates(movieId, new OnGetMovieReleaseDatesCallback() {
                @Override
                public void onSuccess(MovieReleaseDatesResponse response) {
                    int movieIndex = lookup.get(response.getId());

                    int certificationOrder = -1;
                    certifications[movieIndex] = certificationOrder;

                    for (MovieReleaseDateCountry mrdc : response.getResults()) {
                        if (!mrdc.getCountryCode().equals("US"))
                            continue;

                        for (MovieReleaseDate rd : mrdc.getReleaseDates()) {
                            String certification = rd.getCertification();

                            if (certification == null || certification.length() == 0)
                                continue;

                            certificationOrder = certificationLookup.get(certification);
                            certificationCache.put(movieId, certificationOrder);
                            break;
                        }

                        if (certificationOrder >= 0)
                            break;
                    }

                    certifications[movieIndex] = certificationOrder;

                    completionHandler.complete();
                }

                @Override
                public void onFailure(Throwable error) {

                }
            });
        }
    }

    public void clearMovies() {
        movies.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MovieViewHolder
            extends
            RecyclerView.ViewHolder{

        private final TextView releaseDate;
        private final TextView title;
        private final TextView genres;
        private final TextView certification;
        private final ImageView poster;
        private final ImageView favorite;
        private final CircleProgressView ratingCircle;

        Movie movie;

        public MovieViewHolder(@NonNull View view) {
            super(view);
            releaseDate = view.findViewById(R.id.release_date);
            title = view.findViewById(R.id.title);
            certification = view.findViewById(R.id.certification);
            genres = view.findViewById(R.id.genres);
            poster = view.findViewById(R.id.poster);
            ratingCircle = view.findViewById(R.id.rating_circle);
            favorite = view.findViewById(R.id.favorite);

            itemView.setOnClickListener(v -> onMovieClickCallback.onClick(movie));

            favorite.setOnClickListener(v -> {
                FlixiagoDatabase database = FlixiagoDatabase.getInstance(view.getContext());
                FlixiagoDatabaseHelper.toggleWatch(database, favorite, movie);
            });

        }

        public void bind(Movie movie) {

            this.movie = movie;
            String releaseDateText = movie.formatReleaseDate();

            if (releaseDateText != null)
                releaseDate.setText(releaseDateText);
            else
                releaseDate.setText(R.string.release_date_missing);

            title.setText(movie.getTitle());
            float voteAverage = movie.getVoteAverage();
            //rating.setText(String.format(Locale.US, "%3.1f", voteAverage));

            ratingCircle.setValueAnimated((100.0f / 10.0f) * voteAverage);

            FlixiagoDatabase database = FlixiagoDatabase.getInstance(itemView.getContext());
            FlixiagoDatabaseHelper.setupWatchButton(database, favorite, movie);

            String genreText = movie.commaSeparatedGenres(genreLookup);

            if (genreText != null)
                genres.setText(genreText);
            else
                genres.setText(R.string.genres_unknown);

            String posterPath = movie.getPosterPath();

            int certOrder = certificationCache.get(movie.getId());
            for (Map.Entry<String, Integer> cert: certificationLookup.entrySet()) {
                if (cert.getValue() == certOrder) {
                    certification.setText(cert.getKey());
                    break;
                }
            }

            if (posterPath != null) {
                String urlText = TmdbUrls.IMAGE_BASE_URL_200px + movie.getPosterPath();

                Glide.with(itemView)
                        .load(urlText)
                        .error(R.drawable.ic_round_theaters_24)
                        .into(poster);
            } else {
                Glide.with(itemView)
                        .load(R.drawable.ic_round_theaters_24)
                        .into(poster);
            }
        }

    }
}
