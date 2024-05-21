module com.empresa.hito2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.empresa.hito2 to javafx.fxml;
    exports com.empresa.hito2;
}