package ch.hearc.moodymusic.ui.player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ch.hearc.moodymusic.R;
import ch.hearc.moodymusic.model.MoodPlaylist;

/**
 * Created by axel.rieben on 02.12.2017.
 */

public class MoodAdapter extends ArrayAdapter<MoodPlaylist> {

    private ArrayList<MoodPlaylist> mListMoodPlaylist;
    private Context mContext;

    public MoodAdapter(Context context, int textViewResourceId, ArrayList<MoodPlaylist> listMoodPlaylist) {
        super(context, textViewResourceId, listMoodPlaylist);
        mContext = context;
        mListMoodPlaylist = listMoodPlaylist;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.player_list_item, null);
        }

        TextView textTop = (TextView) view.findViewById(R.id.text_top);
        textTop.setText(mListMoodPlaylist.get(position).getName());

        TextView textBottom = (TextView) view.findViewById(R.id.text_bottom);
        textBottom.setText("Playlist");

        return view;
    }

}
