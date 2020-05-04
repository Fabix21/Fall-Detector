package com.name.accelerometr;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle("Ustawienia");
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null)
                return;

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new SettingsFragment()).commit();

        }
    }
}
