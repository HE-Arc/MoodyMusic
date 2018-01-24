package ch.hearc.moodymusic.player;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.MediaController;

/**
 * Created by axel.rieben on 18.12.2017.
 * This class will create a default set of controls for the music player.
 */

public class MusicController extends MediaController{

    public MusicController(Context c){
        super(c);
    }

    public void hide(){}

    public void myHide(){
        super.hide();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            super.hide();
        }
        return false;
    }

}
