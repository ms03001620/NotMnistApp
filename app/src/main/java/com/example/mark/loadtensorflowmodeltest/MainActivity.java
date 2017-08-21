package com.example.mark.loadtensorflowmodeltest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String NOTMNIST_MODEL_FILE = "file:///android_asset/not-mnist-a-j-tf1.2.pb";

    private TensorFlowYoloDetector mDetector;
    private TextView mTextView;
    private PaletteView mPaletteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView)findViewById(R.id.text);
        mPaletteView = (PaletteView)findViewById(R.id.palette);

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
