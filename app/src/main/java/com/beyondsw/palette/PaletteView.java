package com.beyondsw.palette;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wensefu on 17-3-21.
 */
public class PaletteView extends View {

    public static final int MNIST_SIZE = 28;
    private Paint mPaint;
    private Path mPath;

    private float mLastX;
    private float mLastY;
    private Bitmap mBufferBitmap;
    private Canvas mBufferCanvas;

    private Bitmap mBufferBitmapMnist;
    private Canvas mBufferCanvasMnist;
    private RectF mContentRectF = new RectF();

    private static final int MAX_CACHE_STEP = 20;

    private List<DrawingInfo> mDrawingList;
    private List<DrawingInfo> mRemovedList;

    private Xfermode mClearMode;
    private float mDrawSize;
    private float mEraserSize;

    private boolean mCanEraser;

    private Callback mCallback;

    public enum Mode {
        DRAW,
        ERASER
    }

    private Mode mMode = Mode.DRAW;

    public PaletteView(Context context) {
        this(context,null);
    }

    public PaletteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDrawingCacheEnabled(true);
        init();
    }

    public interface Callback {
        void onUndoRedoStatusChanged();
    }

    public void setCallback(Callback callback){
        mCallback = callback;
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawSize = 40;
        mEraserSize = mDrawSize * 10;
        mPaint.setStrokeWidth(mDrawSize);
        mPaint.setColor(0XFF00FF00);

        mClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    }

    private void initBuffer(){
        mBufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mBufferCanvas = new Canvas(mBufferBitmap);

        mBufferBitmapMnist = Bitmap.createBitmap(MNIST_SIZE, MNIST_SIZE, Bitmap.Config.ALPHA_8);
        mBufferCanvasMnist = new Canvas(mBufferBitmapMnist);

    }

    private abstract static class DrawingInfo {
        Paint paint;
        abstract void draw(Canvas canvas);
    }

    private static class PathDrawingInfo extends DrawingInfo{

        Path path;

        @Override
        void draw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }
    }

    public Mode getMode() {
        return mMode;
    }

    public void setMode(Mode mode) {
        if (mode != mMode) {
            mMode = mode;
            if (mMode == Mode.DRAW) {
                mPaint.setXfermode(null);
                mPaint.setStrokeWidth(mDrawSize);
            } else {
                mPaint.setXfermode(mClearMode);
                mPaint.setStrokeWidth(mEraserSize);
            }
        }
    }

    public void setEraserSize(float size) {
        mEraserSize = size;
    }

    public void setPenRawSize(float size) {
        mDrawSize = size;
    }

    public void setPenColor(int color) {
        mPaint.setColor(color);
    }

    public void setPenAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    private void reDraw(){
        if (mDrawingList != null) {
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            for (DrawingInfo drawingInfo : mDrawingList) {
                drawingInfo.draw(mBufferCanvas);
            }
            invalidate();
        }
    }

    public boolean canRedo() {
        return mRemovedList != null && mRemovedList.size() > 0;
    }

    public boolean canUndo(){
        return mDrawingList != null && mDrawingList.size() > 0;
    }

    public void redo() {
        int size = mRemovedList == null ? 0 : mRemovedList.size();
        if (size > 0) {
            DrawingInfo info = mRemovedList.remove(size - 1);
            mDrawingList.add(info);
            mCanEraser = true;
            reDraw();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    public void undo() {
        int size = mDrawingList == null ? 0 : mDrawingList.size();
        if (size > 0) {
            DrawingInfo info = mDrawingList.remove(size - 1);
            if (mRemovedList == null) {
                mRemovedList = new ArrayList<>(MAX_CACHE_STEP);
            }
            if (size == 1) {
                mCanEraser = false;
            }
            mRemovedList.add(info);
            reDraw();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    public void clear() {
        if (mBufferBitmap != null) {
            if (mDrawingList != null) {
                mDrawingList.clear();
            }
            if (mRemovedList != null) {
                mRemovedList.clear();
            }
            mCanEraser = false;
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            invalidate();
            if (mCallback != null) {
                mCallback.onUndoRedoStatusChanged();
            }
        }
    }

    public Bitmap buildBitmap() {
        mBufferBitmapMnist.eraseColor(Color.TRANSPARENT);


        RectF fixedRect = fixWh(mContentRectF);
        Rect rect = new Rect();
        rect.left = (int) fixedRect.left;
        rect.top = (int) fixedRect.top;
        rect.right = (int) fixedRect.right;
        rect.bottom = (int) fixedRect.bottom;


        RectF dst = new RectF();
        dst.right = MNIST_SIZE;
        dst.bottom = MNIST_SIZE;

        mBufferCanvasMnist.drawBitmap(mBufferBitmap, rect, dst, mPaint);
        return Bitmap.createBitmap(mBufferBitmapMnist);
    }

    private void saveDrawingPath(){
        if (mDrawingList == null) {
            mDrawingList = new ArrayList<>(MAX_CACHE_STEP);
        } else if (mDrawingList.size() == MAX_CACHE_STEP) {
            mDrawingList.remove(0);
        }
        Path cachePath = new Path(mPath);
        Paint cachePaint = new Paint(mPaint);
        PathDrawingInfo info = new PathDrawingInfo();
        info.path = cachePath;
        info.paint = cachePaint;
        mDrawingList.add(info);
        mCanEraser = true;
        if (mCallback != null) {
            mCallback.onUndoRedoStatusChanged();
        }
    }

    private void updateContentRect() {
        mContentRectF.set(Float.MAX_VALUE, Float.MAX_VALUE, 0, 0);
        if (mDrawingList != null && mDrawingList.size() > 0) {
            for (DrawingInfo info : mDrawingList) {
                PathDrawingInfo pathInfo = (PathDrawingInfo) info;
                RectF currentRectF = new RectF();
                pathInfo.path.computeBounds(currentRectF, false);

                mContentRectF.left = currentRectF.left < mContentRectF.left ? currentRectF.left : mContentRectF.left;

                mContentRectF.top = currentRectF.top < mContentRectF.top ? currentRectF.top : mContentRectF.top;

                mContentRectF.right = currentRectF.right > mContentRectF.right ? currentRectF.right : mContentRectF.right;

                mContentRectF.bottom = currentRectF.bottom > mContentRectF.bottom ? currentRectF.bottom : mContentRectF.bottom;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBufferBitmap != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        final float x = event.getX();
        final float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                if (mPath == null) {
                    mPath = new Path();
                }
                mPath.moveTo(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                //这里终点设为两点的中心点的目的在于使绘制的曲线更平滑，如果终点直接设置为x,y，效果和lineto是一样的,实际是折线效果
                mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                if (mBufferBitmap == null) {
                    initBuffer();
                }
                if (mMode == Mode.ERASER && !mCanEraser) {
                    break;
                }
                mBufferCanvas.drawPath(mPath,mPaint);
                invalidate();
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                if (mMode == Mode.DRAW || mCanEraser) {
                    saveDrawingPath();
                    updateContentRect();
                }

                //mBufferCanvas.drawRect(mContentRectF, mPaint);
                mPath.reset();
                postInvalidate();
                break;
        }
        return true;
    }


    public static float[] percentToTarget(float width, float height, float targetSize) {
        float[] percent = calc(width, height, targetSize);

        percent[0] /= width;
        percent[1] /= height;
        return percent;
    }

    public static float[] calc(float width, float height, float targetSize) {
        float[] results = new float[]{0, 0};
        float max = Math.max(width, height);
        float times = getTimes(targetSize, max);


        results[0] = width/times;
        results[1] = height/times;
        return results;
    }

    private static float getTimes(float targetSize, float max) {
        return Math.round((max / targetSize) * 10) / 10f;
    }

    private static RectF fixWh(RectF rect){
        RectF result = new RectF(rect);

        float w = rect.width();
        float h = rect.height();


        float more = Math.abs(w-h);

        if(w>h){
            result.bottom+=more/2;
            result.top-=more/2;
        }else{
            result.right+=more/2;
            result.left-=more/2;
        }

        return result;
    }
}
