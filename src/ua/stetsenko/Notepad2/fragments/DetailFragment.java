package ua.stetsenko.Notepad2.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;
import ua.stetsenko.Notepad2.Constants;
import ua.stetsenko.Notepad2.GlobalApplication;
import ua.stetsenko.Notepad2.R;
import ua.stetsenko.Notepad2.activities.MainActivity;
import ua.stetsenko.Notepad2.sqlite.DatabaseHelper;
import ua.stetsenko.Notepad2.sqlite.Note;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DetailFragment extends Fragment implements View.OnClickListener {

    private int noteId, noteType;
    private boolean isTablet;
    private GlobalApplication ga;
    private EditText et;
    private LinearLayout buttonContainer;
    private TextView tv;
    private ImageView iv;
    private Uri fileUri; // = Uri.parse("")
    private DatabaseHelper db;
    private Dialog matchTextDialog;
    private ArrayList<String> matchesText;
    private ImageButton speakButton;


    public static DetailFragment newInstance(int noteId, int noteType) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_NOTE_ID, noteId);
        args.putInt(Constants.ARG_NOTE_TYPE, noteType);
        detailFragment.setArguments(args);
        return detailFragment;
    }

    public int getNoteId() {
        return noteId;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getActivity());
        ga = (GlobalApplication) getActivity().getApplication();
        if (savedInstanceState == null) {
            noteId = getArguments().getInt(Constants.ARG_NOTE_ID, 0);
            noteType = getArguments().getInt(Constants.ARG_NOTE_TYPE, 0);
            Log.d(Constants.LOG, "DetailFragment onCreate() savedInstanceState == null, note id: " + noteId + " type note: " + noteType );
        }

        if (savedInstanceState != null) {
            fileUri = savedInstanceState.getParcelable(Constants.IMG_URI);
            noteId = savedInstanceState.getInt(Constants.ARG_NOTE_ID);
            noteType = savedInstanceState.getInt(Constants.ARG_NOTE_TYPE);
            Log.d(Constants.LOG, "DetailFragment onCreate() savedInstanceState != null restore uri: " + fileUri + " \n note id: " + noteId + " type note: " + noteType);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.detail_fragment_layout, container, false);
        et = (EditText) detailView.findViewById(R.id.detailEditText);
        if (Build.VERSION.SDK_INT > 10)
            et.setTextIsSelectable(true);
//        et.requestFocus();
        tv = (TextView) detailView.findViewById(R.id.detailEmptyTextView);
        iv = (ImageView) detailView.findViewById(R.id.detailImageView);
        speakButton = (ImageButton) detailView.findViewById(R.id.speakButton);
        speakButton.setOnClickListener(this);
        buttonContainer = (LinearLayout) detailView.findViewById(R.id.buttonContainer);
        (detailView.findViewById(R.id.makePhotoButton)).setOnClickListener(this);
        (detailView.findViewById(R.id.addFromGalleryButton)).setOnClickListener(this);
        (detailView.findViewById(R.id.detailUpdateButton)).setOnClickListener(this);
        setHasOptionsMenu(true);
        return detailView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isTablet = (getActivity().findViewById(R.id.fragmentContainer) != null);
        Note note = db.getNote(noteId);
        if (note != null) {
            et.setText(note.getContent());
            fileUri = Uri.parse(note.getUriResource());
            noteType = note.getType();
            Log.d(Constants.LOG, "DetailFragment onActivityCreated() getNote != null, \n" + note.toString());
            if (noteType == Constants.TYPE_TEXT) {
                buttonContainer.setVisibility(View.GONE);
            }
            if (noteType == Constants.TYPE_PHOTO) {
                if (fileUri.toString().length() < 1) {
                    buttonContainer.setVisibility(View.VISIBLE);
                    Log.d(Constants.LOG, "DetailFragment onActivityCreated() getNote != null but fileUri.toString().length()<1");
                } else {
                    if (fileUri.getScheme().equals("file")) {
                        previewCapturedImage();
                    } else {
                        showImageFromGallery(fileUri);
                    }
                    Log.d(Constants.LOG, "DetailFragment onActivityCreated() getNote != null, show image from uri: " + fileUri.toString()); //+ "\n uri Scheme: '" + fileUri.getScheme() + "'"
                }
            }
        } else {
            Note newNote = new Note(noteId, noteType, et.getText().toString(), ga.getDateTime(), "");
            db.addNote(newNote);
            if (noteType == Constants.TYPE_TEXT) {
                buttonContainer.setVisibility(View.GONE);
            }
            if (noteType == Constants.TYPE_PHOTO) {
                buttonContainer.setVisibility(View.VISIBLE);
            }
            fileUri = Uri.parse("");
            Log.d(Constants.LOG, "DetailFragment onActivityCreated() getNote == null, save new note: " + newNote.toString());
//            if (isTablet){
//                ((MainActivity) getActivity()).updateTitlesFragment();
//            }
        }
