package inc.mariani.smsreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by user on 1/16/2017.
 */
public class Panduan extends AppCompatActivity{
    //klas ini digunakan untuk fungsi pembacaan panduan penggunaan aplikasi
    private Speaker speaker;
    private String currentSetting;
    ProximityService myProxService= new ProximityService();
    private final int CHECK_CODE = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.halaman_panduan);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        try{
            setContentView(R.layout.halaman_panduan);

        }catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        //mendapatkan nilai dari jenis/custom pengaturan dapat bernilai custom1 atau custom2
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentSetting= sp.getString("radio","-1");

        checkTTS();
        speaker = new Speaker();
        speaker.allow(true);
        //berikut merupakan text yang akan dibacakan oleh TTS engine
        speaker.say(getString(R.string.panduan1), getApplicationContext());

        if(currentSetting=="custom1"){
            //untuk pembacaan custom 1
            speaker.pause(2000);
            speaker.say(getString(R.string.panduanCustom1_a), getApplicationContext());
            speaker.pause(2000);
            speaker.say(getString(R.string.panduanCustom1_b), getApplicationContext());
            speaker.pause(2000);
            speaker.say(getString(R.string.panduanCustom1_c), getApplicationContext());
        }
        else {
            //untuk pembacaan pengaturan custom 2
            speaker.pause(2000);
            speaker.say(getString(R.string.panduanCustom2_a), getApplicationContext());
            speaker.pause(2000);
            speaker.say(getString(R.string.panduanCustom2_b), getApplicationContext());
            speaker.pause(2000);
            speaker.say(getString(R.string.panduanCustom2_c), getApplicationContext());
        }




        PreferenceManager.setDefaultValues(this, R.xml.autoread, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean statusCheckBox = sharedPreferences.getBoolean("checkBoxModeHafal", true);

        if(statusCheckBox==false) {
            //modeBaca="default";
            //unutk pembacaan penaturan pemilihan keyword
            speaker.pause(2000);
            speaker.say(getString(R.string.panduanModeDefault), getApplicationContext());

        }else{
           // modeBaca="hapal keyword";
            speaker.pause(2000);
            speaker.say(getString(R.string.panduanModeHapal), getApplicationContext());
        }
    }

    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHECK_CODE){
            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                speaker = new Speaker();
            }else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }
    protected void onDestroy() {
        super.onDestroy();
        speaker.destroy();
    }
    protected void onResume(){
        super.onResume();
        checkTTS();
    }
}
