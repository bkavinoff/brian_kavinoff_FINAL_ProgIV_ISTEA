package com.kavinoff.brian.tp.brian_kavinoff_parcial_2.DBHelper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kavinoff.brian.tp.brian_kavinoff_parcial_2.Prenda;
import com.kavinoff.brian.tp.brian_kavinoff_parcial_2.Usuario;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    //singleton de la clase
    private static DBHelper instance;

    private DBHelper(Context context) {
        super(context, DBConfig.DB_NAME, null, DBConfig.DB_VERSION);
    }
    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }
    // /singleton de la clase


    @Override
    public void onCreate(SQLiteDatabase db) {
        //a la primary key sqlite la toma como autoincremental
        //PRENDAS
        String SQL_PRENDAS="CREATE TABLE IF NOT EXISTS " + DBConfig.DB_TABLA_PRENDA +
                " (" + DBConfig.ID_PRENDA + " integer PRIMARY KEY, "
                + DBConfig.CODIGO_PRENDA + " text, "
                + DBConfig.NOMBRE_PRENDA + " text, "
                + DBConfig.DESCRIPCION_PRENDA + " text, "
                + DBConfig.COLOR_PRENDA + " text, "
                + DBConfig.TALLE_PRENDA + " text, "
                + DBConfig.EN_STOCK + " boolean CHECK (" + DBConfig.EN_STOCK + " IN (0,1)))"; //en esta variable solo admito 0 o 1

        db.execSQL(SQL_PRENDAS);

        //USUARIOS
        String SQL_USUARIOS="CREATE TABLE IF NOT EXISTS " + DBConfig.DB_TABLA_USUARIOS +
                " (" + DBConfig.ID_USUARIO + " integer PRIMARY KEY, "
                + DBConfig.USUARIO + " text, "
                + DBConfig.MAIL + " text, "
                + DBConfig.PASSWORD + " text)";

        db.execSQL(SQL_USUARIOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion!=oldVersion){
            try{
            db.execSQL("DROP TABLE "+DBConfig.DB_TABLA_PRENDA);
            db.execSQL("DROP TABLE "+DBConfig.DB_TABLA_USUARIOS);
            onCreate(db);
            }catch (Exception e){
                Log.i("TEST","e");
            }
        }
    }

    //PRENDAS
    //Insert:
    public boolean insertarPrenda(Prenda prenda) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        boolean prendaInsertada = false;
        try {
            contentValues.put(DBConfig.CODIGO_PRENDA, prenda.getCodigo());
            contentValues.put(DBConfig.NOMBRE_PRENDA, prenda.getNombre());
            contentValues.put(DBConfig.DESCRIPCION_PRENDA, prenda.getDescripcion());
            contentValues.put(DBConfig.COLOR_PRENDA, prenda.getColor());
            contentValues.put(DBConfig.TALLE_PRENDA, prenda.getTalle());
            int enStock=(prenda.getEnStock())?1:0;
            //contentValues.put(DBConfig.EN_STOCK, prenda.getEnStock());
            contentValues.put(DBConfig.EN_STOCK, enStock);

            db.insert(DBConfig.DB_TABLA_PRENDA, null, contentValues);
            prendaInsertada = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return prendaInsertada;
    }

    //Update:
    public boolean actualizarPrenda(Prenda prenda) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        boolean prendaActualizada = false;
        try {
            contentValues.put(DBConfig.CODIGO_PRENDA, prenda.getCodigo());
            contentValues.put(DBConfig.NOMBRE_PRENDA, prenda.getNombre());
            contentValues.put(DBConfig.DESCRIPCION_PRENDA, prenda.getDescripcion());
            contentValues.put(DBConfig.COLOR_PRENDA, prenda.getColor());
            contentValues.put(DBConfig.TALLE_PRENDA, prenda.getTalle());
            int enStock=(prenda.getEnStock())?1:0;
            //contentValues.put(DBConfig.EN_STOCK, prenda.getEnStock());
            contentValues.put(DBConfig.EN_STOCK, enStock);
            db.update(DBConfig.DB_TABLA_PRENDA, contentValues, DBConfig.ID_PRENDA +" = ?", new String[]{String.valueOf(prenda.getId())});
            prendaActualizada = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return prendaActualizada;
    }

    //Delete:
    public boolean eliminarPrenda(Prenda prenda) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean prendaBorrada = false;
        try {
            db.delete(DBConfig.DB_TABLA_PRENDA, DBConfig.ID_PRENDA + " = ?", new String[]{String.valueOf(prenda.getId())});
            prendaBorrada = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return prendaBorrada;
    }

    //Get:
    public List<Prenda> obtenerPrendas() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Prenda> prendas = new ArrayList<>();
        try {
        Cursor cur = db.rawQuery("select * from " + DBConfig.DB_TABLA_PRENDA + " ORDER BY " + DBConfig.CODIGO_PRENDA + " ASC", null);
        cur.moveToFirst();

            while (!cur.isAfterLast()) {
                Prenda prenda = new Prenda();
                prenda.setId(cur.getInt(cur.getColumnIndex(DBConfig.ID_PRENDA)));
                prenda.setCodigo(cur.getString(cur.getColumnIndex(DBConfig.CODIGO_PRENDA)));
                prenda.setNombre(cur.getString(cur.getColumnIndex(DBConfig.NOMBRE_PRENDA)));
                prenda.setDescripcion(cur.getString(cur.getColumnIndex(DBConfig.DESCRIPCION_PRENDA)));
                prenda.setColor(cur.getString(cur.getColumnIndex(DBConfig.COLOR_PRENDA)));
                prenda.setTalle(cur.getString(cur.getColumnIndex(DBConfig.TALLE_PRENDA)));

                try {
                    boolean enStock = cur.getInt(cur.getColumnIndex(DBConfig.EN_STOCK)) > 0;
                    //prenda.setEnStock(cur.getInt(cur.getColumnIndex(DBConfig.EN_STOCK)));
                    prenda.setEnStock(enStock);
                }catch(Exception e){
                    Log.i("TEST","La prenda no tiene seteado el stock");
                }


                prendas.add(prenda);
                cur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TEST", e.getMessage());
        }
        return prendas;
    }

    public Prenda obtenerPrendaPorCodigo(String codigo) {
        Prenda prenda = new Prenda();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
        Cursor cur = db.rawQuery("SELECT * FROM " + DBConfig.DB_TABLA_PRENDA+" WHERE "+ DBConfig.CODIGO_PRENDA + " = ? ", new String[] {codigo});

        cur.moveToFirst();

            while (!cur.isAfterLast()) {

                prenda.setId(cur.getInt(cur.getColumnIndex(DBConfig.ID_PRENDA)));
                prenda.setCodigo(cur.getString(cur.getColumnIndex(DBConfig.CODIGO_PRENDA)));
                prenda.setNombre(cur.getString(cur.getColumnIndex(DBConfig.NOMBRE_PRENDA)));
                prenda.setDescripcion(cur.getString(cur.getColumnIndex(DBConfig.DESCRIPCION_PRENDA)));
                prenda.setDescripcion(cur.getString(cur.getColumnIndex(DBConfig.COLOR_PRENDA)));
                prenda.setDescripcion(cur.getString(cur.getColumnIndex(DBConfig.TALLE_PRENDA)));
                boolean enStock = cur.getInt(cur.getColumnIndex(DBConfig.EN_STOCK)) > 0;
                //prenda.setEnStock(cur.getInt(cur.getColumnIndex(DBConfig.EN_STOCK)));
                prenda.setEnStock(enStock);
                cur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prenda;
    }

    public Prenda obtenerPrendaPorId(Integer id) {
        Prenda prenda = new Prenda();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + DBConfig.DB_TABLA_PRENDA+" WHERE "+ DBConfig.ID_PRENDA + " = ? ", new String[]{String.valueOf(id)});
        cur.moveToFirst();
        try {
            while (!cur.isAfterLast()) {

                prenda.setId(cur.getInt(cur.getColumnIndex(DBConfig.ID_PRENDA)));
                prenda.setCodigo(cur.getString(cur.getColumnIndex(DBConfig.CODIGO_PRENDA)));
                prenda.setNombre(cur.getString(cur.getColumnIndex(DBConfig.NOMBRE_PRENDA)));
                prenda.setDescripcion(cur.getString(cur.getColumnIndex(DBConfig.DESCRIPCION_PRENDA)));
                prenda.setColor(cur.getString(cur.getColumnIndex(DBConfig.COLOR_PRENDA)));
                prenda.setTalle(cur.getString(cur.getColumnIndex(DBConfig.TALLE_PRENDA)));
                boolean enStock = cur.getInt(cur.getColumnIndex(DBConfig.EN_STOCK)) > 0;
                //prenda.setEnStock(cur.getInt(cur.getColumnIndex(DBConfig.EN_STOCK)));
                prenda.setEnStock(enStock);
                cur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prenda;
    }


    //USUARIOS:
    //Insert:
    public boolean insertarUsuario(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        boolean usuarioInsertado = false;
        try {
            contentValues.put(DBConfig.USUARIO, usuario.getUsername());
            contentValues.put(DBConfig.MAIL, usuario.getMail());
            contentValues.put(DBConfig.PASSWORD, usuario.getPassword());

            db.insert(DBConfig.DB_TABLA_USUARIOS, null, contentValues);
            usuarioInsertado = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return usuarioInsertado;
    }
    //Get
    public Usuario obtenerUsuarioPorUsername(String username) {
        Usuario usuario = new Usuario();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cur = db.rawQuery("SELECT * FROM " + DBConfig.DB_TABLA_USUARIOS+" WHERE "+ DBConfig.USUARIO + " = ? ", new String[] {username});

            cur.moveToFirst();

            while (!cur.isAfterLast()) {

                usuario.setId(cur.getInt(cur.getColumnIndex(DBConfig.ID_USUARIO)));
                usuario.setUsername(cur.getString(cur.getColumnIndex(DBConfig.USUARIO)));
                usuario.setMail(cur.getString(cur.getColumnIndex(DBConfig.MAIL)));
                usuario.setPassword(cur.getString(cur.getColumnIndex(DBConfig.PASSWORD)));

                cur.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuario;
    }

}
