package sem.group5;

import org.openkinect.freenect.FrameMode;
import org.libjpegturbo.turbojpeg.*;

import java.nio.ByteBuffer;
import java.util.Observable;

public class ComboJpegProvider extends Observable {
    static ByteBuffer latestVideoFrame;
    static ByteBuffer latestDepthFrame;

    public void receiveVideo(FrameMode frameMode, ByteBuffer byteBuffer, int i) {
        latestVideoFrame = byteBuffer;
    }

    public void receiveDepth(FrameMode frameMode, ByteBuffer byteBuffer, int i) {
        latestDepthFrame = byteBuffer;
    }



    int pixelWidth = 1;
    int imageSize = 640 * 480 * pixelWidth;
    byte[] comboFrame = new byte[imageSize];
    public byte[] getLatestJPEG() throws Exception{
        if(latestVideoFrame == null || latestDepthFrame == null)
            Thread.sleep(1000);

        ByteBuffer vFrame = latestVideoFrame;
        byte[] dFrame = new byte[614400];
        ByteBuffer ldf = latestDepthFrame;
        ldf.rewind();
        ldf.get(dFrame, 0 , 614400);

        System.out.println("Making Frame");
        int printNext = 0;
        for (int i =0; i < imageSize; i = i + pixelWidth) {
            int pixel = (i / pixelWidth) * 2; // 2 bytes per pixel for both depth and video
            comboFrame[i + 0] =  (byte)( ( ( ( dFrame[pixel +1] & 0xFF ) << 8) | ( dFrame[pixel+0] & 0xFF ) ) / 16 ); // squish depth
            //comboFrame[i + 1] = vFrame.get(pixel);
            //comboFrame[i + 2] = vFrame.get(pixel + 1);

            if(i % 36845 == 0 )
            {
                printNext = 10;
            }

            if(printNext != 0){
                printNext--;

                int p1 = dFrame[pixel+1] & 0xFF;
                int p2 = dFrame[pixel] & 0xFF;

                System.out.println("current Value : p1: " + p1 + " p2 : "+ p2 + " mod " + (( (dFrame[pixel+1] & 0xFF)<< 8) | (dFrame[pixel] & 0xFF)));
            }
        }


        System.out.println("Compressing Frame");

        try {
            TJCompressor tjc = new TJCompressor();

            tjc.setJPEGQuality(20);
            tjc.setSubsamp(TJ.SAMP_GRAY);
            tjc.setSourceImage(comboFrame, 640, (640*pixelWidth), 480, TJ.PF_GRAY);

            int flags = 0;
            byte[] compressed = tjc.compress(flags);
            byte[] compressedTruncated = new byte[tjc.getCompressedSize()];
            System.arraycopy(compressed, 0, compressedTruncated, 0, tjc.getCompressedSize());
            System.out.println("Sending Frame");
            return compressedTruncated;
        } catch (Exception e) {
            System.err.println("Exception caught, message: " + e.getMessage());
            throw e;
        }
    }
}
