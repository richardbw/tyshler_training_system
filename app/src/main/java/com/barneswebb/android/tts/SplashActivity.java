package com.barneswebb.android.tts;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

/**
 * thanks: https://www.bignerdranch.com/blog/splash-screens-the-right-way/
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("ttsSpash", "==============> Starting MainActivity <===============");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
