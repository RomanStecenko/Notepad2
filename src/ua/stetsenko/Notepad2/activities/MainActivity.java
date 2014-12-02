package ua.stetsenko.Notepad2.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import ua.stetsenko.Notepad2.Constants;
import ua.stetsenko.Notepad2.R;
import ua.stetsenko.Notepad2.fragments.DetailFragment;
import ua.stetsenko.Notepad2.fragments.NoteTypeDialog;
import ua.stetsenko.Notepad2.fragments.TitleFragment;
import ua.stetsenko.Notepad2.sqlite.DatabaseHelper;
import ua.stetsenko.Notepad2.sqlite.Note;

public class MainActivity extends ActionBarActivity implements TitleFragment.onSelectedListener, NoteTypeDialog.onSelectTypeNoteListener {

    private boolean isTablet;
    private int noteId;
    DatabaseHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        db = new DatabaseHelper(this);

        isTablet = (findViewById(R.id.fragmentContainer) != null);
        if (isTablet) {

            try {
                Note note = db.getFirstNote();
                if (note != null) {
                    noteId = note.getId();
                } else {
                    Log.d(Constants.LOG, "MainActivity onCreate() note == null");
                }
            } catch (Exception e) {
                Log.d(Constants.LOG, "MainActivity onCreate() try db.getFirstNote() Exception: " + e.toString());
                e.printStackTrace();
            }
        }

        if (savedInstanceState != null)
            noteId = savedInstanceState.getInt(Constants.ARG_NOTE_ID);

        if (isTablet)
            showDetails(noteId);
    }

    private void showDetails(int nextNoteIdToShow) {
        if (isTablet) {
            try {
                DetailFragment detail = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                if (detail == null || detail.getNoteId() != nextNoteIdToShow) {
                    Note note = db.getNote(nextNoteIdToShow);
                    detail = DetailFragment.newInstance(nextNoteIdToShow, note.getType());
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, detail).commit();
                    Log.d(Constants.LOG, "MainActivity showDetails() isTablet true: " + note.toString());
                }
            } catch (Exception e) {
                Log.d(Constants.LOG, "MainActivity showDetails try db.getNote() Exception: " + e.toString());
                e.printStackTrace();
                FrameLayout layout = (FrameLayout) findViewById(R.id.fragmentContainer);
                LinearLayout hide = (LinearLayout) getLayoutInflater().inflate(R.layout.hide, null);
                layout.addView(hide);
            }
        } else {
            startActivity(new Intent(this, DetailActivity.class)
                    .putExtra(Constants.ARG_NOTE_ID, nextNoteIdToShow));
        }
        Log.d(Constants.LOG, "MainActivity showDetails() noteId: " + noteId + " nextNoteIdToShow: "+ nextNoteIdToShow);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isTablet)
            updateTitlesFragment();
    }

    public void updateTitlesFragment() {
        TitleFragment tf = (TitleFragment) getSupportFragmentManager().findFragmentById(R.id.titles_fragment);
        tf.updateFragment();
    }


    public void updateDetailFragment() {
        if (isTablet) {
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            df.updateFragment();
        }
    }

    public void start() {
        DialogFragment df = new NoteTypeDialog();
        df.show(getSupportFragmentManager(), Constants.NOTE_DIALOG);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.ARG_NOTE_ID, noteId);
    }

    @Override
    public void onSelectNote(int position) {
        this.noteId = position;
        showDetails(position);
    }


    @Override
    public void onSelectTypeNote(int position) {
//        if (isTablet){
//            TitleFragment tf = (TitleFragment) getSupportFragmentManager().findFragmentById(R.id.titles_fragment);
//            tf.unhighlightCurrentRow();
//        }
        switch (position) {
            case Constants.TYPE_TEXT:
                addNextNote(Constants.TYPE_TEXT);
                break;
            case Constants.TYPE_LIST:
                Toast.makeText(this, "TYPE_LIST " + position, Toast.LENGTH_SHORT).show();
                break;
            case Constants.TYPE_PHOTO:
                //I commented out the checks of camera support to test the emulator
//                if (isDeviceSupportCamera()){
                addNextNote(Constants.TYPE_PHOTO);
//                } else
//                    Toast.makeText(this, "Device has no camera.", Toast.LENGTH_SHORT).show();

                break;
            case Constants.TYPE_IMG:
                Toast.makeText(this, "TYPE_IMG " + position, Toast.LENGTH_SHORT).show();
                break;
            case Constants.TYPE_DRAWING:
                Toast.makeText(this, "TYPE_DRAWING " + position, Toast.LENGTH_SHORT).show();
                break;
            case Constants.TYPE_AUDIO:
                addNextNote(Constants.TYPE_AUDIO);
                Toast.makeText(this, "TYPE_AUDIO " + position, Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void addNextNote(int noteType){
        if (isTablet) {
            DetailFragment newNote = DetailFragment.newInstance(db.getNextId(), noteType);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, newNote).commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(Constants.ARG_NOTE_ID, db.getNextId())
                    .putExtra(Constants.ARG_NOTE_TYPE, noteType);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clearDb:
                int result = db.deleteAllNotes();
                Toast.makeText(this, result + " notes is deleted", Toast.LENGTH_SHORT).show();
                updateTitlesFragment();
                updateDetailFragment();
                break;
            case R.id.menu_printDbToLog:
                db.printToLogAllDatabase();
                Toast.makeText(this, "db printed in log", Toast.LENGTH_SHORT).show();
                Log.d(Constants.LOG, " next id: " + db.getNextId());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isDeviceSupportCamera() {
        return getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

}
