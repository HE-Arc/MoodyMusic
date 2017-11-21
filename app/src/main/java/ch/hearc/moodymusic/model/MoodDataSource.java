package ch.hearc.moodymusic.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

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

    public void deleteMood(Mood mood) {
        long id = mood.getId();
        System.out.println("Mood deleted with id: " + id);
        mDatabase.delete(DatabaseHandler.TABLE_MOOD, DatabaseHandler.TABLE_MOOD + " = " + id, null);
    }

    private Mood cursorToMood(Cursor cursor) {
        Mood mood = new Mood(cursor.getLong(0), cursor.getString(1));
        return mood;
    }
}
