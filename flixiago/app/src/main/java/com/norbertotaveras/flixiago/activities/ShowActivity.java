package com.norbertotaveras.flixiago.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.models.show.Show;
import com.norbertotaveras.flixiago.ui.shows.ShowFragment;

public class ShowActivity
        extends
        AppCompatActivity
        implements ShowFragment.ShowActivityInterface {

    private static final String SHOW = "show";
    private static final String TAG = "ShowActivity";

    public static void show(Context context, Show show) {
        Intent showIntent = new Intent(context, ShowActivity.class);
        showIntent.putExtra(ShowActivity.SHOW, show);
        context.startActivity(showIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        // get the intent
        Intent intent = getIntent();

        // get the parcelable extra of the show
        Show show = intent.getParcelableExtra(SHOW);

        if (show == null) {
            Log.e(TAG, "Missing show extra!");
            return;
        }

        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragment_container,
                ShowFragment.newInstance(this, this, show))
                .commit();
    }

    @Override
    public void showFragmentFailed(ShowFragment fragment) {
        finish();
    }

    @Override
    public void setActivityTitle() {

    }
}
