package com.name.accelerometr;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        Button button = findViewById(R.id.nextActivity);
        button.setOnClickListener(v -> openMainActivity());
        checkFirstOpen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    private void checkFirstOpen() {
        boolean isFirstRun = getSharedPreferences("com.name.accelerometr", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        if (!isFirstRun) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        getSharedPreferences("com.name.accelerometr", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
    }
}
