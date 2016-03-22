package com.barneswebb.android.tts.trainingrec;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.barneswebb.android.tts.R;

public class MyTrainingRecordActivity extends AppCompatActivity implements TrainingFragment.OnListFragmentInteractionListener  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytrainingrecord);

        ((TextView)findViewById(R.id.my_trainingheading)).setText( "Training record of " +
                PreferenceManager.getDefaultSharedPreferences(MyTrainingRecordActivity.this).getString("username", "<not set>")
                +":"
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
        //wot goes 'ere?
    }
}
