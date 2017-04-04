package inc.mariani.smsreader;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Mariani on 9/7/2016.
 */
public class Speaker implements OnInitListener{
    //klas ini menghandle fungsi yang dibutuhkan untuk text to speech agar aplikasi mampu membacakan textyang telah ditentukan


    private String text;
    private static TextToSpeech tts;
   // public Speaker speaker;

    private boolean ready = false;
    private boolean allowed = false;

    private static Speaker ourInstance = new Speaker();
//
    public static Speaker getInstance() {
        return ourInstance;
    }
    public Speaker(){}

//    public Speaker(Context context){
//        if (tts==null){
//            tts = new TextToSpeech(context, this);
//        }else{
//            getInstance();
//        }
//    }

    public void say(String text, Context context){
        //fungsi ini dijalankan untuk membacakan text
    this.text=text;
        if(tts == null){
            tts = new TextToSpeech(context.getApplicationContext(), (OnInitListener) ourInstance);
        }
        else{
         //   tts.speak(text, TextToSpeech.QUEUE_ADD, null);
            HashMap<String, String> hash = new HashMap<String,String>();
            hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                    String.valueOf(AudioManager.STREAM_NOTIFICATION));
            tts.speak(text, TextToSpeech.QUEUE_ADD, hash);
        }
    }

    public boolean isAllowed(){
        return allowed;
    }
    public void allow(boolean allowed){
        this.allowed = allowed;
    }

    @Override
    public void onInit(int status) {

        if(status == TextToSpeech.SUCCESS) {
            //untuk melakukan pengecekan apakah language resource yang dibutuhkan ada pada perangkat
            tts.setLanguage(new Locale("in_ID"));
            ready = true;
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);

        }else{
            ready = false;
        }
}
    public void speak(String text){
        //speaker diaktifkan jika TTS ready dan user mengizinkan speech
        if(ready && allowed) {
            HashMap<String, String> hash = new HashMap<String,String>();
            hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                    String.valueOf(AudioManager.STREAM_NOTIFICATION));
            tts.speak(text, TextToSpeech.QUEUE_ADD, hash);
        }
    }
    public void pause(int duration){
        tts.playSilence(duration, TextToSpeech.QUEUE_ADD, null);
    }
    public void destroy(){
        if(tts != null){
            tts.shutdown();
            tts.stop();
            tts = null;
        }
    }
    public void shutDown(){
        tts.shutdown();
    }
}

