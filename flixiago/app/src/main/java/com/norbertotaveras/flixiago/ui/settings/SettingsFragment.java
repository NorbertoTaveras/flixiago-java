package com.norbertotaveras.flixiago.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.helpers.FormHelpers;

public class SettingsFragment
        extends
        Fragment
        implements View.OnClickListener {

    private MaterialCardView creatorCard;
    private MaterialCardView featureCard;
    private MaterialCardView bugCard;

    private Context context;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        if (view == null)
            return;

        context = getContext();

        creatorCard = view.findViewById(R.id.creator_card_view);
        featureCard = view.findViewById(R.id.feature_card_view);
        bugCard = view.findViewById(R.id.report_bug_card_view);

        creatorCard.setOnClickListener(this);
        featureCard.setOnClickListener(this);
        bugCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.feature_card_view:
                FormHelpers.openFeatureRequestEmail(context);
                break;
            case R.id.report_bug_card_view:
                FormHelpers.openBugReportEmail(context);
                break;
            case R.id.creator_card_view:
                FormHelpers.openCreatorTwitter(context);
                break;
            default:
                break;
        }
    }
}
