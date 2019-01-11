package com.kavinoff.brian.tp.brian_kavinoff_parcial_2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kavinoff.brian.tp.brian_kavinoff_parcial_2.DBHelper.DBHelper;

import java.util.Random;

public class AgregarActivity extends AppCompatActivity {
    Toolbar toolbar;
    private EditText etCodigo;
    private EditText etNombre;
    private EditText etDescripcion;
    private EditText etColor;
    private EditText etTalle;
    private Button btnAgregar;
    private Button btnLimpiar;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String color;
    private String talle;
    private RadioGroup rgEnStock;
    private boolean enStock;
    private RadioButton rbEnStock;

    public Boolean mostrarNotificaciones;
    public Boolean vibrar;
    public String tiempoVibrar;

    private Prenda prendaAAgregar = new Prenda();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);

        //levanto las preferencias
        setupPreferences();

        //seteo la toolbar
        setupToolbar();

        //seteo la interfaz
        setupUI();
        setupListeners();
    }

    private void setupPreferences() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        mostrarNotificaciones = pref.getBoolean("chkpMostrarNotificaciones", true);
        vibrar = pref.getBoolean("chkpVibrar", false);
        tiempoVibrar = pref.getString("etpTiempoVibrar", "1000");
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
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        getSupportActionBar().setSubtitle(getResources().getString(R.string.title_activity_agregar));

        //para que se muestre la flechita para volver
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupUI() {

        etCodigo = findViewById(R.id.et_codigo_nueva_prenda);
        etNombre = findViewById(R.id.et_nombre_nueva_prenda);
        etDescripcion = findViewById(R.id.et_descripcion_nueva_prenda);
        etColor = findViewById(R.id.et_color_nueva_prenda);
        etTalle = findViewById(R.id.et_talle_nueva_prenda);

        rgEnStock = findViewById(R.id.rg_stock_agregar);
        rbEnStock = findViewById(R.id.rb_stock_si);

        btnLimpiar = findViewById(R.id.btn_limpiar);
        btnAgregar = findViewById(R.id.btn_agregar);
    }

    private void setupListeners() {
        initializeBtnListener();
    }

    private void initializeBtnListener() {
        etCodigo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String texto = etCodigo.getText().toString();
                    if (!texto.equals("")) {
                        new ExistePrendaAsync().execute(texto);
                    }
                }
            }
        });
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validar campos no vacíos
                if (EstaVacio(etCodigo.getText().toString())
                        || EstaVacio(etNombre.getText().toString())
                        || EstaVacio(etDescripcion.getText().toString())
                        || EstaVacio(etColor.getText().toString())
                        || EstaVacio(etTalle.getText().toString())) {
                    Toast.makeText(getApplicationContext(), R.string.toast_campos_obligatorios, Toast.LENGTH_LONG).show();
                    return;
                }

                //obtener ingreso
                codigo = etCodigo.getText().toString();
                nombre = etNombre.getText().toString();
                descripcion = etDescripcion.getText().toString();
                color = etColor.getText().toString();
                talle = etTalle.getText().toString();

                RadioButton rbSeleccionado = findViewById(rgEnStock.getCheckedRadioButtonId());
                switch (rbSeleccionado.getText().toString()) {
                    case "En Stock":
                        enStock = true;
                        break;

                    case "Sin Stock":
                        enStock = false;
                        break;
                }

                prendaAAgregar.setCodigo(codigo);
                prendaAAgregar.setNombre(nombre);
                prendaAAgregar.setDescripcion(descripcion);
                prendaAAgregar.setColor(color);
                prendaAAgregar.setTalle(talle);
                prendaAAgregar.setEnStock(enStock);

                new ObtenerPrendaPorCodigoAsync().execute(prendaAAgregar.getCodigo());
            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Limpio los campos
                etCodigo.setText("");
                etNombre.setText("");
                etDescripcion.setText("");
                etColor.setText("");
                etTalle.setText("");
                rbEnStock.setChecked(true);
            }
        });
    }

    private boolean EstaVacio(String numero) {
        return (numero.isEmpty());
    }

    private class ObtenerPrendaPorCodigoAsync extends AsyncTask<String, Void, Prenda> {


        @Override
        protected Prenda doInBackground(String... strings) {
            String codigo = strings[0];
            return DBHelper.getInstance(AgregarActivity.this).obtenerPrendaPorCodigo(codigo);
        }

        @Override
        protected void onPostExecute(Prenda prendaEncontrada) {
            verificarPrenda(prendaEncontrada);
        }

        private void verificarPrenda(Prenda prenda) {
            //Encontró la prenda?
            if (prenda.getCodigo() != null) {
                //encontró una prenda.
                //ya existe una prenda con ese codigo, así que aviso y no hago nada
                Toast.makeText(getApplicationContext(), R.string.toast_codigo_ya_existe, Toast.LENGTH_LONG).show();
            } else {
                new CrearPrendaAsync().execute(prendaAAgregar);
            }
        }
    }

    private class CrearPrendaAsync extends AsyncTask<Prenda, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            btnAgregar.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Prenda... prendas) {
            Prenda prenda = prendas[0];
            return DBHelper.getInstance(AgregarActivity.this).insertarPrenda(prenda);
        }

        @Override
        protected void onPostExecute(Boolean prendaAgregada) {
            super.onPostExecute(prendaAgregada);
            btnAgregar.setEnabled(true);

            if (prendaAgregada) {
                prendaAgregadaOK();
            }
        }

        private void prendaAgregadaOK() {
            //Ahora que ya está agregada, creo el intent para que vaya al main:
            Intent intent = new Intent(AgregarActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Toast.makeText(AgregarActivity.this, "La prenda fue creada correctamente", Toast.LENGTH_SHORT).show();
            startActivity(intent);

            if (mostrarNotificaciones) {
                mostrarNotificacion();
            }

            //con finish cierro el activity
            finish();
        }
    }

    private class ExistePrendaAsync extends AsyncTask<String, Void, Prenda> {

        @Override
        protected Prenda doInBackground(String... strings) {
            return DBHelper.getInstance(AgregarActivity.this).obtenerPrendaPorCodigo(strings[0]);
        }

        @Override
        protected void onPostExecute(Prenda prendaEncontrada) {
            verificarPrenda(prendaEncontrada);
        }

        private void verificarPrenda(Prenda prenda) {
            //Encontró la prenda?
            if (prenda.getCodigo() != null) {
                if (!prenda.getCodigo().equals("")) {
                    Log.i("TEST", "Ya existe");
                    Toast.makeText(AgregarActivity.this, R.string.toast_codigo_ya_existe, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * NOTIFICACIONES
     **/
    private void mostrarNotificacion() {

        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.notification_channel_ID));
        builder.setContentTitle("LPGC")
                .setContentText("La prenda fue creada correctamente.")
                .setSmallIcon(R.drawable.ic_notif_icon_ok)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher_notification))
                .setVibrate(verificarVibracion());

        Intent intent = new Intent(AgregarActivity.this, MainActivity.class);
        PendingIntent pendingintent = PendingIntent.getActivity(AgregarActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingintent);

        NotificationManager managerCompact = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); //obtengo el sistema de notificaciones
        managerCompact.notify(randInt(10000, 99999), builder.build()); // le paso un ID y un builder
    }

    private void createNotificationChannel() {
        // Se crea el "NotificationChannel" únicamente para api > 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.notification_channel_ID), name, importance);
            channel.setDescription(description);
            //Se registra la notificación en el sistema
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    private long[] verificarVibracion() {
        //si está habilitada la vibración, seteo el tiempo, sino le seteo 0
        int tiempo = 0;
        try {
            //obtengo el valor seteado
            tiempo = Integer.parseInt(tiempoVibrar);
        } catch (NumberFormatException nfe) {
            Log.i("TEST", "Error al parsear el tiempo de vibracion.");
        }

        return (vibrar) ? new long[]{1000, tiempo} : new long[]{0, 0};
    }
    /** /NOTIFICACIONES **/
}
