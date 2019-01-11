package com.kavinoff.brian.tp.brian_kavinoff_parcial_2;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SettingActivity extends AppCompatActivity {

    public Boolean mostrarNotificaciones;
    public Boolean vibrar;
    public String tiempoVibrar;
    public Boolean recordarUsuario;
    public String usuarioRecordado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //levanto el fragment settings
        mostrarSettings();

        //obtengo los datos
        obtenerDatosSettings();

    }
    protected void mostrarSettings(){
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new settingsFragment())
                .commit();
    }

    protected void obtenerDatosSettings(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        mostrarNotificaciones = pref.getBoolean("chkpMostrarNotificaciones",true);
        vibrar= pref.getBoolean("chkpVibrar",true);
        tiempoVibrar =  pref.getString("etpTiempoVibrar","10000");
        recordarUsuario= pref.getBoolean("chkpRecordarUsuario",false);
        usuarioRecordado =  pref.getString("usuarioRecordado","");

        if (!mostrarNotificaciones) {
            pref.edit().putBoolean("chkpVibrar", false).apply();
            pref.edit().putString("etpTiempoVibrar", "0").apply();
            pref.edit().commit();
        }

    }


}
