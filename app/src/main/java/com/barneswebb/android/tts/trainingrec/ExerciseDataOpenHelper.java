/**
 * 
 */
package com.barneswebb.android.tts.trainingrec;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.barneswebb.android.tts.Debug;

/**
 * @author rbw
 *
 */
public class ExerciseDataOpenHelper extends SQLiteOpenHelper
{

    private static final String tag                 = ExerciseDataOpenHelper.class.getPackage().getName() + "]"
                                                    + ExerciseDataOpenHelper.class.getSimpleName();
    private static final int    DATABASE_VERSION    = 1;
    private static final String TRAININGHISTORY_TABLE_NAME = "myTrainingHistory";
    Context                     context;
    public static final String[] FIELD_NAMES            = new String[]{"id", "userName", "excerzDate", "excerzDur", "program", "comments"};
    private static final String PRIMARY_KEY              = FIELD_NAMES[0];

    public static final SimpleDateFormat ISO8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static final Map<String, String> map = new HashMap<String, String>() {{
            put("foo", "bar");
            put("x", "y");
    }};


    public ExerciseDataOpenHelper(Context context)
    {
        super(context, TRAININGHISTORY_TABLE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }



    public Context getContext()
    {
        return context;
    }



    @Override public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TRAININGHISTORY_TABLE_NAME);
        Log.i(tag, "Creating new table: " + TRAININGHISTORY_TABLE_NAME);

        String sqlStr = "CREATE TABLE " + TRAININGHISTORY_TABLE_NAME + "          (" ;
        
        for (String f: FIELD_NAMES) {
            sqlStr += f.equals(PRIMARY_KEY)?
                " "+PRIMARY_KEY+"   INTEGER PRIMARY KEY AUTOINCREMENT " :
                "    ,"+f+"         TEXT " ;
        }
        sqlStr += ");";


        db.execSQL(sqlStr);
    }



    @Override public void onUpgrade(SQLiteDatabase db,  int oldVersion, int newVersion)
    {
        Log.w(tag, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TRAININGHISTORY_TABLE_NAME);
        onCreate(db);
    }
    
    
    public List<ExerciseSession> getExerciseRowData()
    {
        Log.d(tag, "Loading exercise data....");

        ArrayList<ExerciseSession> retArr = new ArrayList();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cur;
        
        try {
            boolean DISTINCT        = true;
            String  WHERE           = null;
            String[]SELECTIONARGS   = null;
            String  GROUPBY         = null;
            String  HAVING          = null;
            String  ORDERBY         = "excerzDate DESC";
            String  LIMIT           = null;

            cur = db.query(DISTINCT, TRAININGHISTORY_TABLE_NAME, FIELD_NAMES, WHERE, SELECTIONARGS, GROUPBY, HAVING, ORDERBY, LIMIT);
        }
        catch (Exception e)
        {
            Debug.bummer(e, context);
            return retArr;
        }


        
        cur.moveToFirst();
        while (cur.isAfterLast() == false) {
            retArr.add(
                new ExerciseSession(
                       cur.getInt(0), //id;
                    cur.getString(1), //userName;
                    cur.getString(2), //excerzDate;
                    cur.getString(3), //excerzDur;
                    cur.getString(4), //program;
                    cur.getString(5)  //comments;
                )
            );

            cur.moveToNext();
        }
        
        db.close();
        cur.close();
        return retArr;
    }



    public boolean createExcercise(Map<String,String> data)
    {
        SQLiteDatabase db = getWritableDatabase();
        long rowid = db.insert(TRAININGHISTORY_TABLE_NAME, null, initConentValues(data));
        db.close();
   
        return ( rowid >= 0 );
    }

    
    public boolean saveExcercise(Map<String,String> data)
    {
        SQLiteDatabase db = getWritableDatabase();
        long rowid = db.update(TRAININGHISTORY_TABLE_NAME, initConentValues(data), PRIMARY_KEY+" = ?", new String[]{data.get(PRIMARY_KEY)});
        db.close();
   
        return ( rowid >= 0 );
    }

    private ContentValues initConentValues(Map<String, String> data) {
        ContentValues values = new ContentValues();
        for (String f: FIELD_NAMES) values.put(f, data.get(f));
        return values;
    }


    public void delAllData()
    {
        Log.w(tag, "Blitzing the lot..");
        onCreate(getWritableDatabase());
        
    }



    public boolean deleteExercise(String idStr)
    {
        SQLiteDatabase db = getWritableDatabase();
        long rowcount = db.delete(TRAININGHISTORY_TABLE_NAME, PRIMARY_KEY+" = ?", new String[] {idStr});
        db.close();
   
        return ( rowcount > 0 );
    }

}
