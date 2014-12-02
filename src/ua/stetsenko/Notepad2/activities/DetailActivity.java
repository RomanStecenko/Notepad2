package ua.stetsenko.Notepad2.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import ua.stetsenko.Notepad2.Constants;
import ua.stetsenko.Notepad2.R;
import ua.stetsenko.Notepad2.fragments.DetailFragment;


public class DetailActivity extends ActionBarActivity {

    int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity_layout);


        if (savedInstanceState == null) {
            DetailFragment details = DetailFragment.newInstance(
                    getIntent().getIntExtra(Constants.ARG_NOTE_ID, 0),
                    getIntent().getIntExtra(Constants.ARG_NOTE_TYPE, 0)
            );
//            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.detailContainer, details).commit();
            Log.d(Constants.LOG, "DetailActivity onCreate() savedInstanceState == null note id: " + getIntent().getIntExtra(Constants.ARG_NOTE_ID, 0)  + "  type: " + getIntent().getIntExtra(Constants.ARG_NOTE_TYPE, 0));
        }

        if (savedInstanceState != null) {
            noteId = savedInstanceState.getInt(Constants.ARG_NOTE_ID);
            Log.d(Constants.LOG, "DetailActivity onCreate() savedInstanceState != null  type: " + getIntent().getIntExtra(Constants.ARG_NOTE_TYPE, 0) + " id: " + noteId);
            Log.d(Constants.LOG, "note id: " + getIntent().getIntExtra(Constants.ARG_NOTE_ID, 0)  + "  type: " + getIntent().getIntExtra(Constants.ARG_NOTE_TYPE, 0));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.ARG_NOTE_ID,  getIntent().getIntExtra(Constants.ARG_NOTE_ID, 0));
        Log.d(Constants.LOG, "DetailActivity onSaveInstanceState() save note id: " + getIntent().getIntExtra(Constants.ARG_NOTE_ID, 0));
    }
}
