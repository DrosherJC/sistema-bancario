package com.example.frontend;

import com.example.frontend.model.Cliente;
import com.example.frontend.service.AuthService;
import com.example.frontend.service.ClienteService;
import com.example.frontend.util.Dialogos;
import com.example.frontend.util.Navegador;
import com.example.frontend.util.Sesion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class ClientesController {

    @FXML private TextField txfNombre;
    @FXML private TextField txfApellido;
    @FXML private TextField txfCedula;
    @FXML private TextField txfEmail;
    @FXML private TextField txfTelefono;
    @FXML private TextField txfDireccion;
    @FXML private TextField txfUsuario;
    @FXML private PasswordField txfPassword;
    @FXML private TextField txfBuscar;

    @FXML private TableView<Cliente> tblCientes;
    @FXML private TableColumn<Cliente, String> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colApellido;
    @FXML private TableColumn<Cliente, String> colCedula;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, String> colTelefono;

    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnVolver;

    private final ClienteService clienteService = new ClienteService();
    private final AuthService authService = new AuthService();
    private final ObservableList<Cliente> datos = FXCollections.observableArrayList();
    private Cliente clienteSeleccionado;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));
        colApellido.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getApellido()));
        colCedula.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCedula()));
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        colTelefono.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTelefono()));

        FilteredList<Cliente> filtrados = new FilteredList<>(datos, c -> true);
        txfBuscar.textProperty().addListener((obs, viejo, nuevo) -> {
            String filtro = nuevo == null ? "" : nuevo.toLowerCase();
            filtrados.setPredicate(c ->
                    filtro.isBlank()
                            || c.getNombreCompleto().toLowerCase().contains(filtro)
                            || c.getCedula().contains(filtro));
        });
        tblCientes.setItems(filtrados);

        tblCientes.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
            clienteSeleccionado = nuevo;
            if (nuevo != null) {
                txfNombre.setText(nuevo.getNombre());
                txfApellido.setText(nuevo.getApellido());
                txfCedula.setText(nuevo.getCedula());
                txfEmail.setText(nuevo.getEmail());
                txfTelefono.setText(nuevo.getTelefono());
                txfDireccion.setText(nuevo.getDireccion());
            }
        });


        boolean soloLectura = !Sesion.esAdministrador();
        btnGuardar.setDisable(soloLectura);
        btnActualizar.setDisable(soloLectura);
        btnEliminar.setDisable(soloLectura);

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            datos.setAll(clienteService.listarTodos());
        } catch (SQLException e) {
            Dialogos.error("No se pudo cargar la lista de clientes");
            e.printStackTrace();
        }
    }

    @FXML
    private void onGuardar() {
        try {
            Cliente cliente = leerFormulario(null);
            authService.registrar(txfUsuario.getText(), txfPassword.getText(), cliente);
            cargarDatos();
            onLimpiar();
        } catch (IllegalArgumentException | AuthService.UsuarioExistenteException e) {
            Dialogos.error(e.getMessage());
        } catch (SQLException e) {
            Dialogos.error("Error de base de datos al guardar el cliente");
            e.printStackTrace();
        }
    }

    @FXML
    private void onActualizar() {
        if (clienteSeleccionado == null) {
            Dialogos.error("Selecciona un cliente de la tabla primero");
            return;
        }
        try {
            Cliente cliente = leerFormulario(clienteSeleccionado.getId());
            clienteService.actualizar(cliente);
            cargarDatos();
            onLimpiar();
        } catch (IllegalArgumentException e) {
            Dialogos.error(e.getMessage());
        } catch (SQLException e) {
            Dialogos.error("Error de base de datos al actualizar el cliente");
            e.printStackTrace();
        }
    }

    @FXML
    private void onEliminar() {
        if (clienteSeleccionado == null) {
            Dialogos.error("Selecciona un cliente de la tabla primero");
            return;
        }
        if (!Dialogos.confirmar("¿Eliminar al cliente " + clienteSeleccionado.getNombreCompleto() + "?")) {
            return;
        }
        try {
            clienteService.eliminar(clienteSeleccionado.getId());
            cargarDatos();
            onLimpiar();
        } catch (SQLException e) {
            Dialogos.error("No se pudo eliminar: el cliente probablemente tiene cuentas asociadas");
            e.printStackTrace();
        }
    }

    @FXML
    private void onLimpiar() {
        txfNombre.clear();
        txfApellido.clear();
        txfCedula.clear();
        txfEmail.clear();
        txfTelefono.clear();
        txfDireccion.clear();
        txfUsuario.clear();
        txfPassword.clear();
        tblCientes.getSelectionModel().clearSelection();
        clienteSeleccionado = null;
    }

    @FXML
    private void onVolver() {
        Navegador.cambiarEscena(btnVolver, "DashboardView.fxml", "Sistema Bancario - Dashboard");
    }

    private Cliente leerFormulario(Long id) {
        return new Cliente(id, txfNombre.getText(), txfApellido.getText(), txfCedula.getText(),
                txfEmail.getText(), txfTelefono.getText(), txfDireccion.getText());
    }
}
