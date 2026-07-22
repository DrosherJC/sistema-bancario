package com.example.frontend;

import com.example.frontend.dao.ClienteDAO;
import com.example.frontend.model.Cliente;
import com.example.frontend.model.Cuenta;
import com.example.frontend.model.EstadoCuenta;
import com.example.frontend.model.TipoCuenta;
import com.example.frontend.service.CuentaService;
import com.example.frontend.util.Dialogos;
import com.example.frontend.util.LimitadorCampos;
import com.example.frontend.util.Navegador;
import com.example.frontend.util.Sesion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.transformation.FilteredList;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.sql.SQLException;

public class CuentasController {

    @FXML private TextField txfNumeroCuenta;
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private ComboBox<TipoCuenta> cmbTipo;
    @FXML private TextField txfSaldo;
    @FXML private ComboBox<EstadoCuenta> cmbEstado;

    @FXML private TableView<Cuenta> tblCuentas;
    @FXML private TableColumn<Cuenta, String> colId;
    @FXML private TableColumn<Cuenta, String> colNumero;
    @FXML private TableColumn<Cuenta, String> colCliente;
    @FXML private TableColumn<Cuenta, String> colTipo;
    @FXML private TableColumn<Cuenta, String> colSaldo;
    @FXML private TableColumn<Cuenta, String> colEstado;

    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnVolver;
    @FXML private TextField txfBuscar;


