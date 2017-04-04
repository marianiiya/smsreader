package inc.mariani.smsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import static android.R.attr.value;

/**
 * Created by user on 1/17/2017.
 */
public class SMS extends AppCompatActivity{
//klas ini berfungsi untuk menghandle proses yang berkaitan dengan pembuatan dan pengiriman SMS
//apabila pengguna memilih untuk membalas SMS
    private final int CHECK_CODE = 0x1;

    //untuk pengiriman SMS
    private String noPengirim;
    private String isiPesan;

    //untuk widget
    private TextView tvPhoneNo;
    private TextView tvSMS;
    private TextView tvDetik;
    private TextView editNo;
    private TextView editSMS;
    private Button btn;

    //variable untuk speaker
    private Speaker speaker;

    //variable untuk pengaturan
    private String modeBaca;//hapal keyword or default
    private String modeRespon;//custom1 or custom 2

    //variable untuk SQLite database
    protected Cursor cursor;
    private DataHelper dbcenter;
    private String[] daftar;
    private String[] isiPesanDariKey;

    //vareiabel yg dibutuhkan untuk tunggu respon pilihan keyword
    Thread threadBackgroundForModeHapal;
    Thread threadBackgroundForModeDefault;
    Thread threadBackgroundForResponSMS;

    //penggunaan proxiservice
    ProximityService proxiService=new ProximityService();
    public static int jumNear=0;
    public static long detik=0;

    //penggunan counterservice
    CounterService counterService=new CounterService();

    //untuk baca keyword
    Handler handle = new Handler();
    static int count = 0;
    private String keywordSementara;
    private String isiPesanSementara;
    Runnable r  = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                eksekusiRun(keywordSementara,isiPesanSementara);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    Runnable runnableBackgroundForModeHapal = new Runnable() {

        @Override
        public void run() {
            //proses thread yang jalan pada background
            proxiService.restartNearCount();
            proxiService.jenisProxi="mode hapal keyword";

            boolean kondisi=true;
            try {
                while (kondisi) {
                    //mHandler = new Handler();
                    jumNear=  proxiService.getNearCount();
                    if (detik==1){
                        inputPesan(jumNear);
                       // responSMS();
                        kondisi=false;


                    }
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt(); //this is a MUST
            }
            return;
        }
    };

    Runnable runnableBackgroundForResponSMS = new Runnable() {

        @Override
        public void run() {
            //proses thread yang jalan pada background
            proxiService.restartNearCount();
            proxiService.jenisProxi="respon SMS";
            boolean kondisi=true;
            try {
                while (kondisi) {
                    //mHandler = new Handler();
                    jumNear=  proxiService.getNearCount();
                    if (detik==1){
                        if(jumNear==1){
                            //jika pengguna memilih unutk memilih melanjutkan pengiriman
                            kondisi=false;
                            isiPesan=isiPesanSementara;
                            editSMS.setText(isiPesan);
                            kirimSMS(noPengirim,isiPesan);
                        }
                        else if(jumNear==2){
                            kondisi=false;
                            speaker.say("pengiriman SMS telah dibatalkan",getApplicationContext());
                            finish();
                        }
                        else {
                            kondisi=false;
                            isiPesan="";
                            editSMS.setText(isiPesan);
                        }
                    }
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt(); //this is a MUST
            }
            return;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halaman_sms);
        //untuk icon di action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //inisialisasi widget
        btn=(Button)findViewById(R.id.buttonSend);
        tvPhoneNo= (TextView) findViewById(R.id.textViewPhoneNo);
        tvSMS= (TextView) findViewById(R.id.textViewSMS);
        tvDetik= (TextView) findViewById(R.id.textDetikSMS);
        editNo=(TextView) findViewById(R.id.editTextPhoneNo);
        editSMS= (TextView) findViewById(R.id.editTextSMS);

        //mendapatkan nomor pengirim dari intent yang dikirimkan MainActivity.class
        noPengirim = getIntent().getStringExtra("noTlp");

        //code untuk mengganti font
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Brandon_med.otf");
        tvPhoneNo.setTypeface(font);
        tvSMS.setTypeface(font);
        tvDetik.setTypeface(font);
        editSMS.setTypeface(font);
        editSMS.setTypeface(font);

        //set nomor pengirim
        editNo.setText(noPengirim);

        //untuk database
        dbcenter = new DataHelper(getApplicationContext());

        //code untuk speaker
        checkTTS();
        speaker = new Speaker();
        speaker.pause(4000);
        speaker.say("Aplikasi akan membacakan keyword pesan",getApplicationContext());

        //untuk tombol kirim SMS
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                kirimSMS(noPengirim,isiPesan);
            }
        });

        //mendapatkan nilai checkbox dari pengaturan
        PreferenceManager.setDefaultValues(this, R.xml.autoread, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean statusCheckBox = sharedPreferences.getBoolean("checkBoxModeHafal", true);

        //kondisi untuk menentukan mode baca keyword dari pengaturan
        if(statusCheckBox==false) {
            modeBaca="default";
        }else{
            modeBaca="hapal keyword";
        }
        //membacakan keyword pesan
        if(modeBaca=="default"){
            membacakanKeywordDefault();
        }
        else{
            membacakanKeywordModeHapal();
        }

        registerReceiver(uiUpdated, new IntentFilter("COUNTDOWN_UPDATED"));

    }

