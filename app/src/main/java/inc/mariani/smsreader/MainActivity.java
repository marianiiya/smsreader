package inc.mariani.smsreader;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private final int CHECK_CODE = 0x1;
    private final int LONG_DURATION = 5000;
    private final int SHORT_DURATION = 1200;
    public static String TAG="MainActivity";
    public Speaker speaker;
    private ToggleButton toggle;
    private Button buttonKeyword;
    private Button buttonSetting;
    private Button buttonPanduan;
    private Button buttonSMS;

    private CompoundButton.OnCheckedChangeListener toggleListener;

    private TextView smsText;
    private TextView smsSender;
    private TextView textDetik;


    private BroadcastReceiver smsReceiver;
    private BroadcastReceiver callReceiver;//call
    ProximityService proxiService=new ProximityService();
    Telepon callHandler;

    SensorManager mySensorManager;
    Sensor myProximitySensor;
    ProximityService myProxService= new ProximityService();

    int proxiValue;
    //private int nearCount;
    public static int jumNear=0;
    public static long detik=0;
    public static String noPengirim=null;
    public int respon=0;
    private Boolean runner=true;
    //SMSHandler smsHandler= new SMSHandler();
    SMS sms= new SMS();
    public String currentSetting;

    Thread threadBackgroundForSMS;
    Thread threadBackgroundForCall;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        toggle = (ToggleButton)findViewById(R.id.speechToggle);
        buttonKeyword= (Button) findViewById(R.id.tombolKey);
        buttonPanduan= (Button) findViewById(R.id.tombolPanduan);
        buttonSetting= (Button) findViewById(R.id.tombolSet);
        buttonSMS= (Button) findViewById(R.id.buttonSMS);
        smsText = (TextView)findViewById(R.id.sms_text);
        smsSender = (TextView)findViewById(R.id.sms_sender);
        textDetik= (TextView) findViewById(R.id.textDetik);

        //merubah font
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Brandon_med.otf");
        smsText.setTypeface(font);
        smsSender.setTypeface(font);
        textDetik.setTypeface(font);


        //mendapatkan nilai custom/jenis pengaturan respon dapat bernilai custom1 atau custom2
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
      currentSetting= sp.getString("radio","-1");
       Toast.makeText(this, currentSetting, Toast.LENGTH_LONG).show();



        toggleListener= new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean b) {
                if(b){

                    speaker.allow(true);
                    speaker.say(getString(R.string.start_speaking),getApplicationContext());
                  // Context context = getApplicationContext();

                    startService(new Intent(MainActivity.this, CounterService.class));
                    startService(new Intent(MainActivity.this, ProximityService.class));
                    myProxService.restartNearCount();

                }else{
                    speaker.say(getString(R.string.stop_speaking),getApplicationContext());
                    speaker.allow(false);

                    stopService(new Intent(MainActivity.this, ProximityService.class));
                    stopService(new Intent(MainActivity.this, CounterService.class));
                    //stopService(new Intent(MainActivity.this, ResponService.class));

                }
            }
        };
        toggle.setOnCheckedChangeListener(toggleListener);


        checkTTS();
        initializeSMSReceiver();
        initializeCallReceiver();
        registerSMSReceiver();
        registerCallReceiver();
        registerReceiver(uiUpdated, new IntentFilter("COUNTDOWN_UPDATED"));
        registerReceiver(uiProxiUpdated, new IntentFilter("PROXIMITY_UPDATED"));
        registerReceiver(callRespond1, new IntentFilter("RESPOND1"));
        registerReceiver(callRespond2, new IntentFilter("RESPOND2"));

        addListenerOnButton();

    }

    public void addListenerOnButton() {

        buttonKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

              Intent i = new Intent(getApplicationContext(), ListKeyword.class);
                startActivity(i);
            }

        });
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(getApplicationContext(), Pengaturan.class);
                startActivity(i);
            }

        });
        buttonPanduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(getApplicationContext(),Panduan.class);
                startActivity(i);
            }

        });
        buttonSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(getApplicationContext(), SMS.class);
                startActivity(i);
            }

        });
    }
    private BroadcastReceiver uiUpdated = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //This is the part where I get the timer value from the service and I update it every second, because I send the data from the service every second. The coundtdownTimer is a MenuItem
           // .setTitle(intent.getExtras().getString("countdown"));
            //Toast.makeText(getApplicationContext(), "detik :"+intent.getExtras().getString("countdown"), Toast.LENGTH_LONG).show();
            //Untuk menampilkan perhitungan detik
            textDetik.setText("detik :"+intent.getExtras().getLong("countdown"));
            detik=intent.getExtras().getLong("countdown");
        }
    };

    private BroadcastReceiver uiProxiUpdated = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //mendapatkan value jumlah nilai near dari proximity dari
            jumNear=intent.getExtras().getInt("jumlahNearCount");

    if (jumNear==1){
        startService(new Intent(MainActivity.this, CounterService.class));
    }
}
};
    //untuk menerima respon terhadap call dengan setting custom1
    private BroadcastReceiver callRespond1 =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            respon=intent.getExtras().getInt("responProxiCall1");
          //  SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            //currentSetting= sp.getString("radio","-1");
            //Toast.makeText(getApplicationContext(), currentSetting, Toast.LENGTH_LONG).show();

            if(currentSetting=="custom1"){
                //custom1 memiliki pengaturan 1 untuk menerima telepon dan 2 untuk menolak telepon
                if (respon==1) {
                    //Toast.makeText(getApplicationContext(),"tolaakkkk",Toast.LENGTH_SHORT).show();
                    acceptCall();
                } else if (respon==2) {
                    // Toast.makeText(getApplicationContext(),"terima",Toast.LENGTH_SHORT).show();
                    //answerCall(getApplicationContext());
                    disconnectCall();
                }
            }
            else{
                if (respon==1) {
                    //Toast.makeText(getApplicationContext(),"tolaakkkk",Toast.LENGTH_SHORT).show();
                    disconnectCall();
                } else if (respon==2) {
                    // Toast.makeText(getApplicationContext(),"terima",Toast.LENGTH_SHORT).show();
                    //answerCall(getApplicationContext());
                    acceptCall();
                }
            }
        }

    };

    //untuk menerima respon terhadap call dengan setting custom2
    private BroadcastReceiver callRespond2 =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            respon=intent.getExtras().getInt("responProxiCall2");

            if (respon==1) {
                //Toast.makeText(getApplicationContext(),"tolaakkkk",Toast.LENGTH_SHORT).show();
                acceptCall();
            } else if (respon==2) {
                // Toast.makeText(getApplicationContext(),"terima",Toast.LENGTH_SHORT).show();
                //answerCall(getApplicationContext());
                disconnectCall();
            }
        }

    };
    Runnable runnableBackgroundForCall = new Runnable() {

        @Override
        public void run() {
            //proses thread yang jalan pada background
            proxiService.restartNearCount();

            try {
                boolean kondisi=true;
                while (kondisi) {
                    //mHandler = new Handler();
                    jumNear=  proxiService.getNearCount();

                    if (detik==1){
                        if(jumNear==1){
                                Intent i = new Intent("RESPOND1");
                                i.putExtra("responProxiCall1", 1);
                                sendBroadcast(i);
                            kondisi=false;
                        }
                        if(jumNear==2){

                                Intent i = new Intent("RESPOND1");
                                i.putExtra("responProxiCall1", 2);
                                sendBroadcast(i);
                            kondisi=false;
                        }
                    }
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt(); //this is a MUST
            }
            return;
        }
    };

    Runnable runnableBackgroundForSMS = new Runnable() {

        @Override
        public void run() {
            //proses thread yang jalan pada background
            proxiService.restartNearCount();
            boolean kondisi=true;

            try {
                while (kondisi) {
                    //mHandler = new Handler();
                    jumNear=  proxiService.getNearCount();
                    if (detik==1){
                        if(jumNear==1){
                            //threadBackgroundForSMS.stop();
                           kondisi=false;
                        }
                        else if(jumNear==2){
                            kondisi=false;
                            Intent intentSMS = new Intent(getApplicationContext(), SMS.class);
                         //   startActivity(intentSMS);
                            intentSMS.putExtra("noTlp",noPengirim); //Optional parameters
                            MainActivity.this.startActivity(intentSMS);
                        }
                    }
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt(); //this is a MUST
            }
            return;
        }
    };

  //  @Override
    protected void onPause() {
        super.onPause();

       // mySensorManager.unregisterListener(this);
    }
    @Override
    public void onResume(){
        super.onResume();
        checkTTS();
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
    private void initializeSMSReceiver(){
        smsReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                if(bundle!=null){
                    Object[] pdus = (Object[])bundle.get("pdus");
                    for(int i=0;i<pdus.length;i++){
                        byte[] pdu = (byte[])pdus[i];
                        SmsMessage message = SmsMessage.createFromPdu(pdu);
                       // nearCount=0;
                        String text = message.getDisplayMessageBody();
                        String sender = getContactName(context,message.getOriginatingAddress());
                        noPengirim=message.getOriginatingAddress();

                        speaker.pause(4000);
                        speaker.say("Anda mendapatkan es em es dari" + sender + "!",getApplicationContext());
                        speaker.say("Berikut merupakan isi pesan!",getApplicationContext());
                        speaker.pause(2000);
                        speaker.say(text,getApplicationContext());
                        speaker.pause(SHORT_DURATION);
                        speaker.say("apakah anda ingin membalas es em es tersebut?",getApplicationContext());
                        smsSender.setText("Pesan dari " + sender);
                        smsText.setText(text);

                        threadBackgroundForSMS= new Thread(runnableBackgroundForSMS);
                        threadBackgroundForSMS.start();

                    }
                }
            }
        };
    }
    //call1
    private void initializeCallReceiver(){

        callReceiver= new BroadcastReceiver() {

            private static final String TAG = "PhoneStatReceiver";

            private  boolean incomingFlag = false;

            private  String incoming_number = null;
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle=intent.getExtras();
                if (bundle !=null){

                    if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
                        incomingFlag = false;
                        String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                        Log.i(TAG, "call OUT:"+phoneNumber);

                    }else{
                        TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);

                        switch (tm.getCallState()) {

                            case TelephonyManager.CALL_STATE_RINGING:
                                //kondisi ketika state telepon sedang berdering
                                myProxService.nearCount=0;//aplikasi merestart nilai proximity
                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                currentSetting= sp.getString("radio","-1");
                                incomingFlag = true;
                                incoming_number = intent.getStringExtra("incoming_number");
                                Log.i(TAG, "RINGING :"+ incoming_number);
                                String pemanggil = getContactName(context, incoming_number);

                                speaker.say("Anda mendapatkan telpon dari " + pemanggil + "! ",getApplicationContext());
                                speaker.pause(1000);
                                speaker.say("apakah anda ingin menerima telepon?",getApplicationContext());
                                threadBackgroundForCall = new Thread(runnableBackgroundForCall);
                                threadBackgroundForCall.start();
                                break;

                            case TelephonyManager.CALL_STATE_OFFHOOK:
                                //kondisi ketika state telepon sedang menerima telepon
                                if(incomingFlag){
                                    Log.i(TAG, "incoming ACCEPT :"+ incoming_number);
                                  //  callHandler.ActivatedLoudspeaker(getApplicationContext());
                                }
                                break;
                            case TelephonyManager.CALL_STATE_IDLE:
                                //kondisi state telepon ketika tidak jalan/telah dimatikan
                                if(incomingFlag){
                                    Log.i(TAG, "incoming IDLE");
                                }
                                break;
                        }
                    }
                }
            }
    };}

    private String getContactName(Context context, String number){
        String name ="nomor";
        String TAG = "PhoneStatReceiver";
        // define the columns I want the query to return
        String[] projection = new String[] {
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID};

        // encode the phone number and build the filter URI
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        // query time
        Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                name =      cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                Log.v(TAG, "Started uploadcontactphoto: Contact Found @ " + number);
                Log.v(TAG, "Started uploadcontactphoto: Contact name  = " + name);
            } else {
                Log.v(TAG, "Contact Not Found @ " + number);
                name="nomor tidak dikenal";
            }
            cursor.close();
        }
        return name;
    }
    private void registerSMSReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, intentFilter);
    }

    //call
    private void registerCallReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PHONE_STATE");
        registerReceiver(callReceiver, intentFilter);
    }


    private void update(){
        proxiValue= myProxService.getNearCount();
    }
    private void disconnectCall(){
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            methodGetITelephony.setAccessible(true);

            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            methodEndCall.invoke(telephonyInterface);

        } catch (Exception ex) {
            Log.d(TAG, "PhoneStateReceiver **" + ex.toString());
        }
    }
    private void acceptCall() {
        //Log.d(tag, "InSecond Method Ans Call");
        // froyo and beyond trigger on buttonUp instead of buttonDown

        Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
        buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
                KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
        sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");

        Intent headSetUnPluggedintent = new Intent(Intent.ACTION_HEADSET_PLUG);
        headSetUnPluggedintent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        headSetUnPluggedintent.putExtra("state", 0);// 0 = unplugged  1 = Headset with microphone 2 = Headset without microphone
        headSetUnPluggedintent.putExtra("name", "Headset");
        try {
            sendOrderedBroadcast(headSetUnPluggedintent, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void answerCall(final Context mContext) {

        Log.d(TAG, "accept the call ====");
        Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
        buttonUp.putExtra(Intent.EXTRA_KEY_EVENT,
                new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
        mContext.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
    }

    public void ChangeToggleStatus(){
        toggle.toggle();
    }
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
        unregisterReceiver(callReceiver);
        unregisterReceiver(uiUpdated);
        unregisterReceiver(uiProxiUpdated);
        unregisterReceiver(callRespond1);
        unregisterReceiver(callRespond2);
        stopService(new Intent(MainActivity.this, ProximityService.class));
        stopService(new Intent(MainActivity.this, CounterService.class));
        //stopService(new Intent(MainActivity.this, ResponService.class));
        speaker.destroy();
    }
}
