package ua.stetsenko.Notepad2.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import ua.stetsenko.Notepad2.Constants;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "notepad";

    //table notes
    private static final String TABLE_NOTES = "notes";

    //notes table columns name
    public static final String TYPE = "type";
    public static final String CONTENT = "content";
    public static final String DATE_TIME = "date_time";
    public static final String URI_RESOURCE = "uri_resource";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + _ID + " INTEGER PRIMARY KEY ," + TYPE + " TEXT,"      //autoincrement
                + CONTENT + " TEXT," + DATE_TIME + " DATETIME," + URI_RESOURCE + " TEXT" + ")";
        db.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    public int addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(_ID, note.getId());
        values.put(TYPE, note.getType());
        values.put(CONTENT, note.getContent());
        values.put(DATE_TIME, note.getDateTime());
        values.put(URI_RESOURCE, note.getUriResource());

        int newNoteId = (int) db.insert(TABLE_NOTES, null, values);
        db.close();
        return newNoteId;
    }

//    public List<String> getAllContent() {
//        List<String> contentList = new ArrayList<String>();
//        String selectQuery = "SELECT " + CONTENT + " FROM " + TABLE_NOTES;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        if (cursor.moveToFirst()) {
//            do {
//                contentList.add(cursor.getString(0));
//            } while (cursor.moveToNext());
//        }
//        db.close();
//        return contentList;
//    }

    public List<Note> getAllNotes() {
        List<Note> noteList = new ArrayList<Note>();
        String selectQuery = "SELECT * FROM " + TABLE_NOTES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getInt(Constants.NOTE_ID));
                note.setType(cursor.getInt(Constants.NOTE_TYPE));
                note.setContent(cursor.getString(Constants.NOTE_CONTENT));
                note.setDateTime(cursor.getString(Constants.NOTE_DATE_TIME));
                note.setUriResource(cursor.getString(Constants.NOTE_URI_RESOURCE));
                noteList.add(note);
            } while (cursor.moveToNext());
        }
        db.close();
        return noteList;
    }

    public Note getNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NOTES + " WHERE " + _ID + " = " + id;
        Note note = null;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            note = new Note();
            note.setId(cursor.getInt(Constants.NOTE_ID));
            note.setType(cursor.getInt(Constants.NOTE_TYPE));
            note.setContent(cursor.getString(Constants.NOTE_CONTENT));
            note.setDateTime(cursor.getString(Constants.NOTE_DATE_TIME));
            note.setUriResource(cursor.getString(Constants.NOTE_URI_RESOURCE));
        }
        db.close();
        return note;
    }

    public int deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NOTES, _ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    public int deleteAllNotes() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NOTES, null, null);
        db.close();
        return result;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TYPE, note.getType());
        values.put(CONTENT, note.getContent());
        values.put(DATE_TIME, note.getDateTime());
        values.put(URI_RESOURCE, note.getUriResource());
        int result = db.update(TABLE_NOTES, values, _ID + " = ?", new String[]{String.valueOf(note.getId())});
        db.close();
        return result;
    }

    public void printToLogAllDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NOTES;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Log.d(Constants.LOG,
                        "id: " + cursor.getInt(Constants.NOTE_ID)
                        + " type: " + cursor.getInt(Constants.NOTE_TYPE)
                        + " content: " + cursor.getString(Constants.NOTE_CONTENT)
                        + " date/time: " + cursor.getString(Constants.NOTE_DATE_TIME)
                        + " uri resource: " + cursor.getString(Constants.NOTE_URI_RESOURCE)
                        + "\n"
                );

            } while (cursor.moveToNext());
        }
        db.close();

    }

    public Note getFirstNote(){
        Note note = null;
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NOTES + " LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            note = new Note();
            note.setId(cursor.getInt(Constants.NOTE_ID));
            note.setType(cursor.getInt(Constants.NOTE_TYPE));
            note.setContent(cursor.getString(Constants.NOTE_CONTENT));
            note.setDateTime(cursor.getString(Constants.NOTE_DATE_TIME));
            note.setUriResource(cursor.getString(Constants.NOTE_URI_RESOURCE));
        }
        db.close();
        return note;
    }

    public int getNextId(){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT "+ _ID +" FROM " + TABLE_NOTES + " ORDER BY "+ _ID +" DESC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        int result = -1;
        if (cursor.moveToFirst()) {
            result = cursor.getInt(Constants.NOTE_ID);
            Log.d(Constants.LOG, "DB, last id result: "+result);
        }
        db.close();
        return result+1;
    }
}
