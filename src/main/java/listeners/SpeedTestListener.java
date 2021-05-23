package listeners;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import services.SpeedTestCallback;

public class SpeedTestListener implements ISpeedTestListener {
    private final SpeedTestCallback callback;

    public SpeedTestListener(SpeedTestCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onCompletion(SpeedTestReport report) {
        callback.updateSpeed(report.getTransferRateBit().toString());
    }

    @Override
    public void onProgress(float percent, SpeedTestReport report) {
    }

    @Override
    public void onError(SpeedTestError speedTestError, String errorMessage) {

    }
}
