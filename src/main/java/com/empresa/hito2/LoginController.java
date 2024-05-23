package com.empresa.hito2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField loginUsernameField;

    @FXML
    private PasswordField loginPasswordField;

    @FXML
    protected void login(ActionEvent event) {
        String username = loginUsernameField.getText();
        String password = loginPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            // Mostrar mensaje de error
            return;
        }

        // Lógica de inicio de sesión...

        // Redirigir a la aplicación principal después del inicio de sesión exitoso
        try {
            Stage stage = (Stage) loginUsernameField.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("hello-view.fxml")));
            stage.setTitle("CRUD Application");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





