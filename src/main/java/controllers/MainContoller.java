package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainContoller implements Initializable {

    @FXML
    private TextField destFolderTF;
    @FXML
    private Button destFolderBtn;
    @FXML
    private TextField ffplayTF;
    @FXML
    private Button ffplayBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void destFolderBtn_onClick(ActionEvent e) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose the videos directory");
        File file = directoryChooser.showDialog(destFolderBtn.getScene().getWindow());

        if (file != null) {
            destFolderTF.setText(file.getAbsolutePath());
        }
    }

    public void setFfplayBtn_onClick(ActionEvent e) {
        FileChooser.ExtensionFilter exeFilter = null;
        exeFilter = new FileChooser.ExtensionFilter("EXE files (*.exe)", "*.exe");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select ffplay executable");
        fileChooser.setInitialFileName("ffplay.exe");
        fileChooser.getExtensionFilters().add(exeFilter);
        File file = fileChooser.showOpenDialog(ffplayBtn.getScene().getWindow());

        if (file != null) {
            ffplayTF.setText(file.getAbsolutePath());
        }
    }
}
