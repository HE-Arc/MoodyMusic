package ch.hearc.moodymusic.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by axel.rieben on 21.11.2017.
 */

public class MoodDataSource extends DataSource {
    public static final String TAG = "MoodDataSource";

    private String[] mAllColumns = {DatabaseHandler.MOOD_ID, DatabaseHandler.MOOD_NAME};

    public MoodDataSource(Context context) {
        super(context);
    }

    public Mood createMood(String name) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.MOOD_NAME, name);
        long insertId = mDatabase.insert(DatabaseHandler.TABLE_MOOD, null, values);

        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_MOOD, mAllColumns, DatabaseHandler.MOOD_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        Mood newMood = cursorToMood(cursor);
        cursor.close();

        return newMood;
    }

    public long getOrCreate(String name) {
        long id = getMoodId(name);

        if (id == 0) {
            Mood mood = createMood(name);
            return mood.getId();
        } else {
            return id;
        }
    }

    public void deleteMood(Mood mood) {
        long id = mood.getId();
        mDatabase.delete(DatabaseHandler.TABLE_MOOD, DatabaseHandler.MOOD_ID + " = " + id, null);
    }

    public long getMoodId(String name) {
        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_MOOD, new String[]{DatabaseHandler.MOOD_ID, DatabaseHandler.MOOD_NAME}, DatabaseHandler.MOOD_NAME + " LIKE ?",
                new String[]{name}, null, null, null);

        long id = 0;
        if (cursor.moveToFirst()) {
            id = cursor.getLong(0);
        }

        cursor.close();
        return id;
    }

    public int numMood() {
        Cursor cursor = mDatabase.rawQuery("SELECT count(*) FROM " + DatabaseHandler.TABLE_MOOD, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        return count;
    }

    public void showTable() {
        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_MOOD, mAllColumns, null,
                null, null, null, null, null);

        cursor.moveToFirst();

        Log.w(TAG, "Table Mood");
        while (!cursor.isAfterLast()) {
            Mood mood = cursorToMood(cursor);
            Log.w(TAG, "Id : " + mood.getId() + " Name : " + mood.getName());
            cursor.moveToNext();
        }
        cursor.close();
    }

    private Mood cursorToMood(Cursor cursor) {
        Mood mood = new Mood(cursor.getLong(0), cursor.getString(1));
        return mood;
    }
}
