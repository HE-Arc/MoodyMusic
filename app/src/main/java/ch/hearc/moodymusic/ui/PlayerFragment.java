package ch.hearc.moodymusic.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ch.hearc.moodymusic.R;
import ch.hearc.moodymusic.classification.ClassificationEngine;
import ch.hearc.moodymusic.model.Mood;
import ch.hearc.moodymusic.model.MoodDataSource;
import ch.hearc.moodymusic.model.Song;
import ch.hearc.moodymusic.model.SongDataSource;
import ch.hearc.moodymusic.player.MusicService;
import ch.hearc.moodymusic.player.MusicService.MusicBinder;
import ch.hearc.moodymusic.tools.PermissionDialog;
import ch.hearc.moodymusic.ui.player.MoodAdapter;
import ch.hearc.moodymusic.ui.player.SongAdapter;

/**
 * Created by axel.rieben on 29.10.2017.
 */

public class PlayerFragment extends Fragment {

    public static final String TAG = "PlayerFragment";

    private static final int REQUEST_READ_PERMISSION = 2;
    private static final String FRAGMENT_DIALOG = "dialog";

    //Data
    private MoodDataSource mMoodDataSource;
    private SongDataSource mSongDataSource;
    private ArrayList<Mood> mListMood;
    private ArrayList<Song> mListSong;

    //Music service
    private MusicService mMusicService;
    private Intent mIntentService;
    private boolean mIsBound = false;

    private ServiceConnection musicConnection = new ServiceConnection() {

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

    //UI and listeners
    private ListView mListView;
    private AdapterView.OnItemClickListener listenerMood = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            initListWithSong(mListMood.get(position).getId());
            mMusicService.setList(mListSong);
        }
    };

    private AdapterView.OnItemClickListener listenerSong = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mMusicService.setSong(position);
            mMusicService.playSong();
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_player, container, false);

        ClassificationEngine classificationEngine = new ClassificationEngine(getContext());
//        classificationEngine.initializeDatabaseWithSongs(0);
//        ClassificationTask classificationTask = new ClassificationTask(getContext());
//        classificationTask.execute();

        mListView = (ListView) view.findViewById(R.id.list_player);

        mMoodDataSource = new MoodDataSource(getContext());
        mSongDataSource = new SongDataSource(getContext());

        initListWithMood();

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    initListWithMood();
                }
                return true;
            }
        });

        return view;
    }

    private void initListWithMood() {
        mMoodDataSource.open();
        mListMood = mMoodDataSource.getMoodList();
        MoodAdapter moodAdapter = new MoodAdapter(getContext(), R.layout.player_list_item, mListMood);
        mListView.setAdapter(moodAdapter);
        mListView.setOnItemClickListener(listenerMood);
    }

    private void initListWithSong(long moodId) {
        mSongDataSource.open();
        mListSong = mSongDataSource.getSongListByMoodId(moodId);
        SongAdapter songAdapter = new SongAdapter(getContext(), R.layout.player_list_item, mListSong);
        mListView.setAdapter(songAdapter);
        mListView.setOnItemClickListener(listenerSong);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mIntentService == null) {
            mIntentService = new Intent(getActivity(), MusicService.class);
            getActivity().bindService(mIntentService, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(mIntentService);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().stopService(mIntentService);
        mMusicService = null;
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
}
