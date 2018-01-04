package ch.hearc.moodymusic.tools;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hearc.moodymusic.model.MoodPlaylist;
import ch.hearc.moodymusic.model.MoodPlaylistDataSource;

/**
 * Created by axel.rieben on 21.12.2017.
 */

public class MoodMusicMapper {
    private Map<MoodEnum, List<String>> mapping;

    //Data
    private MoodPlaylistDataSource mMoodPlaylistDataSource;

    private String[] playListMood = new String[]
            {
                    "Peaceful",
                    "Romantic",
                    "Sentimental",
                    "Tender",
                    "Easygoing",
                    "Yearning",
                    "Sophisticated",
                    "Sensual",
                    "Cool",
                    "Gritty",
                    "Somber",
                    "Melancholy",
                    "Serious",
                    "Brooding",
                    "Fiery",
                    "Urgent",
                    "Defiant",
                    "Aggressive",
                    "Rowdy",
                    "Excited",
                    "Energizing",
                    "Empowering",
                    "Stirring",
                    "Lively",
                    "Upbeat",
                    "Other"
            };

    public MoodMusicMapper(Context context) {
        mapping = new HashMap<>(7);
        mMoodPlaylistDataSource = new MoodPlaylistDataSource(context);
    }

    private void createMapping() {
        mMoodPlaylistDataSource.open();
        ArrayList<MoodPlaylist> listMoodPlaylist = mMoodPlaylistDataSource.getMoodPlaylistList();

//        List<String> listNeutral = new ArrayList<>(Arrays.asList(playListMood[], "Yearning", "");)


    }
}
