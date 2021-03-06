/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.hearc.moodymusic.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ch.hearc.moodymusic.R;
import ch.hearc.moodymusic.detection.DetectionRequester;
import ch.hearc.moodymusic.tools.ConnectivityTools;
import ch.hearc.moodymusic.tools.PermissionDialog;

public class DetectFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String TAG = "DetectFragment";
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    //UI
    private FloatingActionButton mFabTakePicture;

    private static final int[] FLASH_OPTIONS = {
            CameraView.FLASH_AUTO,
            CameraView.FLASH_OFF,
            CameraView.FLASH_ON,
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };

    private static final int[] FLASH_TITLES = {
            R.string.flash_auto,
            R.string.flash_off,
            R.string.flash_on,
    };

    private int mCurrentFlash;
    private CameraView mCameraView;
    private Handler mBackgroundHandler;

    //Listener
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.take_picture:
                    mFabTakePicture.setEnabled(false);
                    if (ConnectivityTools.isNetworkAvailable(getContext()) && ConnectivityTools.isInternetAvailable()) {
                        if (mCameraView != null) {
                            mCameraView.takePicture();
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
                        mFabTakePicture.setEnabled(true);
                    }
                    break;
            }
        }
    };

    //Mapping
    private PlayerFragment playerFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.tab_detect, container, false);

        mCameraView = (CameraView) view.findViewById(R.id.camera);
        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
            mCameraView.setFacing(CameraView.FACING_FRONT);

        }

        mFabTakePicture = (FloatingActionButton) view.findViewById(R.id.take_picture);
        mFabTakePicture.setImageResource(R.drawable.ic_photo_camera);
        if (mFabTakePicture != null) {
            mFabTakePicture.setOnClickListener(mOnClickListener);
        }

//        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//
//        if (actionBar != null) {
//            actionBar.setDisplayShowTitleEnabled(false);
//        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mCameraView.start();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
            PermissionDialog.ConfirmationDialogFragment
                    .newInstance(R.string.camera_permission_confirmation,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA_PERMISSION,
                            R.string.camera_permission_not_granted)
                    .show(getActivity().getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mBackgroundHandler != null) {
            mBackgroundHandler.getLooper().quitSafely();
            mBackgroundHandler = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }

                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), R.string.camera_permission_not_granted,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        Log.v(TAG, "MENU");
//        inflater.inflate(R.menu.main, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.aspect_ratio:
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//
//                if (mCameraView != null
//                        && fragmentManager.findFragmentByTag(FRAGMENT_DIALOG) == null) {
//                    final Set<AspectRatio> ratios = mCameraView.getSupportedAspectRatios();
//                    final AspectRatio currentRatio = mCameraView.getAspectRatio();
//                    AspectRatioFragment.newInstance(ratios, currentRatio)
//                            .show(fragmentManager, FRAGMENT_DIALOG);
//                }
//                return true;
//            case R.id.switch_flash:
//                if (mCameraView != null) {
//                    mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
//                    item.setTitle(FLASH_TITLES[mCurrentFlash]);
//                    item.setIcon(FLASH_ICONS[mCurrentFlash]);
//                    mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
//                }
//                return true;
//            case R.id.switch_camera:
//                if (mCameraView != null) {
//                    int facing = mCameraView.getFacing();
//                    mCameraView.setFacing(facing == CameraView.FACING_FRONT ?
//                            CameraView.FACING_BACK : CameraView.FACING_FRONT);
//                }
//                return true;
//        }
//        return false;
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.aspect_ratio:
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                if (mCameraView != null
//                        && fragmentManager.findFragmentByTag(FRAGMENT_DIALOG) == null) {
//                    final Set<AspectRatio> ratios = mCameraView.getSupportedAspectRatios();
//                    final AspectRatio currentRatio = mCameraView.getAspectRatio();
//                    AspectRatioFragment.newInstance(ratios, currentRatio)
//                            .show(fragmentManager, FRAGMENT_DIALOG);
//                }
//                return true;
//            case R.id.switch_flash:
//                if (mCameraView != null) {
//                    mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
//                    item.setTitle(FLASH_TITLES[mCurrentFlash]);
//                    item.setIcon(FLASH_ICONS[mCurrentFlash]);
//                    mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
//                }
//                return true;
//            case R.id.switch_camera:
//                if (mCameraView != null) {
//                    int facing = mCameraView.getFacing();
//                    mCameraView.setFacing(facing == CameraView.FACING_FRONT ?
//                            CameraView.FACING_BACK : CameraView.FACING_FRONT);
//                }
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onAspectRatioSelected(@NonNull AspectRatio ratio) {
//        if (mCameraView != null) {
//            Toast.makeText(getContext(), ratio.toString(), Toast.LENGTH_SHORT).show();
//            mCameraView.setAspectRatio(ratio);
//        }
//    }

    public void setPlayerFragment(PlayerFragment playerFragment){
        this.playerFragment = playerFragment;
    }

    private CameraView.Callback mCallback = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {

            Log.d(TAG, "onPictureTaken " + data.length);
            Toast.makeText(cameraView.getContext(), R.string.picture_taken, Toast.LENGTH_SHORT).show();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "picture.jpg");
                    OutputStream os = null;

                    try {
                        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                        os = new FileOutputStream(file);
                        image.compress(Bitmap.CompressFormat.JPEG, 60, os);
                        os.close();
                    } catch (IOException e) {
                        Log.w(TAG, "Cannot write to " + file, e);
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                // Ignore
                            }
                        }
                    }

                    DetectionRequester detectionRequester = new DetectionRequester(getContext(), playerFragment);
                    detectionRequester.execute(file.getPath());

                    mFabTakePicture.setEnabled(true);
                }
            });
        }
    };
}
