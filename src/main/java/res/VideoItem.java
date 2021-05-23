package res;

public class VideoItem {
    private final String title;
    private final String format;
    private final Integer resolution;
    private final int index;

    public VideoItem(String title, String format, Integer resolution, int index) {
        this.title = title;
        this.format = format;
        this.resolution = resolution;
        this.index = index;
    }

    public String getTitle() {
        return this.title;
    }

    public String getFormat() {
        return this.format;
    }

    public Integer getResolution() {
        return this.resolution;
    }

    public int getIndex() {
        return this.index;
    }
}
