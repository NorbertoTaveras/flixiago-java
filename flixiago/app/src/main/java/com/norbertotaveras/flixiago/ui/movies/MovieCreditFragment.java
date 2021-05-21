/*
    Author: Norberto Taveras
    File: MovieCreditFragment.java
    Purpose:
        * Fragment for movie credits to be displayed
 */
package com.norbertotaveras.flixiago.ui.movies;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.helpers.FormHelpers;
import com.norbertotaveras.flixiago.models.base.MediaCredit;
import com.norbertotaveras.flixiago.models.base.PersonCombinedCredits;
import com.norbertotaveras.flixiago.models.base.PersonImage;
import com.norbertotaveras.flixiago.models.movie.MovieCredit;
import com.norbertotaveras.flixiago.models.movie.MoviePerson;
import com.norbertotaveras.flixiago.services.MovieDBApi;
import com.norbertotaveras.flixiago.services.base.OnGetPersonCombinedCreditsCallback;
import com.norbertotaveras.flixiago.services.base.OnGetPersonImagesCallback;
import com.norbertotaveras.flixiago.services.movie.OnGetMoviePersonCallback;

import java.util.ArrayList;

public class MovieCreditFragment extends Fragment {

    private static final String TAG = "MovieCreditFragment";

    private MovieCredit movieCredit;
    private ImageView photoView;
    private TextView nameView;
    private TextView bioView;
    private LinearLayout knownForLayout;
    private LinearLayout picturesLayout;

    private MovieDBApi movieDBApi;
    private TextView knownForText;
    private TextView birthdate;
    private TextView birthplace;

    public static MovieCreditFragment newInstance(MovieCredit movieCredit) {

        Bundle args = new Bundle();

        MovieCreditFragment fragment = new MovieCreditFragment();
        fragment.movieCredit = movieCredit;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_credit, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        movieDBApi = MovieDBApi.getInstance();

        View view = getView();
        Activity activity = getActivity();

        if (view == null) {
            Log.e(TAG, "No view!");
            return;
        }

        photoView = view.findViewById(R.id.photo);
        nameView = view.findViewById(R.id.display_name);
        bioView = view.findViewById(R.id.bio);
        knownForText = view.findViewById(R.id.known_for);
        birthdate = view.findViewById(R.id.birthdate);
        birthplace = view.findViewById(R.id.birthplace);
        knownForLayout = view.findViewById(R.id.known_for_layout);
        picturesLayout = view.findViewById(R.id.pictures_layout);

        Glide.with(view)
                .load(movieCredit.getThumbnailUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(photoView);

        assert activity != null;

        activity.setTitle(movieCredit.getName());
        nameView.setText(movieCredit.getName());

        movieDBApi = MovieDBApi.getInstance();

        OnGetMoviePersonCallback personCallback = prepareGetMoviePersonCallback();
        movieDBApi.getMoviePerson(movieCredit.getId(), personCallback);

        OnGetPersonCombinedCreditsCallback combinedCreditsCallback =
                prepareGetCombinedCreditsCallback();
        movieDBApi.getPersonCombinedCredits(movieCredit.getId(), combinedCreditsCallback);

        OnGetPersonImagesCallback personImagesCallback =
                prepareGetPersonImagesCallback();
        movieDBApi.getPersonImages(movieCredit.getId(), personImagesCallback);

        super.onActivityCreated(savedInstanceState);
    }

    private OnGetMoviePersonCallback prepareGetMoviePersonCallback() {
        final Fragment fragment = this;

        return new OnGetMoviePersonCallback() {
            @Override
            public void onSuccess(MoviePerson person) {
                bioView.setText(person.getBiography());
                knownForText.setText(FormHelpers.replaceEmpty(person.getKnownFor(),
                        "<unknown>"));
                birthdate.setText(FormHelpers.replaceEmpty(person.formatBirthdate(),
                        "<unknown>"));
                birthplace.setText(FormHelpers.replaceEmpty(person.getBirthplace(),
                        "<unknown>"));
            }

            @Override
            public void onFailure(Throwable error) {
                FormHelpers.showSnackbar(fragment, R.string.get_person_error);
            }
        };
    }

    private OnGetPersonCombinedCreditsCallback prepareGetCombinedCreditsCallback() {
        final Fragment fragment = this;
        final Context context = fragment.getContext();

        return new OnGetPersonCombinedCreditsCallback() {
            @Override
            public void onSuccess(PersonCombinedCredits combinedCredits) {
                FormHelpers.ThumbnailClickListener clickListener =
                        (FormHelpers.ThumbnailClickListener<MediaCredit>) role -> role.open(context);

                FormHelpers.populateScrollingImageList(fragment, knownForLayout,
                        combinedCredits.getCast(), false,
                        R.layout.fragment_card_image, true, clickListener);
            }

            @Override
            public void onFailure(Throwable error) {
                FormHelpers.showSnackbar(fragment, R.string.get_person_error);
            }
        };
    }


    private OnGetPersonImagesCallback prepareGetPersonImagesCallback() {
        final Fragment fragment = this;

        return new OnGetPersonImagesCallback() {
            @Override
            public void onSuccess(long id, ArrayList<PersonImage> images) {
                final FormHelpers.ThumbnailClickListener clickListener;
                clickListener = (FormHelpers.ThumbnailClickListener<PersonImage>) item -> {
                };

                FormHelpers.populateScrollingImageList(fragment, picturesLayout,
                        images, false, R.layout.fragment_card_image,
                        false, clickListener);
            }

            @Override
            public void onFailure(Throwable error) {
                FormHelpers.showSnackbar(fragment, R.string.get_person_error);
            }
        };
    }
}
