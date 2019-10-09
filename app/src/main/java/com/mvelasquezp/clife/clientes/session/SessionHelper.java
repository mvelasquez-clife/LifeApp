package com.mvelasquezp.clife.clientes.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.mvelasquezp.clife.clientes.tools.AppHelper;
import com.mvelasquezp.clife.clientes.tools.Constantes;

public class SessionHelper {

    Context context;
    private SharedPreferences session;
    private AppHelper helper;

    public SessionHelper(Context context) {
        this.context = context;
        session = context.getSharedPreferences(Constantes.APP_NAME, Context.MODE_PRIVATE);
        helper = new AppHelper();
    }

    public void saveSession(Sesion usuario) {
        SharedPreferences.Editor editor = session.edit();
            editor.putString(Constantes.USER_CODIGO, usuario.getCodigo());
            editor.putInt(Constantes.USER_EMPRESA, usuario.getEmpresa());
            editor.putString(Constantes.USER_NCOMERCIAL, usuario.getNcomercial());
            editor.putString(Constantes.USER_RSOCIAL, usuario.getRsocial());
            editor.putString(Constantes.USER_FREGISTRO, helper.FormatDate(usuario.getFregistro(), Constantes.DATETIME_FORMAT));
            editor.putString(Constantes.USER_EMAIL, usuario.getEmail());
            editor.putString(Constantes.USER_TELEFONO, usuario.getTelefono());
            editor.putString(Constantes.USER_TOKEN, usuario.getToken());
            editor.putString(Constantes.USER_TPUSUARIO, usuario.getTpusuario());
            editor.putString(Constantes.USER_SALON, usuario.getSalon());
            editor.putString(Constantes.USER_DEPENDIENTE, usuario.getDependiente());
            editor.putInt(Constantes.USER_LOCAL, usuario.getLocal());
            editor.putInt(Constantes.USER_LOCAL_SELECCIONADO, usuario.getLocal_seleccionado());
            editor.putBoolean(Constantes.USER_HABILITA_PUNTOS, usuario.isPuntos_habilitados());
            editor.putBoolean(Constantes.USER_PUBLICIDAD, usuario.isPublicidad());
        editor.commit();
    }

    public void doLogout() {
        session.edit().clear().commit();
    }

    public Sesion getSesion() {
        session = context.getSharedPreferences(Constantes.APP_NAME, Context.MODE_PRIVATE);
        Sesion usesion = new Sesion();
            usesion.setCodigo(session.getString(Constantes.USER_CODIGO, Constantes.DEFAULT_STRING));
            usesion.setEmpresa(session.getInt(Constantes.USER_EMPRESA, Constantes.DEFAULT_INT));
            usesion.setNcomercial(session.getString(Constantes.USER_NCOMERCIAL, Constantes.DEFAULT_STRING));
            usesion.setRsocial(session.getString(Constantes.USER_RSOCIAL, Constantes.DEFAULT_STRING));
            usesion.setFregistro(helper.StringToDate(session.getString(Constantes.USER_FREGISTRO, Constantes.DEFAULT_STRING), Constantes.DATETIME_FORMAT));
            usesion.setEmail(session.getString(Constantes.USER_EMAIL, Constantes.DEFAULT_STRING));
            usesion.setTelefono(session.getString(Constantes.USER_TELEFONO, Constantes.DEFAULT_STRING));
            usesion.setToken(session.getString(Constantes.USER_TOKEN, Constantes.DEFAULT_STRING));
            usesion.setTpusuario(session.getString(Constantes.USER_TPUSUARIO, Constantes.DEFAULT_STRING));
            usesion.setSalon(session.getString(Constantes.USER_SALON, Constantes.DEFAULT_STRING));
            usesion.setDependiente(session.getString(Constantes.USER_DEPENDIENTE, Constantes.DEFAULT_STRING));
            usesion.setLocal(session.getInt(Constantes.USER_LOCAL, Constantes.DEFAULT_INT));
            usesion.setLocal_seleccionado(session.getInt(Constantes.USER_LOCAL_SELECCIONADO, Constantes.DEFAULT_INT));
            usesion.setPuntos_habilitados(session.getBoolean(Constantes.USER_HABILITA_PUNTOS, Constantes.DEFAULT_BOOLEAN));
            usesion.setPublicidad(session.getBoolean(Constantes.USER_PUBLICIDAD, Constantes.DEFAULT_BOOLEAN));
        return usesion;
    }

    public boolean SesionIniciada() {
        return !session.getString(Constantes.USER_CODIGO, Constantes.DEFAULT_STRING).equals(Constantes.DEFAULT_STRING);
    }
}
