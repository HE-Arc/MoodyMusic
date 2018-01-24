package ch.hearc.moodymusic.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.hearc.moodymusic.R;
import ch.hearc.moodymusic.model.MappingDataSource;
import ch.hearc.moodymusic.model.MoodDataSource;
import ch.hearc.moodymusic.model.MoodPlaylist;
import ch.hearc.moodymusic.model.MoodPlaylistDataSource;
import ch.hearc.moodymusic.model.Song;
import ch.hearc.moodymusic.model.SongDataSource;
import ch.hearc.moodymusic.player.MusicController;
import ch.hearc.moodymusic.player.MusicService;
import ch.hearc.moodymusic.player.MusicService.MusicBinder;
import ch.hearc.moodymusic.tools.PermissionDialog;
import ch.hearc.moodymusic.ui.player.MoodAdapter;
import ch.hearc.moodymusic.ui.player.SongAdapter;

/**
 * Created by axel.rieben on 29.10.2017.
 */

public class PlayerFragment extends Fragment implements MediaPlayerControl {

    public static final String TAG = "PlayerFragment";

    private static final int REQUEST_READ_PERMISSION = 2;
    private static final String FRAGMENT_DIALOG = "dialog";

    //Data
    private MoodPlaylistDataSource mMoodPlaylistDataSource;
    private SongDataSource mSongDataSource;
    private MoodDataSource mMoodDataSource;
    private MappingDataSource mMappingDataSource;
    private ArrayList<MoodPlaylist> mListMoodPlaylist;
    private ArrayList<Song> mListSong;

    //Music service
    private MusicService mMusicService;
    private Intent mIntentService;
    private boolean mIsBound = false;