    private final CuentaService cuentaService = new CuentaService();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ObservableList<Cuenta> datos = FXCollections.observableArrayList();
    private Cuenta cuentaSeleccionada;
    private final ObservableList<Cliente> todosLosClientes = FXCollections.observableArrayList();
    private FilteredList<Cliente> clientesFiltrados;
    private boolean actualizandoTexto = false;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colNumero.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNumeroCuenta()));
        colCliente.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getClienteNombre()));
        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo().toString()));
        colSaldo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSaldo().toString()));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstado().toString()));
        tblCuentas.setItems(datos);

        cmbTipo.setItems(FXCollections.observableArrayList(TipoCuenta.values()));
        cmbEstado.setItems(FXCollections.observableArrayList(EstadoCuenta.values()));

        FilteredList<Cuenta> filtrados = new FilteredList<>(datos, c -> true);
        txfBuscar.textProperty().addListener((obs, viejo, nuevo) -> {
            String filtro = nuevo == null ? "" : nuevo.toLowerCase();
            filtrados.setPredicate(c ->
                    filtro.isBlank()
                            || c.getClienteNombre().toLowerCase().contains(filtro)
                            || c.getNumeroCuenta().contains(filtro));
        });
        tblCuentas.setItems(filtrados);
        tblCuentas.getSelectionModel().selectedItemProperty().addListener((obs, viejo, seleccionada) -> {
            cuentaSeleccionada = seleccionada;
            if (seleccionada != null) {
                txfNumeroCuenta.setEditable(true);
                txfNumeroCuenta.setText(seleccionada.getNumeroCuenta());
                cmbCliente.setValue(todosLosClientes.stream()
                        .filter(c -> c.getId().equals(seleccionada.getClienteId()))
                        .findFirst().orElse(null));
                cmbTipo.setValue(seleccionada.getTipo());
                cmbEstado.setValue(seleccionada.getEstado());
                txfSaldo.setText(seleccionada.getSaldo().toString());
                txfSaldo.setDisable(true);
                txfNumeroCuenta.setEditable(false);
            }
        });
        cmbCliente.setEditable(true);

        cmbCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente c) {
                return c == null ? "" : c.toString();
            }
            @Override
            public Cliente fromString(String texto) {
                return todosLosClientes.stream()
                        .filter(c -> c.toString().equalsIgnoreCase(texto))
                        .findFirst().orElse(null);
            }
        });

        cmbCliente.getEditor().textProperty().addListener((obs, viejo, nuevo) -> {
            if (actualizandoTexto) return;
            String filtro = nuevo == null ? "" : nuevo.toLowerCase();
            clientesFiltrados.setPredicate(c ->
                    filtro.isBlank()
                            || c.getNombreCompleto().toLowerCase().contains(filtro)
                            || c.getCedula().contains(filtro));
            if (!filtro.isBlank() && !cmbCliente.isShowing()) {
                cmbCliente.show();
            }
        });

        cmbCliente.setOnAction(e -> {
            Cliente seleccionado = cmbCliente.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                actualizandoTexto = true;
                cmbCliente.getEditor().setText(seleccionado.toString());
                actualizandoTexto = false;
            }
        });

        boolean soloLectura = !Sesion.esAdministrador();
        btnGuardar.setDisable(soloLectura);
        btnActualizar.setDisable(soloLectura);
        btnEliminar.setDisable(soloLectura);

        LimitadorCampos.limitarSoloNumeros(txfNumeroCuenta, 20);
        LimitadorCampos.limitarDecimal(txfSaldo, 12, 2);

        cargarClientes();
        cargarDatos();
        generarNumeroCuentaAutomatico();


    }

    private void generarNumeroCuentaAutomatico() {
        try {
            txfNumeroCuenta.setText(cuentaService.generarNumeroCuentaUnico());
        } catch (SQLException e) {
            Dialogos.error("No se pudo generar el número de cuenta");
            e.printStackTrace();
        } finally {
            txfNumeroCuenta.setEditable(false);
        }
    }

    private void cargarClientes() {
        try {
            todosLosClientes.setAll(clienteDAO.listarTodos());
            clientesFiltrados = new FilteredList<>(todosLosClientes, c -> true);
            cmbCliente.setItems(clientesFiltrados);
        } catch (SQLException e) {
            Dialogos.error("No se pudo cargar la lista de clientes");
            e.printStackTrace();
        }
    }

    private void cargarDatos() {
        try {
            datos.setAll(cuentaService.listarTodos());
        } catch (SQLException e) {
            Dialogos.error("No se pudo cargar la lista de cuentas");
            e.printStackTrace();
        }
    }

    @FXML
    private void onGuardar() {
        try {
            Cuenta cuenta = new Cuenta();
            cuenta.setNumeroCuenta(txfNumeroCuenta.getText());
            cuenta.setClienteId(cmbCliente.getValue() == null ? null : cmbCliente.getValue().getId());
            cuenta.setTipo(cmbTipo.getValue());
            cuenta.setSaldo(parseMonto(txfSaldo.getText()));
            cuenta.setEstado(EstadoCuenta.ACTIVA);
            cuentaService.crear(cuenta);
            cargarDatos();
            onLimpiar();
        } catch (IllegalArgumentException e) {
            Dialogos.error(e.getMessage());
        } catch (SQLException e) {
            Dialogos.error("Error de base de datos al guardar la cuenta");
            e.printStackTrace();
        }
    }

    @FXML
    private void onActualizar() {
        if (cuentaSeleccionada == null) {
            Dialogos.error("Selecciona una cuenta de la tabla primero");
            return;
        }
        try {
            Cuenta cuenta = new Cuenta();
            cuenta.setId(cuentaSeleccionada.getId());
            cuenta.setNumeroCuenta(txfNumeroCuenta.getText());
            cuenta.setClienteId(cmbCliente.getValue() == null ? null : cmbCliente.getValue().getId());
            cuenta.setTipo(cmbTipo.getValue());
            cuenta.setEstado(cmbEstado.getValue() == null ? cuentaSeleccionada.getEstado() : cmbEstado.getValue());
            cuentaService.actualizar(cuenta);
            cargarDatos();
            onLimpiar();
        } catch (IllegalArgumentException e) {
            Dialogos.error(e.getMessage());
        } catch (SQLException e) {
            Dialogos.error("Error de base de datos al actualizar la cuenta");
            e.printStackTrace();
        }
    }

    @FXML
    private void onEliminar() {
        if (cuentaSeleccionada == null) {
            Dialogos.error("Selecciona una cuenta de la tabla primero");
            return;
        }
        if (!Dialogos.confirmar("¿Eliminar la cuenta " + cuentaSeleccionada.getNumeroCuenta() + "?")) {
            return;
        }
        try {
            cuentaService.eliminar(cuentaSeleccionada.getId());
            cargarDatos();
            onLimpiar();
        } catch (SQLException e) {
            Dialogos.error("No se pudo eliminar: la cuenta probablemente tiene transacciones asociadas");
            e.printStackTrace();
        }
    }

    @FXML
    private void onLimpiar() {
        txfSaldo.clear();
        txfSaldo.setDisable(false);
        cmbCliente.setValue(null);
        cmbTipo.setValue(null);
        cmbEstado.setValue(null);
        tblCuentas.getSelectionModel().clearSelection();
        cuentaSeleccionada = null;
        generarNumeroCuentaAutomatico();
    }

    @FXML
    private void onVolver() {
        Navegador.cambiarEscena(btnVolver, "DashboardView.fxml", "Sistema Bancario - Dashboard");
    }

    private BigDecimal parseMonto(String texto) {
        try {
            return new BigDecimal(texto.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("El saldo debe ser un numero valido");
        }
    }
}