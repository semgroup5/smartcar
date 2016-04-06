package sem.group5;


import org.openkinect.freenect.*;

import java.io.*;
import java.lang.Thread;
import java.net.*;

public class Main {

    static InputStream in;
    static OutputStream out;
    static Boolean waiting = false;
    static ServerSocket server = null;
    public static void main(String[] args) {
        Context context = Freenect.createContext();
        Device d = context.openDevice(0);
        Socket socket = null;
        d.setDepthFormat(DepthFormat.REGISTERED);
        d.setVideoFormat(VideoFormat.RGB);
        int port = 50001;

        try {
            server = new ServerSocket(port);
        }
        catch(Exception e) {
            System.out.println("Couldn't create socket");
            System.exit(1);
        }

        try
        {
            System.out.println("Listening on port " + port);
            socket = server.accept();
        }
        catch(IOException e) {
            System.err.println("Could not listen on port:" + port);
            System.exit(1);
        }

        try {

            System.out.println( "Starting Video Stream, " );

            ComboJpegProvider comboJpegProvider = new ComboJpegProvider();
            d.startVideo(comboJpegProvider::receiveVideo);
            d.startDepth(comboJpegProvider::receiveDepth);
            Thread.sleep(1000);

            MjpegStreamer mjpegStreamer = new MjpegStreamer();
            mjpegStreamer.stream(socket, comboJpegProvider);

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}