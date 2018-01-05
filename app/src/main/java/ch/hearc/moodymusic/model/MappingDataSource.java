package ch.hearc.moodymusic.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.Random;

/**
 * Created by axel.rieben on 21.11.2017.
 */

public class MappingDataSource extends DataSource {
    public static final String TAG = "MappingDataSource";

    private String[] mAllColumns = {DatabaseHandler.MAPPING_ID, DatabaseHandler.MAPPING_MOOD_ID, DatabaseHandler.MAPPING_MOOD_PLAYLIST_ID};

    public MappingDataSource(Context context) {
        super(context);
    }

    public Mapping createMapping(long moodId, long moodPlaylistId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.MAPPING_MOOD_ID, moodId);
        values.put(DatabaseHandler.MAPPING_MOOD_PLAYLIST_ID, moodPlaylistId);
        long insertId = mDatabase.insert(DatabaseHandler.TABLE_MAPPING, null, values);

        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_MAPPING, mAllColumns, DatabaseHandler.MAPPING_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();

        Mapping newMapping = cursorToMapping(cursor);
        cursor.close();

        return newMapping;
    }

    public void deleteMapping(Mapping mapping) {
        long id = mapping.getId();
        mDatabase.delete(DatabaseHandler.TABLE_MAPPING, DatabaseHandler.MAPPING_ID + " = " + id, null);
    }

    public int numMapping() {
        Cursor cursor = mDatabase.rawQuery("SELECT count(*) FROM " + DatabaseHandler.TABLE_MAPPING, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        return count;
    }

    public long map(long moodId){
        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_MAPPING, mAllColumns, DatabaseHandler.MAPPING_MOOD_ID + " = " + moodId,
                null, null, null, null);

        Random random = new Random();
        int count = cursor.getCount();
        int randMoodPlaylist = random.nextInt(count);
        long moodPlaylistId = 0;

        if (cursor.move(randMoodPlaylist)) {
            moodPlaylistId = cursor.getLong(2);
        }

        cursor.close();
        return moodPlaylistId;
    }

    public void showTable() {
        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_MAPPING, mAllColumns, null,
                null, null, null, null, null);

        cursor.moveToFirst();

        Log.w(TAG, "Table Mapping");
        while (!cursor.isAfterLast()) {
            Mapping mapping = cursorToMapping(cursor);
            Log.w(TAG, "Id : " + mapping.getId() + " Mood : " + mapping.getMoodId() + " MoodPlaylist : " + mapping.getMoodPlaylistId());
            cursor.moveToNext();
        }
        cursor.close();
    }

    private Mapping cursorToMapping(Cursor cursor) {
        Mapping mapping = new Mapping(cursor.getLong(0), cursor.getLong(1), cursor.getLong(2));
        return mapping;
    }
}
