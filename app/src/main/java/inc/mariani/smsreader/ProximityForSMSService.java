package inc.mariani.smsreader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
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
 * Created by user on 12/27/2016.
 */
public class ProximityForSMSService extends Service implements SensorEventListener {

    Sensor proxSensor;
    SensorManager sm;
    public static int nearCount=0;
    public boolean allowCount=true;

    @Override
    public void onCreate() {//onCreat shouldn't be used for sensor u should use onStartCommand

        Toast.makeText(this, "Proximity Service Created", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        //kondisi jika sensor membaca ada benda dekat
        if(event.values[0] == 0){
            if(allowCount=true) {
                if (nearCount<=1){
                    //menambahkan jumlah lambaian tangan ke sensor
                    nearCount++;
                    Toast.makeText(getApplicationContext(),"Count = "+nearCount, Toast.LENGTH_LONG).show();
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

        //here u should make your service foreground so it will keep working even if app closed

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent bIntent = new Intent(ProximityForSMSService.this, MainActivity.class);
        PendingIntent pbIntent = PendingIntent.getActivity(ProximityForSMSService.this, 0 , bIntent, 0);
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
