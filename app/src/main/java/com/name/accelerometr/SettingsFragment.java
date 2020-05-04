package com.name.accelerometr;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.takisoft.preferencex.PreferenceFragmentCompat;


public class SettingsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }


}
