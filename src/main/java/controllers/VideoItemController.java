package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import listeners.VideoItemClickListener;
import res.VideoItem;

public class VideoItemController {
    private VideoItem video;
    private VideoItemClickListener listener;

    @FXML
    private Label formatLbl;
    @FXML
    private Label resLbl;
    @FXML
    private Label titleLbl;

    public void setData(VideoItem video, VideoItemClickListener listener) {
        this.video = video;
        this.listener = listener;

        setGUIInfo();
    }

    @FXML
    private void click() {
        listener.onClickListener(this.video);
    }

    private void setGUIInfo() {
        formatLbl.setText(this.video.getFormat());
        titleLbl.setText(this.video.getTitle());
        resLbl.setText(String.valueOf(this.video.getResolution()));
    }
}
