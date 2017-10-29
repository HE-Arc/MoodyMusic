package ch.hearc.moodymusic.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.hearc.moodymusic.R;

/**
 * Created by axel.rieben on 29.10.2017.
 */

public class DetectFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_detect, container, false);
        return view;
    }
}
