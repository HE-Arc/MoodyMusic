package ch.hearc.moodymusic.classification;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import ch.hearc.moodymusic.model.SongDataSource;

/**
 * Created by axel.rieben on 21.11.2017.
 */

public class ClassificationEngine {

    public static final String TAG = "ClassificationEngine";

    private Context mContext;
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

                String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";// AND " + MediaStore.Audio.Media.DATA + " LIKE ? ";

                final String[] projection = new String[]{
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DURATION}; //DATA = PATH

                //stating pointer
                Cursor cursor = null;

                try {
                    //the table for query
                    Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                    //Query a specific folder
                    String pathTest = "/storage/0C04-3890/Test/*";
                    Log.w(TAG, "Path : " + pathTest);
//            Uri uri = android.provider.MediaStore.Audio.Media.getContentUriForPath(path);

                    // query the content provider
                    cursor = mContext.getContentResolver().query(uri, projection, selection, new String[]{}, null);

                    if (cursor != null) {
                        int i = 0;
                        cursor.moveToFirst();

                        while (!cursor.isAfterLast()) {
                            //collecting song information and store in array
                            Log.w(TAG, cursor.getString(0) + " "
                                    + cursor.getString(1) + " "
                                    + cursor.getString(2) + " "
                                    + cursor.getString(3) + " "
                                    + cursor.getString(4));

                            if (Integer.parseInt(cursor.getString(4)) > minDuration) {

                                mSongDataSource.createSong(cursor.getString(3), cursor.getString(0), cursor.getString(1),
                                        cursor.getString(2), null, null);
                            }
                            cursor.moveToNext();
                            i++;
                        }

                        Log.w(TAG, "Number of music : " + i);
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
