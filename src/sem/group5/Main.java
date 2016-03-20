package sem.group5;


import org.openkinect.freenect.*;

import java.io.*;
import java.lang.Thread;
import java.nio.ByteBuffer;
import java.net.*;

public class Main {

    static InputStream in;
    static OutputStream out;
    static Boolean waiting = false;
    static Socket client = null;
    public static void main(String[] args) {
        Context context = Freenect.createContext();
        Device d = context.openDevice(0);

        d.setDepthFormat(DepthFormat.D11BIT);
        d.setVideoFormat(VideoFormat.RGB);

        try{DatagramSocket socket = new DatagramSocket();}
        catch(Exception e) {}

        /*d.startVideo(new VideoHandler(){
            @Override
            public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp) {
                sendRGB(mode, frame, timestamp);
            }
        });*/

        d.startDepth(new DepthHandler() {
            @Override
            public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp) {
                sendDepth(mode, frame, timestamp);
            }
        });

        try {
            System.out.println("changed to yellow");
            d.setLed(LedStatus.BLINK_YELLOW);
            d.setTiltAngle(20.0);
            Thread.sleep(2000);
            System.out.println("changed to green");
            d.setLed(LedStatus.BLINK_GREEN);
            d.setTiltAngle(-20.0);
        }
        catch(Exception e) {}

        //Server experiments

        int port = 1234;
        boolean serverUp = true;
        ServerSocket server = null;

        try
        {
            System.out.println("Listening on port " + port);
            server = new ServerSocket(1234);
        }
        catch(IOException e) {
            System.err.println("Could not listen on port: 1234.");
            System.exit(1);
        }


        client = null;
        try {
            client = server.accept();
            System.out.println("Accepted client");
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        try {
            in = client.getInputStream();
            out = client.getOutputStream();
            while( true ) {
                if(in.read() != -1)
                {
                    System.out.println("Something was available");
                    waiting=true;

                }
                Thread.sleep(1000);
            }
        } catch(Exception e){}
    }

    public static void sendDepth(FrameMode mode, ByteBuffer frame, int timestamp)
    {
        if(waiting)
        {
            try {
                waiting = false;
                System.out.println("Sending depth, frameremain" + frame.remaining());

                byte[] frameArray = new byte[frame.remaining()];
                frame.get(frameArray);
                out.write(frameArray);
                System.out.println("flushing");
                out.flush();
            }catch(Exception e){
                System.out.println("Exception " + e.getMessage());
            }

            frame.clear();
        }
    }

    public static void sendRGB(FrameMode mode, ByteBuffer frame, int timestamp)
    {
        if(out != null && false)
        {
            try {
                System.out.println("Sending rgb");
                out.write(frame.array());
                waiting=false;
            }catch(Exception e){}
        }

    }


}