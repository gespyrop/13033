package com.example.sms13033;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.sms13033.models.TransportReason;

import java.util.ArrayList;

/**
 * <p>
 * A helper class to provide a high level interface for interacting with SQLite
 * in order to create,retrieve,update or delete transport reasons.
 * It uses the Singleton design pattern to keep only
 * one instance of the DBHelper class.
 * </p>
 * <br/><b><u>Example:</u></b> DBHelper db = DBHelper.getInstance(getApplicationContext());
 * @author  George Spyropoulos
 * */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null)
            instance = new DBHelper(context.getApplicationContext());

        if (instance.getTransportReasons().isEmpty()) {
            instance.saveTransportReason(new TransportReason(1, "Μετάβαση σε φαρμακείο ή γιατρό"));
            instance.saveTransportReason(new TransportReason(2, "Προμήθειες αγαθών πρώτης ανάγκης"));
            instance.saveTransportReason(new TransportReason(3, "Μετάβαση στην τράπεζα"));
            instance.saveTransportReason(new TransportReason(4, "Παροχή βοήθειας σε ανθρώπους που βρίσκονται σε ανάγκη"));
            instance.saveTransportReason(new TransportReason(5, "Μετάβαση σε τελετή ή εν διαστάσει γονέων σε τέκνα"));
            instance.saveTransportReason(new TransportReason(6, "Σωματική άσκηση σε εξωτερικό χώρο ή κίνηση με κατοικίδιο ζώο"));
        }

        return instance;
    }

    private DBHelper(Context context) {
        super(context, "DB13033", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS TransportReasons(id INTEGER PRIMARY KEY AUTOINCREMENT, code INT, description TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS TransportReasons;");
    }

    public boolean saveTransportReason(TransportReason tr) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("code", tr.getCode());
        contentValues.put("description", tr.getDescription());

        return db.insert("TransportReasons", null, contentValues) != -1;
    }

    public ArrayList<TransportReason> getTransportReasons() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM TransportReasons", null);

        ArrayList<TransportReason> transportReasons = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                int code = c.getInt(1);
                String description = c.getString(2);

                TransportReason tr = new TransportReason(id, code, description);

                transportReasons.add(tr);

            } while (c.moveToNext());
        }
        c.close();

        return transportReasons;
    }
}
