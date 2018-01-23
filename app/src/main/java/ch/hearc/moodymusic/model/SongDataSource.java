package ch.hearc.moodymusic.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by axel.rieben on 21.11.2017.
 * Class giving methods to easily access the data of the table Song.
 */

public class SongDataSource extends DataSource {
    public static final String TAG = "SongDataSource";

    private String[] mAllColumns = {DatabaseHandler.SONG_ID, DatabaseHandler.SONG_PATH, DatabaseHandler.SONG_ARTIST,
            DatabaseHandler.SONG_TITLE, DatabaseHandler.SONG_ALBUM,
            DatabaseHandler.SONG_MOOD_PLAYLIST_ID, DatabaseHandler.SONG_USER_MOOD_PLAYLIST_ID};

    private final Uri URI_MEDIASTORE = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    public SongDataSource(Context context) {
        super(context);
    }

    public Song createSong(String path, String artist, String title, String album, Long moodId, Long userMoodId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.SONG_PATH, path);
        values.put(DatabaseHandler.SONG_ARTIST, artist);
        values.put(DatabaseHandler.SONG_TITLE, title);
        values.put(DatabaseHandler.SONG_ALBUM, album);
        values.put(DatabaseHandler.SONG_MOOD_PLAYLIST_ID, moodId);
        values.put(DatabaseHandler.SONG_USER_MOOD_PLAYLIST_ID, userMoodId);
        long insertId = mDatabase.insert(DatabaseHandler.TABLE_SONG, null, values);

        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_SONG, mAllColumns, DatabaseHandler.SONG_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        Song newSong = cursorToSong(cursor);
        cursor.close();

        return newSong;
    }

    public void deleteSong(Song song) {
        long id = song.getId();
        mDatabase.delete(DatabaseHandler.TABLE_SONG, DatabaseHandler.SONG_ID + " = " + id, null);
    }

    public void updateMood(long songId, long moodId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.SONG_MOOD_PLAYLIST_ID, moodId);
        mDatabase.update(DatabaseHandler.TABLE_SONG, values, DatabaseHandler.SONG_ID + " = " + songId, null);
    }

    public Song[] getSongWithNullMood(int limit) {
        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_SONG, mAllColumns, DatabaseHandler.SONG_MOOD_PLAYLIST_ID + " IS NULL",
                null, null, null, null, Integer.toString(limit));

        Song[] arraySong = new Song[cursor.getCount()];
        cursor.moveToFirst();
        int i = 0;

        while (!cursor.isAfterLast()) {
            arraySong[i] = cursorToSong(cursor);
            cursor.moveToNext();
            i++;
        }
        cursor.close();

        return arraySong;
    }

    public ArrayList<Song> getSongListByMoodId(long moodId) {
        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_SONG, mAllColumns, DatabaseHandler.SONG_MOOD_PLAYLIST_ID + " = ?",
                new String[]{Long.toString(moodId)}, null, null, null);
        ArrayList<Song> listSong = new ArrayList<Song>(cursor.getCount());

        while (cursor.moveToNext()) {
            Song song = cursorToSong(cursor);
            listSong.add(song);
        }

        cursor.close();
        return listSong;
    }

    public int numSong() {
        Cursor cursor = mDatabase.rawQuery("SELECT count(*) FROM " + DatabaseHandler.TABLE_SONG, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        return count;
    }

    public int numSongInPlaylist(long moodPlaylistId) {
        Cursor cursor = mDatabase.rawQuery("SELECT count(*) FROM " + DatabaseHandler.TABLE_SONG + " WHERE " + DatabaseHandler.SONG_MOOD_PLAYLIST_ID + " = " + moodPlaylistId, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        return count;
    }

    public void refreshSongTable() {
        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_SONG, mAllColumns, null,
                null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Song song = cursorToSong(cursor);
            removeIfNotOnPhone(song);
            cursor.moveToNext();
        }

        cursor.close();

        addSongsFromMediaStore(0);
    }

    private void addSongsFromMediaStore(int minDuration) {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        final String[] projection = new String[]{
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION}; //DATA = PATH
        Cursor cursor = null;

        try {
            cursor = mContext.getContentResolver().query(URI_MEDIASTORE, projection, selection, new String[]{}, null);

            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    if (Integer.parseInt(cursor.getString(4)) > minDuration) {
                        if (!songExist(cursor.getString(3))) {
                            createSong(cursor.getString(3), cursor.getString(0), cursor.getString(1),
                                    cursor.getString(2), null, null);
                        }
                    }
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void removeIfNotOnPhone(Song song) {
        String selection = MediaStore.Audio.Media.DATA + " = ?";

        final String[] projection = new String[]{MediaStore.Audio.Media.DATA,}; //DATA = PATH
        Cursor cursor = null;

        try {
            cursor = mContext.getContentResolver().query(URI_MEDIASTORE, projection, selection, new String[]{song.getPath()}, null);

            if (cursor.getCount() == 0) {
                deleteSong(song);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean songExist(String path) {
        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_SONG, mAllColumns, DatabaseHandler.SONG_PATH + " = ?",
                new String[]{path}, null, null, null, null);

        if (cursor.getCount() == 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    public void clearSongs() {
        mDatabase.execSQL("DELETE FROM " + DatabaseHandler.TABLE_SONG);
    }

    public void showTable() {
        Cursor cursor = mDatabase.query(DatabaseHandler.TABLE_SONG, mAllColumns, null,
                null, null, null, null, null);

        cursor.moveToFirst();

        Log.w(TAG, "Table Song");
        while (!cursor.isAfterLast()) {
            Song song = cursorToSong(cursor);
            Log.w(TAG, "Id : " + song.getId() + " Path : " + song.getPath() + " Artist : " + song.getArtist() + " Title : " + song.getTitle() + " MoodId : " + song.getMoodPlaylistId());
            cursor.moveToNext();
        }
        cursor.close();
    }

    private Song cursorToSong(Cursor cursor) {
        return new Song(cursor.getLong(0), cursor.getString(1),
                cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getLong(5), cursor.getLong(6));
    }
}
