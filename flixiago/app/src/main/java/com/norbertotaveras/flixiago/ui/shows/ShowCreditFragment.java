package com.norbertotaveras.flixiago.ui.shows;

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
import com.norbertotaveras.flixiago.models.show.ShowCredit;
import com.norbertotaveras.flixiago.models.show.ShowPerson;
import com.norbertotaveras.flixiago.services.MovieDBApi;
import com.norbertotaveras.flixiago.services.base.OnGetPersonCombinedCreditsCallback;
import com.norbertotaveras.flixiago.services.base.OnGetPersonImagesCallback;
import com.norbertotaveras.flixiago.services.shows.OnGetShowPersonCallback;

import java.util.ArrayList;

public class ShowCreditFragment extends Fragment {

    private static final String TAG = "ShowCreditFragment";

    private ShowCredit showCredit;
    private ImageView photoView;
    private TextView nameView;
    private TextView bioView;
    private TextView knownForText;
    private TextView birthdate;
    private TextView birthplace;
    private LinearLayout knownForLayout;

    private MovieDBApi movieDBApi;
    private LinearLayout picturesLayout;

    public static ShowCreditFragment newInstance(ShowCredit showCredit) {

        Bundle args = new Bundle();

        ShowCreditFragment fragment = new ShowCreditFragment();
        fragment.showCredit = showCredit;
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
        return inflater.inflate(R.layout.fragment_show_credit, container, false);
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
        knownForText = view.findViewById(R.id.known_for);
        birthdate = view.findViewById(R.id.birthdate);
        birthplace = view.findViewById(R.id.birthplace);
        bioView = view.findViewById(R.id.bio);
        knownForLayout = view.findViewById(R.id.known_for_layout);
        picturesLayout = view.findViewById(R.id.pictures_layout);

        Glide.with(view)
                .load(showCredit.getThumbnailUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(photoView);

        assert activity != null;

        activity.setTitle(showCredit.getName());
        nameView.setText(showCredit.getName());

        movieDBApi = MovieDBApi.getInstance();

        OnGetShowPersonCallback personCallback = prepareGetShowPersonCallback();
        movieDBApi.getShowPerson(showCredit.getId(), personCallback);

        OnGetPersonCombinedCreditsCallback combinedCreditsCallback =
                prepareGetPersonCombinedCreditsCallback();
        movieDBApi.getPersonCombinedCredits(showCredit.getId(), combinedCreditsCallback);

        OnGetPersonImagesCallback personImagesCallback =
                prepareGetPersonImagesCallback();
        movieDBApi.getPersonImages(showCredit.getId(), personImagesCallback);

        super.onActivityCreated(savedInstanceState);
    }

    private OnGetPersonImagesCallback prepareGetPersonImagesCallback() {
        final Fragment fragment = this;

        return new OnGetPersonImagesCallback() {
            @Override
            public void onSuccess(long id, ArrayList<PersonImage> images) {
                final FormHelpers.ThumbnailClickListener clickListener;
                clickListener = new FormHelpers.ThumbnailClickListener<PersonImage>() {
                    @Override
                    public void onClick(PersonImage item) {
                    }
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

    private OnGetShowPersonCallback prepareGetShowPersonCallback() {
        final Fragment fragment = this;

        return new OnGetShowPersonCallback() {
            @Override
            public void onSuccess(ShowPerson person) {
                bioView.setText(person.getBiography());
                knownForText.setText(FormHelpers.replaceEmpty(person.getKnownFor(),
                        "<unknown>"));
                birthdate.setText(FormHelpers.replaceEmpty(person.formatBirthDate(),
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

    private OnGetPersonCombinedCreditsCallback prepareGetPersonCombinedCreditsCallback() {
        final Fragment fragment = this;
        final Context context = fragment.getContext();

        return new OnGetPersonCombinedCreditsCallback() {
            @Override
            public void onSuccess(PersonCombinedCredits combinedCredits) {
                FormHelpers.ThumbnailClickListener clickListener =
                        new FormHelpers.ThumbnailClickListener<MediaCredit>() {
                    @Override
                    public void onClick(MediaCredit item) {
                        item.open(context);
                    }
                };

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
}
