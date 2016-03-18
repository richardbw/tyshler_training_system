package com.barneswebb.android.tts;

import static com.barneswebb.android.tts.trainingrec.ExerciseDataOpenHelper.*;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import com.barneswebb.android.tts.trainingrec.ExerciseDataOpenHelper;
import com.barneswebb.android.tts.trainingrec.MyTrainingRecordActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends AppCompatActivity {

    private static final int TOTAL_NO_PROGRAMMES = 8;
    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Map<String,List<String>> soundList = new HashMap<>();

    protected static ExerciseDataOpenHelper db;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private CustomViewPager mCustomViewPager;
    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mCustomViewPager = (CustomViewPager) findViewById(R.id.custom_viewpager);
        mCustomViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mCustomViewPager);

        db = new ExerciseDataOpenHelper(this);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        //loadAssets();
    }

    /** this method is now superflous, since i had to opt for reading from a zip file rather than assets */
    private void loadAssets()
    {
        AssetManager assetMgr = getAssets();

        try
        {
            String programmeList[] = assetMgr.list(getString(R.string.tts_setting_prog_dir));

            for (String prog : programmeList) {
                List<String> progSoundList = new ArrayList<>();
                for (String sndFile: assetMgr.list(getString(R.string.tts_setting_prog_dir)+"/"+prog) ) {
                    if ( ! sndFile.endsWith("index.html"))
                        progSoundList.add(getString(R.string.tts_setting_prog_dir)+'/'+prog+'/'+sndFile);
                }
                Collections.sort(progSoundList); //sort, by default - fragment will randomize
                soundList.put(prog, progSoundList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<String> getSoundList(String progKey) {
        return soundList.get(progKey);
    }
    public int getNoProgs() {
        return soundList.keySet().size();
    }

    void setPagingEnabled(boolean enabled){
        mCustomViewPager.setPagingEnabled(enabled);
        tabLayout.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_trainingrecord:
                startActivity(new Intent(this, MyTrainingRecordActivity.class));
                break;
            /*case R.id.action_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), 1);
                break;*/
            case R.id.action_about:
                aboutDlg();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    // http://stackoverflow.com/questions/2257963/android-how-to-show-dialog-to-confirm-user-wishes-to-exit-activity
    public boolean confirmFinish() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Quit")
                .setMessage("Have you saved all?")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which)  {
                        MainActivity.this.finish();
                    } })
                .setNegativeButton("Cancel", null)
                .show();

        return true;
    }


    private void aboutDlg()
    {
        String verString = "Release: "+ BuildConfig.VERSION_NAME+" ("+BuildConfig.VERSION_CODE+")";

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.tyshler_0x3f51b5)
                .setTitle("About")
                .setMessage("(c) 2016 FencingMultimedia.com\n\nDevelopment:\nRichard@Barnes-Webb.com\n"+verString+"\nAndroid API ver: "+android.os.Build.VERSION.SDK_INT)
                .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which)  { dialog.dismiss();  } })
                .show();

    }


    //*********************************************************************************************

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ProgrammeFragment (defined as a static inner class below).
            return ProgrammeFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return TOTAL_NO_PROGRAMMES;// TODO: getNoProgs();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(R.string.tts_setting_prog_disp_pref) + zeroPaddedStr(position + 1);
        }
    }

    //*********************************************************************************************

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ProgrammeFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        MediaPlayer     player          = new MediaPlayer();
        AssetManager    fragAssetMgr    ;
        String currentProg;
        List<String> progSoundList;
        //Queue<String> playQueue;
        Button soundButton;
        Chronometer chronometer;
        TextView currentSound;

        final Random random = new Random();

        public ProgrammeFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ProgrammeFragment newInstance(int sectionNumber) {
            ProgrammeFragment fragment = new ProgrammeFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            //init 'globals'(class fields):
            currentProg = getString(R.string.tts_setting_prog_pref) +zeroPaddedStr(getArguments().getInt(ARG_SECTION_NUMBER));
            fragAssetMgr = getActivity().getAssets();
            soundButton = (Button) rootView.findViewById(R.id.start_button);     //http://stackoverflow.com/a/18459352
            chronometer = (Chronometer) rootView.findViewById(R.id.chronometer);
            currentSound  = (TextView) rootView.findViewById(R.id.current_sound);

            final TextView currentSound = (TextView) rootView.findViewById(R.id.current_sound);
            final WebView excerciseText = (WebView) rootView.findViewById(R.id.excercise_text);

            excerciseText.getSettings().setJavaScriptEnabled(true);
            excerciseText.loadDataWithBaseURL("", readRawFile(currentProg, getActivity()), "text/html", "UTF-8", ""); //http://stackoverflow.com/a/13741394

            progSoundList = cacheFilesinSoundsZip();

            soundButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!soundButton.getText().equals(getString(R.string.tts_button_stop))) {
                        ((MainActivity) getActivity()).setPagingEnabled(false);
                        soundButton.setText(getString(R.string.tts_button_stop));
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();

                        playSndFile(getRandomSoundFromList());
                    } else // STOP
                    {
                        ((MainActivity) getActivity()).setPagingEnabled(true);
                        chronometer.stop();
                        player.reset();
                        player.release();
                        saveExercise();
                        soundButton.setText(getString(R.string.tts_button_start));
                    }

                }

            });

            return rootView;
        }

        private String getRandomSoundFromList() {
            return progSoundList.get(random.nextInt(progSoundList.size()));
        }

        private void saveExercise() {

            final Map<String, String> data = new HashMap<String, String>() {{
                put(FIELD_NAMES[1], PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.tts_pref_username), "not_set")); //userName
                put(FIELD_NAMES[2], ISO8601Format.format(new Date())); //excerzDate
                put(FIELD_NAMES[3], chronometer.getText().toString()); //excerzDur
                put(FIELD_NAMES[4], currentProg); //program
            }};


            final EditText input = new EditText(getActivity());
            (new AlertDialog.Builder(getActivity()))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Save Exercise")
                .setMessage("How did it go?:")
                .setView(input)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        data.put(FIELD_NAMES[5], input.getText().toString());
                        db.createExcercise(data);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
            .show();

        }

        /** cache files, and return a list of the file names
         * @thanks: http://stackoverflow.com/a/11615158 */
        private List<String> cacheFilesinSoundsZip() {
            currentSound.setText("Caching sounds...");
            ArrayList<String> retList = new ArrayList<String>();
            try {
                ZipInputStream zipIs = new ZipInputStream(
                        getActivity().getResources().openRawResource(
                                getResources().getIdentifier(currentProg+"_sounds", "raw", getActivity().getPackageName())
                        )
                );
                ZipEntry ze = null;

                while ((ze = zipIs.getNextEntry()) != null) {
                    retList.add(ze.getName());

                    String cacheFileName = getActivity().getCacheDir() +"/"+ ze.getName();

                    if ((new File(cacheFileName).exists())) continue;  //if it's already cached then ignore

                    FileOutputStream fout = new FileOutputStream(cacheFileName);
                    byte[] buffer = new byte[1024]; int length = 0;

                    while ((length = zipIs.read(buffer))>0) {
                        fout.write(buffer, 0, length);
                    }
                    zipIs .closeEntry();
                    fout.close();
                }
                zipIs.close();
            } catch (Exception e) {
                Debug.bummer(e, getActivity());
            }
            currentSound.setText("");
            return retList;
        }



        MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                playSndFile(getRandomSoundFromList());
            }
        };

        void playSndFile(String sndFile){
            try {
                currentSound.setText(sndFile);
                player = new MediaPlayer();
                player.setOnCompletionListener(completionListener);
                player.setDataSource(new FileInputStream(getActivity().getCacheDir() +"/"+sndFile).getFD());
                player.prepare();
                player.start();
            } catch (Exception e) {
                Debug.bummer(e, getActivity());
            }
        }

    }



    //*********************************************************************************************
    //*********************************************************************************************

    public static String zeroPaddedStr(int anInt) {
        return String.format("%02d", anInt);
    }


    /**
     * http://stackoverflow.com/a/10043533
     */
    public static String readFile(String fileName, Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets().open(fileName, Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null) isr.close();
                if (fIn != null) fIn.close();
                if (input != null) input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }


    public static String readRawFile(String currentProg, Context context) {
        //return new Scanner(context.getResources().openRawResource(
         //       context.getResources().getIdentifier(currentProg+"_index", "raw", context.getPackageName())
        //   )
        //).useDelimiter("\\z").next();
        InputStream is = context.getResources().openRawResource(context.getResources().getIdentifier(currentProg+"_index", "raw", context.getPackageName()));
        try
        { //ta: http://stackoverflow.com/a/16161277
            byte[] buffer = new byte[0];
            buffer = new byte[is.available()];
            while (is.read(buffer) != -1);
            return new String(buffer);
        }
        catch (IOException e) {
            Debug.bummer(e, context);
        }
        return "<err>";
    }



}
