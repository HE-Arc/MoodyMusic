package ch.hearc.moodymusic.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ch.hearc.moodymusic.R;
import ch.hearc.moodymusic.classification.ClassificationEngine;
import ch.hearc.moodymusic.classification.ClassificationTask;
import ch.hearc.moodymusic.tools.PermissionDialog;

/**
 * Created by axel.rieben on 29.10.2017.
 */

public class PlayerFragment extends Fragment {

    public static final String TAG = "PlayerFragment";

    private static final int REQUEST_READ_PERMISSION = 2;
    private static final String FRAGMENT_DIALOG = "dialog";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_player, container, false);

        ClassificationEngine classificationEngine = new ClassificationEngine(getContext());
//            classificationEngine.initializeDatabaseWithSongs(0);
        ClassificationTask classificationTask = new ClassificationTask(getContext());
        classificationTask.execute();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

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
