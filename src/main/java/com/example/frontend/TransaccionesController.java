package com.example.frontend;

import com.example.frontend.dao.ClienteDAO;
import com.example.frontend.dao.CuentaDAO;
import com.example.frontend.model.Cliente;
import com.example.frontend.model.Cuenta;
import com.example.frontend.model.Transaccion;
import com.example.frontend.model.TipoTransaccion;
import com.example.frontend.service.TransaccionService;
import com.example.frontend.util.Dialogos;
import com.example.frontend.util.Navegador;
import com.example.frontend.util.Sesion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransaccionesController {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private ComboBox<TipoTransaccion> cmbTipo;
    @FXML private ComboBox<Cuenta> cmbCuentaOrigen;
    @FXML private TextField txfCuentaDestino;
    @FXML private TextField txfMonto;
    @FXML private Button btnEnviar;
    @FXML private Label lblResultado;
    @FXML private Button btnVolver;

    @FXML private TableView<Transaccion> tblTransacciones;
    @FXML private TableColumn<Transaccion, String> colTipo;
    @FXML private TableColumn<Transaccion, String> colMonto;
    @FXML private TableColumn<Transaccion, String> colFecha;
    @FXML private TableColumn<Transaccion, String> colCuenta;
    @FXML private TableColumn<Transaccion, String> colReceptor;

    private final TransaccionService transaccionService = new TransaccionService();
    private final CuentaDAO cuentaDAO = new CuentaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ObservableList<Transaccion> historial = FXCollections.observableArrayList();


    private Cliente clienteActual;

    @FXML
    private void initialize() {
        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo().toString()));
        colMonto.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMonto().toString()));
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFecha().format(FORMATO_FECHA)));
        colCuenta.setCellValueFactory(d -> {
            Transaccion t = d.getValue();
            String texto = switch (t.getTipo()) {
                case DEPOSITO -> "-> " + t.getCuentaDestinoNumero();
                case RETIRO -> t.getCuentaOrigenNumero() + " ->";
                case TRANSFERENCIA -> t.getCuentaOrigenNumero() + " -> " + t.getCuentaDestinoNumero();
            };
            return new SimpleStringProperty(texto);
        });
        colReceptor.setCellValueFactory(d -> {
            Transaccion t = d.getValue();
            String receptor = switch (t.getTipo()) {
                case DEPOSITO, TRANSFERENCIA -> t.getClienteDestinoNombre();
                case RETIRO -> t.getClienteOrigenNombre();
            };
            return new SimpleStringProperty(receptor == null ? "" : receptor);
        });
        tblTransacciones.setItems(historial);

        cmbTipo.setItems(FXCollections.observableArrayList(TipoTransaccion.values()));
        cmbTipo.valueProperty().addListener((obs, viejo, nuevo) -> actualizarCamposSegunTipo(nuevo));
        actualizarCamposSegunTipo(null);

        cargarClienteActual();
        cargarCuentas();
        cargarHistorial();
    }

    private void cargarClienteActual() {
        if (Sesion.esAdministrador()) {
            return;
        }
        try {
            clienteActual = clienteDAO.buscarPorUsuarioId(Sesion.getUsuarioActual().getId());
        } catch (SQLException e) {
            Dialogos.error("No se pudo cargar tus datos de cliente");
            e.printStackTrace();
        }
    }

    private void actualizarCamposSegunTipo(TipoTransaccion tipo) {
        if (tipo == null) {
            cmbCuentaOrigen.setDisable(true);
            txfCuentaDestino.setDisable(true);
            return;
        }
        switch (tipo) {
            case DEPOSITO -> {
                cmbCuentaOrigen.setDisable(false);
                txfCuentaDestino.setDisable(true);
                txfCuentaDestino.clear();
            }
            case RETIRO -> {
                cmbCuentaOrigen.setDisable(false);
                txfCuentaDestino.setDisable(true);
                txfCuentaDestino.clear();
            }
            case TRANSFERENCIA -> {
                cmbCuentaOrigen.setDisable(false);
                txfCuentaDestino.setDisable(false);
            }
        }
    }

    private void cargarCuentas() {
        try {

            if (Sesion.esAdministrador()) {
                cmbCuentaOrigen.setItems(FXCollections.observableArrayList(cuentaDAO.listarTodos()));
            } else if (clienteActual != null) {
                cmbCuentaOrigen.setItems(FXCollections.observableArrayList(
                        cuentaDAO.listarPorCliente(clienteActual.getId())));
            } else {
                cmbCuentaOrigen.setItems(FXCollections.observableArrayList());
                Dialogos.error("Tu usuario no tiene un cliente asociado, contacta a un administrador");
            }
        } catch (SQLException e) {
            Dialogos.error("No se pudo cargar la lista de cuentas");
            e.printStackTrace();
        }
    }

    private void cargarHistorial() {
        try {
            if (Sesion.esAdministrador()) {
                historial.setAll(transaccionService.listarTodos());
            } else if (clienteActual != null) {
                historial.setAll(transaccionService.listarPorCliente(clienteActual.getId()));
            } else {
                historial.setAll(List.of());
            }
        } catch (SQLException e) {
            Dialogos.error("No se pudo cargar el historial de transacciones");
            e.printStackTrace();
        }
    }

    @FXML
    private void onEnviar() {
        TipoTransaccion tipo = cmbTipo.getValue();
        if (tipo == null) {
            mostrarResultado("Selecciona el tipo de transaccion", true);
            return;
        }

        BigDecimal monto;
        try {
            monto = new BigDecimal(txfMonto.getText().trim());
        } catch (Exception e) {
            mostrarResultado("El monto debe ser un numero valido", true);
            return;
        }

        Long usuarioId = Sesion.getUsuarioActual().getId();

        try {
            switch (tipo) {
                case DEPOSITO -> {
                    requerirCuenta(cmbCuentaOrigen.getValue(), "a depositar");
                    transaccionService.depositar(cmbCuentaOrigen.getValue().getId(), monto, usuarioId);
                }
                case RETIRO -> {
                    requerirCuenta(cmbCuentaOrigen.getValue(), "origen");
                    transaccionService.retirar(cmbCuentaOrigen.getValue().getId(), monto, usuarioId);
                }
                case TRANSFERENCIA -> {
                    requerirCuenta(cmbCuentaOrigen.getValue(), "origen");
                    Cuenta destino = buscarCuentaDestinoEscrita();
                    transaccionService.transferir(cmbCuentaOrigen.getValue().getId(),
                            destino.getId(), monto, usuarioId);
                }
            }
            mostrarResultado("Transaccion realizada con exito", false);
            txfMonto.clear();
            txfCuentaDestino.clear();
            cargarCuentas();
            cargarHistorial();
        } catch (IllegalArgumentException e) {
            mostrarResultado(e.getMessage(), true);
        } catch (SQLException e) {
            mostrarResultado("Error de conexion a la base de datos", true);
            e.printStackTrace();
        }
    }

    private Cuenta buscarCuentaDestinoEscrita() throws SQLException {
        String numero = txfCuentaDestino.getText() == null ? "" : txfCuentaDestino.getText().trim();
        if (numero.isBlank()) {
            throw new IllegalArgumentException("Escribe el numero de cuenta destino");
        }
        Cuenta destino = cuentaDAO.buscarPorNumero(numero);
        if (destino == null) {
            throw new IllegalArgumentException("No existe ninguna cuenta con el numero " + numero);
        }
        return destino;
    }

    private void requerirCuenta(Cuenta cuenta, String etiqueta) {
        if (cuenta == null) {
            throw new IllegalArgumentException("Selecciona la cuenta " + etiqueta);
        }
    }

    @FXML
    private void onVolver() {
        Navegador.cambiarEscena(btnVolver, "DashboardView.fxml", "Sistema Bancario - Dashboard");
    }

    private void mostrarResultado(String mensaje, boolean esError) {
        lblResultado.setTextFill(esError ? javafx.scene.paint.Color.RED : javafx.scene.paint.Color.GREEN);
        lblResultado.setText(mensaje);
    }
}
