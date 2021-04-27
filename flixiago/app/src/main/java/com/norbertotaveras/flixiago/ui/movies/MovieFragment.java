/*
    Author: Norberto Taveras
    File: MovieFragment.java
    Purpose:
        * Fragment for movie details to be displayed
 */
package com.norbertotaveras.flixiago.ui.movies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import java.util.HashMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.activities.MovieCreditActivity;
import com.norbertotaveras.flixiago.helpers.FormHelpers;
import com.norbertotaveras.flixiago.helpers.TmdbUrls;
import com.norbertotaveras.flixiago.models.base.Dates;
import com.norbertotaveras.flixiago.models.movie.Movie;
import com.norbertotaveras.flixiago.models.movie.MovieCredit;
import com.norbertotaveras.flixiago.models.movie.MovieCreditsResponse;
import com.norbertotaveras.flixiago.models.movie.MovieReview;
import com.norbertotaveras.flixiago.models.movie.MovieTrailer;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabase;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabaseHelper;
import com.norbertotaveras.flixiago.database.room.entities.MovieEntity;
import com.norbertotaveras.flixiago.services.MovieDBApi;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieCreditsCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieGenreLookupCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieReviewsCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieTrailersCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMoviesCallback;


import java.util.ArrayList;

public class MovieFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MovieFragment";
    private static final String VIDEO_BASE_URL = "http://www.youtube.com/watch?v=%s";

    MovieActivityInterface movieActivityInterface;

    // media urls
    private static final String BACKDROP_BASE_URL = TmdbUrls.IMAGE_BASE_URL_780px;
    private static final String POSTER_BASE_URL = TmdbUrls.IMAGE_BASE_URL_342px;

    // movie detail uis
    private ImageView backdrop;
    private ImageView poster;
    private TextView title;
    private TextView genres;
    private TextView overview;
    private TextView releaseDate;
    private RatingBar rating;
    private LinearLayout trailers;
    private LinearLayout reviews;
    private MaterialButton addToWatchList;

    private ArrayList<Long> genreIds;
    final MovieDBApi movieDBApi;
    FlixiagoDatabase database;

    private Activity movieActivity;
    private Movie movie;
    private LinearLayout credits;
    private ImageButton showTimes;
    private LinearLayout similarLayout;

    public interface MovieActivityInterface {
        void movieFragmentFailed(MovieFragment fragment);
        void setActivityTitle();
    }

    public MovieFragment() {
        movieDBApi = MovieDBApi.getInstance();
    }

    @NonNull
    public static MovieFragment newInstance(@NonNull Context context,
                                            @NonNull MovieActivityInterface movieActivityInterface,
                                            @NonNull Movie movie) {
        Bundle args = new Bundle();

        MovieFragment fragment = new MovieFragment();
        fragment.movie = movie;
        fragment.movieActivityInterface = movieActivityInterface;
        fragment.database = FlixiagoDatabase.getInstance(context);
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
        return inflater.inflate(R.layout.fragment_movie_detail, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        movieActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        movieActivity = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        addToWatchList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        Context context = getContext();

        if (context == null) {
            Log.w(TAG, "Context is null!");
            return;
        }

        switch (viewId) {
            case R.id.btn_watch_list:
                FlixiagoDatabaseHelper.toggleWatch(database, addToWatchList, movie);
                break;

            case R.id.show_times:
                FormHelpers.openGoogleSearch(context, movie.getTitle() + " showtimes");
                break;

            default:
                Log.w(TAG, "Unhandled onClick!");
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initMovieDetailUI();
        getMovie();
        getMovieTrailers(movie);
        getCredits(movie);
        getMovieReviews(movie);
        getSimilar(movie);
    }

    private void initMovieDetailUI() {
        View view = getView();

        assert view != null;
        backdrop = view.findViewById(R.id.backdrop);
        poster = view.findViewById(R.id.poster);
        title = view.findViewById(R.id.title);
        genres = view.findViewById(R.id.genres);
        overview = view.findViewById(R.id.movie_overview);
        releaseDate = view.findViewById(R.id.release_date);
        rating = view.findViewById(R.id.rating);
        trailers = view.findViewById(R.id.movie_trailers);
        similarLayout = view.findViewById(R.id.similar_layout);
        reviews = view.findViewById(R.id.movie_reviews);
        credits = view.findViewById(R.id.movie_cast);
        addToWatchList = view.findViewById(R.id.btn_watch_list);
        showTimes = view.findViewById(R.id.show_times);

        showTimes.setOnClickListener(this);

        Activity activity = getActivity();
        assert activity != null;
        activity.setTitle(movie.getTitle());
    }

    @Nullable
    private MovieEntity getMovieFromRoom(long id) {
        Context context = getContext();

        if (context == null)
            return null;

        return FlixiagoDatabase.getInstance(context).movieDao().findMovieById(id);
    }

    private void getMovie() {

        if(movie == null) {
            Log.e(TAG, "Missing movie!");
            return;
        }

        OnGetMovieCallback getMovieCallback = prepareGetMovieCallback();
        movieDBApi.getMovie(movie.getId(), getMovieCallback);
    }

    private OnGetMovieCallback prepareGetMovieCallback() {

        final MovieFragment fragment = this;

        FlixiagoDatabaseHelper.setupWatchButton(database, addToWatchList, movie);

        return new OnGetMovieCallback() {
            @Override
            public void onSuccess(final Movie movie) {

                title.setText(movie.getTitle());
                overview.setText(movie.getOverview());
                releaseDate.setText(movie.formatReleaseDate() + " • " + movie.formatDuration());
                rating.setRating(movie.getVoteAverage() / 2);

                movieDBApi.getMovieGenreList(new OnGetMovieGenreLookupCallback() {
                    @Override
                    public void onSuccess(HashMap<Long, String> movieGenreList) {
                        genres.setText(movie.commaSeparatedGenres(movieGenreList));
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        genres.setText("");
                    }
                });

                String backdropPath = movie.getBackdropPath();
                String posterPath = movie.getPosterPath();

                if (backdropPath != null) {
                    String backdropUrl = BACKDROP_BASE_URL + backdropPath;
                    Glide.with(fragment)
                            .load(backdropUrl)
                            .error(R.drawable.ic_round_theaters_24)
                            .into(backdrop);
                } else {
                    Glide.with(fragment)
                            .load(R.drawable.ic_round_theaters_24)
                            .into(backdrop);
                }

                if (posterPath != null) {
                    String urlText = POSTER_BASE_URL + movie.getPosterPath();

                    Glide.with(fragment)
                            .load(urlText)
                            .error(R.drawable.ic_round_theaters_24)
                            .into(poster);
                } else {
                    Glide.with(fragment)
                            .load(R.drawable.ic_round_theaters_24)
                            .into(poster);
                }

            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "Failed to get movie info from server");
                movieActivityInterface.movieFragmentFailed(fragment);

                FormHelpers.showSnackbar(fragment, R.string.get_movie_error);
            }
        };
    }

    private void getMovieTrailers(Movie movie) {
        OnGetMovieTrailersCallback getMovieTrailersCallback = prepareGetMovieTrailersCallback();
        movieDBApi.getMovieTrailers(movie.getId(), getMovieTrailersCallback);
    }

    private void getMovieReviews(Movie movie) {
        OnGetMovieReviewsCallback getMovieReviewsCallback = prepareGetMovieReviewsCallback();
        movieDBApi.getMovieReviews(movie.getId(), getMovieReviewsCallback);
    }

    private void getCredits(Movie movie) {
        OnGetMovieCreditsCallback getMovieCreditsCallback = prepareGetMovieCreditsCallback();
        movieDBApi.getMovieCredits(movie.getId(), getMovieCreditsCallback);
    }

    private void getSimilar(Movie movie) {
        OnGetMoviesCallback getSimilarMoviesCallback = prepareGetSimilarMovieCallback();
        movieDBApi.getMovieSimilar(movie.getId(), getSimilarMoviesCallback);
    }

    private OnGetMoviesCallback prepareGetSimilarMovieCallback() {
        final Fragment fragment = this;
        final Context context = fragment.getContext();

        final FormHelpers.ThumbnailClickListener clickListener;
        clickListener = new FormHelpers.ThumbnailClickListener<Movie>() {
            @Override
            public void onClick(Movie movie) {
                movie.open(context);
            }
        };

        return new OnGetMoviesCallback() {
            @Override
            public void onSuccess(int page, int totalPages, ArrayList<Movie> movies) {
                FormHelpers.populateScrollingImageList(
                        fragment, similarLayout,
                        movies, false,
                        R.layout.fragment_card_image, true, clickListener);
            }

            @Override
            public void onFailure(Throwable error) {

            }
        };
    }

    private OnGetMovieReviewsCallback prepareGetMovieReviewsCallback() {

        final NestedScrollView nestedScrollView = (NestedScrollView)getView();

        if (nestedScrollView == null)
            return null;

        final View.OnLayoutChangeListener layoutChangeListener = new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(final View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int height = bottom - top;
                final int limitedLines = 9;

                final TextView author = view.findViewById(R.id.movie_review_author);
                final TextView readMore = view.findViewById(R.id.read_more);
                final TextView readLess = view.findViewById(R.id.read_less);
                final Point savedPoint = new Point();

                if (height > 200) {
                    final TextView content = view.findViewById(R.id.movie_review_content);

                    content.setMaxLines(limitedLines);
                    readMore.setVisibility(View.VISIBLE);
                    readLess.setVisibility(View.GONE);

                    readMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            savedPoint.y = nestedScrollView.getScrollY();
                            content.setMaxLines(Integer.MAX_VALUE);
                            readMore.setVisibility(View.GONE);
                            readLess.setVisibility(View.VISIBLE);
                            view.requestLayout();
                        }
                    });

                    readLess.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            content.setMaxLines(limitedLines);
                            readMore.setVisibility(View.VISIBLE);
                            readLess.setVisibility(View.GONE);
                            view.requestLayout();

                            nestedScrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    int top = savedPoint.y;
                                    nestedScrollView.scrollTo(0, top);
                                }
                            });
                        }
                    });
                } else {
                    readMore.setVisibility(View.GONE);
                    readLess.setVisibility(View.GONE);
                }

                view.removeOnLayoutChangeListener(this);
            }
        };

        return new OnGetMovieReviewsCallback() {
            @Override
            public void onSuccess(ArrayList<MovieReview> movieReviews) {

                View view;
                TextView author;
                TextView content;
                TextView readMore;
                TextView readLess;

                ArrayList<View> views = new ArrayList<>(movieReviews.size());

                for (MovieReview movieReview : movieReviews) {
                    view = getLayoutInflater().inflate(R.layout.fragment_review,
                            reviews, false);

                    view.addOnLayoutChangeListener(layoutChangeListener);

                    author = view.findViewById(R.id.movie_review_author);
                    content = view.findViewById(R.id.movie_review_content);

                    author.setText(movieReview.getAuthor());
                    content.setText(movieReview.getContent());

                    views.add(view);
                    reviews.addView(view);

                }

                for (View reviewView: views)
                    reviewView.requestLayout();
            }

            @Override
            public void onFailure(Throwable error) {

            }
        };
    }

    private OnGetMovieCreditsCallback prepareGetMovieCreditsCallback() {
        final Fragment fragment = this;

        return new OnGetMovieCreditsCallback() {
            @Override
            public void onSuccess(MovieCreditsResponse creditsResponse) {
                FormHelpers.populateScrollingImageList(fragment, credits,
                        creditsResponse.getCast(), true,
                        R.layout.fragment_credit_image, true,
                        new FormHelpers.ThumbnailClickListener<MovieCredit>() {
                            @Override
                            public void onClick(MovieCredit item) {
                                Activity activity = getActivity();
                                if (activity == null) {
                                    Log.e(TAG, "No activity!");
                                    return;
                                }

                                MovieCreditActivity.run(activity, item);
                            }
                        });
            }

            @Override
            public void onFailure(Throwable error) {
                FormHelpers.showSnackbar(fragment, R.string.get_credits_error);
            }
        };
    }

    private OnGetMovieTrailersCallback prepareGetMovieTrailersCallback() {

        final MovieFragment fragment = this;

        return new OnGetMovieTrailersCallback() {
            @Override
            public void onSuccess(ArrayList<MovieTrailer> movieTrailers) {
                FormHelpers.populateScrollingImageList(fragment,
                        trailers, movieTrailers, false,
                        R.layout.fragment_image,
                        false, new FormHelpers.ThumbnailClickListener<MovieTrailer>() {
                            @Override
                            public void onClick(MovieTrailer item) {
                                showMovieTrailer(String.format(VIDEO_BASE_URL, item.getKey()));
                            }
                        });
            }

            @Override
            public void onFailure(Throwable error) {
                FormHelpers.showSnackbar(fragment, R.string.get_trailers_error);
            }
        };
    }

    private void showMovieTrailer(String urlText) {
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlText));
        startActivity(trailerIntent);
    }
}