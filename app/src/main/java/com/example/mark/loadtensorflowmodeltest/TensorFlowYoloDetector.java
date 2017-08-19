package com.example.mark.loadtensorflowmodeltest;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.List;


public class TensorFlowYoloDetector implements Classifier {
    // Config values.
    private String inputName;
    private int inputSize;

    // Pre-allocated buffers.
    private int[] intValues;
    private float[] floatValues;
    private String[] outputNames;


    private boolean logStats = false;

    private TensorFlowInferenceInterface inferenceInterface;

    /**
     * Initializes a native TensorFlow session for classifying images.
     */
    public static Classifier create(
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

    private TensorFlowYoloDetector() {
    }


    @Override
    public List<Recognition> recognizeImage(final Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();


        // Preprocess the image data from 0-255 int to normalized float based
        // on the provided parameters.
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for(int i = 0;i<intValues.length;i++){
            int a = intValues[i];
            floatValues[i] = a;
        }

        inferenceInterface.feed(inputName, floatValues, 1, inputSize, inputSize, 1);


        // Run the inference call.
        inferenceInterface.run(outputNames, logStats);



        final float[] output = new float[10];
        inferenceInterface.fetch(outputNames[0], output);


        return null;
    }

    @Override
    public void enableStatLogging(final boolean logStats) {
        this.logStats = logStats;
    }

    @Override
    public String getStatString() {
        return inferenceInterface.getStatString();
    }

    @Override
    public void close() {
        inferenceInterface.close();
    }
}
