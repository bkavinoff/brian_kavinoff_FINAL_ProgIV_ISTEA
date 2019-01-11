package com.kavinoff.brian.tp.brian_kavinoff_parcial_2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kavinoff.brian.tp.brian_kavinoff_parcial_2.DBHelper.DBHelper;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {

    Toolbar toolbar;
    private EditText etUsuario;
    private EditText etPassword;
    private EditText etConfirmacionPassword;
    private EditText etMail;
    private Button btnRegistrar;
    private Button btnLimpiar;

    private String usuario;
    private String password;
    private String confirmacionPassword;
    private String mail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //seteo la toolbar
        setupToolbar();

        //seteo la interfaz
        setupUI();
        initializeBtnListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //se hace referencia al xml que se va a inflar
        getMenuInflater().inflate(R.menu.menu_vacio, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //con finish cierro el activity
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle(R.string.btn_registro);

        //para que se muestre la flechita para volver
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupUI() {
        etUsuario = findViewById(R.id.et_usuario_registro);
        etPassword = findViewById(R.id.et_password_registro);
        etConfirmacionPassword = findViewById(R.id.et_password_confirmacion);
        etMail = findViewById(R.id.et_mail_registro);

        btnRegistrar = findViewById(R.id.btn_registro_registro);
        btnLimpiar = findViewById(R.id.btn_limpiar_registro);
    }

    private void initializeBtnListener() {
        etUsuario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String texto = etUsuario.getText().toString();
                    if (!texto.equals("")) {
                        new ExisteUserAsync().execute(texto);
                    }
                }
            }
        });
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validar campos no vacíos
                if (EstaVacio(etUsuario.getText().toString())
                        || EstaVacio(etPassword.getText().toString())
                        || EstaVacio(etConfirmacionPassword.getText().toString())
                        || EstaVacio(etMail.getText().toString())) {
                    Toast.makeText(getApplicationContext(), R.string.toast_campos_obligatorios, Toast.LENGTH_LONG).show();
                    return;
                }

                //obtener ingreso
                usuario = etUsuario.getText().toString();
                password = etPassword.getText().toString();
                confirmacionPassword = etConfirmacionPassword.getText().toString();
                mail = etMail.getText().toString();

                if (!password.equals(confirmacionPassword)) {
                    Toast.makeText(getApplicationContext(), R.string.toast_password_no_coinciden, Toast.LENGTH_LONG).show();
                    return;
                }
                if (!mailValido(mail)) {
                    Toast.makeText(getApplicationContext(), R.string.mail_invalido, Toast.LENGTH_LONG).show();
                    return;
                }

                Usuario user = new Usuario();
                user.setUsername(usuario);
                user.setPassword(password);
                user.setMail(mail);

                //Llamo al asynctask que verifica si existe el usuario?
                new BuscarUsuarioAsync().execute(user);
            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Limpio los campos
                etUsuario.setText("");
                etPassword.setText("");
                etConfirmacionPassword.setText("");
                etMail.setText("");
            }
        });
    }

    private boolean EstaVacio(String string) {
        return (string.isEmpty());
    }

    private boolean mailValido(String mail) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(mail).matches();
    }
    private class BuscarUsuarioAsync extends AsyncTask<Usuario, Void, Usuario> {
        boolean existeUsuario = false;
        Usuario usuarioARegistrar = new Usuario();

        @Override
        protected Usuario doInBackground(Usuario... usuarios) {
            String username = usuarios[0].getUsername();
            usuarioARegistrar=usuarios[0];

            //verifico que si existe un usuario con ese username y lo traigo
            return DBHelper.getInstance(RegistroActivity.this).obtenerUsuarioPorUsername(username);
        }

        @Override
        protected void onPostExecute(Usuario usuarioBuscado) {
            super.onPostExecute(usuarioBuscado);
            ExisteUsuario(usuarioBuscado);
        }

        private void ExisteUsuario(Usuario usuario) {
            if (usuario.getUsername() != null) {
                if (usuario.getUsername().equals(usuarioARegistrar.getUsername())) {
                    Log.i("TEST", "Ya existe");
                    Toast.makeText(RegistroActivity.this, "El usuario ya existe", Toast.LENGTH_LONG).show();
                    existeUsuario = true;
                    btnRegistrar.setEnabled(true);
                }
            }else{
                //procedo al registro
                new RegistrarUsuarioAsync().execute(usuarioARegistrar);
            }
        }
    }

    private class RegistrarUsuarioAsync extends AsyncTask<Usuario, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            btnRegistrar.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Usuario... usuarios) {
            Usuario usuario = usuarios[0];
            return DBHelper.getInstance(RegistroActivity.this).insertarUsuario(usuario);
        }

        @Override
        protected void onPostExecute(Boolean usuarioInsertado) {
            super.onPostExecute(usuarioInsertado);
            btnRegistrar.setEnabled(true);

            if (usuarioInsertado) {
                usuarioInsertadoOK();
            }
        }

        private void usuarioInsertadoOK() {
            //Ahora que ya está registrado, creo el intent para que entre al main:
            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Toast.makeText(RegistroActivity.this, "El usuario fue creado correctamente", Toast.LENGTH_SHORT).show();
            startActivity(intent);

            //con finish cierro el activity
            finish();
        }
    }

    private class ExisteUserAsync extends AsyncTask<String, Void, Usuario> {

        @Override
        protected Usuario doInBackground(String... strings) {
            return DBHelper.getInstance(RegistroActivity.this).obtenerUsuarioPorUsername(strings[0]);
        }

        @Override
        protected void onPostExecute(Usuario usuarioEncontrado) {
            verificarPrenda(usuarioEncontrado);
        }

        private void verificarPrenda(Usuario user) {
            //Encontró la prenda?
            if (user.getUsername() != null) {
                if (!user.getUsername().equals("")) {
                    Log.i("TEST", "Ya existe");
                    Toast.makeText(RegistroActivity.this, R.string.toast_usuario_ya_existe, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

