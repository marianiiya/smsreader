package inc.mariani.smsreader;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by user on 12/27/2016.
 */
public class Splash extends Activity {

    /**
     * App defined int constants for permissions
     */
    private static final int MY_PERMISSION_GROUP_REQUEST_CONTACTS = 1;
    private static final int MY_PERMISSION_GROUP_REQUEST_MICROPHONE = 2;
    private static final int MY_PERMISSION_GROUP_REQUEST_PHONE = 3;
    private static final int MY_PERMISSION_GROUP_REQUEST_SMS = 4;
    private static final int MY_PERMISSION_GROUP_REQUEST_STORAGE = 5;

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Klas ini berfungsi untuk mneampilkan splash screen selama detik yang telah ditentukan
        super.onCreate(savedInstanceState);
        checkAndRequestPermission();
        setContentView(R.layout.splash_screen);
    }

    /**
     * this method used to check and request permission for Android API 23 or above.
     */
    public void checkAndRequestPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSION_GROUP_REQUEST_CONTACTS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSION_GROUP_REQUEST_MICROPHONE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSION_GROUP_REQUEST_PHONE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSION_GROUP_REQUEST_SMS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_GROUP_REQUEST_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_GROUP_REQUEST_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permission was granted
                } else {
                    // permission denied
                }
            }
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                //method ini akn dieksekusi sekali ketika timer is over
                // memulai main activity
                Intent i = new Intent(Splash.this, MainActivity.class);
                startActivity(i);

                // menutup activity splash
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
