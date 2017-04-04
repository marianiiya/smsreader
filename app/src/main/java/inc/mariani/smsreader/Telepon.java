package inc.mariani.smsreader;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 12/9/2016.
 */
public class Telepon extends AppCompatActivity{

    public void ActivatedLoudspeaker(Context context){
        try {
            Thread.sleep(500); // Delay 0,5 seconds to handle better turning on
            // loudspeaker
        } catch (InterruptedException e) {
        }
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(true);

    }
}
