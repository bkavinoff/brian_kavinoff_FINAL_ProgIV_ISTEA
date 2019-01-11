package com.kavinoff.brian.tp.brian_kavinoff_parcial_2;

import java.io.Serializable;

public class Prenda implements Serializable {

    private int id;
    private String Codigo;
    private String Nombre;
    private String Descripcion;
    private String Color;
    private String Talle;
    private boolean EnStock;

    //constructor sin parámetros
    public Prenda(){
    }

    //Constructor completo
    public Prenda(int id, String codigo, String nombre, String descripcion, String color, String talle, boolean enStock) {
        this.id=id;
        this.Codigo=codigo;
        this.Nombre=nombre;
        this.Descripcion = descripcion;
        this.Color=color;
        this.Talle=talle;
        this.EnStock=enStock;
    }


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return this.Codigo;
    }

    public void setCodigo(String codigo) {
        this.Codigo = codigo;
    }

    public String getNombre() {
        return this.Nombre;
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;
    }

    public String getDescripcion() {
        return this.Descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.Descripcion = descripcion;
    }

    public String getColor() {
        return this.Color;
    }

    public void setColor(String color) {
        this.Color = color;
    }

    public String getTalle() {
        return this.Talle;
    }

    public void setTalle(String talle) {
        this.Talle = talle;
    }

    public boolean getEnStock() {
        return EnStock;
    }

    public void setEnStock(boolean enStock) {
        EnStock = enStock;
    }
}
