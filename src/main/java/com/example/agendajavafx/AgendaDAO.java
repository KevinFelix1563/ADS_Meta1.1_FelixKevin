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
        String sqlPersona = "insert into Personas (nombre) values (?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmtPersona = conn.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {

            pstmtPersona.setString(1, persona.getNombre());
            pstmtPersona.executeUpdate();

            try (ResultSet generatedKeys = pstmtPersona.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    persona.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // CONSULTA (Leer todas las personas)
    public List<Persona> obtenerTodasLasPersonas() {
        List<Persona> lista = new ArrayList<>();
        String sql = "select * from Personas";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Persona p = new Persona(rs.getInt("id"), rs.getString("nombre"));
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // MODIFICACIÓN (Actualizar Persona)
    public boolean actualizarPersona(Persona persona) {
        String sql = "update Personas set nombre = ? where id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, persona.getNombre());
            pstmt.setInt(2, persona.getId());

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

    // BUSCAR O AGREGAR DIRECCIÓN
    public int buscarOAgregarDireccion(String direccionCompleta) {
        String sqlBuscar = "select id from Direcciones where direccionCompleta = ?";
        String sqlInsertar = "insert into Direcciones (direccionCompleta) values (?)";

        try (Connection conn = getConnection()) {
            // 1. Buscar si ya existe
            try (PreparedStatement pstmtBuscar = conn.prepareStatement(sqlBuscar)) {
                pstmtBuscar.setString(1, direccionCompleta);
                try (ResultSet rs = pstmtBuscar.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id"); // Retorna el ID existente
                    }
                }
            }

            // 2. Si no existe, insertarla
            try (PreparedStatement pstmtInsertar = conn.prepareStatement(sqlInsertar, Statement.RETURN_GENERATED_KEYS)) {
                pstmtInsertar.setString(1, direccionCompleta);
                pstmtInsertar.executeUpdate();
                try (ResultSet generatedKeys = pstmtInsertar.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Retorna el nuevo ID
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // VINCULAR PERSONA CON DIRECCIÓN (Tabla intermedia)
    public boolean vincularPersonaDireccion(int personaId, int direccionId) {
        String sql = "insert into Personas_Direcciones (personaId, direccionId) values (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, personaId);
            pstmt.setInt(2, direccionId);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // CONSULTAR LAS DIRECCIONES DE UNA PERSONA (Usando INNER JOIN)
    public List<Direccion> obtenerDireccionesPorPersona(int personaId) {
        List<Direccion> listaDirecciones = new ArrayList<>();
        String sql = "select d.id, d.direccionCompleta from Direcciones d " +
                "inner join Personas_Direcciones pd on d.id = pd.direccionId " +
                "where pd.personaId = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, personaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Direccion dir = new Direccion(rs.getInt("id"), rs.getString("direccionCompleta"));
                    listaDirecciones.add(dir);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaDirecciones;
    }
}
