package services;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class StreamReceiver {
    private final String protocol;
    private final String parameter;
    private final String ffPlayDir;
    private final String workingDirectory;

    public StreamReceiver(String protocol, String parameter, String ffPlayDir, String workingDirectory) {
        this.protocol = protocol;
        this.parameter = parameter;
        this.ffPlayDir = ffPlayDir;
        this.workingDirectory = workingDirectory;
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
                args.add(Paths.get(workingDirectory, parameter).toString());
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
