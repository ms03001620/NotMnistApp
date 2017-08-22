package com.example.mark.loadtensorflowmodeltest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beyondsw.palette.PaletteView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String NOTMNIST_MODEL_FILE = "file:///android_asset/not-mnist-a-j-tf1.2.pb";

    private TensorFlowYoloDetector mDetector;
    private TextView mTextView;
    private TextView mTextViewMs;
    private PaletteView mPaletteView;
    private ImageView mImagePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView)findViewById(R.id.text);
        mTextViewMs = (TextView)findViewById(R.id.text_time);
        mPaletteView = (PaletteView)findViewById(R.id.palette);
        mImagePreview = (ImageView) findViewById(R.id.image_preview);

        findViewById(R.id.undo).setOnClickListener(this);
        findViewById(R.id.redo).setOnClickListener(this);
        findViewById(R.id.pen).setOnClickListener(this);
        findViewById(R.id.eraser).setOnClickListener(this);
        findViewById(R.id.clear).setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long start = System.currentTimeMillis();

                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ALPHA_8;

                    //final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b0, options);

                    final Bitmap bitmap = mPaletteView.buildBitmap();

                    mImagePreview.setImageBitmap(bitmap);

                    String charString = mDetector.decodeBitmap(bitmap);

                    mTextView.setText(String.format("= %s", charString));
                    mTextViewMs.setText(String.format("(%s)ms", String.valueOf(System.currentTimeMillis() - start)));
                } catch (Exception e) {
                    e.printStackTrace();
                    mTextView.setText(e.getMessage());
                }
            }
        });

        mDetector = TensorFlowYoloDetector.create(getAssets(), NOTMNIST_MODEL_FILE, 28, "input", "out_softmax");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.undo:
                mPaletteView.undo();
                break;
            case R.id.redo:
                mPaletteView.redo();
                break;
            case R.id.pen:
                v.setSelected(true);
                //mEraserView.setSelected(false);
                mPaletteView.setMode(PaletteView.Mode.DRAW);
                break;
            case R.id.eraser:
                v.setSelected(true);
                //mPenView.setSelected(false);
                mPaletteView.setMode(PaletteView.Mode.ERASER);
                break;
            case R.id.clear:
                mPaletteView.clear();
                break;
        }
    }

}
