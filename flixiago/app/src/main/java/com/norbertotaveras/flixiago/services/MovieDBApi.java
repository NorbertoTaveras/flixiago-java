package com.norbertotaveras.flixiago.services;

import com.norbertotaveras.flixiago.models.base.GenreList;
import com.norbertotaveras.flixiago.models.base.PersonCombinedCredits;
import com.norbertotaveras.flixiago.models.base.PersonImagesResponse;
import com.norbertotaveras.flixiago.models.movie.Movie;
import com.norbertotaveras.flixiago.models.movie.MovieCertificationsResponse;
import com.norbertotaveras.flixiago.models.movie.MovieCombinedCredits;
import com.norbertotaveras.flixiago.models.movie.MovieCreditsResponse;
import com.norbertotaveras.flixiago.models.movie.MoviePerson;
import com.norbertotaveras.flixiago.models.movie.MovieReleaseDatesResponse;
import com.norbertotaveras.flixiago.models.movie.MovieReviewResponse;
import com.norbertotaveras.flixiago.models.movie.MovieTrailerResponse;
import com.norbertotaveras.flixiago.models.movie.MoviesResponse;
import com.norbertotaveras.flixiago.models.show.Show;
import com.norbertotaveras.flixiago.models.show.ShowCertificationsResponse;
import com.norbertotaveras.flixiago.models.show.ShowContentRatingsResponse;
import com.norbertotaveras.flixiago.models.show.ShowCreditsResponse;
import com.norbertotaveras.flixiago.models.show.ShowGenreList;
import com.norbertotaveras.flixiago.models.show.ShowPerson;
import com.norbertotaveras.flixiago.models.show.ShowReviewResponse;
import com.norbertotaveras.flixiago.models.show.ShowSeasonDetail;
import com.norbertotaveras.flixiago.models.show.ShowTrailerResponse;
import com.norbertotaveras.flixiago.models.show.ShowsResponse;
import com.norbertotaveras.flixiago.services.base.OnGetPersonCombinedCreditsCallback;
import com.norbertotaveras.flixiago.services.base.OnGetPersonImagesCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetCombinedMovieCreditsCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieCertificationsCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieCreditsCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieGenreLookupCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMoviePersonCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieReleaseDatesCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieReviewsCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMovieTrailersCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMoviesCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowCertificationsCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowContentRatingsCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowCreditsCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowGenreLookupCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowPersonCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowReviewsCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowTrailersCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowsCallback;
import com.norbertotaveras.flixiago.services.shows.OnShowSeasonDetailCallback;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDBApi {

    private static final String BASE_URL = "https://api.themoviedb.org/";
    private static final String LANGUAGE = "en-US";
    private static final String API_KEY = "440f271c026c7acdb889c9c109f49daa";

    public static final String POPULAR = "popular";

    public static final String TOP_RATED = "top_rated";
    public static final String SHOW_TOP_RATED = "top_rated";

    public static final String LATEST = "latest";

    public static final String UPCOMING = "upcoming";
    public static final String NOW_PLAYING = "now_playing";

    public static final String AIRING_TODAY = "airing_today";
    public static final String ON_THE_AIR = "on_the_air";

    private static MovieDBApi instance;
    private TMDBService service;

    private HashMap<Long, String> cachedMovieGenreLookup;
    private HashMap<Long, String> cachedShowGenreLookup;

    private MovieDBApi(TMDBService service) {
        this.service = service;
    }

    public static MovieDBApi getInstance() {
        if (instance != null)
            return instance;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        instance = new MovieDBApi(retrofit.create(TMDBService.class));

        return instance;
    }

    /* Movies */
    public void getMovies(int page, String sortBy, final OnGetMoviesCallback callback) {
        switch (sortBy) {
            default:
                service.getMovies(sortBy, API_KEY, LANGUAGE, page)
                        .enqueue(prepareMoviesResponseCallback(callback));
                break;

            case MovieDBApi.LATEST:
                service.getLatestMovie(API_KEY, LANGUAGE, page)
                        .enqueue(prepareLatestMoviesResponseCallback(callback));
                break;

        }
    }

    public void getMovieReleaseDates(long movieId, final OnGetMovieReleaseDatesCallback callback) {
        service.getMovieReleaseDates(movieId, API_KEY)
                .enqueue(prepareMovieReleaseDatesResponseCallback(callback));
    }

    public void searchMovies(int page, String query, final OnGetMoviesCallback callback) {
        service.searchMovies(API_KEY, LANGUAGE, page, query, false)
                .enqueue(prepareMoviesResponseCallback(callback));
    }

    public void getMovieCertifications(OnGetMovieCertificationsCallback callback) {
        service.getMovieCertifications(API_KEY)
                .enqueue(prepareMovieCertificationsCallback(callback));
    }

    public void getMovieGenreList(final OnGetMovieGenreLookupCallback callback) {

        if (cachedMovieGenreLookup != null) {
            callback.onSuccess(cachedMovieGenreLookup);
            return;
        }

        service.getGenres(API_KEY, LANGUAGE)
                .enqueue(new Callback<GenreList>() {
                    @Override
                    public void onResponse(Call<GenreList> call,
                                           Response<GenreList> response) {
                        if (response.isSuccessful()) {
                            GenreList movieGenreList = response.body();
                            if (movieGenreList != null)
                                cachedMovieGenreLookup = movieGenreList.genreNameLookupTable();
                            callback.onSuccess(cachedMovieGenreLookup);
                        } else {
                            callback.onFailure(new Throwable(response.message()));
                        }
                    }

                    @Override
                    public void onFailure(Call<GenreList> call,
                                          Throwable error) {
                        callback.onFailure(error);
                    }
                });
    }

    public void getMovie(long id, final OnGetMovieCallback callback) {
        service.getMovie(id, API_KEY, LANGUAGE)
                .enqueue(prepareMovieResponseCallback(callback));
    }

    public void getMovieTrailers(long id, final OnGetMovieTrailersCallback callback) {
        service.getMovieTrailers(id, API_KEY, LANGUAGE)
                .enqueue(prepareMovieTrailersResponseCallback(callback));
    }

    public void getMovieReviews(long id, final OnGetMovieReviewsCallback callback) {
        service.getMovieReviews(id, API_KEY, LANGUAGE)
                .enqueue(prepareMovieReviewsResponseCallback(callback));
    }

    public void getMoviePerson(long personId, final OnGetMoviePersonCallback callback) {
        service.getMoviePerson(personId, API_KEY, LANGUAGE)
                .enqueue(prepareMoviePersonResponseCallback(callback));
    }

    public void getMovieSimilar(long movieId, OnGetMoviesCallback callback) {
        service.getMovieSimilar(movieId, API_KEY)
                .enqueue(prepareMoviesResponseCallback(callback));
    }

    public void getPersonImages(long personId, OnGetPersonImagesCallback callback) {
        service.getPersonImages(personId, API_KEY)
                .enqueue(prepareGetPersonImagesCallback(callback));
    }

    public void getPersonCombinedCredits(long personId,
                                         OnGetPersonCombinedCreditsCallback callback) {
        service.getPersonCombinedCredits(personId, API_KEY, LANGUAGE)
                .enqueue(prepareShowCombinedCreditsCallback(callback));
    }

    private Callback<MoviesResponse> prepareMoviesResponseCallback(
            final OnGetMoviesCallback callback) {
        return new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call,
                                   Response<MoviesResponse> response) {
                if (response.isSuccessful()) {
                    MoviesResponse moviesResponse = response.body();
                    assert moviesResponse != null;
                    callback.onSuccess(moviesResponse.getPage(),
                            moviesResponse.getTotalPages(),
                            moviesResponse.getMovies());
                } else {
                    callback.onFailure(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<Movie> prepareLatestMoviesResponseCallback(
            final OnGetMoviesCallback callback) {
        return new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call,
                                   Response<Movie> response) {
                if (response.isSuccessful()) {
                    ArrayList<Movie> movies = new ArrayList<>(1);
                    movies.add(response.body());
                   // callback.onSuccess(1, 1, movies);
                } else {
                    callback.onFailure(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<MovieReleaseDatesResponse> prepareMovieReleaseDatesResponseCallback(
            final OnGetMovieReleaseDatesCallback callback)
    {
        return new Callback<MovieReleaseDatesResponse>() {
            @Override
            public void onResponse(Call<MovieReleaseDatesResponse> call,
                                   Response<MovieReleaseDatesResponse> response) {
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<MovieReleaseDatesResponse> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<MovieCertificationsResponse> prepareMovieCertificationsCallback(
            final OnGetMovieCertificationsCallback callback) {
        return new Callback<MovieCertificationsResponse>() {
            @Override
            public void onResponse(Call<MovieCertificationsResponse> call,
                                   Response<MovieCertificationsResponse> response) {
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<MovieCertificationsResponse> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<PersonImagesResponse> prepareGetPersonImagesCallback(
            final OnGetPersonImagesCallback callback) {
        return new Callback<PersonImagesResponse>() {
            @Override
            public void onResponse(Call<PersonImagesResponse> call,
                                   Response<PersonImagesResponse> response) {
                if (response.isSuccessful()) {
                    PersonImagesResponse images = response.body();
                    callback.onSuccess(images.getId(), images.getProfiles());
                }
            }

            @Override
            public void onFailure(Call<PersonImagesResponse> call,
                                  Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<MoviePerson> prepareMoviePersonResponseCallback(
            final OnGetMoviePersonCallback callback) {
        return new Callback<MoviePerson>() {
            @Override
            public void onResponse(Call<MoviePerson> call,
                                   Response<MoviePerson> response) {
                if (response.isSuccessful()) {
                    MoviePerson moviePerson = response.body();
                    assert moviePerson != null;
                    callback.onSuccess(moviePerson);
                } else {
                    callback.onFailure(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<MoviePerson> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<MovieCombinedCredits> prepareMovieCombinedCreditsCallback(
            final OnGetCombinedMovieCreditsCallback callback) {
        return new Callback<MovieCombinedCredits>() {
            @Override
            public void onResponse(Call<MovieCombinedCredits> call,
                                   Response<MovieCombinedCredits> response) {
                if (response.isSuccessful()) {
                    MovieCombinedCredits combinedCredits = response.body();
                    assert combinedCredits != null;
                    callback.onSuccess(combinedCredits);
                } else {
                    callback.onFailure(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<MovieCombinedCredits> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<MovieReviewResponse> prepareMovieReviewsResponseCallback(
            final OnGetMovieReviewsCallback callback) {
        return new Callback<MovieReviewResponse>() {
            @Override
            public void onResponse(Call<MovieReviewResponse> call, Response<MovieReviewResponse> response) {
                MovieReviewResponse movieReviewResponse = response.body();
                assert movieReviewResponse != null;
                callback.onSuccess(movieReviewResponse.getResults());
            }

            @Override
            public void onFailure(Call<MovieReviewResponse> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<MovieTrailerResponse> prepareMovieTrailersResponseCallback(
            final OnGetMovieTrailersCallback callback) {
        return new Callback<MovieTrailerResponse>() {
            @Override
            public void onResponse(Call<MovieTrailerResponse> call,
                                   Response<MovieTrailerResponse> response) {
                if (response.isSuccessful()) {
                    MovieTrailerResponse movieTrailerResponse = response.body();
                    assert movieTrailerResponse != null;
                    callback.onSuccess(movieTrailerResponse.geTrailers());
                }
            }

            @Override
            public void onFailure(Call<MovieTrailerResponse> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    public void getMovieCredits(long movieId, final OnGetMovieCreditsCallback callback) {
        service.getMovieCredits(movieId, API_KEY)
                .enqueue(new Callback<MovieCreditsResponse>() {
                    @Override
                    public void onResponse(Call<MovieCreditsResponse> call,
                                           Response<MovieCreditsResponse> response) {
                        if (response.isSuccessful()) {
                            MovieCreditsResponse movieCreditsResponse = response.body();
                            callback.onSuccess(movieCreditsResponse);
                        } else {
                            callback.onFailure(new Throwable(response.message()));
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieCreditsResponse> call, Throwable error) {
                        callback.onFailure(error);
                    }
                });
    }

    private Callback<Movie> prepareMovieResponseCallback(final OnGetMovieCallback callback) {
        return new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful()) {
                    Movie movie = response.body();
                    callback.onSuccess(movie);
                } else {
                    callback.onFailure(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    /* TV Shows */
    public void getShows(int page, String sortBy, final OnGetShowsCallback callback) {

        switch (sortBy) {
            default:
                service.getShows(sortBy, API_KEY, LANGUAGE, page)
                        .enqueue(prepareShowsResponseCallback(callback));
                break;
            case MovieDBApi.LATEST:
                service.getLatestShow(API_KEY, LANGUAGE, page)
                        .enqueue(prepareLatestShowResponseCallback(callback));
                break;
        }
    }

    public void getShow(long id, final OnGetShowCallback callback) {
        service.getShow(id, API_KEY, LANGUAGE)
                .enqueue(prepareShowResponseCallback(callback));
    }

    public void getShowTrailers(long id, final OnGetShowTrailersCallback callback) {
        service.getShowTrailers(id, API_KEY, LANGUAGE)
                .enqueue(prepareShowTrailersResponseCallback(callback));
    }

    public void getShowReviews(long id, final OnGetShowReviewsCallback callback) {
        service.getShowReviews(id, API_KEY, LANGUAGE)
                .enqueue(prepareShowReviewsResponseCallback(callback));
    }

    public void searchShows(int page, String query, final OnGetShowsCallback callback) {
        service.searchShows(API_KEY, LANGUAGE, page, query)
                .enqueue(prepareShowsResponseCallback(callback));
    }

    public void getShowPerson(long personId, final OnGetShowPersonCallback callback) {
        service.getShowPerson(personId, API_KEY, LANGUAGE)
                .enqueue(prepareShowPersonResponseCallback(callback));
    }

    public void getShowSimilar(long showId, OnGetShowsCallback callback) {
        service.getShowSimilar(showId, API_KEY)
                .enqueue(prepareShowsResponseCallback(callback));
    }

    public void getShowSeason(long showId, int seasonNumber,
                              OnShowSeasonDetailCallback callback) {
        service.getShowSeason(showId, seasonNumber, API_KEY)
                .enqueue(prepareGetShowSeasonCallback(callback));
    }

    public void getShowCertifications(OnGetShowCertificationsCallback callback) {
        service.getShowCertifications(API_KEY)
                .enqueue(prepareShowCertificationsCallback(callback));
    }

    public void getShowContentRatings(long showId,
                                      final OnGetShowContentRatingsCallback callback) {
        service.getShowContentRatings(showId, API_KEY)
                .enqueue(prepareShowContentRatingsResponseCallback(callback));
    }

    private Callback<ShowContentRatingsResponse> prepareShowContentRatingsResponseCallback(
            final OnGetShowContentRatingsCallback callback)
    {
        return new Callback<ShowContentRatingsResponse>() {
            @Override
            public void onResponse(Call<ShowContentRatingsResponse> call,
                                   Response<ShowContentRatingsResponse> response) {
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<ShowContentRatingsResponse> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<Show> prepareLatestShowResponseCallback(
            final OnGetShowsCallback callback) {
        return new Callback<Show>() {
            @Override
            public void onResponse(Call<Show> call,
                                   Response<Show> response) {
                if (response.isSuccessful()) {
                    ArrayList<Show> shows = new ArrayList<>(1);
                    shows.add(response.body());
                    callback.onSuccess(1, 1, shows);
                } else {
                    callback.onFailure(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<Show> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<ShowsResponse> prepareShowsResponseCallback
            (final OnGetShowsCallback callback) {
        return new Callback<ShowsResponse>() {
            @Override
            public void onResponse(Call<ShowsResponse> call,
                                   Response<ShowsResponse> response) {
                if (response.isSuccessful()) {
                    ShowsResponse showsResponse = response.body();
                    assert showsResponse != null;
                    callback.onSuccess(showsResponse.getPage(),
                            showsResponse.getTotalPages(),
                            showsResponse.getShows());
                } else {
                    callback.onFailure(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<ShowsResponse> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<ShowPerson> prepareShowPersonResponseCallback(
            final OnGetShowPersonCallback callback) {
        return new Callback<ShowPerson>() {
            @Override
            public void onResponse(Call<ShowPerson> call,
                                   Response<ShowPerson> response) {
                if (response.isSuccessful()) {
                    ShowPerson showPerson = response.body();
                    assert showPerson != null;
                    callback.onSuccess(showPerson);
                } else {
                    callback.onFailure(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<ShowPerson> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<PersonCombinedCredits> prepareShowCombinedCreditsCallback(
            final OnGetPersonCombinedCreditsCallback callback) {
        return new Callback<PersonCombinedCredits>() {
            @Override
            public void onResponse(Call<PersonCombinedCredits> call,
                                   Response<PersonCombinedCredits> response) {
                if (response.isSuccessful()) {
                    PersonCombinedCredits combinedCredits = response.body();
                    assert combinedCredits != null;
                    callback.onSuccess(combinedCredits);
                } else {
                    callback.onFailure(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<PersonCombinedCredits> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<ShowSeasonDetail> prepareGetShowSeasonCallback(
            final OnShowSeasonDetailCallback callback) {
        return new Callback<ShowSeasonDetail>() {
            @Override
            public void onResponse(Call<ShowSeasonDetail> call,
                                   Response<ShowSeasonDetail> response) {
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<ShowSeasonDetail> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<ShowCertificationsResponse> prepareShowCertificationsCallback(
            final OnGetShowCertificationsCallback callback) {
        return new Callback<ShowCertificationsResponse>() {
            @Override
            public void onResponse(Call<ShowCertificationsResponse> call,
                                   Response<ShowCertificationsResponse> response) {
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<ShowCertificationsResponse> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<ShowReviewResponse> prepareShowReviewsResponseCallback(
            final OnGetShowReviewsCallback callback) {
        return new Callback<ShowReviewResponse>() {
            @Override
            public void onResponse(Call<ShowReviewResponse> call, Response<ShowReviewResponse> response) {
                ShowReviewResponse showReviewResponse = response.body();
                assert showReviewResponse != null;
                callback.onSuccess(showReviewResponse.getResults());
            }

            @Override
            public void onFailure(Call<ShowReviewResponse> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<ShowTrailerResponse> prepareShowTrailersResponseCallback(
            final OnGetShowTrailersCallback callback) {
        return new Callback<ShowTrailerResponse>() {
            @Override
            public void onResponse(Call<ShowTrailerResponse> call,
                                   Response<ShowTrailerResponse> response) {
                if (response.isSuccessful()) {
                    ShowTrailerResponse showTrailerResponse = response.body();
                    assert showTrailerResponse != null;
                    callback.onSuccess(showTrailerResponse.geTrailers());
                }
            }

            @Override
            public void onFailure(Call<ShowTrailerResponse> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    private Callback<Show> prepareShowResponseCallback(final OnGetShowCallback callback) {
        return new Callback<Show>() {
            @Override
            public void onResponse(Call<Show> call, Response<Show> response) {
                if (response.isSuccessful()) {
                    Show show = response.body();
                    callback.onSuccess(show);
                } else {
                    callback.onFailure(new Throwable(response.message()));
                }
            }

            @Override
            public void onFailure(Call<Show> call, Throwable error) {
                callback.onFailure(error);
            }
        };
    }

    public void getShowGenreList(final OnGetShowGenreLookupCallback callback) {

        if (cachedShowGenreLookup != null) {
            callback.onSuccess(cachedShowGenreLookup);
            return;
        }

        service.getShowGenres(API_KEY, LANGUAGE)
                .enqueue(new Callback<ShowGenreList>() {
                    @Override
                    public void onResponse(Call<ShowGenreList> call,
                                           Response<ShowGenreList> response) {
                        if (response.isSuccessful()) {
                            ShowGenreList showGenreList = response.body();
                            if (showGenreList != null)
                                cachedShowGenreLookup = showGenreList.genreNameLookupTable();
                            callback.onSuccess(cachedShowGenreLookup);
                        } else {
                            callback.onFailure(new Throwable(response.message()));
                        }
                    }

                    @Override
                    public void onFailure(Call<ShowGenreList> call,
                                          Throwable error) {
                        callback.onFailure(error);
                    }
                });
    }

    public void getShowCredits(long showId, final OnGetShowCreditsCallback callback) {
        service.getShowCredits(showId, API_KEY)
                .enqueue(new Callback<ShowCreditsResponse>() {
                    @Override
                    public void onResponse(Call<ShowCreditsResponse> call,
                                           Response<ShowCreditsResponse> response) {
                        if (response.isSuccessful()) {
                            ShowCreditsResponse showCreditsResponse = response.body();
                            callback.onSuccess(showCreditsResponse);
                        } else {
                            callback.onFailure(new Throwable(response.message()));
                        }
                    }

                    @Override
                    public void onFailure(Call<ShowCreditsResponse> call, Throwable error) {
                        callback.onFailure(error);
                    }
                });
    }
}
