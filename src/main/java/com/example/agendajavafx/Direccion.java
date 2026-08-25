package com.example.agendajavafx;

import java.util.ArrayList;
import java.util.List;

public class Direccion {
    private int id;
    private String direccionCompleta;
    private List<Persona> habitantes;

    public Direccion(int id, String direccionCompleta) {
        this.id = id;
        this.direccionCompleta = direccionCompleta;
        this.habitantes = new ArrayList<>();
    }

    public Direccion(String direccionCompleta) {
        this.direccionCompleta = direccionCompleta;
        this.habitantes = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDireccionCompleta() { return direccionCompleta; }
    public void setDireccionCompleta(String direccionCompleta) { this.direccionCompleta = direccionCompleta; }
    public List<Persona> getHabitantes() { return habitantes; }
    public void setHabitantes(List<Persona> habitantes) { this.habitantes = habitantes; }
}