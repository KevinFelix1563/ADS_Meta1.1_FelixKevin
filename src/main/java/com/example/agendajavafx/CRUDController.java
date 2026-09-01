package com.example.agendajavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CRUDController {

    // Elementos de Personas
    @FXML private TableView<Persona> tablaPersonas;
    @FXML private TableColumn<Persona, Integer> colId;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TextField txtNombre;

    // Elementos de Direcciones
    @FXML private ListView<Direccion> listaDirecciones;
    @FXML private TextField txtDireccion;

    // Elementos de Teléfonos
    @FXML private ListView<Telefono> listaTelefonos;
    @FXML private TextField txtTelefono;

    private IAgendaDAO dao = new AgendaDAO();

    private ObservableList<Persona> listaObservablePersonas;
    private ObservableList<Direccion> listaObservableDirecciones;
    private ObservableList<Telefono> listaObservableTelefonos;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Configurar vista de las listas
        listaTelefonos.setCellFactory(param -> new ListCell<Telefono>() {
            @Override
            protected void updateItem(Telefono item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getTelefono());
            }
        });

        listaDirecciones.setCellFactory(param -> new ListCell<Direccion>() {
            @Override
            protected void updateItem(Direccion item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getDireccionCompleta());
            }
        });

        // Escuchador de selección en la tabla de personas
        tablaPersonas.getSelectionModel().selectedItemProperty().addListener((obs, vieja, nueva) -> {
            if (nueva != null) {
                txtNombre.setText(nueva.getNombre());
                cargarTelefonos(nueva.getId());
                cargarDirecciones(nueva.getId());
            }
        });

        cargarPersonas();
    }

    private void cargarPersonas() {
        listaObservablePersonas = FXCollections.observableArrayList(dao.obtenerTodasLasPersonas());
        tablaPersonas.setItems(listaObservablePersonas);
    }

    private void cargarTelefonos(int personaId) {
        listaObservableTelefonos = FXCollections.observableArrayList(dao.obtenerTelefonosPorPersona(personaId));
        listaTelefonos.setItems(listaObservableTelefonos);
    }

    private void cargarDirecciones(int personaId) {
        listaObservableDirecciones = FXCollections.observableArrayList(dao.obtenerDireccionesPorPersona(personaId));
        listaDirecciones.setItems(listaObservableDirecciones);
    }

    @FXML
    protected void onAgregarPersona() {
        if (!txtNombre.getText().trim().isEmpty()) {
            if (dao.agregarPersona(new Persona(txtNombre.getText()))) {
                cargarPersonas();
                txtNombre.clear();
            }
        }
    }

    @FXML
    protected void onActualizarPersona() {
        Persona seleccionada = tablaPersonas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            seleccionada.setNombre(txtNombre.getText());
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
                listaTelefonos.getItems().clear();
                listaDirecciones.getItems().clear();
                txtNombre.clear();
            }
        }
    }

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
        Telefono tel = listaTelefonos.getSelectionModel().getSelectedItem();
        Persona per = tablaPersonas.getSelectionModel().getSelectedItem();
        if (tel != null && per != null && dao.eliminarTelefono(tel.getId())) {
            cargarTelefonos(per.getId());
        }
    }

    @FXML
    protected void onAgregarDireccion() {
        Persona seleccionada = tablaPersonas.getSelectionModel().getSelectedItem();
        String dir = txtDireccion.getText();

        if (seleccionada != null && !dir.trim().isEmpty()) {
            // Este método verifica si existe, si no, la crea, y nos devuelve el ID
            int idDireccion = dao.buscarOAgregarDireccion(dir);
            if (idDireccion != -1) {
                dao.vincularPersonaDireccion(seleccionada.getId(), idDireccion);
                cargarDirecciones(seleccionada.getId());
                txtDireccion.clear();
            }
        }
    }

    @FXML
    protected void onEliminarDireccion() {
        Direccion dirSeleccionada = listaDirecciones.getSelectionModel().getSelectedItem();
        Persona personaSeleccionada = tablaPersonas.getSelectionModel().getSelectedItem();

        if (dirSeleccionada != null && personaSeleccionada != null) {
            if (dao.desenlazarDireccion(personaSeleccionada.getId(), dirSeleccionada.getId())) {
                cargarDirecciones(personaSeleccionada.getId()); // Recargar la lista visual
            }
        }
    }
}