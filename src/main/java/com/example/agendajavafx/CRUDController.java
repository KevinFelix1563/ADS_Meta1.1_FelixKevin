package com.example.agendajavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CRUDController {

    // Referencias a los elementos del panel izquierdo (Personas)
    @FXML private TableView<Persona> tablaPersonas;
    @FXML private TableColumn<Persona, Integer> colId;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, String> colDireccion;

    @FXML private TextField txtNombre;
    @FXML private TextField txtDireccion;

    // Referencias a los elementos del panel derecho (Teléfonos)
    @FXML private ListView<Telefono> listaTelefonos;
    @FXML private TextField txtTelefono;

    // Instancia de nuestra clase de base de datos
    private AgendaDAO dao = new AgendaDAO();

    // Listas especiales de JavaFX que actualizan la interfaz automáticamente al cambiar
    private ObservableList<Persona> listaObservablePersonas;
    private ObservableList<Telefono> listaObservableTelefonos;

    @FXML
    public void initialize() {
        // 1. Configurar de dónde sacará los datos cada columna de la tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        // 2. Configurar cómo se mostrará el texto en la lista de teléfonos
        listaTelefonos.setCellFactory(param -> new ListCell<Telefono>() {
            @Override
            protected void updateItem(Telefono item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getTelefono() == null) {
                    setText(null);
                } else {
                    setText(item.getTelefono());
                }
            }
        });

        // 3. Crear un "escuchador" para saber cuándo haces clic en una persona de la tabla
        tablaPersonas.getSelectionModel().selectedItemProperty().addListener((obs, viejaSeleccion, nuevaSeleccion) -> {
            if (nuevaSeleccion != null) {
                // Autocompletar los campos de texto
                txtNombre.setText(nuevaSeleccion.getNombre());
                txtDireccion.setText(nuevaSeleccion.getDireccion());
                // Cargar los teléfonos de esa persona específica
                cargarTelefonos(nuevaSeleccion.getId());
            }
        });

        // 4. Cargar los datos de la base de datos al iniciar el programa
        cargarPersonas();
    }

    // --- MÉTODOS DE APOYO ---
    private void cargarPersonas() {
        listaObservablePersonas = FXCollections.observableArrayList(dao.obtenerTodasLasPersonas());
        tablaPersonas.setItems(listaObservablePersonas);
    }

    private void cargarTelefonos(int personaId) {
        listaObservableTelefonos = FXCollections.observableArrayList(dao.obtenerTelefonosPorPersona(personaId));
        listaTelefonos.setItems(listaObservableTelefonos);
    }

    // --- MÉTODOS DE LOS BOTONES (Personas) ---
    @FXML
    protected void onAgregarPersona() {
        String nombre = txtNombre.getText();
        String direccion = txtDireccion.getText();

        if (!nombre.trim().isEmpty()) {
            Persona nuevaPersona = new Persona(nombre, direccion);
            if (dao.agregarPersona(nuevaPersona)) {
                cargarPersonas();
                txtNombre.clear();
                txtDireccion.clear();
            }
        }
    }

    @FXML
    protected void onActualizarPersona() {
        Persona seleccionada = tablaPersonas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            seleccionada.setNombre(txtNombre.getText());
            seleccionada.setDireccion(txtDireccion.getText());

            if (dao.actualizarPersona(seleccionada)) {
                cargarPersonas();
                tablaPersonas.refresh();
            }
        }
    }

    @FXML
    protected void onEliminarPersona() {
        Persona seleccionada = tablaPersonas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            if (dao.eliminarPersona(seleccionada.getId())) {
                cargarPersonas();
                listaTelefonos.getItems().clear(); // Limpiamos la lista visual de teléfonos
                txtNombre.clear();
                txtDireccion.clear();
            }
        }
    }

    // --- MÉTODOS DE LOS BOTONES (Teléfonos) ---
    @FXML
    protected void onAgregarTelefono() {
        Persona seleccionada = tablaPersonas.getSelectionModel().getSelectedItem();
        String tel = txtTelefono.getText();

        if (seleccionada != null && !tel.trim().isEmpty()) {
            if (dao.agregarTelefono(seleccionada.getId(), tel)) {
                cargarTelefonos(seleccionada.getId());
                txtTelefono.clear();
            }
        }
    }

    @FXML
    protected void onEliminarTelefono() {
        Telefono telSeleccionado = listaTelefonos.getSelectionModel().getSelectedItem();
        Persona personaSeleccionada = tablaPersonas.getSelectionModel().getSelectedItem();

        if (telSeleccionado != null && personaSeleccionada != null) {
            if (dao.eliminarTelefono(telSeleccionado.getId())) {
                cargarTelefonos(personaSeleccionada.getId());
            }
        }
    }
}