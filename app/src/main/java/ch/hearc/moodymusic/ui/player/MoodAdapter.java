package ch.hearc.moodymusic.ui.player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ch.hearc.moodymusic.R;
import ch.hearc.moodymusic.model.Mood;

/**
 * Created by axel.rieben on 02.12.2017.
 */

public class MoodAdapter extends ArrayAdapter<Mood> {

    private ArrayList<Mood> mListMood;
    private Context mContext;

    public MoodAdapter(Context context, int textViewResourceId, ArrayList<Mood> listMood) {
        super(context, textViewResourceId, listMood);
        mContext = context;
        mListMood = listMood;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.player_list_item, null);
        }

        TextView textView = (TextView) view.findViewById(R.id.text_top);
        textView.setText(mListMood.get(position).getName());
        return view;
    }

}
