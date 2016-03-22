package com.barneswebb.android.tts.trainingrec;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.barneswebb.android.tts.R;

public class MyTrainingRecordActivity extends AppCompatActivity implements TrainingFragment.OnListFragmentInteractionListener  {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytrainingrecord);

        MyTrainingRecordActivity.this.setTitle(
                PreferenceManager.getDefaultSharedPreferences(MyTrainingRecordActivity.this).getString("username", "<not set>")
                        + "'s Training Record"
        );

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MyTrainingRecordActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete Record")
                        .setMessage("Please confirm that you want to delete all exercise records?\n" +
                                "(Cannot be undone)")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which)  {
                                ExerciseDataOpenHelper db = new ExerciseDataOpenHelper(MyTrainingRecordActivity.this);
                                db.delAllData();
                                MyTrainingRecordActivity.this.finish();
                            } })
                        .setNegativeButton("Cancel", null)
                        .show();

            }
                    //db.delAllSMSes();

        });
    }

    @Override
    public void onListFragmentInteraction(ExerciseSession item) {
        Log.d("ttsTraininRecAct", "wot goes 'ere?");
        //wot goes 'ere?
    }
}
