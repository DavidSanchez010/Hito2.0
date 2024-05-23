package com.empresa.hito2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class RegistroController {

    @FXML
    private TextField registerUsernameField;

    @FXML
    private PasswordField registerPasswordField;

    @FXML
    protected void register(ActionEvent event) {
        String username = registerUsernameField.getText();
        String password = registerPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            // Mostrar mensaje de error
            return;
        }

        // Lógica de registro...

        // Redirigir a la pantalla de inicio de sesión después del registro exitoso
        try {
            Stage stage = (Stage) registerUsernameField.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("login.fxml")));
            stage.setTitle("Inicio de Sesión");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



