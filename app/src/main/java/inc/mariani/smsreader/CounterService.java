package inc.mariani.smsreader;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by user on 11/14/2016.
 */
public class CounterService extends Service {
    //kelas ini digunakan untuk perhitungan detik

    public static String jenisHitung=null;
    static long TIME_LIMIT = 7000;


   // public int detikMax=1000;
    CountDownTimer Count;

    @Override
    public void onCreate() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //rubah nilai jenisHitung
   // Intent serviceIntent = new Intent(CounterService.this,ResponService.class);
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // Toast.makeText(this, "Penghitung detik Service Created", Toast.LENGTH_LONG).show();
        super.onStartCommand(intent, flags, startId);
      //  jenisHitung=intent.getExtras().getString("jenisCounter");

        if (jenisHitung=="sms"){
            TIME_LIMIT =9000;}
        else if (jenisHitung=="mode hapal keyword"){
            TIME_LIMIT =12000;
        }

        Count = new CountDownTimer(TIME_LIMIT, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
               // String time = String.format("%02d:%02d", (seconds % 3600) / 60, (seconds % 60));
                Long waktu=(seconds % 60);
                Intent i = new Intent("COUNTDOWN_UPDATED");
                i.putExtra("countdown",waktu);
               // serviceIntent.putExtra("detik",waktu);
                sendBroadcast(i);
            }
            //jika perhitungan detik selesai
            public void onFinish() {
                //coundownTimer.setTitle("Sedned!");
                Intent i = new Intent("COUNTDOWN_UPDATED");
                long stop=00;
                i.putExtra("countdown",stop);
                sendBroadcast(i);
                stopSelf();
            }
        };

        Count.start();
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        Count.cancel();
        super.onDestroy();
    }
}
