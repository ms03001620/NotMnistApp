package com.example.mark.loadtensorflowmodeltest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mark.loadtensorflowmodeltest.widget.InfoImageView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String YOLO_MODEL_FILE = "file:///android_asset/tiny-yolo-voc.pb";

    private static final String NOTMNIST_MODEL_FILE = "file:///android_asset/not-mnist-a-j-tf1.2.pb";

    private static final int YOLO_INPUT_SIZE = 416;
    private static final String YOLO_INPUT_NAME = "input";
    private static final String YOLO_OUTPUT_NAMES = "output";
    private static final int YOLO_BLOCK_SIZE = 32;

    private static final int CROP_SIZE = YOLO_INPUT_SIZE;

    private Classifier detector;

    TextView mTextView;
    InfoImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView)findViewById(R.id.text);
        image = (InfoImageView)findViewById(R.id.image);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long start = System.currentTimeMillis();




                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a0);


                detector.recognizeImage(bitmap);

                mTextView.setText("total pass:"+(System.currentTimeMillis()-start));

                Log.v("MainAcivity", "total pass:"+(System.currentTimeMillis()-start));

            }
        });

        detector = TensorFlowYoloDetector.create(
                        getAssets(),
                NOTMNIST_MODEL_FILE,
                        28,
                        "input:0",
                        "out_softmax:0");

    }



}
