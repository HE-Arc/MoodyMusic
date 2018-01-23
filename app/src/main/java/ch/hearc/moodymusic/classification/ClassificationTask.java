package ch.hearc.moodymusic.classification;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

import ch.hearc.moodymusic.R;
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

    //Input
    private Context mContext;

    //Dialog
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;

    //Data
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

        mProgressDialog.setMessage(mContext.getString(R.string.dialog_refresh));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAlertDialog = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog).create();
        } else {
            mAlertDialog = new AlertDialog.Builder(mContext).create();
        }

        mSongDataSource.open();
        mMoodPlaylistDataSource.open();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        mSongDataSource.refreshSongTable();

        try {
            /* TODO register and store user id in cache on first app launch */
            GracenoteWebAPI api = new GracenoteWebAPI(GRACE_CLIENT_ID, GRACE_CLIENT_TAG, GRACE_USER_ID);
            api.register();
            GracenoteMetadata results;

            Song[] newSongs = mSongDataSource.getSongWithNullMood(100);

            for (Song newSong : newSongs) {
                String artist = removeSpecialCharacters(newSong.getArtist());
                String title = removeSpecialCharacters(newSong.getTitle());

                if (!artist.isEmpty() && !title.isEmpty()) {
                    results = api.searchTrack(artist, "", title);

                    if (results != null) {
                        ArrayList<GracenoteMetadataOET> moods = (ArrayList<GracenoteMetadataOET>) results.getAlbumData(0, "mood");

                        switch (moods.size()) {
                            case 0:
                                Log.w(TAG, title + " no mood ");
                                setOtherMoodSong(newSong);
                                break;
                            case 1:
                                Log.w(TAG, title + " one mood : " + moods.get(0).getText());
                                updateMoodFromSong(newSong, moods.get(0).getText());
                                break;
                            case 2:
                                Log.w(TAG, title + " " + " two mood : " + moods.get(0).getText() + " " + moods.get(1).getText());
                                updateMoodFromSong(newSong, moods.get(0).getText());
                                break;
                            default:
                                Log.w(TAG, title + " error");
                                setOtherMoodSong(newSong);
                        }
                    } else {
                        setOtherMoodSong(newSong);
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
        Log.w(TAG, "Update song : " + song.getTitle() + " mood : " + mood);
        long moodId = mMoodPlaylistDataSource.getMoodPlaylistId(mood);
        mSongDataSource.updateMood(song.getId(), moodId);
    }

    private void setOtherMoodSong(Song song) {
        Log.w(TAG, "Update song : " + song.getTitle() + " mood : Other");
        long moodId = mMoodPlaylistDataSource.getMoodPlaylistId("Other");
        mSongDataSource.updateMood(song.getId(), moodId);
    }

    /**
     * Remove special characters that generate errors
     *
     * @param input
     * @return
     */
    private String removeSpecialCharacters(String input) {
        final String[] metaCharacters = {"\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|", "<", ">", "-", "&"};

        for (int i = 0; i < metaCharacters.length; i++) {
            if (input.contains(metaCharacters[i])) {
                input.replace(metaCharacters[i], "");

            }
        }

        return input;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        mSongDataSource.close();
        mMoodPlaylistDataSource.close();

        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if (result == false) {
            //Show error dialog
            mAlertDialog.setTitle("Sorry");
            mAlertDialog.setMessage("An error has occured during refresh !");

            mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Nothing
                        }
                    });

            mAlertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            mAlertDialog.show();
        }
    }
}
