package com.norbertotaveras.flixiago.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.models.show.ShowCredit;
import com.norbertotaveras.flixiago.ui.shows.ShowCreditFragment;

public class ShowCreditActivity extends AppCompatActivity {

    private static final String SHOW_CREDIT = "show_credit";

    // method to passed a show credit object through activity intents
    // as an extra.
    // starts the activity with the expected show credit details
    public static void run(Activity activity, ShowCredit showCredit) {
        Intent intent = new Intent(activity, ShowCreditActivity.class);
        intent.putExtra(SHOW_CREDIT, showCredit);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_credit);

        // get the intent
        Intent intent = getIntent();

        // get the parcelable show credit data
        ShowCredit showCredit = intent.getParcelableExtra(SHOW_CREDIT);

        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container,
                ShowCreditFragment.newInstance(showCredit))
                .commit();
    }
}
