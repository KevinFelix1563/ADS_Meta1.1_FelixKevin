package com.example.agendajavafx;
import java.util.List;

public interface IDireccionDAO {
    int buscarOAgregarDireccion(String direccionCompleta);
    boolean vincularPersonaDireccion(int personaId, int direccionId);
    List<Direccion> obtenerDireccionesPorPersona(int personaId);
    boolean desenlazarDireccion(int personaId, int direccionId);
}