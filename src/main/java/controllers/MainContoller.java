package controllers;

import fr.bmartel.speedtest.SpeedTestSocket;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import listeners.SpeedTestListener;
import listeners.VideoItemClickListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import res.VideoInfo;
import res.VideoItem;
import services.SpeedTestCallback;
import services.StreamReceiver;
import sockets.ClientSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainContoller implements Initializable {
    private ClientSocket client;
    private ArrayList<VideoInfo> videos = null;

    @FXML
    private TextField destFolderTF;
    @FXML
    private Button destFolderBtn;
    @FXML
    private TextField ffplayTF;
    @FXML
    private Button ffplayBtn;
    @FXML
    private ChoiceBox<String> formatCB;
    @FXML
    private Label bitrateLbl;
    @FXML
    private GridPane videoItemGrid;
    @FXML
    private ComboBox<Integer> videoResCB;
    @FXML
    private ComboBox<String> videoFormatCB;
    @FXML
    private ComboBox<String> protocolCB;
    @FXML
    private Button startStreamBtn;
    @FXML
    private Label videoTitleLbl;
    @FXML
    private Label videoResLbl;
    @FXML
    private Button startBtn;
    @FXML
    private VBox chosenVideoCard;

    private static final Logger LOGGER = LogManager.getLogger(MainContoller.class);

    private void setChoiceBoxValues() {
        formatCB.getItems().add("avi");
        formatCB.getItems().add("mp4");
        formatCB.getItems().add("mkv");

        formatCB.getSelectionModel().select(0);
    }

    public void destFolderBtn_onClick(ActionEvent e) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose the videos directory");
        File file = directoryChooser.showDialog(destFolderBtn.getScene().getWindow());

        if (file != null) {
            destFolderTF.setText(file.getAbsolutePath());
        }
    }

    public void setFFplayBtn_onClick(ActionEvent e) {
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

    public void startBtn_onClick(ActionEvent e) {
        int bitrate = Integer.parseInt(bitrateLbl.getText());
        String format = (String) formatCB.getValue();

        try {
            client = new ClientSocket();
            videos = client.sendBitAndFormat(bitrate, format);

            setGrid();
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage());
            exception.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server could not be found");
            alert.setHeaderText(null);
            alert.setContentText("Server could not be found, please try again!");
            alert.showAndWait();
        }

    }

    private void setChoosenVideo(VideoItem video) {
        // Empty items in case user click in more than
        // one videos
        videoResCB.getItems().clear();
        videoFormatCB.getItems().clear();
        protocolCB.getItems().clear();

        videoTitleLbl.setText(video.getTitle());
        videoResLbl.setText(String.valueOf(video.getResolution()));
        videoFormatCB.getItems().add(video.getFormat());
        videoFormatCB.getSelectionModel().select(0);

        int pos = 0;
        int i = 0;
        for (Integer resolution : videos.get(video.getIndex()).getResolutions()) {
            videoResCB.getItems().add(resolution);

            if (resolution == video.getResolution()) {
                pos = i;
            }

            i++;
        }

        videoResCB.getSelectionModel().select(pos);

        protocolCB.getItems().add("Recomended");
        protocolCB.getItems().add("TCP");
        protocolCB.getItems().add("UDP");
        protocolCB.getItems().add("RTP");
        protocolCB.getSelectionModel().select(0);

        chosenVideoCard.setVisible(true);

        LOGGER.info("New video setted!");
    }

    private void setGrid() {
        VideoItemClickListener listener = new VideoItemClickListener() {
            @Override
            public void onClickListener(VideoItem video) {
                setChoosenVideo(video);
            }
        };

        int column = 0;
        int row = 1;

        try {
            int i = 0;
            String format = (String) formatCB.getValue();
            for (VideoInfo video : videos) {
                for (Integer resolution : video.getResolutions()) {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/VideoItem.fxml"));

                    AnchorPane anchorPane = fxmlLoader.load();

                    VideoItemController videoItemController = fxmlLoader.getController();
                    videoItemController.setData(new VideoItem(video.getTitle(), format, resolution, i), listener);

                    if (column == 3) {
                        column = 0;
                        row++;
                    }

                    videoItemGrid.add(anchorPane, column++, row);

                    //set grid width
                    videoItemGrid.setMinWidth(Region.USE_COMPUTED_SIZE);
                    videoItemGrid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                    videoItemGrid.setMaxWidth(Region.USE_PREF_SIZE);

                    //set grid height
                    videoItemGrid.setMinHeight(Region.USE_COMPUTED_SIZE);
                    videoItemGrid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                    videoItemGrid.setMaxHeight(Region.USE_PREF_SIZE);

                    GridPane.setMargin(anchorPane, new Insets(10));

                    LOGGER.info("Added new video item: " + video.getTitle() + " " + format + " " + resolution);
                }

                LOGGER.info("Grid created!");

                i++;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void startStreamBtn_onClick(ActionEvent e) {
        String title = videoTitleLbl.getText();
        int res = (int) videoResCB.getValue();
        String protocol = (String) protocolCB.getValue();
        String destDir = destFolderTF.getText();

        if (protocol.equals("Recomended")) {
            switch (res) {
                case 240: protocol = "TCP"; break;
                case 360:
                case 480: protocol = "UDP"; break;
                case 720:
                case 1080: protocol = "RTP";
            }
        }

        if (checkPaths(protocol) == 0) {
            String response;
            response = client.sendSelection(title, res, protocol, destDir);

            new StreamReceiver(protocol, response, ffplayTF.getText(), destDir).startStream();
        }
    }

    private int checkPaths(String protocol) {
        if (ffplayTF.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ffplay directory not provided");
            alert.setHeaderText(null);
            alert.setContentText("Please provide ffplay directory!");
            alert.showAndWait();

            LOGGER.warn("ffplay directory not provided!");

            return 1;
        }

        if (protocol.equals("RTP")) {
            if (destFolderTF.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle(".rdp destination directory not provided");
                alert.setHeaderText(null);
                alert.setContentText("Please provide .rdp destination directory!");
                alert.showAndWait();

                LOGGER.warn(".rdp directory not provided!");

                return 1;
            }
        }

        LOGGER.info("Paths ok!");

        return 0;
    }

    public void updateSpeed(String speed) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bitrateLbl.setText(speed.substring(0,5));
                startBtn.setDisable(false);

                LOGGER.info("Speedtest ended. Speed: " + speed.substring(0,5));
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setChoiceBoxValues();
        startBtn.setDisable(true);

        String speedTestURI = "http://ipv4.ikoula.testdebit.info/100M.iso";
        long duration = 5000;

        SpeedTestSocket speedTest = new SpeedTestSocket();
        speedTest.addSpeedTestListener(new SpeedTestListener(new SpeedTestCallback(this)));
        speedTest.setDownloadSetupTime(5000);
        speedTest.startDownload("http://ipv4.ikoula.testdebit.info/100M.iso");

        LOGGER.info("Speedtest started!");

    }
}
