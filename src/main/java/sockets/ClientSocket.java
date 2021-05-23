package sockets;

import res.VideoInfo;
import services.FileServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientSocket {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private final String LOCALHOST = "127.0.0.1";
    private final int PORT = 5000;

    public ClientSocket() {
        try {
            socket = new Socket(LOCALHOST, PORT);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<VideoInfo> sendBitAndFormat(int bitrate, String format) {
        // Send bitrate and desired format to server
        try {
            output.writeObject(bitrate + "#" + format);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Receive information about the files that fits specifications
        HashMap<String, ArrayList<Integer>> supportedFiles = null;
        try {
            supportedFiles = (HashMap<String, ArrayList<Integer>>) input.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Make a list of all supported videos
        ArrayList<VideoInfo> videos = new ArrayList<VideoInfo>();
        for (Map.Entry<String, ArrayList<Integer>> entry : supportedFiles.entrySet()) {
            videos.add(new VideoInfo(entry.getKey(), entry.getValue()));
        }

        return videos;
    }

    public String sendSelection(String title, int resolution, String protocol, String destDir) {
        // Send video title, resolution and protocol to server
        try {
            output.writeObject(title + "#" + resolution + "#" + protocol);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TCP and UDP will have as response only the streaming port.
        // RTP will also send the .sdp file.
        String response = "";
        if (!protocol.equals("RTP")) {
            try {
                response = (String) input.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                response = new FileServer(destDir, input, socket.getInputStream()).receiveFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }
}
