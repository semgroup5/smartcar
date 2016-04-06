package sem.group5;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by jpp on 04/04/16.
 */
public class Listener {

    public static void main(String[] args)
    {
        try
        {
            Socket s = new Socket(InetAddress.getByName("10.42.0.53"), 50001);
            InputStream in = s.getInputStream();

            s.getOutputStream().write("bleh".getBytes());
            s.getOutputStream().flush();

            String buffer = "";

            int prev = ' ';
            int cur  = ' ';
            boolean bin = true;
            while(true)
            {
                prev = cur;
                cur = in.read();
                if(prev == '\r' && cur == '\n' )
                {
                    bin = !bin;
                }

                if(bin)
                {
                    System.out.print(cur);
                }
                else {
                    System.out.print((char) cur);
                }
            }
        }
        catch(Exception e)
        {

        }


    }
}
