package com.mvelasquezp.clife.clientes.db;

import android.provider.BaseColumns;

public class DatabaseContract {

    public static final int DATABASE_VERSION = 1;
    public static final  String DATABASE_NAME = "lifeapp.db";
    private static final String C_TABLE = "create table if not exists ";
    private static final String D_TABLE = "drop table if exists ";
    private static final String T_TABLE = "delete from ";
    private static final String A_TABLE = "alter table ";
    private static final String U_TABLE = "update ";
    private static final String ADD_COLUMN = " add column ";
    private static final String SET_VALUE = " set ";
    private static final String PAR_OPEN = " (";
    private static final String PAR_CLOSE = ")";
    private static final String P_KEY = " primary key";
    private static final String INT_TYPE = " integer";
    private static final String TEXT_TYPE = " text";
    private static final String DOUBLE_TYPE = " real";
    private static final String COMMA_SEP = ", ";
    private static final String EQUALS = " = ";
    private static final String C_CASE = " case ";
    private static final String C_WHEN = " when ";
    private static final String C_THEN = " then ";
    private static final String C_ELSE = " else ";
    private static final String C_END = " end";
    private static final String W_MENOR = " < ";
    private static final String W_MAYOR = " > ";

    public class LifeApp_Mensajes implements BaseColumns {

        public static final String TABLE_NAME = "lifeapp_mensajes";
        public static final String MSJ_TITULO = "de_titulo";
        public static final String MSJ_DESCRIPCION = "de_descripcion";
        public static final String MSJ_MEDIA = "de_recurso";
        public static final String MSJ_URL = "de_url";
        public static final String MSJ_FECHA = "fe_recepcion";
        public static final String MSJ_TIPO = "st_tipo";
        public static final String MSJ_ESTADO = "st_leido";

        public static final String CREATE_TABLE = C_TABLE + TABLE_NAME + PAR_OPEN +
                _ID + INT_TYPE + P_KEY + COMMA_SEP +
                MSJ_TITULO + TEXT_TYPE + COMMA_SEP +
                MSJ_DESCRIPCION + TEXT_TYPE + COMMA_SEP +
                MSJ_MEDIA + TEXT_TYPE + COMMA_SEP +
                MSJ_URL + TEXT_TYPE + COMMA_SEP +
                MSJ_FECHA + TEXT_TYPE + COMMA_SEP +
                MSJ_TIPO + TEXT_TYPE + COMMA_SEP +
                MSJ_ESTADO + INT_TYPE + PAR_CLOSE;
        public static final String DELETE_TABLE = D_TABLE + TABLE_NAME;
        public static final String TRUNCATE_TABLE = T_TABLE + TABLE_NAME;
    }
}
