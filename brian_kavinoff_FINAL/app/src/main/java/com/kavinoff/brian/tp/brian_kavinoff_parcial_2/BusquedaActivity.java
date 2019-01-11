package com.kavinoff.brian.tp.brian_kavinoff_parcial_2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kavinoff.brian.tp.brian_kavinoff_parcial_2.DBHelper.DBHelper;

public class BusquedaActivity extends AppCompatActivity {

    private Button btnBuscarPorCodigo;
    private TextView etBuscarPorCodigo;
    private Prenda PrendaBuscada=new Prenda();
    private ProgressBar pgBusqueda;

    public static String PRENDA_ID="PRENDA_ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.support.v7.widget.Toolbar toolbar;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle(R.string.title_activity_busqueda);

        //para que se muestre la flechita para volver
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupUI();
        initializeBtnListener();

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

    private void setupUI() {
        etBuscarPorCodigo = findViewById(R.id.et_codigo_buscar);
        btnBuscarPorCodigo = findViewById(R.id.btnBuscarPorCodigo);
        pgBusqueda=findViewById(R.id.progressBarBusqueda);
        pgBusqueda.setVisibility(View.INVISIBLE);
    }

    private void initializeBtnListener() {
        btnBuscarPorCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //BUSQUEDA
                if (etBuscarPorCodigo.getText().toString().matches("")){
                    Toast.makeText(getApplicationContext(), R.string.toast_codigo_obligatorio, Toast.LENGTH_LONG).show();
                    return;
                }

                String codigo=etBuscarPorCodigo.getText().toString();

                new ObtenerPrendaAsync().execute(codigo);
            }
        });

    }

    private class ObtenerPrendaAsync extends AsyncTask<String, Void, Prenda> {

        @Override
        protected void onPreExecute() {
            pgBusqueda.setVisibility(View.VISIBLE);
            btnBuscarPorCodigo.setEnabled(false);
        }

        @Override
        protected Prenda doInBackground(String... strings) {
            return DBHelper.getInstance(BusquedaActivity.this).obtenerPrendaPorCodigo(strings[0]);
        }

        @Override
        protected void onPostExecute(Prenda prendaEncontrada) {
            pgBusqueda.setVisibility(View.INVISIBLE);
            btnBuscarPorCodigo.setEnabled(true);

            verificarPrenda(prendaEncontrada);
        }

        private void verificarPrenda(Prenda prenda){
            //Encontró la prenda?
            if (prenda.getCodigo()==null){
                Toast.makeText(getApplicationContext(), R.string.toast_no_se_encuentra_codigo, Toast.LENGTH_LONG).show();
                return;
            }

            //abro el intent de ver detalle
            Intent intent = new Intent(BusquedaActivity.this, DetalleActivity.class);
            int idPrenda = prenda.getId();
            intent.putExtra(PRENDA_ID, idPrenda);
            startActivity(intent);

            //con finish cierro el activity
            finish();
        }
    }


}
