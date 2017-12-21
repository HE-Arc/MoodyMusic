package ch.hearc.moodymusic.tools;

import ch.hearc.moodymusic.R;

/**
 * Created by axel.rieben on 21.12.2017.
 */

public enum MoodEnum {
    NEUTRAL(0, R.string.neutral),
    HAPPY(1, R.string.happy),
    SAD(2, R.string.sad),
    ANGRY(3, R.string.angry),
    SURPRISED(4, R.string.surprised),
    DISGUSTED(5, R.string.disgusted),
    SCARED(6, R.string.scared);

    private int value;
    private int ressourceID;

    MoodEnum(int value, int ressourceID){
        this.value = value;
        this.ressourceID = ressourceID;
    }
}
