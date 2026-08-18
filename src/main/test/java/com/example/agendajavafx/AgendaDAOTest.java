package com.example.agendajavafx;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AgendaDAOTest {

    private static AgendaDAO dao;
    private static int personaIdPrueba;
    private static int telefonoIdPrueba;

    @BeforeAll
    public static void setUp() {
        // Se ejecuta una sola vez antes de todas las pruebas
        dao = new AgendaDAO();
    }

    @Test
    @Order(1)
    public void testAgregarPersona() {
        Persona p = new Persona("Usuario de Prueba", "Calle Test 999");
        boolean resultado = dao.agregarPersona(p);

        assertTrue(resultado, "Debería retornar true al insertar la persona.");
        assertTrue(p.getId() > 0, "El ID de la persona debería haberse generado y asignado.");

        personaIdPrueba = p.getId(); // Guardamos el ID para las siguientes pruebas
    }

    @Test
    @Order(2)
    public void testActualizarPersona() {
        Persona p = new Persona(personaIdPrueba, "Usuario Modificado", "Direccion Modificada");
        boolean resultado = dao.actualizarPersona(p);

        assertTrue(resultado, "Debería retornar true al actualizar la persona.");
    }

    @Test
    @Order(3)
    public void testAgregarYConsultarTelefono() {
        boolean resultadoAgregar = dao.agregarTelefono(personaIdPrueba, "999-888-7777");
        assertTrue(resultadoAgregar, "Debería retornar true al agregar el teléfono.");

        List<Telefono> telefonos = dao.obtenerTelefonosPorPersona(personaIdPrueba);
        assertFalse(telefonos.isEmpty(), "La lista de teléfonos no debería estar vacía.");

        telefonoIdPrueba = telefonos.get(0).getId(); // Guardamos el ID del teléfono
    }

    @Test
    @Order(4)
    public void testEliminarTelefono() {
        boolean resultado = dao.eliminarTelefono(telefonoIdPrueba);
        assertTrue(resultado, "Debería retornar true al eliminar el teléfono específico.");
    }

    @Test
    @Order(5)
    public void testEliminarPersona() {
        boolean resultado = dao.eliminarPersona(personaIdPrueba);
        assertTrue(resultado, "Debería retornar true al eliminar la persona de prueba.");
    }
}