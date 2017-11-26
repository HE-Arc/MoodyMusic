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

    public void initializeDatabaseWithSongs() {
        //creating selection for the database
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        final String[] projection = new String[]{
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA};

        //creating sort by for database
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE
                + " COLLATE LOCALIZED ASC";

        //stating pointer
        Cursor cursor = null;

        try {
            //the table for query
            Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            // query the db
            cursor = mContext.getContentResolver().query(uri, projection, selection, null, sortOrder);

            if (cursor != null) {


                //go to the first row
                cursor.moveToFirst();


                while (!cursor.isAfterLast()) {
                    //collecting song information and store in array,
                    //moving to the next row
                    Log.w(TAG, cursor.getString(0));
                    Log.w(TAG, cursor.getString(1));
                    Log.w(TAG, cursor.getString(2));
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

//    private void populateMusicData(File file) {
//        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//        mmr.setDataSource(file.getAbsolutePath());
//
//        String name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
//        String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
//        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//
//        if (name != null && !name.equals("")) this.name = name;
//        if (artist != null && !artist.equals("")) this.artist = artist;
//        if (album != null && !album.equals("")) this.album = album;
//        if (duration != null && !duration.equals("")) {
//            this.duration = duration;
//            this.time = Integer.parseInt(this.duration) / 1000;
//        }
//    }
}
