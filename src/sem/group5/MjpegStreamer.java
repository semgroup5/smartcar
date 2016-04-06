package sem.group5;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MjpegStreamer {
    String ip;
    int port;
    Socket socket;

    public void stream(Socket s, ComboJpegProvider cjp) throws Exception
    {
        OutputStream out = s.getOutputStream();
        out.write( ( "HTTP/1.0 200 OK\r\n" +
                     "Server: YourServerName\r\n" +
                     "Connection: close\r\n" +
                     "Max-Age: 0\r\n" +
                     "Expires: 0\r\n" +
                     "Cache-Control: no-cache, private\r\n" +
                     "Pragma: no-cache\r\n" +
                     "Content-Type: multipart/x-motion-jpeg; " +
                     "boundary=--BoundaryString\r\n\r\n" ).getBytes() );

        byte[] data;
        while(true) {
            data = cjp.getLatestJPEG();
            out.write(("--BoundaryString\r\n" +
                    "Content-type: image/jpeg\r\n" +
                    "Content-Length: " +
                    data.length +
                    "\r\n\r\n").getBytes());
            out.write(data);
            out.write("\r\n\r\n".getBytes());
            out.flush();
        }
    }
}
