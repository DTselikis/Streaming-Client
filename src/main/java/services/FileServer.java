package services;

import java.io.*;
import java.nio.file.Paths;

public class FileServer {
    private final String destDir;
    private final ObjectInputStream input;
    private final InputStream socketInput;

    public FileServer(String destDir, ObjectInputStream input, InputStream socketInput) {
        this.destDir = destDir;
        this.input = input;
        this.socketInput = socketInput;
    }

    public String receiveFile() {
        // Get .rdp file name and size
        String[] fileInfo = null;
        try {
            fileInfo = (String[]) input.readObject().toString().split("#");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String fileName = fileInfo[0];
        int fileSize = Integer.parseInt(fileInfo[1]);

        byte[] byteArray = new byte[fileSize];
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(Paths.get(destDir, fileName).toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Open the connection from where the .rdp file will
        // be sent
        DataInputStream data = new DataInputStream(socketInput);

        byte[] bytearray = new byte[fileSize];
        int read = 0;
        int totalRead = 0;
        int remaining = fileSize;

        // Construct the .rdp file
        try {
            while ((read = data.read(byteArray, 0, Math.min(byteArray.length, remaining))) > 0) {
                totalRead += read;
                remaining -= read;
                fos.write(byteArray, 0, read);
            }

            fos.close();
            data.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return fileName;
    }
}
