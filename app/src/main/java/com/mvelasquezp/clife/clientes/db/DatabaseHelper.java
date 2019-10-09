package com.mvelasquezp.clife.clientes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mvelasquezp.clife.clientes.dbo.DboMensaje;
import com.mvelasquezp.clife.clientes.tools.AppHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.LifeApp_Mensajes.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }

    //crud de las tablas

    public class DB_Mensajes {

        private long insert(DboMensaje dboMensaje) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
                contentValues.put(DatabaseContract.LifeApp_Mensajes.MSJ_TITULO, dboMensaje.getTitulo());
                contentValues.put(DatabaseContract.LifeApp_Mensajes.MSJ_DESCRIPCION, dboMensaje.getDescripcion());
                contentValues.put(DatabaseContract.LifeApp_Mensajes.MSJ_MEDIA, dboMensaje.getRecurso());
                contentValues.put(DatabaseContract.LifeApp_Mensajes.MSJ_URL, dboMensaje.getUrl());
                contentValues.put(DatabaseContract.LifeApp_Mensajes.MSJ_FECHA, new AppHelper().FormatDate(dboMensaje.getFecha(), "yyyy-MM-dd HH:mm:ss"));
                contentValues.put(DatabaseContract.LifeApp_Mensajes.MSJ_TIPO, dboMensaje.getTipo());
                contentValues.put(DatabaseContract.LifeApp_Mensajes.MSJ_ESTADO, dboMensaje.getEstado());
            return db.insert(DatabaseContract.LifeApp_Mensajes.TABLE_NAME, null, contentValues);
        }

        private List<DboMensaje> select() {
            List<DboMensaje> result = new ArrayList<DboMensaje>();
            SQLiteDatabase db = getReadableDatabase();
            String SqlString = "select * from " + DatabaseContract.LifeApp_Mensajes.TABLE_NAME +
                    " order by " + DatabaseContract.LifeApp_Mensajes._ID + " asc";
            String[] params = {};
            Cursor res = db.rawQuery(SqlString, params);
            res.moveToFirst();
            while(res.isAfterLast() == false) {
                DboMensaje mensaje = new DboMensaje();
                    mensaje.setId(res.getLong(res.getColumnIndex(DatabaseContract.LifeApp_Mensajes._ID)));
                    mensaje.setTitulo(res.getString(res.getColumnIndex(DatabaseContract.LifeApp_Mensajes.MSJ_TITULO)));
                    mensaje.setDescripcion(res.getString(res.getColumnIndex(DatabaseContract.LifeApp_Mensajes.MSJ_DESCRIPCION)));
                    mensaje.setRecurso(res.getString(res.getColumnIndex(DatabaseContract.LifeApp_Mensajes.MSJ_MEDIA)));
                    mensaje.setUrl(res.getString(res.getColumnIndex(DatabaseContract.LifeApp_Mensajes.MSJ_URL)));
                    mensaje.setFecha(new AppHelper().StringToDate(res.getString(res.getColumnIndex(DatabaseContract.LifeApp_Mensajes.MSJ_FECHA)),"yyyy-MM-dd HH:mm:ss"));
                    mensaje.setTipo(res.getString(res.getColumnIndex(DatabaseContract.LifeApp_Mensajes.MSJ_TIPO)));
                    mensaje.setEstado(res.getInt(res.getColumnIndex(DatabaseContract.LifeApp_Mensajes.MSJ_ESTADO)));
                result.add(mensaje);
                res.moveToNext();
            }
            return result;
        }
    }
}
