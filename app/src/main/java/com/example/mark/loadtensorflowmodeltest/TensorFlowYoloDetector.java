package com.example.mark.loadtensorflowmodeltest;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;


public class TensorFlowYoloDetector {
    String[] labels = new String[]{
            "A", "B", "C",
            "D", "E", "F",
            "G", "H", "I",
            "J"
    };
    private String inputName;
    private int inputSize;
    private int[] intValues;
    private float[] floatValues;
    private String[] outputNames;

    private TensorFlowInferenceInterface inferenceInterface;

    private TensorFlowYoloDetector() {
    }

    public static TensorFlowYoloDetector create(
            final AssetManager assetManager,
            final String modelFilename,
            final int inputSize,
            final String inputName,
            final String outputName) {
        TensorFlowYoloDetector d = new TensorFlowYoloDetector();
        d.inputName = inputName;
        d.inputSize = inputSize;

        // Pre-allocate buffers.
        d.outputNames = outputName.split(",");
        d.intValues = new int[inputSize * inputSize];
        d.floatValues = new float[inputSize * inputSize];


        d.inferenceInterface = new TensorFlowInferenceInterface(assetManager, modelFilename);
        return d;
    }


    public String decodeBitmap(final Bitmap bitmap) throws Exception{
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for(int i = 0;i<intValues.length;i++){
            int a = intValues[i];
            floatValues[i] = a;
        }

        inferenceInterface.feed(inputName, floatValues, 1, inputSize, inputSize, 1);
        inferenceInterface.run(outputNames, false);

        final float[] output = new float[10];
        inferenceInterface.fetch(outputNames[0], output);


        return labels[argmax(output)];
    }

    private int argmax(float[] data){
        for(int i = 0;i<data.length;i++){
            if(data[i]==1)
                return i;
        }
        return -1;
    }

    //inferenceInterface.close();
}
