package ch.hearc.moodymusic.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by axel.rieben on 21.11.2017.
 * Class giving methods to easily access the data of the table MoodPlaylist.
 */

public class MoodPlaylistDataSource extends DataSource {
    public static final String TAG = "MoodPlaylistDataSource";

    private String[] mAllColumns = {DatabaseHandler.MOOD_PLAYLIST_ID, DatabaseHandler.MOOD_PLAYLIST_NAME};

    public MoodPlaylistDataSource(Context context) {
        super(context);
    }

    public MoodPlaylist createMoodPlaylist(String name) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.MOOD_PLAYLIST_NAME, name);
        long insertId = mDatabase.insert(DatabaseHandler.TABLE_MOOD_PLAYLIST, null, values);

        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_MOOD_PLAYLIST, mAllColumns, DatabaseHandler.MOOD_PLAYLIST_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        MoodPlaylist newMoodPlaylist = cursorToMoodPlaylist(cursor);
        cursor.close();

        return newMoodPlaylist;
    }

    public long getOrCreate(String name) {
        long id = getMoodPlaylistId(name);

        if (id == 0) {
            MoodPlaylist moodPlaylist = createMoodPlaylist(name);
            return moodPlaylist.getId();
        } else {
            return id;
        }
    }

    public void deleteMoodPlaylist(MoodPlaylist moodPlaylist) {
        long id = moodPlaylist.getId();
        mDatabase.delete(DatabaseHandler.TABLE_MOOD_PLAYLIST, DatabaseHandler.MOOD_PLAYLIST_ID + " = " + id, null);
    }

    public long getMoodPlaylistId(String name) {
        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_MOOD_PLAYLIST, mAllColumns, DatabaseHandler.MOOD_PLAYLIST_NAME + " = ?",
                new String[]{name}, null, null, null);

        long id = 0;
        if (cursor.moveToFirst()) {
            MoodPlaylist moodPlaylist = cursorToMoodPlaylist(cursor);
            id = moodPlaylist.getId();
        }

        cursor.close();
        return id;
    }

    public String getMoodPlaylistName(long id) {
        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_MOOD_PLAYLIST, mAllColumns, DatabaseHandler.MOOD_PLAYLIST_ID + " = " + id,
                null, null, null, null);

        String name = "";

        if (cursor.moveToFirst()) {
            MoodPlaylist moodPlaylist = cursorToMoodPlaylist(cursor);
            name = moodPlaylist.getName();
        }

        cursor.close();
        return name;
    }

    public ArrayList<MoodPlaylist> getMoodPlaylistList() {
        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_MOOD_PLAYLIST, mAllColumns, null,
                null, null, null, null);
        ArrayList<MoodPlaylist> listMoodPlaylist = new ArrayList<MoodPlaylist>();

        while (cursor.moveToNext()) {
            MoodPlaylist moodPlaylist = cursorToMoodPlaylist(cursor);
            listMoodPlaylist.add(moodPlaylist);
        }

        cursor.close();
        return listMoodPlaylist;
    }

    public int numMoodPlaylist() {
        Cursor cursor = mDatabase.rawQuery("SELECT count(*) FROM " + DatabaseHandler.TABLE_MOOD_PLAYLIST, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        return count;
    }

    public void showTable() {
        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_MOOD_PLAYLIST, mAllColumns, null,
                null, null, null, null, null);

        cursor.moveToFirst();

        Log.w(TAG, "Table MoodPlaylist");
        while (!cursor.isAfterLast()) {
            MoodPlaylist moodPlaylist = cursorToMoodPlaylist(cursor);
            Log.w(TAG, "Id : " + moodPlaylist.getId() + " Name : " + moodPlaylist.getName());
            cursor.moveToNext();
        }
        cursor.close();
    }

    private MoodPlaylist cursorToMoodPlaylist(Cursor cursor) {
        return new MoodPlaylist(cursor.getLong(0), cursor.getString(1));
    }
}
