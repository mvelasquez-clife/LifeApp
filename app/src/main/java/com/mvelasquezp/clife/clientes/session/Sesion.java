package com.mvelasquezp.clife.clientes.session;

import java.util.Date;

public class Sesion {

    private String codigo;
    private int empresa;
    private String ncomercial;
    private String rsocial;
    private Date fregistro;
    private String email;
    private String telefono;
    private String token;
    private String tpusuario;
    private String salon;
    private String dependiente;
    private int local;
    private int local_seleccionado;
    private boolean puntos_habilitados;

    private boolean publicidad;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getEmpresa() {
        return empresa;
    }

    public void setEmpresa(int empresa) {
        this.empresa = empresa;
    }

    public String getNcomercial() {
        return ncomercial;
    }

    public void setNcomercial(String ncomercial) {
        this.ncomercial = ncomercial;
    }

    public String getRsocial() {
        return rsocial;
    }

    public void setRsocial(String rsocial) {
        this.rsocial = rsocial;
    }

    public Date getFregistro() {
        return fregistro;
    }

    public void setFregistro(Date fregistro) {
        this.fregistro = fregistro;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSalon() {
        return salon;
    }

    public void setSalon(String salon) {
        this.salon = salon;
    }

    public int getLocal() {
        return local;
    }

    public void setLocal(int local) {
        this.local = local;
    }

    public int getLocal_seleccionado() {
        return local_seleccionado;
    }

    public void setLocal_seleccionado(int local_seleccionado) {
        this.local_seleccionado = local_seleccionado;
    }

    public String getTpusuario() {
        return tpusuario;
    }

    public void setTpusuario(String tpusuario) {
        this.tpusuario = tpusuario;
    }

    public String getDependiente() {
        return dependiente;
    }

    public void setDependiente(String dependiente) {
        this.dependiente = dependiente;
    }

    public boolean isPuntos_habilitados() {
        return puntos_habilitados;
    }

    public void setPuntos_habilitados(boolean puntos_habilitados) {
        this.puntos_habilitados = puntos_habilitados;
    }

    public boolean isPublicidad() {
        return publicidad;
    }

    public void setPublicidad(boolean publicidad) {
        this.publicidad = publicidad;
    }
}