//        setFocusOnEditText();


//        if (isTablet) {
//            et.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {}
//                @Override
//                public void afterTextChanged(Editable s) {
//                    Toast.makeText(getActivity(), "afterTextChanged() start updateNote and updateTitlesFragment ", Toast.LENGTH_SHORT).show();
//                    Note note = new Note(noteId, noteType, et.getText().toString(), ga.getDateTime(), fileUri.toString());
//                    db.updateNote(note);
//                    Log.d(Constants.LOG, "DetailFragment afterTextChanged() updateNote, Editable s: " + s.toString() + "\n" + note.toString());
//                    ((MainActivity) getActivity()).updateTitlesFragment();
//                }
//            });
//            et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (!hasFocus) {
//                        Toast.makeText(getActivity(), "onFocusChange() lost focus, start updateNote and updateTitlesFragment ", Toast.LENGTH_SHORT).show();
//                        Note note = new Note(noteId, noteType, et.getText().toString(), ga.getDateTime(), fileUri.toString());
//                        db.updateNote(note);
//                        Log.d(Constants.LOG, "DetailFragment onFocusChange() lost focus updateNote \n" + note.toString());
//                        ((MainActivity) getActivity()).updateTitlesFragment();
//                    }
//                }
//            });
//        }
    }

//    public void setFocusOnEditText(){
//        et.setSelection(et.getText().length());
//    }

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
            case R.id.detailUpdateButton:
                Note note = new Note(noteId, noteType, et.getText().toString(), ga.getDateTime(), fileUri.toString());
                db.updateNote(note);
                Log.d(Constants.LOG, "DetailFragment onClick() detailUpdateButton restore uri: " + fileUri + " \n note id: " + noteId + " type note: " + noteType + " isTablet: " + isTablet);
                if (isTablet){
                    ((MainActivity) getActivity()).updateTitlesFragment();
                }
                else
                    getActivity().finish();
                break;
            case R.id.makePhotoButton:
                captureImage();
                break;
            case R.id.addFromGalleryButton:
                getImageFromGallery();
                break;
            case R.id.speakButton:
                if (isConnected()) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    try {
                        startActivityForResult(intent, Constants.SPEAK_REQUEST_CODE);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity().getApplicationContext(), "Your device doesn't support Speech to Text", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
//        setFocusOnEditText();
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (!isTablet){
//        Note note = new Note(noteId, noteType, et.getText().toString(), ga.getDateTime(), fileUri.toString());
//        db.updateNote(note);
//        Log.d(Constants.LOG, "DetailFragment onPause() !isTablet updateNote, " + note.toString());
//        } else {
//            if (noteType == Constants.TYPE_TEXT) {
//                if (et.getText().toString().length()<1) {
//                    db.deleteNote(noteId);
//                    Log.d(Constants.LOG, "DetailFragment onPause() isTablet TYPE_TEXT getNote deleteNote id: " +noteId);
//                }
//            }
//            if (noteType == Constants.TYPE_PHOTO) {
//                if (et.getText().toString().length()<1 && iv.getDrawable() == null) {
//                    db.deleteNote(noteId);
//                    Log.d(Constants.LOG, "DetailFragment onPause() isTablet TYPE_PHOTO getNote deleteNote id: " +noteId);
//                }
//            }
//        }
    }

    public void updateFragment() {
        Note note = db.getFirstNote();
        if (note != null) {
            noteId = note.getId();
            et.setText(note.getContent());
            noteType = note.getType();
            fileUri = Uri.parse(note.getUriResource());
            Log.d(Constants.LOG, "DetailFragment updateFragment() getNote != null, \n" + note.toString());
            if (noteType == Constants.TYPE_TEXT) {
                buttonContainer.setVisibility(View.GONE);
            }
            if (noteType == Constants.TYPE_PHOTO) {
                if (fileUri.toString().length() < 1) {
                    buttonContainer.setVisibility(View.VISIBLE);
                    Log.d(Constants.LOG, "DetailFragment updateFragment() getNote != null but fileUri.toString().length()<1");
                } else {
                    if (fileUri.getScheme().equals("file")) {
                        previewCapturedImage();
                    } else {
                        showImageFromGallery(fileUri);
                    }
                    Log.d(Constants.LOG, "DetailFragment updateFragment() getNote != null, show image from uri: " + fileUri.toString());
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

    private void getImageFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, Constants.IMAGE_FROM_GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // successfully captured the image, display it in image view
                previewCapturedImage();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getActivity().getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image
                Toast.makeText(getActivity().getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == Constants.IMAGE_FROM_GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                showImageFromGallery(data.getData());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity().getApplicationContext(), "User cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Sorry! Failed to get image from gallery", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == Constants.SPEAK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                matchTextDialog = new Dialog(getActivity());
                matchTextDialog.setContentView(R.layout.dialog_matches_frag);
                matchTextDialog.setTitle("Select Matching Text");
                ListView textList = (ListView) matchTextDialog.findViewById(R.id.listMatches);
                matchesText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, matchesText);
                textList.setAdapter(adapter);
                textList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        et.setText(et.getText().toString() + matchesText.get(position));
                        matchTextDialog.hide();
                    }
                });
                matchTextDialog.show();

                Log.d(Constants.LOG, "DetailFragment onActivityResult() RESULT_OK requestCode: SPEAK_REQUEST_CODE");
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity().getApplicationContext(), "User cancelled speak recognition", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Sorry! Failed to speak recognition", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (noteType == Constants.TYPE_PHOTO){
            inflater.inflate(R.menu.det_frag_menu, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.changeImageView:
                iv.setVisibility(View.GONE);
                buttonContainer.setVisibility(View.VISIBLE);
                fileUri = Uri.parse("");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideDetails() {
        et.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
        iv.setVisibility(View.GONE);
        buttonContainer.setVisibility(View.GONE);
        speakButton.setVisibility(View.GONE);
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        return net != null && net.isAvailable() && net.isConnected();
    }

    private void previewCapturedImage() {
        iv.setVisibility(View.VISIBLE);
        buttonContainer.setVisibility(View.GONE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        // downsizing image as it throws OutOfMemory Exception for larger images
        options.inSampleSize = 8;
        final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
        iv.setImageBitmap(bitmap);
//            Log.d(Constants.LOG, "DetailFragment previewCapturedImage() try set img to ImageView, Exception: " + e.toString());
    }

    private void showImageFromGallery(Uri dataUri) {
        iv.setVisibility(View.VISIBLE);
        buttonContainer.setVisibility(View.GONE);
//        Uri selectedImage = data.getData();
        fileUri = dataUri;
        Log.d(Constants.LOG, "DetailFragment showImageFromGallery() uri:" + fileUri);
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(fileUri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        iv.setImageBitmap(BitmapFactory.decodeFile(picturePath));
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