    private ServiceConnection mMusicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder) service;
            mMusicService = binder.getService();
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;
        }
    };

    //Music player
    private MusicController mController;
    private boolean mPaused = false;
    private boolean mPlaybackPaused = false;

    //UI and listeners
    private boolean isShowingSongs = false;
    private ListView mListView;
    private AlertDialog mAlertDialog;

    private AdapterView.OnItemClickListener mListenerMood = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            initListWithSong(mListMoodPlaylist.get(position).getId());
            mMusicService.setList(mListSong);
        }
    };

    private AdapterView.OnItemClickListener mListenerSong = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            songPicked(position);
        }
    };

    private View.OnTouchListener mListenerScrollView = new View.OnTouchListener() {
        float y0 = 0;
        float y1 = 0;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                y0 = motionEvent.getY();

                if (y1 - y0 > 0) {
                    mController.myHide();

                } else if (y1 - y0 < 0) {
                    if (!mController.isShowing()) {
                        mController.show();
                    }
                }
                y1 = motionEvent.getY();
            }
            return false;
        }
    };

    View.OnKeyListener mListenerKey = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (isShowingSongs) {
                    initListWithMood();
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_player, container, false);

        mListView = (ListView) view.findViewById(R.id.list_player);
        mListView.setOnTouchListener(mListenerScrollView);

        mMoodPlaylistDataSource = new MoodPlaylistDataSource(getContext());
        mSongDataSource = new SongDataSource(getContext());
        mMoodDataSource = new MoodDataSource(getContext());
        mMappingDataSource = new MappingDataSource(getContext());

        setControllerView(view);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(mListenerKey);

        initListWithMood();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAlertDialog = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog).create();
        } else {
            mAlertDialog = new AlertDialog.Builder(getContext()).create();
        }

        initServiceIntent();

        return view;
    }

    /**
     * Launch playlist after a detection for a giving mood
     * @param mood
     */
    public void launchPlaylist(String mood) {
        mMoodDataSource.open();
        mMappingDataSource.open();

        long moodId = mMoodDataSource.getMoodId(mood);
        List<Long> moodPlaylistIds = mMappingDataSource.map(moodId);

        mMoodDataSource.close();
        mMappingDataSource.close();

        Random random = new Random();
        boolean playlistEmpty = true;

        //Loop in all the playlists available for the detected mood and randomly try to launch one.
        while (moodPlaylistIds.size() != 0 && playlistEmpty) {
            initListWithMood();
            int randListIndex = random.nextInt(moodPlaylistIds.size());
            int position = moodPlaylistIds.get(randListIndex).intValue() - 1; //-1 to get the correct position in the listview
            mListView.performItemClick(mListView.getAdapter().getView(position, null, null), position, mListView.getAdapter().getItemId(position));

            if (!mListView.getAdapter().isEmpty()) {
                playlistEmpty = false;
                mListView.performItemClick(mListView.getAdapter().getView(0, null, null), 0, mListView.getAdapter().getItemId(0));

                mMoodPlaylistDataSource.open();
                String name = mMoodPlaylistDataSource.getMoodPlaylistName(moodPlaylistIds.get(randListIndex));
                mMoodPlaylistDataSource.close();
                dialogPlaylist(name);
            } else {
                moodPlaylistIds.remove(randListIndex);
            }
        }

        if (playlistEmpty) {
            initListWithMood();
            dialogError();
        }
    }

    public void hideController() {
        if (mController != null) {
            mController.myHide();
        }
    }

    public void showController() {
        if (mController != null && mController.isShowing() != true) {
            mController.show(0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initServiceIntent();
    }

    @Override
    public void onStop() {
        mController.myHide();
        getActivity().stopService(mIntentService);
        mMusicService = null;
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().stopService(mIntentService);
        mMusicService = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //TODO Load data
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            PermissionDialog.ConfirmationDialogFragment
                    .newInstance(R.string.read_permission_confirmation,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_PERMISSION,
                            R.string.read_permission_not_granted)
                    .show(getActivity().getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
        }

        if (mPaused) {
            setControllerView(getView());
            mPaused = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting read permission.");
                }

                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), R.string.read_permission_not_granted,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void start() {
        mMusicService.go();
    }

    @Override
    public void pause() {
        mPlaybackPaused = true;
        mMusicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (mMusicService != null && mIsBound && mMusicService.isPng()) {
            return mMusicService.getDur();
        } else {
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        if (mMusicService != null && mIsBound && mMusicService.isPng()) {
            return mMusicService.getPosn();
        } else {
            return 0;
        }
    }

    @Override
    public void seekTo(int pos) {
        mMusicService.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if (mMusicService != null && mIsBound) {
            return mMusicService.isPng();
        } else {
            return false;
        }
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public void songPicked(int position) {
        mMusicService.setSong(position);
        mMusicService.playSong();

        if (mPlaybackPaused) {
            setControllerView(getView());
            mPlaybackPaused = false;
        }

        showController();
    }

    private void initServiceIntent() {
        if (mIntentService == null) {
            mIntentService = new Intent(getActivity(), MusicService.class);
            getActivity().bindService(mIntentService, mMusicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(mIntentService);
        }
    }

    private void dialogPlaylist(String name) {
        mAlertDialog.setTitle("Playlist launch");
        mAlertDialog.setMessage("Playlist " + name + " is currently playing, enjoy !");

        mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        mAlertDialog.show();
    }

    private void dialogError() {
        mAlertDialog.setTitle("No match");
        mAlertDialog.setMessage("You have no songs that fit your current mood, try to add new songs on your phone and refresh the playlists !");

        mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        mAlertDialog.show();
    }

    private void initListWithMood() {
        mMoodPlaylistDataSource.open();
        mListMoodPlaylist = mMoodPlaylistDataSource.getMoodPlaylistList();
        MoodAdapter moodAdapter = new MoodAdapter(getContext(), R.layout.player_list_item, mListMoodPlaylist);
        mListView.setAdapter(moodAdapter);
        mListView.setOnItemClickListener(mListenerMood);
        isShowingSongs = false;
        mMoodPlaylistDataSource.close();
    }

    private void initListWithSong(long moodId) {
        mSongDataSource.open();
        mListSong = mSongDataSource.getSongListByMoodId(moodId);
        SongAdapter songAdapter = new SongAdapter(getContext(), R.layout.player_list_item, mListSong);
        mListView.setAdapter(songAdapter);
        mListView.setOnItemClickListener(mListenerSong);
        isShowingSongs = true;
        mSongDataSource.close();
    }

    private void setControllerView(View view) {
        if (mController == null) {
            mController = new MusicController(getActivity());
            mController.setPrevNextListeners(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playNext();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playPrev();
                }
            });
            mController.setMediaPlayer(this);
            mController.setAnchorView(view.findViewById(R.id.list_player));
            mController.setEnabled(true);
        }
    }

    private void playNext() {
        mMusicService.playNext();
        if (mPlaybackPaused) {
            setControllerView(getView());
            mPlaybackPaused = false;
        }
        mController.show(0);
    }

    private void playPrev() {
        mMusicService.playPrev();
        if (mPlaybackPaused) {
            setControllerView(getView());
            mPlaybackPaused = false;
        }
        mController.show(0);
    }
}
