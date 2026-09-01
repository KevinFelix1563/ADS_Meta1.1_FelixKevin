package com.example.agendajavafx;
import java.util.List;

public interface ITelefonoDAO {
    boolean agregarTelefono(int personaId, String telefono);
    boolean eliminarTelefono(int telefonoId);
    List<Telefono> obtenerTelefonosPorPersona(int personaId);
}