package inc.mariani.smsreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by user on 12/8/2016.
 */
public class DataHelper extends SQLiteOpenHelper {
    //Class ini digunakan untuk pengaksesan SQLite database
    private static final String DATABASE_NAME = "keyword.db";
    private static final int DATABASE_VERSION = 1;
    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
}

    @Override
    public void onCreate(SQLiteDatabase db) {
    //membuat tabel pada database
        String sql = "create table keyword( namaKeyword text null, isiPesan text null);";
        Log.d("Data", "onCreate: " + sql);
        db.execSQL(sql);
        sql = "INSERT INTO keyword (namaKeyword, isiPesan) VALUES ('Sibuk', 'ini message pertama')," +
                "('Terima kasih', ' ')," +
                "('sibuk informal', '')," +
                "('Berkendara', '')," +
                "('lima', '')," +
                "('enam', 'ini isi keyword yang ke-enam');";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
