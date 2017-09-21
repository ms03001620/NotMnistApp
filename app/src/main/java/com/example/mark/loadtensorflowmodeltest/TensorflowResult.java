package com.example.mark.loadtensorflowmodeltest;

import java.util.Arrays;

/**
 * Created by mark on 2017/9/21.
 */

public class TensorflowResult {
    private static String[] labels = new String[]{
            "A", "B", "C",
            "D", "E", "F",
            "G", "H", "I",
            "J"
    };

    private float[] output = new float[labels.length];

    public float[] getOutput() {
        return output;
    }

    public String getLabel() {
        return labels[argmax(output)];
    }

    private int argmax(float[] array) {
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

    public String getTopInfo() {
        int topIndex = argmax(output);
        String label = labels[topIndex];
        float accuracy = output[topIndex];
        return label + ", " + (String.valueOf(accuracy));
    }

    @Override
    public String toString() {
        return "TensorflowResult{" +
                "output=" + Arrays.toString(output) +
                '}';
    }
}