    BroadcastReceiver uiUpdated = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //This is the part where I get the timer value from the service and I update it every second, because I send the data from the service every second. The coundtdownTimer is a MenuItem
           //Untuk menampilkan perhitungan detik
            tvDetik.setText("detik :"+intent.getExtras().getLong("countdown"));
            detik=intent.getExtras().getLong("countdown");
        }
    };

    private void membacakanKeywordModeHapal() {
        Toast.makeText(getApplicationContext(),"Mode hapal",Toast.LENGTH_LONG).show();

        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM keyword",null);
        daftar = new String[cursor.getCount()];
        isiPesanDariKey=new String[cursor.getCount()];
        cursor.moveToFirst();

        for (int cc=0; cc < cursor.getCount(); cc++){
            cursor.moveToPosition(cc);
            daftar[cc] = cursor.getString(0).toString();
            isiPesanDariKey[cc]=cursor.getString(1).toString();
            keywordSementara= daftar[cc];
            //isiPesanSementara=isiPesanDariKey[cc];
            int urutanCC=cc+1;
            speaker.say(String.valueOf(urutanCC),getApplicationContext());
            speaker.say(keywordSementara,getApplicationContext());
            speaker.pause(1000);
        }

        speaker.pause(2000);
        speaker.say("silahkan memberi respon ke proximity sensor sebanyak urutan keyword yang anda pilih",getApplicationContext());
        //thread untuk memproses respon pengguna
        proxiService.jenisProxi="mode hapal keyword";
        counterService.jenisHitung="mode hapal keyword";
        threadBackgroundForModeHapal= new Thread(runnableBackgroundForModeHapal);
        threadBackgroundForModeHapal.start();

    }

    private void membacakanKeywordDefault() {
        Toast.makeText(getApplicationContext(),"Mode default",Toast.LENGTH_LONG).show();

        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM keyword",null);
        daftar = new String[cursor.getCount()];
        isiPesanDariKey=new String[cursor.getCount()];
        cursor.moveToFirst();

        for (int cc=0; cc < cursor.getCount(); cc++){

            cursor.moveToPosition(cc);
            daftar[cc] = cursor.getString(0).toString();
            isiPesanDariKey[cc]=cursor.getString(1).toString();
            keywordSementara= daftar[cc];
            isiPesanSementara=isiPesanDariKey[cc];

           // speaker.say(keywordSementara,getApplicationContext());
           // eksekusiRun(keywordSementara,isiPesanSementara);
           // r.run();
            speaker.say(keywordSementara,getApplicationContext());
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    //speaker.say(keywordSementara,getApplicationContext());
                    //method ini akn dieksekusi sekali ketika timer is over
                    if(jumNear==1){
                        //berhenti
                    }
                }
            }, 6*cc);
        }

    }
    public void responSMS(){
        speaker.say("Apakah anda ingin mengirimkan SMS ini:",getApplicationContext());
        speaker.pause(1000);
        speaker.say(isiPesanSementara,getApplicationContext());
        speaker.say("dekatkan tangan ke proximity sensor satu kali untuk menyutujui pengiriman pesan tersebut",getApplicationContext());
        speaker.pause(1000);
        speaker.say(" dua kali untuk membatalkan pengiriman pesan tersebut",getApplicationContext());
        speaker.pause(1000);
        speaker.say("tiga kali untuk mengulangi memilih keyword",getApplicationContext());
        threadBackgroundForResponSMS= new Thread(runnableBackgroundForResponSMS);
        threadBackgroundForResponSMS.start();

    }
    private void eksekusiRun(String key, String isi) throws InterruptedException {

        count+=5000;
        if(count==30000){
            handle.removeCallbacks(r);
        }else{
            speaker.say(key,getApplicationContext());
            //wait(8000);
            handle.postDelayed(r, 8000);
        }
    }

    public void kirimSMS(String phone, String message){
    //fungsi untuk mengirimkan SMS
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null,message, null, null);
            Toast.makeText(SMS.this, "Message Sent", Toast.LENGTH_LONG).show();
            speaker.say("Pesan telah terkirim",getApplicationContext());
            finish();
        }
        catch (Exception e)
        {
            Toast.makeText(SMS.this, "Message not Sent",
                    Toast.LENGTH_LONG).show();
            speaker.say("Pesan tidak dapat terkirim",getApplicationContext());

        }
    }

    public void inputPesan(int urutan){
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM keyword",null);
        daftar = new String[cursor.getCount()];
        isiPesanDariKey=new String[cursor.getCount()];
        cursor.moveToFirst();

        for (int cc=0; cc < urutan; cc++){
            cursor.moveToPosition(cc);
            daftar[cc] = cursor.getString(0).toString();
            isiPesanDariKey[cc]=cursor.getString(1).toString();
            keywordSementara= daftar[cc];
            isiPesanSementara=isiPesanDariKey[cc];
        }

      //  editSMS.setText(isiPesanSementara);
        //responSMS();
        speaker.say("Apakah anda ingin mengirimkan SMS ini:",getApplicationContext());
        speaker.pause(1000);
        speaker.say(isiPesanSementara,getApplicationContext());
        speaker.say("dekatkan tangan ke proximity sensor satu kali untuk menyutujui pengiriman pesan tersebut",getApplicationContext());
        speaker.pause(1000);
        speaker.say(" dua kali untuk membatalkan pengiriman pesan tersebut",getApplicationContext());
        speaker.pause(1000);
        speaker.say("tiga kali untuk mengulangi memilih keyword",getApplicationContext());

        threadBackgroundForResponSMS= new Thread(runnableBackgroundForResponSMS);
        threadBackgroundForResponSMS.start();


    }

    //untuk mengecek apakah TTS sudah terinisialisasi
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

    @Override
    protected void onResume() {
        super.onResume();
        checkTTS();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(uiUpdated);
        super.onDestroy();
        speaker.destroy();
    }
}
