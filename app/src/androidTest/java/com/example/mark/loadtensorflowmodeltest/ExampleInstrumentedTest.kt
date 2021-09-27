package com.example.mark.loadtensorflowmodeltest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.ByteBuffer;
import java.util.Locale;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.mark.loadtensorflowmodeltest", appContext.getPackageName());
    }

    @Test
    public void useBitmap() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        //                                          10                                      20                          27
        //[  0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   2   0 185 192   0   2   0   0]
        Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.a0);

        //Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.a0);

        int pixels[] = new int[bitmap.getHeight()* bitmap.getWidth()];

        int pixels1[] = new int[bitmap.getHeight()* bitmap.getWidth()];

        bitmap.getPixels(pixels1, 0, bitmap.getWidth(), 0, 0, bitmap.getHeight(), bitmap.getWidth());

        int k = 0;
        for(int i=0;i<28;i++){
            for(int j=0;j<28;j++){
                pixels[k]=bitmap.getPixel(i,j);
                k++;
            }
        }

        int aa = Color.alpha(pixels[22]);
        int rr = Color.red(pixels[22]);
        int gg = Color.green(pixels[22]);
        int bb = Color.blue(pixels[22]);

        int aa1 = Color.alpha(pixels1[22]);
        int rr1 = Color.red(pixels1[22]);
        int gg1 = Color.green(pixels1[22]);
        int bb1 = Color.blue(pixels1[22]);


        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getHeight() * bitmap.getRowBytes());
        bitmap.copyPixelsToBuffer(buffer);
        byte[] bytes = buffer.array();

        int size = bytes.length;




        assertTrue(true);

    }

    @Test
    public void useBitmap2() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        //                                          10                                      20                          27
        //[  0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   2   0 185 192   0   2   0   0]
        Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.a3);

        int pixels1[] = new int[bitmap.getHeight()* bitmap.getWidth()];

        bitmap.getPixels(pixels1, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());


        for(int i = 0;i<pixels1.length;i++){
            int aa1 = Color.alpha(pixels1[i]);
            int rr1 = Color.red(pixels1[i]);
            int gg1 = Color.green(pixels1[i]);
            int bb1 = Color.blue(pixels1[i]);


            if( aa1!=0 ||
                rr1!=0 ||
                gg1!=0 ||
                bb1!=0
                    ){

                Log.v("useBitmap2", "index:"+i);
            }
        }

        assertTrue(Color.red(pixels1[15])==0);
        assertTrue(Color.green(pixels1[15])==0);
        assertTrue(Color.blue(pixels1[15])==0);
        assertTrue(Color.alpha(pixels1[15])==159);

    }

    @Test
    public void useBitmap3() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        //                                          10                                      20                          27
        //[  0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   0   2   0 185 192   0   2   0   0]

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;

        Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.a3, options);

        int pixels1[] = new int[bitmap.getHeight()* bitmap.getWidth()];

        bitmap.getPixels(pixels1, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getHeight() * bitmap.getRowBytes());
        bitmap.copyPixelsToBuffer(buffer);
        byte[] bytes = buffer.array();

        int pixelsSize = pixels1.length;
        int bytesSize = bytes.length;

        for (int i = 0; i < pixelsSize; i++) {
            int p = Color.alpha(pixels1[i]);
            int b = byteToGary(bytes[i]);

            Log.v("useBitmap3", "p:" + p + ", b:" + b);
        }




    }

    private static int byteToGary(int pixels){
        int pixelsElement = pixels & 0xff;
        return pixelsElement;
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String byteToInt(byte[] bytes){
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i<bytes.length;i++){
            String number = String.format("%5d", bytes[i], Locale.getDefault());
            sb.append(number);

            if(i>10 && i%28==0){
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
