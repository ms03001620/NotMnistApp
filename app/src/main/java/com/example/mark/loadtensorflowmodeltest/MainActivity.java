package com.example.mark.loadtensorflowmodeltest;

import android.graphics.Bitmap;
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
    private static final int YOLO_INPUT_SIZE = 416;
    private static final String YOLO_INPUT_NAME = "input";
    private static final String YOLO_OUTPUT_NAMES = "output";
    private static final int YOLO_BLOCK_SIZE = 32;

    private static final int CROP_SIZE = YOLO_INPUT_SIZE;

    private Classifier detector;

    TextView mTextView;
    InfoImageView image;
    InfoImageView imageCopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView)findViewById(R.id.text);
        image = (InfoImageView)findViewById(R.id.image);
        imageCopped = (InfoImageView)findViewById(R.id.image_crop);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long start = System.currentTimeMillis();


                Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();

                Bitmap cropped = getCroppedBitmap(bitmap);

                imageCopped.setImageBitmap(cropped);

                final List<Classifier.Recognition> results = detector.recognizeImage(cropped);

                mTextView.setText("total pass:"+(System.currentTimeMillis()-start));

                Log.v("MainAcivity", "total pass:"+(System.currentTimeMillis()-start));


                imageCopped.setResult(results);
            }
        });

        detector = TensorFlowYoloDetector.create(
                        getAssets(),
                        YOLO_MODEL_FILE,
                        YOLO_INPUT_SIZE,
                        YOLO_INPUT_NAME,
                        YOLO_OUTPUT_NAMES,
                        YOLO_BLOCK_SIZE);

    }

    private Bitmap getCroppedBitmap(Bitmap source){
        long start = System.currentTimeMillis();
        int previewWidth = source.getWidth();
        int previewHeight = source.getHeight();
        int[] rgbBytes = new int[previewWidth * previewHeight];


        Bitmap rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
        Bitmap croppedBitmap = Bitmap.createBitmap(CROP_SIZE, CROP_SIZE, Bitmap.Config.ARGB_8888);

        Matrix frameToCropTransform = ImageUtils.getTransformationMatrix(previewWidth, previewHeight, CROP_SIZE, CROP_SIZE, 0, true);

        Matrix cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        byte[][] yuvBytes = new byte[3][];


        rgbFrameBitmap.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth, previewHeight);

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(source, frameToCropTransform, null);

        Log.v("MainAcivity", "getCroppedBitmap pass:"+(System.currentTimeMillis()-start));

        return croppedBitmap;
    }

}
