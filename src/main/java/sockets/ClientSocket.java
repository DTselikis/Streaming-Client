package sockets;

import controllers.MainContoller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger LOGGER = LogManager.getLogger(ClientSocket.class);

    public ClientSocket() throws Exception {
        try {
            socket = new Socket(LOCALHOST, PORT);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    public ArrayList<VideoInfo> sendBitAndFormat(int bitrate, String format) {
        // Send bitrate and desired format to server
        try {
            output.writeObject(bitrate + "#" + format);
            LOGGER.info("Bitrate and format sent! " + bitrate + " " + format);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

        // Receive information about the files that fits specifications
        HashMap<String, ArrayList<Integer>> supportedFiles = null;
        try {
            supportedFiles = (HashMap<String, ArrayList<Integer>>) input.readObject();
            LOGGER.info("Received file: " + supportedFiles.keySet());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
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
            LOGGER.error("Sent selection: " + title + " " + resolution + " " + protocol);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

        // TCP and UDP will have as response only the streaming port.
        // RTP will also send the .sdp file.
        String response = "";
        if (!protocol.equals("RTP")) {
            try {
                response = (String) input.readObject();
                LOGGER.info("Response received: " + response);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
        }
        else {
            try {
                response = new FileServer(destDir, input, socket.getInputStream()).receiveFile();
                LOGGER.info("RTP response: " + response);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
        }

        return response;
    }
}
