package inc.mariani.smsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by user on 11/10/2016.
 */
public class PhoneCallReceiver extends BroadcastReceiver {
    Context context = null;
    private static final String TAG = "Phone call";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals("android.intent.action.PHONE_STATE"))
            return;
        else {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            //    answerPhoneHeadsethook(context, intent);
                return;
            }
            else if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                Log.d(TAG, "CALL ANSWERED NOW!!");
                try {
                    synchronized(this) {
                        Log.d(TAG, "Waiting for 10 sec ");
                        this.wait(10000);
                    }
                }
                catch(Exception e) {
                    Log.d(TAG, "Exception while waiting !!");
                    e.printStackTrace();
                }
              //  disconnectPhoneItelephony(context);
                return;
            }
            else {
                Log.d(TAG, "ALL DONE ...... !!");
            }
        }
    }

        }