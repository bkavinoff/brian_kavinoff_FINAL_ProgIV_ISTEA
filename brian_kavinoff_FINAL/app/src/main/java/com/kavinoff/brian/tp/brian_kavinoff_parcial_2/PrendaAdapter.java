package com.kavinoff.brian.tp.brian_kavinoff_parcial_2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PrendaAdapter extends ArrayAdapter<Prenda> {
    private List<Prenda> prendas;
    private int resource;
    //constructor
    //super es la clase ArrayAdapter
    public PrendaAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.resource=resource;
    }

    @Override
    public int getCount() {
        return prendas == null ? 0 : prendas.size();
    }

    @Nullable
    @Override
    public Prenda getItem(int position) {
        return prendas.get(position);
    }

    public void setPrendas(List<Prenda> prendas) {
        this.prendas = prendas;
        notifyDataSetChanged();
    }

    //el getView lo va a realizar para cada item de la lista, internamente el adapter por detras recorre la lista
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_prenda, null);
        Prenda prenda=prendas.get(position);

        //en el listado solo muestro el código y el nombre
        TextView txtCodigo=view.findViewById(R.id.tvCodigoPrenda);
        TextView txtNombre=view.findViewById(R.id.tvNombrePrenda);

        txtCodigo.setText(prenda.getCodigo());
        txtNombre.setText(prenda.getNombre());

        return view;
    }
}