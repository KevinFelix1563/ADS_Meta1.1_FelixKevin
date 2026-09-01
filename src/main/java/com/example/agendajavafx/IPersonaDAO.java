package com.example.agendajavafx;
import java.util.List;

public interface IPersonaDAO {
    boolean agregarPersona(Persona persona);
    List<Persona> obtenerTodasLasPersonas();
    boolean actualizarPersona(Persona persona);
    boolean eliminarPersona(int id);
}