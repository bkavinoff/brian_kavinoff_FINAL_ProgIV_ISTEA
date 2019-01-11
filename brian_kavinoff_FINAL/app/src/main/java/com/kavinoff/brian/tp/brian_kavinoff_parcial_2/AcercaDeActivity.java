package com.kavinoff.brian.tp.brian_kavinoff_parcial_2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class AcercaDeActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button btnVerSitioWeb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_de);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        getSupportActionBar().setSubtitle(getResources().getString(R.string.title_activity_acerca_de));

        //para que se muestre la flechita para volver
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //seteo la UI
        setupBTN();
        initializeBtnListener();
    }

    private void setupBTN(){
        btnVerSitioWeb=findViewById(R.id.btnVerSitioWeb);
    }

    private void initializeBtnListener() {
        btnVerSitioWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getResources().getString(R.string.url_web_leport);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //se hace referencia al xml que se va a inflar
        getMenuInflater().inflate(R.menu.menu_vacio, menu);

        return super.onCreateOptionsMenu(menu);
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
}
