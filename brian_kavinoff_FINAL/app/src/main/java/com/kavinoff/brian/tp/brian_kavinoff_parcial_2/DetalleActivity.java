package com.kavinoff.brian.tp.brian_kavinoff_parcial_2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kavinoff.brian.tp.brian_kavinoff_parcial_2.DBHelper.DBHelper;
import java.util.Random;

public class DetalleActivity extends AppCompatActivity {
    public static String PRENDA_ID="PRENDA_ID";
    Toolbar toolbar;
    Prenda prendaDetalle=new Prenda();

    private TextView tvDetalle_codigo;
    private TextView tvDetalle_nombre;
    private TextView tvDetalle_descripcion;
    private TextView tvDetalle_color;
    private TextView tvDetalle_talle;
    private TextView tvDetalle_EnStock;

    private Button btnModificar;
    private Button btnEliminar;

    private int idPrenda=-1;

    public Boolean mostrarNotificaciones;
    public Boolean vibrar;
    public String tiempoVibrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        //levanto las preferencias
        setupPreferences();

        //seteo la toolbar
        setupToolbar();

        //obtengo los datos del item
        obtenerBundle();

        //obtengo la prenda por id
        obtenerPrendaPorId(idPrenda);

        //seteo la interfaz
        setupUi();

        //seteo Listeners
        setBtnListener();
    }

    private void setupPreferences() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        mostrarNotificaciones = pref.getBoolean("chkpMostrarNotificaciones",true);
        vibrar= pref.getBoolean("chkpVibrar",false);
        tiempoVibrar =  pref.getString("etpTiempoVibrar","100");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar(){
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle(R.string.title_activity_detalle);

        //para que se muestre la flechita para volver
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void obtenerBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null) {
            idPrenda = bundle.getInt(DetalleActivity.PRENDA_ID);
        }
    }

    private void setupUi() {
        //obtengo los controles
        tvDetalle_codigo = findViewById(R.id.tvDetalle_codigo);
        tvDetalle_nombre = findViewById(R.id.tvDetalle_nombre);
        tvDetalle_descripcion = findViewById(R.id.tvDetalle_descripcion);
        tvDetalle_color = findViewById(R.id.tvDetalle_color);
        tvDetalle_talle = findViewById(R.id.tvDetalle_talle);
        tvDetalle_EnStock=findViewById(R.id.tv_Detalle_EnStock);
        btnModificar = findViewById(R.id.btn_editar);
        btnEliminar=findViewById(R.id.btn_eliminar);
    }

    private void setBtnListener() {
        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DetalleActivity.this, ModificarActivity.class);
                int id = idPrenda;
                intent.putExtra(PRENDA_ID, id);
                startActivity(intent);
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAlertDialog("Confirme eliminación de Prenda: " + tvDetalle_codigo.getText());
            }
        });
    }

    private void ShowAlertDialog(String texto){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_eliminar_item)
                .setMessage(texto)
                .setNegativeButton(R.string.btn_cancelar, null)
                .setPositiveButton(R.string.btn_confirmar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        //ELIMINO EL ITEM DE LA DB
                        eliminarPrenda(prendaDetalle);
                        //cierro el dialogo
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void eliminarPrenda(Prenda prenda){
        new eliminarPrendaAsync().execute(prenda);
    }

    private void obtenerPrendaPorId(int id){
        new ObtenerPrendaPorIdAsync().execute(id);
    }

    private class ObtenerPrendaPorIdAsync extends AsyncTask<Integer, Void, Prenda> {


        @Override
        protected Prenda doInBackground(Integer... integers) {
            return DBHelper.getInstance(DetalleActivity.this).obtenerPrendaPorId(integers[0]);
        }

        @Override
        protected void onPostExecute(Prenda prendaEncontrada) {
            verificarPrenda(prendaEncontrada);
        }

        private void verificarPrenda(Prenda prenda){
            //Encontró la prenda?
            if (prenda.getCodigo()==null){
                Toast.makeText(getApplicationContext(), R.string.toast_no_se_encuentra_codigo, Toast.LENGTH_LONG).show();
                return;
            }
            prendaDetalle=prenda;
            //Seteo los valores
            tvDetalle_codigo.setText(prenda.getCodigo());
            tvDetalle_nombre.setText(prenda.getNombre());
            tvDetalle_descripcion.setText(prenda.getDescripcion());
            tvDetalle_color.setText(prenda.getColor());
            tvDetalle_talle.setText(prenda.getTalle());
            String texto = (prenda.getEnStock())?"En Stock":"Sin Stock";
            tvDetalle_EnStock.setText(texto);
        }
    }

    private class eliminarPrendaAsync extends AsyncTask<Prenda, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Prenda... prendas) {
            Prenda prenda = prendas[0];
            return DBHelper.getInstance(DetalleActivity.this).eliminarPrenda(prenda);
        }

        @Override
        protected void onPostExecute(Boolean prendaEliminada) {
            verificarPrenda(prendaEliminada);
        }

        private void verificarPrenda(Boolean prendaEliminada){
            //Se eliminó la prenda??
            if (prendaEliminada){
                //MUESTRO NOTIFICACION
                if (mostrarNotificaciones) {
                    mostrarNotificacion("LPGC","La prenda fue eliminada correctamente");
                }
                Toast.makeText(DetalleActivity.this, "La prenda fue eliminada correctamente", Toast.LENGTH_LONG).show();
            }

            //con finish cierro el activity
            finish();
        }
    }

    /** NOTIFICACIONES */
    private void mostrarNotificacion(String titulo, String texto){

        createNotificationChannel();
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this, getString(R.string.notification_channel_ID));
        builder.setContentTitle(titulo)
                .setContentText(texto)
                .setSmallIcon(R.drawable.ic_notif_icon_ok)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.mipmap.ic_launcher_notification))
                .setVibrate(verificarVibracion());

        Intent intent = new Intent(DetalleActivity.this, MainActivity.class);
        PendingIntent pendingintent = PendingIntent.getActivity(DetalleActivity.this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingintent);

        NotificationManager managerCompact = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); //obtengo el sistema de notificaciones
        managerCompact.notify(randInt(10000,99999), builder.build()); // le paso un ID y un builder
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
    private long[] verificarVibracion(){
        //si está habilitada la vibración, seteo el tiempo, sino le seteo 0
        int tiempo=0;
        try {
            //obtengo el valor seteado
            tiempo = Integer.parseInt(tiempoVibrar);
        } catch(NumberFormatException nfe) {
            Log.i("TEST","Error al parsear el tiempo de vibracion.");
        }

        return (vibrar)? new long[] {1000,  tiempo}: new long[] {0, 0};
    }
    /** /NOTIFICACIONES */
}
