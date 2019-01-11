package com.kavinoff.brian.tp.brian_kavinoff_parcial_2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kavinoff.brian.tp.brian_kavinoff_parcial_2.DBHelper.DBHelper;

import java.util.Random;

public class ModificarActivity extends AppCompatActivity {
    Toolbar toolbar;

    Prenda prendaAModificar = new Prenda();

    private EditText etDetalle_codigo;
    private EditText etDetalle_nombre;
    private EditText etDetalle_descripcion;
    private EditText etDetalle_color;
    private EditText etDetalle_talle;
    private RadioGroup rgEnStock;
    private boolean enStock;
    private RadioButton rbEnStock;
    private RadioButton rbSinStock;

    private Button btnModificar;
    private Button btnLimpiar;

    public Boolean mostrarNotificaciones;
    public Boolean vibrar;
    public String tiempoVibrar;

    private String CodigoOriginal;
    private Integer IdOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);

        int idPrenda;

        //seteo las preferencias
        setupPreferences();

        //seteo la toolbar
        setupToolbar();

        //obtengo los datos del item
        idPrenda = obtenerBundle();

        //obtengo la prenda por id
        obtenerPrendaPorId(idPrenda);

        //cargo los campos
        setupUi();

        setBtnListener();
    }

    private void setupPreferences() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        mostrarNotificaciones = pref.getBoolean("chkpMostrarNotificaciones", true);
        vibrar = pref.getBoolean("chkpVibrar", true);
        tiempoVibrar = pref.getString("etpTiempoVibrar", "10000");
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
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle(R.string.title_activity_modificar);

        //para que se muestre la flechita para volver
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private int obtenerBundle() {
        int id = -1;
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                id = bundle.getInt(DetalleActivity.PRENDA_ID);
            }
        } catch (Exception e) {
            Log.i("TEST", "Error al obtener bundle.");
        }
        return id;
    }

    private void setupUi() {
        //obtengo los controles
        etDetalle_codigo = findViewById(R.id.et_codigo_modificar);
        etDetalle_nombre = findViewById(R.id.et_nombre_modificar);
        etDetalle_descripcion = findViewById(R.id.et_descripcion_modificar);
        etDetalle_color = findViewById(R.id.et_color_modificar);
        etDetalle_talle = findViewById(R.id.et_talle_modificar);
        btnModificar = findViewById(R.id.btn_modificar_modificar);
        btnLimpiar = findViewById(R.id.btn_limpiar_modificar);

        rgEnStock = findViewById(R.id.rg_stock_agregar);
        rbEnStock = findViewById(R.id.rb_stock_si);
        rbSinStock = findViewById(R.id.rb_stock_no);

    }

    private void setBtnListener() {
        etDetalle_codigo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String texto = etDetalle_codigo.getText().toString();
                    if (!texto.equals("")) {
                        new ExistePrendaAsync().execute(texto);
                    }
                }
            }
        });
        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validar campos no vacíos
                if (EstaVacio(etDetalle_codigo.getText().toString())
                        || EstaVacio(etDetalle_nombre.getText().toString())
                        || EstaVacio(etDetalle_descripcion.getText().toString())
                        || EstaVacio(etDetalle_color.getText().toString())
                        || EstaVacio(etDetalle_talle.getText().toString())) {
                    Toast.makeText(getApplicationContext(), R.string.toast_campos_obligatorios, Toast.LENGTH_LONG).show();
                    return;
                }

                prendaAModificar.setCodigo(etDetalle_codigo.getText().toString());
                prendaAModificar.setNombre(etDetalle_nombre.getText().toString());
                prendaAModificar.setDescripcion(etDetalle_descripcion.getText().toString());
                prendaAModificar.setColor(etDetalle_color.getText().toString());
                prendaAModificar.setTalle(etDetalle_talle.getText().toString());

                RadioButton rbSeleccionado = findViewById(rgEnStock.getCheckedRadioButtonId());
                switch (rbSeleccionado.getText().toString()) {
                    case "En Stock":
                        enStock = true;
                        break;

                    case "Sin Stock":
                        enStock = false;
                        break;
                }
                prendaAModificar.setEnStock(enStock);

                //Llamo al asynctask que verifica si existe la prenda?
                new ObtenerPrendaPorCodigoAsync().execute(prendaAModificar.getCodigo());
            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etDetalle_codigo.setText("");
                etDetalle_nombre.setText("");
                etDetalle_descripcion.setText("");
                etDetalle_color.setText("");
                etDetalle_talle.setText("");
                rbEnStock.setChecked(true);
            }
        });
    }

    private void obtenerPrendaPorId(int id) {
        new ObtenerPrendaPorIdAsync().execute(id);
    }

    private class ObtenerPrendaPorIdAsync extends AsyncTask<Integer, Void, Prenda> {


        @Override
        protected Prenda doInBackground(Integer... integers) {
            return DBHelper.getInstance(ModificarActivity.this).obtenerPrendaPorId(integers[0]);
        }

        @Override
        protected void onPostExecute(Prenda prendaEncontrada) {
            verificarPrenda(prendaEncontrada);
        }

        private void verificarPrenda(Prenda prenda) {
            //Encontró la prenda?
            if (prenda.getCodigo() == null) {
                Toast.makeText(getApplicationContext(), R.string.toast_no_se_encuentra_codigo, Toast.LENGTH_LONG).show();
            } else {
                CodigoOriginal = prenda.getCodigo();
                IdOriginal = prenda.getId();
                //Seteo los valores
                etDetalle_codigo.setText(prenda.getCodigo());
                etDetalle_nombre.setText(prenda.getNombre());
                etDetalle_descripcion.setText(prenda.getDescripcion());
                etDetalle_color.setText(prenda.getColor());
                etDetalle_talle.setText(prenda.getTalle());
                if ((prenda.getEnStock())) {
                    rbEnStock.setChecked(true);
                } else {
                    rbSinStock.setChecked(true);
                }
            }

        }
    }

    private class ObtenerPrendaPorCodigoAsync extends AsyncTask<String, Void, Prenda> {


        @Override
        protected Prenda doInBackground(String... strings) {
            String codigo = strings[0];
            return DBHelper.getInstance(ModificarActivity.this).obtenerPrendaPorCodigo(codigo);
        }

        @Override
        protected void onPostExecute(Prenda prendaEncontrada) {
            verificarPrenda(prendaEncontrada);
        }

        private void verificarPrenda(Prenda prenda) {
            //Encontró la prenda?
            if (prenda.getCodigo() != null) {
                //encontró una prenda.
                //verifico si el codigo original es distinto al de la prenda
                if (prenda.getCodigo().equals(CodigoOriginal)) {
                    prendaAModificar.setId(prenda.getId());
                    new ModificarPrendaAsync().execute(prendaAModificar);
                } else {
                    //ya existe una prenda con ese codigo, así que aviso y no hago nada
                    Toast.makeText(getApplicationContext(), R.string.toast_codigo_ya_existe, Toast.LENGTH_LONG).show();
                }
            } else {
                prendaAModificar.setId(IdOriginal);
                new ModificarPrendaAsync().execute(prendaAModificar);
            }
        }
    }

    private class ModificarPrendaAsync extends AsyncTask<Prenda, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            btnModificar.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Prenda... prendas) {
            Prenda prenda = prendas[0];
            return DBHelper.getInstance(ModificarActivity.this).actualizarPrenda(prenda);
        }

        @Override
        protected void onPostExecute(Boolean prendaModificada) {
            super.onPostExecute(prendaModificada);
            btnModificar.setEnabled(true);

            if (prendaModificada) {
                prendaModificadaOK();
            }
        }

        private void prendaModificadaOK() {
            //Ahora que ya está modificada, creo el intent para que vaya al main:
            Intent intent = new Intent(ModificarActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Toast.makeText(ModificarActivity.this, "La prenda fue modificada correctamente", Toast.LENGTH_SHORT).show();
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
            return DBHelper.getInstance(ModificarActivity.this).obtenerPrendaPorCodigo(strings[0]);
        }

        @Override
        protected void onPostExecute(Prenda prendaEncontrada) {
            verificarPrenda(prendaEncontrada);
        }

        private void verificarPrenda(Prenda prenda) {
            if (!CodigoOriginal.equals(prenda.getCodigo())) {
                //Encontró la prenda?
                if (prenda.getCodigo() != null) {
                    if (!prenda.getCodigo().equals(""))
                        Log.i("TEST", "Ya existe");
                    Toast.makeText(ModificarActivity.this, R.string.toast_codigo_ya_existe, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // NOTIFICACIONES //
    private void mostrarNotificacion() {
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.notification_channel_ID));
        builder.setContentTitle("LPGC")
                .setContentText("La prenda fue modificada correctamente.")
                .setSmallIcon(R.drawable.ic_notif_icon_ok)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher_notification))
                .setVibrate(verificarVibracion());

        Intent intent = new Intent(ModificarActivity.this, MainActivity.class);
        PendingIntent pendingintent = PendingIntent.getActivity(ModificarActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
    // /NOTIFICACIONES //

    private boolean EstaVacio(String string) {
        return (string.isEmpty());
    }
}
