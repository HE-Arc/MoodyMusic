package ch.hearc.moodymusic.classification;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import ch.hearc.moodymusic.model.MoodPlaylistDataSource;
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
    private MoodPlaylistDataSource mMoodPlaylistDataSource;

    public ClassificationTask(Context context) {
        mProgressDialog = new ProgressDialog(context);
        mSongDataSource = new SongDataSource(context);
        mMoodPlaylistDataSource = new MoodPlaylistDataSource(context);
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mProgressDialog.setMessage("Playlists are being refreshed, this may take a moment...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        mError = "";

        mSongDataSource.open();
        mMoodPlaylistDataSource.open();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        mSongDataSource.refreshSongTable();

        try {
            /* You first need to register your client information in order to get a userID.
            Best practice is for an application to call this only once, and then cache the userID in
            persistent storage, then only use the userID for subsequent API calls. The class will cache
            it for just this session on your behalf, but TODO you should store it yourself. */

            GracenoteWebAPI api = new GracenoteWebAPI(GRACE_CLIENT_ID, GRACE_CLIENT_TAG, GRACE_USER_ID);
            String userID = api.register();
            GracenoteMetadata results;

            Song[] newSongs = mSongDataSource.getSongWithNullMood(50);

            for (int i = 0; i < newSongs.length; i++) {
                String artist = removeSpecialCharacters(newSongs[i].getArtist());
                String title = removeSpecialCharacters(newSongs[i].getTitle());
                String album = newSongs[i].getAlbum();

                if (!artist.isEmpty() && !title.isEmpty()) {
                    results = api.searchTrack(artist, "", title);

                    if(results != null) {
                        ArrayList<GracenoteMetadataOET> moods = (ArrayList<GracenoteMetadataOET>) results.getAlbumData(0, "mood");

                        switch (moods.size()) {
                            case 0:
                                Log.w(TAG, title + " no mood ");
                                setOtherMoodSong(newSongs[i]);
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
                                setOtherMoodSong(newSongs[i]);
                        }
                    }
                    else
                    {
                        setOtherMoodSong(newSongs[i]);
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
        Log.w(TAG, "Update song : " + song + " mood : " + mood);
        long moodId = mMoodPlaylistDataSource.getMoodPlaylistId(mood);
        mSongDataSource.updateMood(song.getId(), moodId);
    }

    private void setOtherMoodSong(Song song) {
        Log.w(TAG, "Update song : " + song + " mood : Other");
        long moodId = mMoodPlaylistDataSource.getMoodPlaylistId("Other");
        mSongDataSource.updateMood(song.getId(), moodId);
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
