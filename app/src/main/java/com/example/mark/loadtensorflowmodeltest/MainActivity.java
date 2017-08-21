package com.example.mark.loadtensorflowmodeltest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String NOTMNIST_MODEL_FILE = "file:///android_asset/not-mnist-a-j-tf1.2.pb";

    private TensorFlowYoloDetector mDetector;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView)findViewById(R.id.text);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;
        //options.inPremultiplied = false;


        //http://blog.csdn.net/haozipi/article/details/47183543?ref=myread
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a0, options);

        int x = bitmap.getWidth();
        int y = bitmap.getHeight();
        int[] intArray = new int[x * y];
        bitmap.getPixels(intArray, 0, x, 0, 0, x, y);

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long start = System.currentTimeMillis();

                try {
                    String charString = mDetector.decodeBitmap(bitmap);
                    mTextView.setText(String.format("解析用时间:(%d)毫秒, 字符为:(%s)", (System.currentTimeMillis()-start), charString));
                } catch (Exception e) {
                    e.printStackTrace();
                    mTextView.setText(e.getMessage());
                }
            }
        });

        mDetector = TensorFlowYoloDetector.create(getAssets(), NOTMNIST_MODEL_FILE, 28, "input", "out_softmax");
    }



}
