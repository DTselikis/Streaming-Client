package services;

import java.io.IOException;
import java.util.ArrayList;

public class StreamReceiver {
    private final String protocol;
    private final String parameter;
    private final String ffPlayDir;

    public StreamReceiver(String protocol, String parameter, String ffPlayDir) {
        this.protocol = protocol;
        this.parameter = parameter;
        this.ffPlayDir = ffPlayDir;
    }

    public void startStream() {
        ArrayList<String> args = new ArrayList<String>();
        args.add(ffPlayDir);

        switch (protocol) {
            case "UDP": {
                args.add("udp://127.0.0.1:" + parameter);
                break;
            }
            case "TCP": {
                args.add("tcp://127.0.0.1:" + parameter);
                break;
            }
            case "RTP": {
                args.add("-protocol_whitelist");
                args.add("file,rtp,udp");
                args.add("-i");
                args.add(parameter);
            }
        }

        ProcessBuilder cmd = new ProcessBuilder(args);
        try {
            cmd.inheritIO().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
