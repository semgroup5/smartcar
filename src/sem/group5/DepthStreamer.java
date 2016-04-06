package sem.group5;

import org.openkinect.freenect.FrameMode;
import org.openkinect.freenect.VideoHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class DepthStreamer implements VideoHandler{

    DatagramSocket videoSocket;
    InetAddress ip;
    int port;

    public class videoBlock{
                                //    0-32 |   0-2048  |
        public short   blockId; //    00000 00000000000
                                //    frame| block id  |
        public byte[] pixels = new byte[256];
    }

    public DepthStreamer(InetAddress ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;

        videoSocket = new DatagramSocket(port);
    }

    static int frameCount = 0;
    @Override
    public void onFrameReceived(FrameMode mode, ByteBuffer frame, int timestamp){
        frameCount++;
        byte[] buffer = new byte[510];
        byte frameId = (byte)(frameCount % 30);

        buffer[0]  = (byte)(frameId << 3);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        packet.setAddress(ip);
        for(int i=0; i<768; i++){
            for (int j=0; j<20; j++){
                for(int k=0; k<20; k++){
                    int pixel    = ( k * 20 + j );
                    int blockCol = i % 32;
                    int blockRow = i / 32;
                    int blockOffset = ( blockCol * 20 * blockRow );
                    System.out.println(j + " " +k  );
                    System.out.println("pixel" + pixel + " " +k  );

                    buffer[ pixel ] = frame.get(blockOffset);
                }
            }
            try{
                videoSocket.send(packet);
            }
            catch(IOException e){
                System.out.println("Couldn't send packet.");
            }
        }
    }
}
