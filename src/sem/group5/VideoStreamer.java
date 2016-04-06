package sem.group5;

import org.openkinect.freenect.FrameMode;
import org.openkinect.freenect.VideoHandler;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class VideoStreamer implements VideoHandler{

    DatagramSocket videoSocket;
    InetAddress ip;
    int port;
    SocketAddress socketAddress;
    public class videoBlock{
                                //    0-32 |   0-2048  |
        public short   blockId; //    00000 00000000000
                                //    frame| block id  |
        public short[] pixels = new short[256];
    }

    public VideoStreamer(InetAddress ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        this.socketAddress = new InetSocketAddress(ip, port);
        videoSocket = new DatagramSocket(port);
    }

    static int frameCount = 0;
    @Override
    public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp){
        frameCount++;
        byte[] buffer = new byte[510];
        byte frameId = (byte)(frameCount % 30);

        buffer[0]  = (byte)(frameId << 3);
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, socketAddress);
            for (int i = 0; i < 1200; i++) {
                System.out.print("Block " + i);
                for (int j = 0; j < 16; j++) {
                    System.out.println();
                    for (int k = 0; k < 16; k++) {
                        int pixel = (k * 16 + j);
                        int blockCol = i % 40;
                        int blockRow = i / 40;
                        int blockOffset = (blockCol * 16 * blockRow);
                        System.out.println("pixel" + pixel + " " + k);

                        buffer[2 * pixel] = frame.get(2 * blockOffset);
                        buffer[2 * pixel + 1] = frame.get(2 * blockOffset + 1);
                    }
                }
                try {
                    videoSocket.send(packet);
                } catch (IOException e) {
                    System.out.println("Couldn't send packet.");
                }
            }
        }
        catch(Exception e)
        {

        }
    }
}
