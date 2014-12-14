package ua.stetsenko.Notepad2.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import ua.stetsenko.Notepad2.Constants;
import ua.stetsenko.Notepad2.GlobalApplication;
import ua.stetsenko.Notepad2.R;
import ua.stetsenko.Notepad2.activities.FullScreenActivity;
import ua.stetsenko.Notepad2.activities.MainActivity;
import ua.stetsenko.Notepad2.sqlite.DatabaseHelper;
import ua.stetsenko.Notepad2.sqlite.Note;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DetailFragment extends Fragment implements View.OnClickListener {

    private int noteId, noteType, screenHeight, screenWidth;
    private boolean isTablet, recordState = true, playState = true;
    private GlobalApplication ga;
    private EditText editText;
    private LinearLayout buttonContainer, audioButtonContainer;
    private TextView textView;
    private ImageView imageView;
    private Uri fileUri; // = Uri.parse("")
    private DatabaseHelper db;
    private Dialog matchTextDialog;
    private ArrayList<String> matchesText;
    private ImageButton speakButton;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private Button recordButton, playButton;


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
            Log.d(Constants.LOG, "DetailFragment onCreate() savedInstanceState == null, note id: " + noteId + " type note: " + noteType);
        }

        if (savedInstanceState != null) {
            fileUri = savedInstanceState.getParcelable(Constants.IMG_URI);
            noteId = savedInstanceState.getInt(Constants.ARG_NOTE_ID);
            noteType = savedInstanceState.getInt(Constants.ARG_NOTE_TYPE);
            Log.d(Constants.LOG, "DetailFragment onCreate() savedInstanceState != null restore uri: " + fileUri + " \n note id: " + noteId + " type note: " + noteType);
        }

        getScreenSize();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.detail_fragment_layout, container, false);
        editText = (EditText) detailView.findViewById(R.id.detailEditText);
        textView = (TextView) detailView.findViewById(R.id.detailEmptyTextView);
        imageView = (ImageView) detailView.findViewById(R.id.detailImageView);
        imageView.setOnClickListener(this);
        speakButton = (ImageButton) detailView.findViewById(R.id.speakButton);
        speakButton.setOnClickListener(this);
        buttonContainer = (LinearLayout) detailView.findViewById(R.id.buttonContainer);
        audioButtonContainer = (LinearLayout) detailView.findViewById(R.id.audioButtonContainer);
        (detailView.findViewById(R.id.makePhotoButton)).setOnClickListener(this);
        (detailView.findViewById(R.id.addFromGalleryButton)).setOnClickListener(this);
        (detailView.findViewById(R.id.detailUpdateButton)).setOnClickListener(this);
        recordButton = (Button) detailView.findViewById(R.id.recordButton);
        recordButton.setOnClickListener(this);
        playButton = (Button) detailView.findViewById(R.id.playRecordButton);
        playButton.setOnClickListener(this);
        setHasOptionsMenu(true);
        return detailView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isTablet = (getActivity().findViewById(R.id.fragmentContainer) != null);
        Note note = db.getNote(noteId);
        if (note != null) {
            editText.setText(note.getContent());
            fileUri = Uri.parse(note.getUriResource()); //if need to save uri after rotate make checking here: if (fileUri.toString < 1), or maybe: if (savedInstanceState == null)
            noteType = note.getType();
            Log.d(Constants.LOG, "DetailFragment onActivityCreated() getNote != null, \n" + note.toString());
//            if (noteType == Constants.TYPE_TEXT) {
//                buttonContainer.setVisibility(View.GONE);
//                audioButtonContainer.setVisibility(View.GONE);
//            }
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
            if (noteType == Constants.TYPE_AUDIO) {
                audioButtonContainer.setVisibility(View.VISIBLE);
                if (fileUri.toString().length() > 0)
                    recordButton.setText(R.string.startAudioRerecording);
            }
        } else {
            Note newNote = new Note(noteId, noteType, editText.getText().toString(), ga.getDateTime(), "");
            db.addNote(newNote);
            if (noteType == Constants.TYPE_PHOTO) {
                buttonContainer.setVisibility(View.VISIBLE);
            }
            if (noteType == Constants.TYPE_AUDIO) {
                audioButtonContainer.setVisibility(View.VISIBLE);
            }
            fileUri = Uri.parse("");
            Log.d(Constants.LOG, "DetailFragment onActivityCreated() getNote == null, save new note: " + newNote.toString());
//            if (isTablet){
//                ((MainActivity) getActivity()).updateTitlesFragment();
//            }

        }
