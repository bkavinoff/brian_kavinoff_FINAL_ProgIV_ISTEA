package com.kavinoff.brian.tp.brian_kavinoff_parcial_2;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;


public class settingsFragment extends PreferenceFragment {
    @Override
    //escribo OnCreate y selecciono el primero para que se autocomplete.
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //llamo al add preference y le paso el xml de mi settings
        addPreferencesFromResource(R.xml.settings);

    }
}