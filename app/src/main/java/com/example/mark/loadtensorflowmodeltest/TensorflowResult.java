package com.example.mark.loadtensorflowmodeltest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    public String getTopInfo2() {
        int topIndex = argmax(output);
        String label = labels[topIndex];
        float accuracy = output[topIndex];
        return label + ", " + (String.valueOf(accuracy));
    }

    public String getTopInfo() {
        List<SortData> data1 = sort();

        SortData No1 = data1.get(0);
        SortData No2 = data1.get(1);

        StringBuffer sb = new StringBuffer();
        sb.append(labels[No1.index]);
        sb.append("("+No1.accuracy+")");

        sb.append("\n");
        sb.append(labels[No2.index]);
        sb.append("("+No2.accuracy+")");

        return sb.toString();
    }

    @Override
    public String toString() {
        return "TensorflowResult{" +
                "output=" + Arrays.toString(output) +
                '}';
    }

    private List<SortData> sort(){
        List<SortData> list = new ArrayList<>();

        for(int i=0;i<output.length;i++){
            SortData data = new SortData();
            data.index = i;
            data.accuracy = output[i];
            list.add(data);
        }

        Collections.sort(list);
        return list;
    }

    class SortData implements Comparable<SortData>{
        int index;
        float accuracy;

        @Override
        public int compareTo(SortData sortData) {
            if (accuracy == sortData.accuracy) {
                return 0;
            }
            if (accuracy < sortData.accuracy) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
