package com.kavinoff.brian.tp.brian_kavinoff_parcial_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kavinoff.brian.tp.brian_kavinoff_parcial_2.DBHelper.DBHelper;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsuario;
    private EditText etContrasena;
    private Button btnRegistro;
    private Button btnIngresar;
    private String usuario;
    private String contrasena;
    private ProgressBar progressBar;
    public Boolean recordarUsuario;
    public CheckBox chkRecordarUsuario;
    public String usuarioRecordado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //levanto las preferencias
        setupPreferences();

        //seteo la interfaz
        setupUI();
        initializeChkListener();
        initializeBtnListener();
    }

    private void setupPreferences() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        recordarUsuario = pref.getBoolean("chkpRecordarUsuario", false);
        usuarioRecordado = pref.getString("usuarioRecordado", "");
    }

    private void setupUI() {
        etUsuario = findViewById(R.id.et_usuario_login);
        etContrasena = findViewById(R.id.et_contrasena_login);

        btnRegistro = findViewById(R.id.btn_registro_login);
        btnIngresar = findViewById(R.id.btn_ingresar_login);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        chkRecordarUsuario = findViewById(R.id.chkRecordarUsuario);
        chkRecordarUsuario.setChecked(recordarUsuario);
        if (recordarUsuario) {
            etUsuario.setText(usuarioRecordado);
            etContrasena.requestFocus();
        }
    }

    private void initializeChkListener() {
        chkRecordarUsuario.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

                if (isChecked) {

                    pref.edit().putBoolean("chkpRecordarUsuario", true).apply();
                    pref.edit().putString("usuarioRecordado", etUsuario.getText().toString()).apply();
                    pref.edit().commit();
                } else {
                    pref.edit().putBoolean("chkpRecordarUsuario", false).apply();
                    pref.edit().putString("usuarioRecordado", "").apply();
                    pref.edit().commit();
                }
            }

        });
    }

    private void initializeBtnListener() {
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //validar campos no vacíos
                if (EstaVacio(etUsuario.getText().toString())
                        || EstaVacio(etContrasena.getText().toString())) {
                    Toast.makeText(getApplicationContext(), R.string.toast_campos_obligatorios, Toast.LENGTH_LONG).show();
                    return;
                }

                //obtener ingreso
                usuario = etUsuario.getText().toString();
                contrasena = etContrasena.getText().toString();

                //VERIFICO DATOS VALIDOS
                if (EstaVacio(usuario) || EstaVacio(contrasena)) {
                    Toast.makeText(getApplicationContext(), R.string.toast_campos_obligatorios, Toast.LENGTH_LONG).show();
                    return;
                }
                new ObtenerUsuarioAsync().execute();
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Inicio la pantalla de Registro
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean EstaVacio(String numero) {
        return (numero.isEmpty());
    }

    private boolean ClaveOK(Usuario usuario, String claveIngresada) {
        return usuario.getPassword().equals(claveIngresada);
    }

    private class ObtenerUsuarioAsync extends AsyncTask<String, Void, Usuario> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            btnIngresar.setEnabled(false);
        }

        @Override
        protected Usuario doInBackground(String... strings) {
            Usuario UsuarioEncontrado = new Usuario();
            try {
                UsuarioEncontrado = DBHelper.getInstance(LoginActivity.this).obtenerUsuarioPorUsername(usuario);
            } catch (Exception e) {
                Log.i("TEST", "Error al obtener usuario");
            }
            return UsuarioEncontrado;
        }

        @Override
        protected void onPostExecute(Usuario UsuarioEncontrado) {
            progressBar.setVisibility(View.INVISIBLE);
            btnIngresar.setEnabled(true);

            //Encontró al usuario?
            if (UsuarioEncontrado.getUsername() == null) {
                Toast.makeText(getApplicationContext(), R.string.toast_usuario_no_existe, Toast.LENGTH_LONG).show();
                return;
            }

            //Coincide la clave?
            if (!ClaveOK(UsuarioEncontrado, contrasena)) {
                Toast.makeText(getApplicationContext(), R.string.toast_password_incorrecto, Toast.LENGTH_LONG).show();
                return;
            }

            //Usuario y Clave OK, creo el intent a la pantalla principal
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


            //con finish cierro el activity
            finish();
        }
    }
}


