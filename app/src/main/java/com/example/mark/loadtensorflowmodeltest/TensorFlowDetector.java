package com.example.mark.loadtensorflowmodeltest;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.nio.ByteBuffer;


public class TensorFlowDetector {
    private String[] labels = new String[]{
            "A", "B", "C",
            "D", "E", "F",
            "G", "H", "I",
            "J"
    };
    private String inputName;
    private int inputSize;
    private String[] outputNames;

    private TensorFlowInferenceInterface inferenceInterface;

    private TensorFlowDetector() {
    }

    public static TensorFlowDetector create(
            final AssetManager assetManager,
            final String modelFilename,
            final int inputSize,
            final String inputName,
            final String outputName) {
        TensorFlowDetector d = new TensorFlowDetector();
        d.inputName = inputName;
        d.inputSize = inputSize;

        // Pre-allocate buffers.
        d.outputNames = outputName.split(",");
        d.inferenceInterface = new TensorFlowInferenceInterface(assetManager, modelFilename);
        return d;
    }


    public String decodeBitmapV1(final Bitmap bitmap) throws Exception{
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getHeight() * bitmap.getRowBytes());
        bitmap.copyPixelsToBuffer(buffer);
        byte[] bytes = buffer.array();

        float[] floatValues = normalizedPixels(bytes, bitmap.getWidth(), bitmap.getHeight());

        inferenceInterface.feed(inputName, floatValues, 1, inputSize, inputSize, 1);
        inferenceInterface.run(outputNames, false);

        final float[] output = new float[10];
        inferenceInterface.fetch(outputNames[0], output);

        return labels[argmax(output)];
    }

    public String decodeBitmap(final Bitmap bitmap) throws Exception{
        int pixels[] = new int[bitmap.getHeight()* bitmap.getWidth()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        float[] floatValues = normalizedPixels(pixels, bitmap.getWidth(), bitmap.getHeight());

        inferenceInterface.feed(inputName, floatValues, 1, inputSize, inputSize, 1);
        inferenceInterface.run(outputNames, false);

        final float[] output = new float[10];
        inferenceInterface.fetch(outputNames[0], output);

        return labels[argmax(output)];
    }

    private int argmax(float[] array){
        int index = 0;
        float largest = Integer.MIN_VALUE;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > largest) {
                largest = array[i];
                index = i;
            }
        }
        return index;
    }


    //     0.    0.    0.    0.    0.    0.    0.    0.    0.    0.
    //     0.    0.    0.    0.    0.    0.    0.    0.    0.    0.
    //     2.    0. 185. 192.  0.    2.    0.    0.


    //19 = 0
    //20 = 33686018
    //21 = 0
    //22 = -1179010631
    //23 = -1061109568
    //24 = 0
    //25 = 33686018
    //26 = 0
    //27 = 0

    /**
     * 正则化数据，均值为0，标准差为0.5
     * @param pixels 字节码像素
     * @param width 宽度
     * @param height 高度
     * @return 正则后数据 -0.5~0.5
     */
    private float[] normalizedPixels(byte[] pixels, int width, int height) {
        float[] floatValues = new float[width * height];
        for (int i = 0; i < pixels.length; ++i) {
            floatValues[i] = byteToGary(pixels[i]);
            floatValues[i] -= (255.0 / 2.0);
            floatValues[i] /= 255.0;
        }
        return floatValues;
    }

    /**
     * Bitmap.Config.ALPHA_8 这种格式数据中只有alpha是有数据的。虽然它也是32位。RGB位数是0填充的
     * 而且默认值实际表示不透明度。0为透明。255为黑色
     * 这里将alpha取出作为8bpp所代表的黑白色（这种定义只是为了和tensorflow识别模型中的数据一致）
     * pilutils.py 'L' (8-bit pixels, black and white) 和 Python Imaging Library (PIL) image.py 中的L模式
     * 因为实际8bpp是可以表示彩色的有256中颜色表示。
     *
     * 正则化数据，均值为0，标准差为0.5
     * @param pixels 字节码像素，其所在bitmap的格式为Bitmap.Config.ALPHA_8
     * @param width 宽度
     * @param height 高度
     * @return 正则后数据 -0.5~0.5
     */
    private float[] normalizedPixels(int[] pixels, int width, int height) {
        float[] floatValues = new float[width * height];
        for (int i = 0; i < pixels.length; ++i) {
            floatValues[i] = Color.alpha(pixels[i]);
            floatValues[i] -= (255.0 / 2.0);
            floatValues[i] /= 255.0;
        }
        return floatValues;
    }


    /**
     * 灰度像素的转化为0-255数值
     * @param pixels 灰度图像素
     * @return 0~255
     */
    private int byteToGary(int pixels){
        int pixelsElement = pixels & 0xff;
        return pixelsElement;
    }

    protected void onDestroy() {
        inferenceInterface.close();
    }
}
