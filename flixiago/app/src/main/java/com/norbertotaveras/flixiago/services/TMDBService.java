package com.norbertotaveras.flixiago.services;

import com.norbertotaveras.flixiago.models.base.GenreList;
import com.norbertotaveras.flixiago.models.base.PersonCombinedCredits;
import com.norbertotaveras.flixiago.models.base.PersonImagesResponse;
import com.norbertotaveras.flixiago.models.movie.Movie;
import com.norbertotaveras.flixiago.models.movie.MovieCertificationsResponse;
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

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDBService {

    @GET("/3/genre/movie/list")
    Call<GenreList> getGenres(
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("/3/movie/{sortBy}")
    Call<MoviesResponse> getMovies(
            @Path("sortBy") String sortBy,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("/3/movie/latest")
    Call<Movie> getLatestMovie(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("/3/search/movie")
    Call<MoviesResponse> searchMovies (
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page,
            @Query("query") String query,
            @Query("include_adult") boolean adult
    );

    @GET("/3/certification/movie/list")
    Call<MovieCertificationsResponse> getMovieCertifications(
            @Query("api_key") String apiKey);

    @GET("/3/movie/{movieId}/release_dates")
    Call<MovieReleaseDatesResponse> getMovieReleaseDates(
            @Path("movieId") long movieId,
            @Query("api_key") String apiKey);

    @GET("/3/movie/{movie_id}")
    Call<Movie> getMovie(
            @Path("movie_id") long id,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("/3/movie/{movie_id}/videos")
    Call<MovieTrailerResponse> getMovieTrailers(
            @Path("movie_id") long id,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("/3/movie/{movie_id}/credits")
    Call<MovieCreditsResponse> getMovieCredits(
            @Path("movie_id") long id,
            @Query("api_key") String apiKey
    );

    @GET("/3/movie/{movie_id}/reviews")
    Call<MovieReviewResponse> getMovieReviews(
            @Path("movie_id") long id,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("/3/person/{person_id}")
    Call<MoviePerson> getMoviePerson(
            @Path("person_id") long personId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("/3/movie/{movie_id}/similar")
    Call<MoviesResponse> getMovieSimilar(
            @Path("movie_id") long movieId,
            @Query("api_key") String apiKey);

    @GET("/3/person/{person_id}/combined_credits")
    Call<PersonCombinedCredits> getPersonCombinedCredits(
            @Path("person_id") long personId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("/3/person/{person_id}/images")
    Call<PersonImagesResponse> getPersonImages(
            @Path("person_id") long personId,
            @Query("api_key") String apiKey);

    @GET("/3/genre/tv/list")
    Call<ShowGenreList> getShowGenres(
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("/3/tv/{sortBy}")
    Call<ShowsResponse> getShows(
            @Path("sortBy") String sortBy,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("/3/tv/latest")
    Call<Show> getLatestShow(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("/3/search/tv")
    Call<ShowsResponse> searchShows (
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page,
            @Query("query") String query
    );

    @GET("/3/tv/{tv_id}")
    Call<Show> getShow(
            @Path("tv_id") long id,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("/3/tv/{tv_id}/videos")
    Call<ShowTrailerResponse> getShowTrailers(
            @Path("tv_id") long id,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("/3/tv/{tv_id}/credits")
    Call<ShowCreditsResponse> getShowCredits(
            @Path("tv_id") long id,
            @Query("api_key") String apiKey
    );

    @GET("/3/tv/{tv_id}/reviews")
    Call<ShowReviewResponse> getShowReviews(
            @Path("tv_id") long id,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("/3/person/{person_id}")
    Call<ShowPerson> getShowPerson(
            @Path("person_id") long personId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("/3/tv/{showId}/seasons")
    void getShowSeasons(
            @Path("showId") long showId,
            @Query("api_key") String apiKey);

    @GET("/3/tv/{showId}/season/{seasonNumber}")
    Call<ShowSeasonDetail> getShowSeason(
            @Path("showId") long showId,
            @Path("seasonNumber") int seasonNumber,
            @Query("api_key") String apiKey);

    @GET("/3/certification/tv/list")
    Call<ShowCertificationsResponse> getShowCertifications(
            @Query("api_key") String apiKey);

    @GET("/3/tv/{showId}/content_ratings")
    Call<ShowContentRatingsResponse> getShowContentRatings(
            @Path("showId") long showId,
            @Query("api_key") String apiKey);

    @GET("/3/tv/{tv_id}/similar")
    Call<ShowsResponse> getShowSimilar(
            @Path("tv_id") long showId,
            @Query("api_key") String apiKey);
}
