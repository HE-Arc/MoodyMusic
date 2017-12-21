package ch.hearc.moodymusic.tools;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hearc.moodymusic.model.Mood;
import ch.hearc.moodymusic.model.MoodDataSource;

/**
 * Created by axel.rieben on 21.12.2017.
 */

public class MoodMusicMapper {
    private Map<MoodEnum, List<String>> mapping;

    //Data
    private MoodDataSource mMoodDataSource;

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
        mMoodDataSource = new MoodDataSource(context);
    }

    private void createMapping() {
        mMoodDataSource.open();
        ArrayList<Mood> listMood = mMoodDataSource.getMoodList();

//        List<String> listNeutral = new ArrayList<>(Arrays.asList(playListMood[], "Yearning", "");)


    }
}
