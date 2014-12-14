package ua.stetsenko.Notepad2;

public class Constants {
    private Constants(){}

    public static final String ARG_NOTE_ID = "noteId";
//    public static final String ARG_TO_INSERT ="toInsert"; //true - insert, false - update
//    public static final String ARG_IS_TABLET="isTablet"; //true - tablet, false - handset
    public static final String ARG_NOTE_TYPE="noteType";
    public static final String LOG="MY_LOG";
    public static final String NOTEPAD_FILES = "Notepad_Files";
    public static final String IMG_URI = "ImageUri";

    public static final String NOTE_DIALOG="noteDialog";
//    public static final String POSITION_TO_HIGHLIGHT = "positionToHighLight";


    public static final int MEDIA_TYPE_IMAGE = 11;
    public static final int MEDIA_TYPE_AUDIO = 12;
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 111;
    public static final int SPEAK_REQUEST_CODE = 112;
    public static final int IMAGE_FROM_GALLERY_REQUEST_CODE = 113;

    //int key type note
    public static final int TYPE_TEXT = 0;// "text";
    public static final int TYPE_LIST = 1;//"list";
    public static final int TYPE_PHOTO = 2;//"photo";
    public static final int TYPE_IMG = 3;//"image";
    public static final int TYPE_DRAWING = 4;//"drawing";
    public static final int TYPE_AUDIO = 5;//"audio";

    //string key type note
//    public static final String KEY_TYPE_TEXT = "text";
//    public static final String KEY_TYPE_LIST = "list";
//    public static final String KEY_TYPE_PHOTO = "photo";
//    public static final String KEY_TYPE_IMG = "image";
//    public static final String KEY_TYPE_DRAWING = "drawing";
//    public static final String KEY_TYPE_AUDIO = "audio";

    //database structure int
    public static final int NOTE_ID = 0;
    public static final int NOTE_TYPE = 1;
    public static final int NOTE_CONTENT = 2;
    public static final int NOTE_DATE_TIME = 3;
    public static final int NOTE_URI_RESOURCE = 4;


    public static final int STATE_NONE = 0;
    public static final int STATE_DRAG = 1;
    public static final int STATE_ZOOM = 2;
}
