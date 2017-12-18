package ch.hearc.moodymusic.player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import ch.hearc.moodymusic.MainActivity;
import ch.hearc.moodymusic.R;
import ch.hearc.moodymusic.model.Song;

/**
 * Created by axel.rieben on 12.12.2017.
 */

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    public static final String TAG = "MusicService";

    //Notification
    private String songTitle;
    private static final int NOTIFY_ID = 1;

    //Media player
    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> mlistSongs;
    private int mSongPosition;
    private AudioManager audioManager;

    //Shuffle
    private boolean shuffle = false;
    private Random rand;

    //Binder
    private final IBinder musicBind = new MusicBinder();

    public void onCreate() {
        super.onCreate();
        mSongPosition = 0;
        mMediaPlayer = new MediaPlayer();

        initMusicPlayer();
        rand = new Random();
    }

    public void initMusicPlayer() {
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs) {
        mlistSongs = theSongs;
    }

    public void setSong(int songIndex) {
        mSongPosition = songIndex;
    }

    public void setShuffle() {
        if (shuffle) {
            shuffle = false;
        } else {
            shuffle = true;
        }
    }

    public void playSong() {
        mMediaPlayer.reset();
        Song playSong = mlistSongs.get(mSongPosition);
        songTitle = playSong.getTitle();
        String currSong = playSong.getPath();
//        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);

        try {
            mMediaPlayer.setDataSource(getApplicationContext(), Uri.parse(currSong));
        } catch (Exception e) {
            Log.w(TAG, "Error while setting data source", e);
        }

        mMediaPlayer.prepareAsync();
    }

    public int getPosn() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDur() {
        return mMediaPlayer.getDuration();
    }

    public boolean isPng() {
        return mMediaPlayer.isPlaying();
    }

    public void pausePlayer() {
        mMediaPlayer.pause();
    }

    public void seek(int posn) {
        mMediaPlayer.seekTo(posn);
    }

    public void go() {
        mMediaPlayer.start();
    }

    public void playPrev() {
        mSongPosition--;
        if (mSongPosition < 0) {
            mSongPosition = mlistSongs.size() - 1;
        }
        playSong();
    }

    public void playNext() {
        if (shuffle) {
            int newSong = mSongPosition;
            while (newSong == mSongPosition) {
                newSong = rand.nextInt(mlistSongs.size());
            }
            mSongPosition = newSong;
        } else {
            mSongPosition++;
            if (mSongPosition >= mlistSongs.size()) {
                mSongPosition = 0;
            }
        }
        playSong();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Nullable
    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(mediaPlayer.getCurrentPosition() > 0){
            mediaPlayer.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();

        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }
}
