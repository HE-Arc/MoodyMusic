package ch.hearc.moodymusic.ui.player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ch.hearc.moodymusic.R;
import ch.hearc.moodymusic.model.Song;

/**
 * Created by axel.rieben on 02.12.2017.
 * Class used to show songs of a playlist in a listview.
 */

public class SongAdapter extends ArrayAdapter<Song> {

    private ArrayList<Song> mListSong;
    private Context mContext;

    public SongAdapter(Context context, int textViewResourceId, ArrayList<Song> listSong) {
        super(context, textViewResourceId, listSong);
        mContext = context;
        mListSong = listSong;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.player_list_item, null);
        }

        ImageView image = (ImageView) view.findViewById(R.id.image_player_item);
        image.setImageResource(R.drawable.ic_audiotrack);

        TextView textTop = (TextView) view.findViewById(R.id.text_top);
        textTop.setText(mListSong.get(position).getTitle());

        TextView textBottom = (TextView) view.findViewById(R.id.text_bottom);
        textBottom.setText(mListSong.get(position).getArtist());

        return view;
    }

}
