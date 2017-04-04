package inc.mariani.smsreader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by mariani on 10/10/2016.
 */
public class ProximityService extends Service implements SensorEventListener {
    //klass ini bertujuan untuk menghandle semua penggunaan sensor proximity

    Sensor proxSensor;
    SensorManager sm;
    public static int nearCount=0;
    public boolean allowCount=true;
    public static String jenisProxi=null;

    @Override
    public void onCreate() {//onCreat shouldn't be used for sensor u should use onStartCommand

        Toast.makeText(this, "Proximity Service Created", Toast.LENGTH_LONG).show();

       // registerReceiver(uiUpdated, new IntentFilter("JENIS_PROXI_SMS"));
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

//    private BroadcastReceiver uiUpdated = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //jenisProxi=intent.getExtras().getLong("countdown");
//        jenisProxi=intent.getExtras().getString("jenisProxiSMS");
//        }
//    };
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        //kondisi jika sensor membaca ada benda dekat

        if(event.values[0] == 0){

            if(jenisProxi=="pilih keyword"){
                pilihKeyword();
            }else if(jenisProxi=="hidupkan aplikasi"){

            }else if(jenisProxi=="respon SMS"){
                pilihResponSMS();
            }else if (jenisProxi=="mode hapal keyword"){
                pilihKeywordModeHafal();
            }
            else {
                proxiMenu();
            }
        }
    }

    public void proxiMenu(){
    //fungsi ini dijalankan apabila aplikasi meminta respon pengguna untuk memilih
        if(allowCount=true) {
            if (nearCount<=1){
                //menambahkan jumlah deteksi objek ke sensor
                nearCount++;
                Toast.makeText(getApplicationContext(),"Count = "+nearCount+" "+jenisProxi, Toast.LENGTH_LONG).show();
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_LOW_PBX_SSL, 300);

                if (nearCount == 1) {
                    //mengirimkan nilai nearCount untuk memulai menghitung detik
                    Intent i = new Intent("PROXIMITY_UPDATED");
                    i.putExtra("jumlahNearCount", nearCount);
                    sendBroadcast(i);
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Count = "+"flase proxy", Toast.LENGTH_LONG).show();
        }
    }

    public void pilihKeyword(){
        //fungsi ini dijalankan apabila aplikasi meminta respon pengguna untuk memilih keyword yang
        //digunakan untuk mengirimkan SMS
        if(allowCount=true) {
            if (nearCount<=0){
                //menambahkan jumlah deteksi objek ke sensor
                nearCount++;
                Toast.makeText(getApplicationContext(),"Count = "+nearCount+" "+jenisProxi, Toast.LENGTH_LONG).show();
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_LOW_PBX_SSL, 300);

                if (nearCount == 1) {
                    //mengirimkan nilai nearCount untuk memulai menghitung detik
                    Intent i = new Intent("PROXIMITY_UPDATED");
                    i.putExtra("jumlahNearCount", nearCount);
                    sendBroadcast(i);
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Count = "+"flase proxy", Toast.LENGTH_LONG).show();
        }
    }
    public void pilihKeywordModeHafal(){
        //fungsi ini dijalankan apabila aplikasi meminta respon pengguna untuk memilih keyword untuk membalas SMSM
        //namun dengan mode hafal keyword

        if(allowCount=true) {
            if (nearCount<=10){
                //menambahkan jumlah deteksi objek ke sensor
                nearCount++;
                Toast.makeText(getApplicationContext(),"Count = "+nearCount+" "+jenisProxi, Toast.LENGTH_LONG).show();
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_LOW_PBX_SSL, 300);

                if (nearCount == 1) {
                    //mengirimkan nilai nearCount untuk memulai menghitung detik
                    Intent i = new Intent("PROXIMITY_UPDATED");
                    i.putExtra("jumlahNearCount", nearCount);
                    sendBroadcast(i);
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Count = "+"flase proxy", Toast.LENGTH_LONG).show();
        }
    }
    public void pilihResponSMS(){
        //fungsi ini dijalankan apabila aplikasi meminta respon pengguna untuk memilih apakah akan melanjutkan pengiriman SMS atau tidak

        if(allowCount=true) {
            if (nearCount<=2){
                //menambahkan jumlah deteksi objek ke sensor
                nearCount++;
                Toast.makeText(getApplicationContext(),"Count = "+nearCount+" "+jenisProxi, Toast.LENGTH_LONG).show();
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_LOW_PBX_SSL, 300);
                if (nearCount == 1) {
                    //mengirimkan nilai nearCount untuk memulai menghitung detik
                    Intent i = new Intent("PROXIMITY_UPDATED");
                    i.putExtra("jumlahNearCount", nearCount);
                    sendBroadcast(i);
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Count = "+"flase proxy", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onDestroy() {//unregister sensor
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        sm.unregisterListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    //register sensor dan menulis perintah untuk onStartCommand bukan onStart
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        proxSensor=sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sm.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //disini harus dibuat service foreground yang akan membantu meskipun aplikasi dimatikan

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent bIntent = new Intent(ProximityService.this, MainActivity.class);
        PendingIntent pbIntent = PendingIntent.getActivity(ProximityService.this, 0 , bIntent, 0);
        NotificationCompat.Builder bBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Title")
                        .setContentText("Subtitle")
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setContentIntent(pbIntent);
        Notification barNotif = bBuilder.build();
        this.startForeground(1, barNotif);

        //then you should return sticky
        return Service.START_STICKY;
    }
    public int getNearCount(){
        return nearCount;
    }
    public void restartNearCount(){
        nearCount=0;
    }
}
