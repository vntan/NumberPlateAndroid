package com.tiger.platedetection.Process;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.List;

public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {
    public final String TAG = CameraSurface.class.getSimpleName();

    Context context;
    List<PlateInfo> plateInfoList;

    CameraSurface(Context context) {
        super(context);
    }

    public CameraSurface(Context context, int width, int height) {
        super(context);
        this.context = context;
        setWillNotDraw(false);
    }

    public void reDraw(List<PlateInfo> plateInfoList){
        this.plateInfoList = plateInfoList;
        invalidate();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, String.format("onDraw: --------------------------------------"));

        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);

        for (PlateInfo plate: plateInfoList) {
            canvas.drawRect(plate.getLeft(), plate.getTop(), plate.getRight(), plate.getBottom(), paint);
        }
    }
}
