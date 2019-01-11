package com.kavinoff.brian.tp.brian_kavinoff_parcial_2;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kavinoff.brian.tp.brian_kavinoff_parcial_2.DBHelper.DBHelper;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String PRENDA_ID="PRENDA_ID";
    private Toolbar toolbar;
    private PrendaAdapter adapter;
    private ListView lvPrendas;
    private List<Prenda> prendas ;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //seteo la toolbar
        setupToolbar();

        //seteo la UI
        setupListView();
        setupBTN();
        initializeBtnListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ObtenerPrendasAsync().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //se hace referencia al xml que se va a inflar
        getMenuInflater().inflate(R.menu.menu_principal, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        ShowAlertDialog("Presione Aceptar para salir de la aplicación.");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.menu_buscar:
                intent = new Intent(MainActivity.this, BusquedaActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_configuracion:
                intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_acerca_de:
                intent = new Intent(MainActivity.this, AcercaDeActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                ShowAlertDialog("¿Desea salir de la aplicacion?");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar(){
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle(R.string.title_activity_listado);
    }

    private void setupListView(){
        lvPrendas=findViewById(R.id.lvPrendas);

        adapter = new PrendaAdapter(this, R.layout.item_prenda);

        lvPrendas.setAdapter(adapter);
        lvPrendas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Prenda prenda = prendas.get(position);

                //abro el intent de ver detalle
                Intent intent = new Intent(MainActivity.this, DetalleActivity.class);
                int idPrenda = prenda.getId();
                intent.putExtra(PRENDA_ID, idPrenda);
                startActivity(intent);
            }
        });
    }

    private void setupBTN(){
        fab = findViewById(R.id.fabAgregar);
    }

    private void initializeBtnListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AgregarActivity.class);
                startActivity(intent);
            }
        });

    }

    private void ShowAlertDialog(String texto){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
                .setMessage(texto)
                .setNegativeButton(R.string.btn_cancelar, null)
                .setPositiveButton(R.string.btn_aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        //CIERRO LA APP
                        dialog.dismiss();
                        finish();
                    }
                });
        builder.show();
    }

    // ASYNCTASK //

   private class ObtenerPrendasAsync extends AsyncTask<Void, Void, List<Prenda>> {

       @Override
        protected List<Prenda> doInBackground(Void... voids) {
            return DBHelper.getInstance(MainActivity.this).obtenerPrendas();
        }

        @Override
        protected void onPostExecute(List<Prenda> prendas) {
            super.onPostExecute(prendas);
            actualizarListaPrendas(prendas);
        }
    }

    private void actualizarListaPrendas(List<Prenda> productos) {
        this.prendas = productos;
        if (adapter != null) {
            adapter.setPrendas(productos);
            adapter.notifyDataSetChanged();
        }
    }


}
