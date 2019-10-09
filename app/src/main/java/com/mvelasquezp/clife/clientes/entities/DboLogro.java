package com.mvelasquezp.clife.clientes.entities;

public class DboLogro {
    private int id;
    private String nombre;
    private String descripcion;
    private int avance;
    private int requisito;
    private int puntos;
    private boolean disponible;
    private boolean reclamado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getAvance() {
        return avance;
    }

    public void setAvance(int avance) {
        this.avance = avance;
    }

    public int getRequisito() {
        return requisito;
    }

    public void setRequisito(int requisito) {
        this.requisito = requisito;
    }

    public boolean isReclamado() {
        return reclamado;
    }

    public void setReclamado(boolean reclamado) {
        this.reclamado = reclamado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
