package com.kavinoff.brian.tp.brian_kavinoff_parcial_2.DBHelper;

public interface DBConfig {
    String DB_NAME="lpgc_db";
    int DB_VERSION=4;

    //Tabla de Prendas
    String DB_TABLA_PRENDA="prendas";
    String ID_PRENDA="id_prenda";
    String CODIGO_PRENDA="codigo_prenda";
    String NOMBRE_PRENDA="nombre_prenda";
    String DESCRIPCION_PRENDA="descripcion_prenda";
    String COLOR_PRENDA="color_prenda";
    String TALLE_PRENDA="talle_prenda";
    String EN_STOCK="en_stock";

    //Tabla de Usuarios
    String DB_TABLA_USUARIOS="usuarios";
    String ID_USUARIO="id_usuario";
    String USUARIO="usuario";
    String MAIL="mail";
    String PASSWORD="password";

}
