package com.example.mark.loadtensorflowmodeltest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondsw.palette.PaletteView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String NOTMNIST_MODEL_FILE = "file:///android_asset/not-mnist-a-j-tf1.2.pb";

    private TensorFlowDetector mDetector;
    private TextView mTextView;
    private TextView mTextViewMs;
    private PaletteView mPaletteView;
    private ImageView mImagePreview;
    private Bitmap mCurrentBitmap28x28;

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

                    mCurrentBitmap28x28 = mPaletteView.buildBitmap();

                    mImagePreview.setImageBitmap(mCurrentBitmap28x28);

                    String charString = mDetector.decodeBitmap(mCurrentBitmap28x28);

                    mTextView.setText(String.format("= %s", charString));
                    mTextViewMs.setText(String.format("(%s)ms", String.valueOf(System.currentTimeMillis() - start)));
                } catch (Exception e) {
                    e.printStackTrace();
                    mTextView.setText(e.getMessage());
                }
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
/*
                if(mCurrentBitmap28x28!=null){
                    saveBitmap(mCurrentBitmap28x28, "");
                }
*/


                Bitmap testBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

                String saveTo = getFilesDir().getPath()+"/sample_text.bmp";
                AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
                bmpUtil.save(testBitmap, saveTo);


                //Bitmap bitmap = AndroidBmpUtil.toGrayscale(testBitmap);
                //saveBitmap(bitmap, "");



                return false;
            }
        });

        mDetector = TensorFlowDetector.create(getAssets(), NOTMNIST_MODEL_FILE, 28, "input", "out_softmax");
    }

    private void saveBitmap(Bitmap bmp, String filename){
        //adb pull /data/data/com.example.mark.loadtensorflowmodeltest/files .

        File file = getFilesDir();

        filename = file.getPath() + "/" + System.currentTimeMillis() + ".png";

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDetector.onDestroy();
    }
}
