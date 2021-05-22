package res;

import java.util.ArrayList;

public class VideoInfo {
    private final String title;
    private final ArrayList<Integer> resolutions;

    public VideoInfo(String title, ArrayList<Integer> resolutions) {
        this.title = title;
        this.resolutions = resolutions;
    }

    public String getTitle() {
        return this.title;
    }

    public ArrayList<Integer> getResolutions() {
        return this.resolutions;
    }
}
