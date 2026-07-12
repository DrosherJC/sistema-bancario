package com.example.frontend;

import com.example.frontend.model.Rol;
import com.example.frontend.model.Usuario;
import com.example.frontend.service.UsuarioService;
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

import java.sql.SQLException;

public class UsuariosController {

    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, String> colId;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private ComboBox<Rol> cmbNuevoRol;
    @FXML private Button btnCambiarRol;
    @FXML private Button btnVolver;
    @FXML private Label lblMensaje;

    private final UsuarioService usuarioService = new UsuarioService();
    private final ObservableList<Usuario> datos = FXCollections.observableArrayList();
    private Usuario usuarioSeleccionado;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colUsername.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));
        colRol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRol().toString()));
        tblUsuarios.setItems(datos);

        cmbNuevoRol.setItems(FXCollections.observableArrayList(Rol.values()));

        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
            usuarioSeleccionado = nuevo;
            if (nuevo != null) {
                cmbNuevoRol.setValue(nuevo.getRol());
            }
        });

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            datos.setAll(usuarioService.listarTodos());
        } catch (SQLException e) {
            Dialogos.error("No se pudo cargar la lista de usuarios");
            e.printStackTrace();
        }
    }

    @FXML
    private void onCambiarRol() {
        if (usuarioSeleccionado == null) {
            lblMensaje.setText("Selecciona un usuario de la tabla primero");
            return;
        }
        if (cmbNuevoRol.getValue() == null) {
            lblMensaje.setText("Selecciona el nuevo rol");
            return;
        }
        try {
            usuarioService.cambiarRol(Sesion.getUsuarioActual(), usuarioSeleccionado.getId(), cmbNuevoRol.getValue());
            lblMensaje.setTextFill(javafx.scene.paint.Color.GREEN);
            lblMensaje.setText("Rol actualizado correctamente");
            cargarDatos();
        } catch (SecurityException | IllegalArgumentException e) {
            lblMensaje.setTextFill(javafx.scene.paint.Color.RED);
            lblMensaje.setText(e.getMessage());
        } catch (SQLException e) {
            lblMensaje.setTextFill(javafx.scene.paint.Color.RED);
            lblMensaje.setText("Error de conexion a la base de datos");
            e.printStackTrace();
        }
    }

    @FXML
    private void onVolver() {
        Navegador.cambiarEscena(btnVolver, "DashboardView.fxml", "Sistema Bancario - Dashboard");
    }
}
