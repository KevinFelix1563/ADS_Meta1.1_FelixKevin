package com.example.agendajavafx;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgendaDAO {
    private static final String URL = "jdbc:mariadb://localhost:3306/agenda";
    private static final String USER = "usuario1";
    private static final String PASSWORD = "superpassword";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ALTA (Insertar Persona y sus Teléfonos)
    public boolean agregarPersona(Persona persona) {
        String sqlPersona = "INSERT INTO Personas (nombre, direccion) VALUES (?, ?)";
        String sqlTelefono = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmtPersona = conn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {

            // Iniciar transacción
            conn.setAutoCommit(false);

            pstmtPersona.setString(1, persona.getNombre());
            pstmtPersona.setString(2, persona.getDireccion());
            pstmtPersona.executeUpdate();

            // Obtener el ID generado para la persona
            try (ResultSet generatedKeys = pstmtPersona.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int personaId = generatedKeys.getInt(1);
                    persona.setId(personaId);

                    // Insertar teléfonos asociados
                    try (PreparedStatement pstmtTel = conn.prepareStatement(sqlTelefono)) {
                        for (Telefono tel : persona.getTelefonos()) {
                            pstmtTel.setInt(1, personaId);
                            pstmtTel.setString(2, tel.getTelefono());
                            pstmtTel.executeUpdate();
                        }
                    }
                }
            }
            conn.commit(); // Confirmar transacción
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // CONSULTA (Leer todas las personas)
    public List<Persona> obtenerTodasLasPersonas() {
        List<Persona> lista = new ArrayList<>();
        String sql = "SELECT * FROM Personas";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Persona p = new Persona(rs.getInt("id"), rs.getString("nombre"), rs.getString("direccion"));
                // Aquí podrías agregar otra consulta para llenar sus teléfonos si lo necesitas en la vista
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // MODIFICACIÓN (Actualizar Persona)
    public boolean actualizarPersona(Persona persona) {
        String sql = "UPDATE Personas SET nombre = ?, direccion = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, persona.getNombre());
            pstmt.setString(2, persona.getDireccion());
            pstmt.setInt(3, persona.getId());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // BAJA (Eliminar Persona)
    public boolean eliminarPersona(int id) {
        String sql = "DELETE FROM Personas WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // AGREGAR TELÉFONO A UNA PERSONA EXISTENTE
    public boolean agregarTelefono(int personaId, String telefono) {
        String sql = "insert into Telefonos (personaId, telefono) values (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, personaId);
            pstmt.setString(2, telefono);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ELIMINAR UN TELÉFONO ESPECÍFICO
    public boolean eliminarTelefono(int telefonoId) {
        String sql = "delete from Telefonos where id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, telefonoId);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // CONSULTAR LOS TELÉFONOS DE UNA PERSONA
    public List<Telefono> obtenerTelefonosPorPersona(int personaId) {
        List<Telefono> listaTelefonos = new ArrayList<>();
        String sql = "select * from Telefonos where personaId = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, personaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Telefono tel = new Telefono(
                            rs.getInt("id"),
                            rs.getInt("personaId"),
                            rs.getString("telefono")
                    );
                    listaTelefonos.add(tel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaTelefonos;
    }
}
