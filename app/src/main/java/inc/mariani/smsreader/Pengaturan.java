package inc.mariani.smsreader;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 12/17/2016.
 */
public class Pengaturan  extends PreferenceActivity {
  //  private static final String KEY_EDIT_TEXT_PREFERENCE = "template1";
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.autoread);
        //menggunakan preference yang ada di file xml/autoread
    }
}
