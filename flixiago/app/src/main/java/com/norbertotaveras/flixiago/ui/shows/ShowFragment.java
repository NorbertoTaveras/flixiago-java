package com.norbertotaveras.flixiago.ui.shows;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.activities.ShowCreditActivity;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabase;
import com.norbertotaveras.flixiago.database.room.FlixiagoDatabaseHelper;
import com.norbertotaveras.flixiago.database.room.entities.ShowEntity;
import com.norbertotaveras.flixiago.helpers.FormHelpers;
import com.norbertotaveras.flixiago.helpers.TmdbUrls;
import com.norbertotaveras.flixiago.models.show.Show;
import com.norbertotaveras.flixiago.models.show.ShowCreditsResponse;
import com.norbertotaveras.flixiago.models.show.ShowReview;
import com.norbertotaveras.flixiago.models.show.ShowTrailer;
import com.norbertotaveras.flixiago.services.MovieDBApi;
import com.norbertotaveras.flixiago.services.shows.OnGetShowCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowCreditsCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowGenreLookupCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowReviewsCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowTrailersCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowsCallback;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ShowFragment";
    private static final String VIDEO_BASE_URL = "http://www.youtube.com/watch?v=%s";

    ShowActivityInterface showActivityInterface;

    // media urls
    private static final String POSTER_BASE_URL = TmdbUrls.IMAGE_BASE_URL_342px;

    // movie detail uis
    private ImageView backdrop;
    private ImageView poster;
    private TextView title;
    private TextView genres;
    private TextView overview;
    private TextView releaseDate;
    private TextView seasonInfo;
    private RatingBar rating;
    private LinearLayout trailers;
    private LinearLayout reviews;
    private MaterialButton addToWatchList;

    private ArrayList<Long> genreIds;
    final MovieDBApi movieDBApi;

    private Activity showActivity;
    private Show show;
    private LinearLayout credits;
    private FlixiagoDatabase database;
    private LinearLayout similarLayout;

    public interface ShowActivityInterface {
        void showFragmentFailed(ShowFragment fragment);
        void setActivityTitle();
    }

    public ShowFragment() {
        movieDBApi = MovieDBApi.getInstance();
    }

    public static ShowFragment newInstance(@NonNull Context context,
                                           @NonNull ShowFragment.ShowActivityInterface showActivityInterface,
                                           @NonNull Show show) {

        Bundle args = new Bundle();

        ShowFragment fragment = new ShowFragment();
        fragment.show = show;
        fragment.showActivityInterface = showActivityInterface;
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
        return inflater.inflate(R.layout.fragment_show_detail, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        showActivity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        showActivity = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        addToWatchList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.btn_watch_list) {
            FlixiagoDatabaseHelper.toggleWatch(database, addToWatchList, show);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initShowDetailUI();
        getShow();
        getShowTrailers(show);
        getCredits(show);
        getShowReviews(show);
        getSimilar(show);
    }

    private void initShowDetailUI() {
        View view = getView();

        assert view != null;
        backdrop = view.findViewById(R.id.backdrop);
        poster = view.findViewById(R.id.poster);
        title = view.findViewById(R.id.title);
        genres = view.findViewById(R.id.genres);
        overview = view.findViewById(R.id.movie_overview);
        releaseDate = view.findViewById(R.id.release_date);
        seasonInfo = view.findViewById(R.id.season_info);
        rating = view.findViewById(R.id.rating);
        trailers = view.findViewById(R.id.movie_trailers);
        similarLayout = view.findViewById(R.id.similar_layout);
        reviews = view.findViewById(R.id.movie_reviews);
        credits = view.findViewById(R.id.movie_cast);
        addToWatchList = view.findViewById(R.id.btn_watch_list);
    }

    private FlixiagoDatabase flixiagoDatabase() {

        Activity activity = getActivity();
        assert activity != null;
        Context context = activity.getApplicationContext();
        return Room.databaseBuilder(
                context,
                FlixiagoDatabase.class,
                "flxiago.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    private ShowEntity getShowRecord(long id) {
        return flixiagoDatabase().showDao().findShowById(id);
    }

    private void getShow() {

        if(show == null) {
            Log.e(TAG, "Missing show!");
            return;
        }

        OnGetShowCallback getShowCallback = prepareGetShowCallback();
        movieDBApi.getShow(show.getId(), getShowCallback);
    }

    private OnGetShowCallback prepareGetShowCallback() {

        final ShowFragment fragment = this;

        FlixiagoDatabaseHelper.setupWatchButton(database, addToWatchList, show);

        return new OnGetShowCallback() {
            @Override
            public void onSuccess(final Show show) {

                Activity activity = fragment.getActivity();
                if (activity == null)
                    return;
                activity.setTitle(show.getTitle());

                title.setText(show.getTitle());
                overview.setText(show.getOverview());
                releaseDate.setText(show.formatReleaseDate());
                seasonInfo.setText(show.getEpisodeCount() + " episodes" + " • " + show.getSeasonCount() + " seasons");
                rating.setRating(show.getVoteAverage() / 2);

                movieDBApi.getShowGenreList(new OnGetShowGenreLookupCallback() {
                    @Override
                    public void onSuccess(HashMap<Long, String> showGenreList) {
                        genres.setText(show.commaSeparatedGenres(showGenreList));
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        genres.setText("");
                    }
                });

                String backdropUrl = show.getBackdropUrl();
                String posterPath = show.getThumbnailUrl();

                if (backdropUrl != null) {
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
                    String urlText = POSTER_BASE_URL + show.getPosterPath();

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
                showActivityInterface.showFragmentFailed(fragment);

                FormHelpers.showSnackbar(fragment, R.string.get_movie_error);
            }
        };
    }

    private void getShowTrailers(Show show) {
        OnGetShowTrailersCallback getShowTrailersCallback = prepareGetShowTrailersCallback();
        movieDBApi.getShowTrailers(show.getId(), getShowTrailersCallback);
    }

    private void getShowReviews(Show show) {
        OnGetShowReviewsCallback getShowReviewsCallback = prepareGetShowReviewsCallback();
        movieDBApi.getShowReviews(show.getId(), getShowReviewsCallback);
    }

    private void getSimilar(Show show) {
        OnGetShowsCallback getSimilarShowsCallback = prepareGetShowSimilarCallback();
        movieDBApi.getShowSimilar(show.getId(), getSimilarShowsCallback);
    }

    private OnGetShowsCallback prepareGetShowSimilarCallback() {
        final Fragment fragment = this;
        final Context context = fragment.getContext();

        return new OnGetShowsCallback() {
            @Override
            public void onSuccess(int page, int totalPages, ArrayList<Show> shows) {
                final FormHelpers.ThumbnailClickListener clickListener;
                clickListener = (FormHelpers.ThumbnailClickListener<Show>) show -> show.open(context);

                FormHelpers.populateScrollingImageList(fragment, similarLayout, shows,
                        false, R.layout.fragment_card_image, true,
                        clickListener);
            }

            @Override
            public void onFailure(Throwable error) {

            }
        };
    }

    private void getCredits(Show show) {
        OnGetShowCreditsCallback getShowCreditsCallback = prepareGetShowCreditsCallback();
        movieDBApi.getShowCredits(show.getId(), getShowCreditsCallback);
    }

    private OnGetShowReviewsCallback prepareGetShowReviewsCallback() {

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

                    readMore.setOnClickListener(v -> {
                        savedPoint.y = nestedScrollView.getScrollY();
                        content.setMaxLines(Integer.MAX_VALUE);
                        readMore.setVisibility(View.GONE);
                        readLess.setVisibility(View.VISIBLE);
                        view.requestLayout();
                    });

                    readLess.setOnClickListener(v -> {

                        content.setMaxLines(limitedLines);
                        readMore.setVisibility(View.VISIBLE);
                        readLess.setVisibility(View.GONE);
                        view.requestLayout();

                        nestedScrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                int top1 = savedPoint.y;
                                nestedScrollView.scrollTo(0, top1);
                            }
                        });
                    });
                } else {
                    readMore.setVisibility(View.GONE);
                    readLess.setVisibility(View.GONE);
                }

                view.removeOnLayoutChangeListener(this);
            }
        };

        return new OnGetShowReviewsCallback() {
            @Override
            public void onSuccess(ArrayList<ShowReview> showReviews) {

                View view;
                TextView author;
                TextView content;
                TextView readMore;
                TextView readLess;

                ArrayList<View> views = new ArrayList<>(showReviews.size());

                for (ShowReview showReview : showReviews) {
                    view = getLayoutInflater().inflate(R.layout.fragment_review,
                            reviews, false);

                    view.addOnLayoutChangeListener(layoutChangeListener);

                    author = view.findViewById(R.id.movie_review_author);
                    content = view.findViewById(R.id.movie_review_content);

                    author.setText(showReview.getAuthor());
                    content.setText(showReview.getContent());

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

    private OnGetShowCreditsCallback prepareGetShowCreditsCallback() {
        final Fragment fragment = this;

        if (fragment == null) {
            Log.e(TAG, "No fragment!");
            return null;
        }

        return new OnGetShowCreditsCallback() {
            @Override
            public void onSuccess(ShowCreditsResponse creditsResponse) {
                FormHelpers.populateScrollingImageList(fragment, credits,
                        creditsResponse.getCast(), true,
                        R.layout.fragment_credit_image, true,
                        item -> {
                            Activity activity = getActivity();
                            if (activity == null) {
                                Log.e(TAG, "No activity!");
                                return;
                            }

                            ShowCreditActivity.run(activity, item);
                        });
            }

            @Override
            public void onFailure(Throwable error) {
                FormHelpers.showSnackbar(fragment, R.string.get_credits_error);
            }
        };
    }

    private OnGetShowTrailersCallback prepareGetShowTrailersCallback() {

        final ShowFragment fragment = this;

        return new OnGetShowTrailersCallback() {
            @Override
            public void onSuccess(ArrayList<ShowTrailer> showTrailers) {
                FormHelpers.populateScrollingImageList(fragment,
                        trailers, showTrailers, false,
                        R.layout.fragment_image,
                        false, item -> showTVTrailer(String.format(VIDEO_BASE_URL, item.getKey())));
            }

            @Override
            public void onFailure(Throwable error) {
                FormHelpers.showSnackbar(fragment, R.string.get_trailers_error);
            }
        };
    }

    private void showTVTrailer(String urlText) {
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlText));
        startActivity(trailerIntent);
    }
}
