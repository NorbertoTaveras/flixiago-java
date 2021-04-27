package com.norbertotaveras.flixiago.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.models.show.Show;
import com.norbertotaveras.flixiago.ui.shows.ShowSeasonFragment;

public class ShowSeasonActivity extends AppCompatActivity {
    private static final String TAG = "ShowSeasonActivity";

    private static final String ARG_SHOW = "show";

    public static void run(Context context, Show show) {
        Intent intent = new Intent(context, ShowSeasonActivity.class);
        intent.putExtra(ARG_SHOW, show);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_season);

        // gets the intent
        Intent intent = getIntent();

        // gets the show parcelable data
        Show show = intent.getParcelableExtra(ARG_SHOW);

        if (intent == null) {
            Log.e(TAG, "no showId!");
            return;
        }

        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container,
                ShowSeasonFragment.newInstance(show))
                .commit();
    }
}
