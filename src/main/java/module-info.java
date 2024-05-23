module com.empresa.hito2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core; // Agrega esta línea
    requires org.mongodb.bson;

    opens com.empresa.hito2 to javafx.fxml;
    exports com.empresa.hito2;
}