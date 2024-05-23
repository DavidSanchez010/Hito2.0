package com.empresa.hito2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;

import static com.mongodb.client.model.Filters.eq;

import java.util.Comparator;

public class HelloController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField ageField;

    @FXML
    private TextField dniField;

    @FXML
    private TextField birthDateField;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<Person> tableView;

    @FXML
    private TableColumn<Person, String> nameColumn;

    @FXML
    private TableColumn<Person, Integer> ageColumn;

    @FXML
    private TableColumn<Person, String> dniColumn;

    @FXML
    private TableColumn<Person, String> birthDateColumn;
    @FXML
    private TextField registerUsernameField;

    @FXML
    private PasswordField registerPasswordField;

    @FXML
    private TextField loginUsernameField;

    @FXML
    private PasswordField loginPasswordField;

    @FXML
    private TabPane tabPane;
    @FXML
    private TextField searchField;

    @FXML
    private ChoiceBox<String> filterChoiceBox;

    private MongoCollection<Document> collection;

    private ObservableList<Person> personList = FXCollections.observableArrayList();
    private FilteredList<Person> filteredList;

    @FXML
    public void initialize() {
        DatabaseConnection.connect("mongodb+srv://davidjesussanchezrobles23:admin@cluster0.qzp7eor.mongodb.net/", "Hito2");
        tableView.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        collection = DatabaseConnection.getDatabase().getCollection("Hito2", Document.class);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        dniColumn.setCellValueFactory(new PropertyValueFactory<>("dni"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));

        tableView.setItems(personList);

        loadRecords();

        filteredList = new FilteredList<>(personList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(person -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return person.getName().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Person> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
    }

    @FXML
    protected void filterRecords() {
        String selectedFilter = filterChoiceBox.getSelectionModel().getSelectedItem();
        if (selectedFilter != null) {
            Comparator<Person> comparator;
            switch (selectedFilter) {
                case "A-Z":
                    comparator = Comparator.comparing((Person p) -> {
                        String name = p.getName();
                        return name != null ? name : "";
                    }, String.CASE_INSENSITIVE_ORDER);
                    break;
                case "Recientes":
                    comparator = Comparator.comparing(Person::getBirthDate, Comparator.nullsFirst(Comparator.reverseOrder()));
                    break;
                default:
                    return; // No se hace nada si no se selecciona un filtro válido
            }

            // Crear una nueva lista filtrada y ordenada
            FilteredList<Person> newFilteredList = new FilteredList<>(personList, p -> true);
            newFilteredList.setPredicate(filteredList.getPredicate()); // Copiar el predicado actual
            newFilteredList.predicateProperty().bind(filteredList.predicateProperty()); // Mantener actualizado el predicado

            SortedList<Person> sortedList = new SortedList<>(newFilteredList, comparator);
            TableView<Person> newTableView = new TableView<>(sortedList);
            setupTableColumns(newTableView);

            // Establecer que la tabla ocupe toda la pestaña
            newTableView.prefWidthProperty().bind(((TabPane)newTableView.getParent().getParent()).widthProperty());
            newTableView.prefHeightProperty().bind(((TabPane)newTableView.getParent().getParent()).heightProperty());

            // Mostrar la nueva tabla en una nueva pestaña
            Tab newTab = new Tab("Filtered Table", newTableView);
            tabPane.getTabs().add(newTab);
            tabPane.getSelectionModel().select(newTab);
        }
    }

    private void setupTableColumns(TableView<Person> tableView) {
        tableView.getColumns().addAll(nameColumn, ageColumn, dniColumn, birthDateColumn);
    }

    private void showFilteredTable(TableView<Person> tableView) {
        Stage stage = new Stage();
        Scene scene = new Scene(new Group(tableView));
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void createRecord() {
        String name = nameField.getText();
        String ageText = ageField.getText();
        String dni = dniField.getText();
        String birthDate = birthDateField.getText();

        if (name.isEmpty() || ageText.isEmpty() || dni.isEmpty() || birthDate.isEmpty()) {
            statusLabel.setText("Please enter all fields!");
            return;
        }

        try {
            int age = Integer.parseInt(ageText);

            Document doc = new Document("name", name)
                    .append("age", age)
                    .append("dni", dni)
                    .append("birthDate", birthDate);

            collection.insertOne(doc);

            loadRecords();

            statusLabel.setText("Record created successfully!");
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid age format!");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    protected void deleteRecord() {
        String name = nameField.getText();
        String ageText = ageField.getText();

        if (name.isEmpty() || ageText.isEmpty()) {
            statusLabel.setText("Please enter name and age to delete!");
            return;
        }

        try {
            int age = Integer.parseInt(ageText);
            Document query = new Document("name", name).append("age", age);
            DeleteResult result = collection.deleteOne(query);

            if (result.getDeletedCount() > 0) {
                loadRecords();
                statusLabel.setText("Record deleted successfully!");
            } else {
                statusLabel.setText("Record not found!");
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid age format!");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    protected void updateRecord() {
        String name = nameField.getText();
        String ageText = ageField.getText();
        String dni = dniField.getText();
        String birthDate = birthDateField.getText();

        if (name.isEmpty() || ageText.isEmpty() || dni.isEmpty() || birthDate.isEmpty()) {
            statusLabel.setText("Please enter all fields!");
            return;
        }

        try {
            int age = Integer.parseInt(ageText);
            Document query = new Document("name", name);

            Document update = new Document("$set", new Document("age", age)
                    .append("dni", dni)
                    .append("birthDate", birthDate));

            collection.updateOne(query, update);

            loadRecords();

            statusLabel.setText("Record updated successfully!");
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid age format!");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    protected void readRecord() {
        String name = nameField.getText();
        String age = ageField.getText();
        String dni = dniField.getText();
        String birthDate = birthDateField.getText();

        if (name.isEmpty() || age.isEmpty() || dni.isEmpty() || birthDate.isEmpty()) {
            statusLabel.setText("Please enter all fields!");
            return;
        }

        statusLabel.setText("Nombre: " + name + "\nEdad: " + age + "\nDNI: " + dni + "\nFecha de nacimiento: " + birthDate);
    }

    private void loadRecords() {
        personList.clear();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String name = doc.getString("name");
                Object ageObj = doc.get("age");
                int age = 0;
                if (ageObj instanceof Integer) {
                    age = (int) ageObj;
                } else if (ageObj instanceof String) {
                    age = Integer.parseInt((String) ageObj);
                }
                String dni = doc.getString("dni");
                String birthDate = doc.getString("birthDate");
                personList.add(new Person(name, age, dni, birthDate));
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading records: " + e.getMessage());
        }
    }

    public static class Person {
        private final String name;
        private final int age;
        private final String dni;
        private final String birthDate;

        public Person(String name, int age, String dni, String birthDate) {
            this.name = name;
            this.age = age;
            this.dni = dni;
            this.birthDate = birthDate;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public String getDni() {
            return dni;
        }

        public String getBirthDate() {
            return birthDate;
        }
    }
}