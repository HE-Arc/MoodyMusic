package ch.hearc.moodymusic.classification;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import ch.hearc.moodymusic.model.MoodDataSource;
import ch.hearc.moodymusic.model.Song;
import ch.hearc.moodymusic.model.SongDataSource;
import radams.gracenote.webapi.GracenoteException;
import radams.gracenote.webapi.GracenoteMetadata;
import radams.gracenote.webapi.GracenoteMetadataOET;
import radams.gracenote.webapi.GracenoteWebAPI;

import static ch.hearc.moodymusic.tools.Constants.GRACE_CLIENT_ID;
import static ch.hearc.moodymusic.tools.Constants.GRACE_CLIENT_TAG;
import static ch.hearc.moodymusic.tools.Constants.GRACE_USER_ID;

/**
 * Created by axel.rieben on 27.11.2017.
 */

public class ClassificationTask extends AsyncTask<String, Integer, Boolean> {

    public static final String TAG = "ClassificationTask";

    private ProgressDialog mProgressDialog;
    private String mError;
    private Context mContext;
    private SongDataSource mSongDataSource;
    private MoodDataSource mMoodDataSource;

    public ClassificationTask(Context context) {
        mProgressDialog = new ProgressDialog(context);
        mSongDataSource = new SongDataSource(context);
        mMoodDataSource = new MoodDataSource(context);
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mProgressDialog.setMessage("Playlists are being created");
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();

        mError = "";

        mSongDataSource.open();
        mMoodDataSource.open();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            /* You first need to register your client information in order to get a userID.
            Best practice is for an application to call this only once, and then cache the userID in
            persistent storage, then only use the userID for subsequent API calls. The class will cache
            it for just this session on your behalf, but TODO you should store it yourself. */

            GracenoteWebAPI api = new GracenoteWebAPI(GRACE_CLIENT_ID, GRACE_CLIENT_TAG, GRACE_USER_ID);
            String userID = api.register();
            System.out.println("UserID = " + userID);
            GracenoteMetadata results;

            Song[] newSongs = mSongDataSource.getSongWithNullMood(10);

            for (int i = 0; i < newSongs.length; i++) {
                long songId = newSongs[i].getId();
                String artist = removeSpecialCharacters(newSongs[i].getArtist());
                String title = removeSpecialCharacters(newSongs[i].getTitle());
                String album = newSongs[i].getAlbum();

                if (!artist.isEmpty() && !title.isEmpty()) {
                    results = api.searchTrack(artist, "", title);
                    ArrayList<GracenoteMetadataOET> moods = (ArrayList<GracenoteMetadataOET>) results.getAlbumData(0, "mood");

                    switch (moods.size()) {
                        case 0:
                            Log.w(TAG, title + " no mood ");
                            break;
                        case 1:
                            Log.w(TAG, title + " one mood : " + moods.get(0).getText());
                            updateMoodFromSong(newSongs[i], moods.get(0).getText());
                            break;
                        case 2:
                            Log.w(TAG, title + " " + " two mood : " + moods.get(0).getText() + " " + moods.get(1).getText());
                            updateMoodFromSong(newSongs[i], moods.get(0).getText());
                            break;
                        default:
                            Log.w(TAG, title + " error");
                    }
                }
            }

        } catch (GracenoteException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void updateMoodFromSong(Song song, String mood) {
        Log.w(TAG, "Inserting mood : " + mood);
        long moodId = mMoodDataSource.getOrCreate(mood);
        mSongDataSource.updateMood(song.getId(), moodId);
        Log.w(TAG, "Inserted !");
    }

    private String removeSpecialCharacters(String input) {
        final String[] metaCharacters = {"\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|", "<", ">", "-", "&"};

        for (int i = 0; i < metaCharacters.length; i++) {
            if (input.contains(metaCharacters[i])) {
//                output = input.replace(metaCharacters[i], "\\" + metaCharacters[i]);
                input.replace(metaCharacters[i], "");

            }
        }

        return input;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        mSongDataSource.close();

        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
