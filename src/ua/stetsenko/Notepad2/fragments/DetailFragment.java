package ua.stetsenko.Notepad2.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ua.stetsenko.Notepad2.Constants;
import ua.stetsenko.Notepad2.GlobalApplication;
import ua.stetsenko.Notepad2.R;
import ua.stetsenko.Notepad2.activities.MainActivity;
import ua.stetsenko.Notepad2.sqlite.DatabaseHelper;
import ua.stetsenko.Notepad2.sqlite.Note;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailFragment extends Fragment implements View.OnClickListener {

    private int noteId, noteType;
    private boolean isTablet;
    private GlobalApplication ga;
    private EditText et;
    private Button updateButton, makePhoto;
    private TextView tv;
    private ImageView iv;
    private Uri fileUri; // = Uri.parse("")
    private DatabaseHelper db;


    public static DetailFragment newInstance(int noteId, int noteType) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_NOTE_ID, noteId);
        args.putInt(Constants.ARG_NOTE_TYPE, noteType);
        detailFragment.setArguments(args);
        return detailFragment;
    }

    public int getNoteId() {
        return getArguments().getInt(Constants.ARG_NOTE_ID, 0);
    }

    public int getNoteType() {
        return getArguments().getInt(Constants.ARG_NOTE_TYPE, 0);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getActivity());
        ga = (GlobalApplication) getActivity().getApplication();
        isTablet = (getActivity().findViewById(R.id.fragmentContainer) != null);
        if (savedInstanceState == null) {
            noteId = getArguments().getInt(Constants.ARG_NOTE_ID, 0);
            noteType = getArguments().getInt(Constants.ARG_NOTE_TYPE, 0);
            Log.d(Constants.LOG, "DetailFragment onCreate() savedInstanceState == null, note id: " + noteId + " type note: " + noteType + " isTablet: " + isTablet);
        }

        if (savedInstanceState != null) {
            fileUri = savedInstanceState.getParcelable(Constants.IMG_URI);
            noteId = savedInstanceState.getInt(Constants.ARG_NOTE_ID);
            noteType = savedInstanceState.getInt(Constants.ARG_NOTE_TYPE);
            Log.d(Constants.LOG, "DetailFragment onCreate() savedInstanceState != null restore uri: " + fileUri + " \n note id: " + noteId + " type note: " + noteType + " isTablet: " + isTablet);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.detail_fragment_layout, container, false);
        et = (EditText) detailView.findViewById(R.id.detailEditText);
//        et.requestFocus();
        tv = (TextView) detailView.findViewById(R.id.detailEmptyTextView);
        iv = (ImageView) detailView.findViewById(R.id.detailImageView);
        makePhoto = (Button) detailView.findViewById(R.id.addPhotoButton);
        makePhoto.setOnClickListener(this);

//        updateButton = (Button) detailView.findViewById(R.id.detailUpdateButton);
//        updateButton.setOnClickListener(this);


        return detailView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Note note = db.getNote(noteId);
        if (note != null) {
            et.setText(note.getContent());
            fileUri = Uri.parse(note.getUriResource());
            noteType = note.getType();
            Log.d(Constants.LOG, "DetailFragment onActivityCreated() getNote != null, \n" + note.toString());
            if (noteType == Constants.TYPE_PHOTO) {
                if (fileUri.toString().length() < 1) {
                    makePhoto.setVisibility(View.VISIBLE);
                    Log.d(Constants.LOG, "DetailFragment onActivityCreated() getNote != null but fileUri.toString().length()<1");
                } else {
                    previewCapturedImage();
                    Log.d(Constants.LOG, "DetailFragment onActivityCreated() getNote != null previewCapturedImage() " + fileUri.toString());
                }
            }
        } else {
            Note newNote = new Note(noteId, noteType, et.getText().toString(), ga.getDateTime(), "");
            db.addNote(newNote);
            if (noteType == Constants.TYPE_PHOTO) {
                makePhoto.setVisibility(View.VISIBLE);
            }
            fileUri = Uri.parse("");
            Log.d(Constants.LOG, "DetailFragment onActivityCreated() getNote == null, save new note: " + newNote.toString());
        }
        if (isTablet){
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    Toast.makeText(getActivity(), "afterTextChanged() start updateNote and updateTitlesFragment ", Toast.LENGTH_SHORT).show();
                    Note note = new Note(noteId, noteType, et.getText().toString(), ga.getDateTime(), fileUri.toString());
                    db.updateNote(note);
                    Log.d(Constants.LOG, "DetailFragment afterTextChanged() updateNote, Editable s" +s.toString()+ "\n" + note.toString());
                    ((MainActivity)getActivity()).updateTitlesFragment();
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.IMG_URI, fileUri);
        outState.putInt(Constants.ARG_NOTE_ID, noteId);
        outState.putInt(Constants.ARG_NOTE_TYPE, noteType);
        Log.d(Constants.LOG, "DetailFragment onSaveInstanceState save uri: " + fileUri + ", note id: " + noteId + ", type note: " + noteType);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.detailUpdateButton:
//
//                Note note = new Note(noteId, noteType, et.getText().toString(), ga.getDateTime(), fileUri.toString());
//                db.updateNote(note);
//                Log.d(Constants.LOG, "DetailFragment onClick() detailUpdateButton restore uri: " + fileUri + " \n note id: " + noteId + " type note: " + noteType + " isTablet: " + isTablet);
//                if (isTablet)
//                    ((MainActivity) getActivity()).updateTitlesFragment();
//                else
//                    getActivity().finish();
//                break;
            case R.id.addPhotoButton:
                captureImage();
                break;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Note note = new Note(noteId, noteType, et.getText().toString(), ga.getDateTime(), fileUri.toString());
        db.updateNote(note);
        Log.d(Constants.LOG, "DetailFragment onPause() updateNote, " + note.toString());
    }

    public void updateFragment() {
            Note note = db.getFirstNote();
            if (note != null) {
                noteId = note.getId();
                et.setText(note.getContent());
                noteType = note.getType();
                fileUri = Uri.parse(note.getUriResource());
                Log.d(Constants.LOG, "DetailFragment updateFragment() getNote != null, \n" + note.toString());
                if (noteType == Constants.TYPE_PHOTO) {
                    if (fileUri.toString().length() < 1) {
                        makePhoto.setVisibility(View.VISIBLE);
                        Log.d(Constants.LOG, "DetailFragment updateFragment() getNote != null but fileUri.toString().length()<1");
                    } else {
                        previewCapturedImage();
                        Log.d(Constants.LOG, "DetailFragment updateFragment() getNote != null previewCapturedImage() " + fileUri.toString());
                    }
                }
            } else {
                Log.d(Constants.LOG, "DetailFragment updateFragment() note == null");
                hideDetails();
            }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(Constants.MEDIA_TYPE_IMAGE);
        Log.d(Constants.LOG, "DetailFragment captureImage() URI: " + fileUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
                Log.d(Constants.LOG, "DetailFragment onActivityResult() RESULT_OK fileUri: " + fileUri + " id: " + noteId + " getNoteId: " + getNoteId() + " type: " + noteType + " getNoteType(): " + getNoteType());

//                Note note = new Note(noteId, noteType, et.getText().toString(), ga.getDateTime(), fileUri.toString());
//                int result = db.updateNote(note);
//                Log.d(Constants.LOG, "DetailFragment onActivityResult() RESULT_OK result update" + result + " \n" + db.getNote(result).toString());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getActivity().getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image
                Toast.makeText(getActivity().getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void hideDetails() {
        et.setVisibility(View.GONE);
        updateButton.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
        makePhoto.setVisibility(View.GONE);
    }

    private void previewCapturedImage() {
        try {
            iv.setVisibility(View.VISIBLE);
            makePhoto.setVisibility(View.GONE);
            // bitmap factory
            BitmapFactory.Options options = new BitmapFactory.Options();
            // downsizing image as it throws OutOfMemory Exception for larger images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

            iv.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d(Constants.LOG, "DetailFragment previewCapturedImage() try set img to ImageView, Exception: " + e.toString());
        }
    }

    private boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {
        File mediaStorageDir;
        if (isExternalStorageWritable()) {
            // External sdcard location
            mediaStorageDir = new File(Environment.getExternalStorageDirectory(), Constants.NOTEPAD_FILES);
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(Constants.LOG, "Oops! Failed create " + Constants.NOTEPAD_FILES + " directory");
                    return null;
                }
            }
            Log.d(Constants.LOG, "Use External storage " + mediaStorageDir);
        } else {
            // Internal storage location
            mediaStorageDir = getActivity().getApplicationContext().getFilesDir();
            Log.d(Constants.LOG, "Use Internal storage " + mediaStorageDir);
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HH-mm-ss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == Constants.MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

}
