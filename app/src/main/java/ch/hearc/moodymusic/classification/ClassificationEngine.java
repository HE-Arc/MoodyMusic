package ch.hearc.moodymusic.classification;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import ch.hearc.moodymusic.model.SongDataSource;

/**
 * Created by axel.rieben on 21.11.2017.
 * This class is not really used for the moment but can be used to init database on the app first launch
 */

public class ClassificationEngine {
    public static final String TAG = "ClassificationEngine";

    //Input
    private Context mContext;

    //Data
    private SongDataSource mSongDataSource;

    public ClassificationEngine(Context context) {
        mContext = context;
        mSongDataSource = new SongDataSource(context);
    }

    public void initializeDatabaseWithSongs(final int minDuration) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                mSongDataSource.open();
                mSongDataSource.clearSongs();
                Log.w(TAG, "Num song in database : " + mSongDataSource.numSong());

                String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

                final String[] projection = new String[]{
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DURATION}; //DATA = PATH
                Cursor cursor = null;

                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                try {
                    cursor = mContext.getContentResolver().query(uri, projection, selection, new String[]{}, null);

                    if (cursor != null) {
                        cursor.moveToFirst();
                        while (!cursor.isAfterLast()) {
                            if (Integer.parseInt(cursor.getString(4)) > minDuration) {
                                mSongDataSource.createSong(cursor.getString(3), cursor.getString(0), cursor.getString(1),
                                        cursor.getString(2), null, null);
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

                Log.w(TAG, "Num song in database : " + mSongDataSource.numSong());
                mSongDataSource.close();
            }
        };

        thread.start();
    }
}
