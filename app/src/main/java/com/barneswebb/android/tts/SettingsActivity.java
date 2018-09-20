package com.barneswebb.android.tts;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //rbw20180912, was: addPreferencesFromResource(R.xml.preferences);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    //@see https://stackoverflow.com/a/13441715
    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    //**RBW: @thanks: http://stackoverflow.com/a/15179475
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            int SUCCESS_RESULT=1;
            setResult(SUCCESS_RESULT, new Intent());
            finish();  //return to caller
            return true;
        }
        return false;
    }

}
