package com.example.agendajavafx;

import java.util.List;

public interface IAgendaDAO {
    // Métodos de Persona
    boolean agregarPersona(Persona persona);
    List<Persona> obtenerTodasLasPersonas();
    boolean actualizarPersona(Persona persona);
    boolean eliminarPersona(int id);

    // Métodos de Teléfono
    boolean agregarTelefono(int personaId, String telefono);
    boolean eliminarTelefono(int telefonoId);
    List<Telefono> obtenerTelefonosPorPersona(int personaId);

    // Métodos de Dirección
    int buscarOAgregarDireccion(String direccionCompleta);
    boolean vincularPersonaDireccion(int personaId, int direccionId);
    List<Direccion> obtenerDireccionesPorPersona(int personaId);
    boolean desenlazarDireccion(int personaId, int direccionId);
}