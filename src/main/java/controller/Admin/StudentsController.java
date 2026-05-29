package controller.Admin;

import com.example.byod.model.Student;
import utils.DataStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;

public class StudentsController extends BaseAdminController {

    @FXML private TextField searchBarField;
    @FXML private Label statusSummaryLabel;
    @FXML private TableView<Student> studentsTableView;

    @FXML private TableColumn<Student, String> colStudentName;
    @FXML private TableColumn<Student, String> colStudentID;
    @FXML private TableColumn<Student, String> colCourse;
    @FXML private TableColumn<Student, String> colEmail;
    @FXML private TableColumn<Student, String> colStatus;

    private FilteredList<Student> filteredStudents;

    @FXML
    public void initialize() {
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colStudentID.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colCourse.setCellValueFactory(new PropertyValueFactory<>("course"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Wrap the list in a FilteredList
        filteredStudents = new FilteredList<>(DataStore.getInstance().getStudentsList(), p -> true);

        // Bind search bar to filter
        searchBarField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredStudents.setPredicate(student -> {
                if (newValue == null || newValue.isBlank()) {
                    updateLabel(filteredStudents.size());
                    return true;
                }

                String keyword = newValue.toLowerCase();

                if (student.getFullName() != null && student.getFullName().toLowerCase().contains(keyword)) return true;
                if (student.getStudentId() != null && student.getStudentId().toLowerCase().contains(keyword)) return true;
                if (student.getCourse() != null && student.getCourse().toLowerCase().contains(keyword)) return true;
                if (student.getEmail() != null && student.getEmail().toLowerCase().contains(keyword)) return true;
                if (student.getStatus() != null && student.getStatus().toLowerCase().contains(keyword)) return true;

                return false;
            });
            updateLabel(filteredStudents.size());
        });

        // Wrap in SortedList so column sorting still works
        SortedList<Student> sortedStudents = new SortedList<>(filteredStudents);
        sortedStudents.comparatorProperty().bind(studentsTableView.comparatorProperty());

        studentsTableView.setItems(sortedStudents);
        updateLabel(filteredStudents.size());
    }

    private void updateLabel(int filtered) {
        int total = DataStore.getInstance().getStudentsList().size();
        if (filtered == total) {
            statusSummaryLabel.setText("Showing 1 to " + total + " of " + total + " entries");
        } else {
            statusSummaryLabel.setText("Showing " + filtered + " of " + total + " entries");
        }
    }

    @FXML
    private void handleAddStudent(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/example/byod/Admin/AddStudentModal.fxml"));
            javafx.scene.Parent root = loader.load();

            AddStudentController dialogController = loader.getController();

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Add New Student");
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.showAndWait();

            Student createdStudent = dialogController.getNewStudent();
            if (createdStudent != null) {
                DataStore.getInstance().getStudentsList().add(createdStudent);
                updateLabel(filteredStudents.size());
            }

        } catch (java.io.IOException e) {
            System.err.println("CRITICAL ERROR: Could not load the Add Student Modal.");
            e.printStackTrace();
        }
    }
}