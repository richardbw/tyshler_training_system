package com.barneswebb.android.tts.trainingrec;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.barneswebb.android.tts.Debug;
import com.barneswebb.android.tts.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyTrainingRecordActivity extends AppCompatActivity implements TrainingFragment.OnListFragmentInteractionListener  {
    private static final String  TAG            = "ttsMyTrainingRecord";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytrainingrecord);

        MyTrainingRecordActivity.this.setTitle(
                PreferenceManager.getDefaultSharedPreferences(MyTrainingRecordActivity.this).getString("username", "<not set>") + "'s Training Record"
        );

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MyTrainingRecordActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete Record")
                        .setMessage("Please confirm that you want to delete all exercise records?\n" +
                                "(Cannot be undone)")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which)  {
                                new ExerciseDataOpenHelper(MyTrainingRecordActivity.this).delAllData();
                                MyTrainingRecordActivity.this.finish();
                            } })
                        .setNegativeButton("Cancel", null)
                        .show();

            }
        });
        findViewById(R.id.fab_email).setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view) {

                final String username       = PreferenceManager.getDefaultSharedPreferences(MyTrainingRecordActivity.this).getString("username", "      #User#");
                final String email_dest     = PreferenceManager.getDefaultSharedPreferences(MyTrainingRecordActivity.this).getString("email_dest",      "richard_bw@gmail.com");
                final String email_subject  = PreferenceManager.getDefaultSharedPreferences(MyTrainingRecordActivity.this).getString("email_subject",   username+"'s training records");
                final String email_body     = PreferenceManager.getDefaultSharedPreferences(MyTrainingRecordActivity.this).getString("email_body",      username+"'s training records");

                if (! isExternalStorageWritable()) Debug.dialog("Unwriteable External Storage", "Unable to save records to external storage.  Is you SD card mounted correctly", MyTrainingRecordActivity.this);
                else {
                    new AlertDialog.Builder(MyTrainingRecordActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_email)
                            .setTitle("Email Your Record")
                            .setMessage("Save your exercise records to a .csv on your external storage and email to <"+email_dest+"> ?")
                            .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which)
                                {
                                    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                                    emailIntent.setType("plain/text");
                                    emailIntent.putExtra(Intent.EXTRA_EMAIL,    new String[] { email_dest });
                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT,  email_subject);
                                    emailIntent.putExtra(Intent.EXTRA_TEXT,     email_body);
                                    emailIntent.putExtra(Intent.EXTRA_STREAM,   getTrainingRecords(username));
                                    MyTrainingRecordActivity.this.startActivity(Intent.createChooser(emailIntent, "Sending email..."));

                                    MyTrainingRecordActivity.this.finish();
                                } })
                            .setNegativeButton("Cancel", null)
                            .show();
                }//fi
            }
        });

    }

    public boolean isExternalStorageWritable() {
        return (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()));
    }

    private Uri getTrainingRecords(String username)
    {
        final Uri csvUri= Uri.parse("file://" + Environment.getExternalStorageDirectory()+"/tts-"+username+"-"+new SimpleDateFormat("yyyy-MM-dd'T'hh_mm_ss").format(new Date())+".csv");
        Log.d(TAG, "Will write to: "+csvUri);
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(csvUri.getPath())));
            out.println(ExerciseSession.csvHeaders());
            for (ExerciseSession session: new ExerciseDataOpenHelper(MyTrainingRecordActivity.this).getExerciseRowData() ) out.println(session.toCsvRecord());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            Debug.bummer(e, MyTrainingRecordActivity.this);
        }

        return csvUri;
    }

    @Override
    public void onListFragmentInteraction(ExerciseSession item) {
        Log.d("ttsTraininRecAct", "wot goes 'ere?");
        //wot goes 'ere?
    }
}


/*
        final ProgressDialog progDailog = ProgressDialog.show(MyTrainingRecordActivity.this, "Writing records", "even geduld aub...", true);//please wait
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {


            }
        };

        MyTrainingRecordActivity.this.runOnUiThread(new Runnable() {
            @Override public void run() {
                handler.sendEmptyMessage(0);
                progDailog.dismiss();
            }
        });

 */