//        setFocusOnEditText();


//        if (isTablet) {
//            editText.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {}
//                @Override
//                public void afterTextChanged(Editable s) {
//                    Toast.makeText(getActivity(), "afterTextChanged() start updateNote and updateTitlesFragment ", Toast.LENGTH_SHORT).show();
//                    Note note = new Note(noteId, noteType, editText.getText().toString(), ga.getDateTime(), fileUri.toString());
//                    db.updateNote(note);
//                    Log.d(Constants.LOG, "DetailFragment afterTextChanged() updateNote, Editable s: " + s.toString() + "\n" + note.toString());
//                    ((MainActivity) getActivity()).updateTitlesFragment();
//                }
//            });
//            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (!hasFocus) {
//                        Toast.makeText(getActivity(), "onFocusChange() lost focus, start updateNote and updateTitlesFragment ", Toast.LENGTH_SHORT).show();
//                        Note note = new Note(noteId, noteType, editText.getText().toString(), ga.getDateTime(), fileUri.toString());
//                        db.updateNote(note);
//                        Log.d(Constants.LOG, "DetailFragment onFocusChange() lost focus updateNote \n" + note.toString());
//                        ((MainActivity) getActivity()).updateTitlesFragment();
//                    }
//                }
//            });
//        }
    }

//    public void setFocusOnEditText(){
//        editText.setSelection(editText.getText().length());
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
                updateNote();
                break;
            case R.id.makePhotoButton:
                captureImage();
                break;
            case R.id.addFromGalleryButton:
                getImageFromGallery();
                break;
            case R.id.speakButton:
                startSpeechRecognize();
                break;
            case R.id.detailImageView:
                startActivity(new Intent(getActivity(), FullScreenActivity.class).putExtra(Constants.IMG_URI, fileUri));
                break;
            case R.id.recordButton:
                if (recordState){
                    fileUri = Uri.parse(getOutputMediaFile(Constants.MEDIA_TYPE_AUDIO).getAbsolutePath());
                    startRecording();
//                    onRecord(recordState);
                    recordState = !recordState;
                    recordButton.setText(R.string.stopAudioRecording);
                    playButton.setEnabled(false);
                    Log.d(Constants.LOG, "DetailFragment onClick recordButton startRecording()  fileUri:" + fileUri + ", note id: " + noteId + ", type note: " + noteType);
                } else {
                    stopRecording();
//                    onRecord(recordState);
                    recordState = !recordState;
                    recordButton.setText(R.string.startAudioRerecording);
                    playButton.setEnabled(true);
                    Log.d(Constants.LOG, "DetailFragment onClick recordButton stopRecording() ");
                }
                Toast.makeText(getActivity().getApplicationContext(), "Record button", Toast.LENGTH_SHORT).show();
                break;
            case R.id.playRecordButton:
                if (fileUri.toString().length() > 0){
                    if (playState){
                        recordButton.setEnabled(false);
                        startPlaying();
                        playButton.setText(R.string.audioPlaybackOff);
                        playState = !playState;
                        Log.d(Constants.LOG, "DetailFragment onClick playRecordButton startPlaying()  fileUri:" + fileUri);
                    } else {
                        stopPlaying();
                        playState = !playState;
                        recordButton.setEnabled(true);
                        playButton.setText(R.string.audioPlaybackOn);
                        Log.d(Constants.LOG, "DetailFragment onClick playRecordButton stopPlaying()");
                    }
                    Toast.makeText(getActivity().getApplicationContext(), "playback button", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "have nothing to play", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        editText.clearFocus();
//        setFocusOnEditText();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
//        if (!isTablet){
//        Note note = new Note(noteId, noteType, editText.getText().toString(), ga.getDateTime(), fileUri.toString());
//        db.updateNote(note);
//        Log.d(Constants.LOG, "DetailFragment onPause() !isTablet updateNote, " + note.toString());
//        } else {
//            if (noteType == Constants.TYPE_TEXT) {
//                if (editText.getText().toString().length()<1) {
//                    db.deleteNote(noteId);
//                    Log.d(Constants.LOG, "DetailFragment onPause() isTablet TYPE_TEXT getNote deleteNote id: " +noteId);
//                }
//            }
//            if (noteType == Constants.TYPE_PHOTO) {
//                if (editText.getText().toString().length()<1 && imageView.getDrawable() == null) {
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
            editText.setText(note.getContent());
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

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(fileUri.toString());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(Constants.LOG, "  PLAYING: prepare() failed");
            Toast.makeText(getActivity().getApplicationContext(), "PLAYING: prepare() failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(fileUri.toString());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(Constants.LOG, " RECORDING: prepare() failed");
            Toast.makeText(getActivity().getApplicationContext(), "RECORDING: prepare() failed", Toast.LENGTH_SHORT).show();
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
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
                        editText.setText(editText.getText().toString() + matchesText.get(position));
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
        if (noteType == Constants.TYPE_PHOTO) {
            inflater.inflate(R.menu.det_frag_menu, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.changeImageView:
                imageView.setVisibility(View.GONE);
                buttonContainer.setVisibility(View.VISIBLE);
                fileUri = Uri.parse("");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideDetails() {
        editText.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        buttonContainer.setVisibility(View.GONE);
        speakButton.setVisibility(View.GONE);
    }

    private void updateNote(){
        Note note = new Note(noteId, noteType, editText.getText().toString(), ga.getDateTime(), fileUri.toString());
        db.updateNote(note); //Log.d(Constants.LOG, "DetailFragment onClick() detailUpdateButton restore uri: " + fileUri + " \n note id: " + noteId + " type note: " + noteType + " isTablet: " + isTablet);
        if (isTablet) {
            ((MainActivity) getActivity()).updateTitlesFragment();
        } else
            getActivity().finish();
    }

    private void startSpeechRecognize(){
        if (isConnected()) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            try {
                startActivityForResult(intent, Constants.SPEAK_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Your device doesn't support Speech to Text", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        return net != null && net.isAvailable() && net.isConnected();
    }

    private void previewCapturedImage() {
        imageView.setVisibility(View.VISIBLE);
        buttonContainer.setVisibility(View.GONE);
        imageView.setImageBitmap(decodeFile(fileUri.getPath()));
    }

    private void showImageFromGallery(Uri dataUri) {
        imageView.setVisibility(View.VISIBLE);
        buttonContainer.setVisibility(View.GONE);
        fileUri = dataUri;
        Log.d(Constants.LOG, "DetailFragment showImageFromGallery() uri:" + fileUri);
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(fileUri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        imageView.setImageBitmap(decodeFile(picturePath));
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
        } else if (type == Constants.MEDIA_TYPE_AUDIO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "AUDIO_" + timeStamp + ".3gp");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void getScreenSize(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
    }

    private Bitmap decodeFile(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        final int longest = options.outHeight > options.outWidth ? options.outHeight : options.outWidth;
        int required = screenHeight > screenWidth ? screenHeight/2 : screenWidth/2;
        int inSampleSize = 1;
        if (longest > required) {
            while ((longest / inSampleSize) > required) {
                inSampleSize *= 2;
            }
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

}
