package inc.mariani.smsreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by user on 12/27/2016.
 */
public class Splash extends Activity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Klas ini berfungsi untuk mneampilkan splash screen selama detik yang telah ditentukan
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
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
