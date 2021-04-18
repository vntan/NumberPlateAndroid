package com.tiger.platedetection.Process;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.List;

public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {
    public final String TAG = CameraSurface.class.getSimpleName();

    Context context;
    int rotation;
    List<PlateInfo> plateInfoList;

    CameraSurface(Context context) {
        super(context);
    }

    public CameraSurface(Context context, int width, int height) {
        super(context);
        this.context = context;
        this.rotation = 0;
        setWillNotDraw(false);
    }

    public void reDraw(List<PlateInfo> plateInfoList, int rotation){
        this.rotation = rotation;
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

        if (plateInfoList == null || plateInfoList.size() <= 0) return;
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        Paint paintText = new Paint();
        paintText.setColor(Color.YELLOW);
        paintText.setStyle(Paint.Style.FILL);

        int size = 0;

        for (PlateInfo plate: plateInfoList) {
            canvas.drawRect(plate.getLeft(), plate.getTop(), plate.getRight(), plate.getBottom(), paint);
            if (plate.getNamePlate() != ""){
                do {
                    paintText.setTextSize(++ size);
                } while(paintText.measureText(plate.getNamePlate()) < ( plate.getRight() - plate.getLeft()));
                paintText.setTextSize(size - 1);

                switch (rotation){
                    case 0:
                        canvas.drawText(plate.getNamePlate(), plate.getLeft(), plate.getTop() - 20, paintText);                        break;
                    case 90:
                        canvas.drawText(plate.getNamePlate(), plate.getLeft(), plate.getTop() - 10 , paintText);
                        break;
                    case 180:
                        canvas.drawText(plate.getNamePlate(), plate.getLeft() , plate.getBottom() - 10, paintText);
                        break;
                    case 270:
                        canvas.drawText(plate.getNamePlate(), plate.getLeft(), plate.getBottom() - 10 , paintText);
                        break;
                }

            }

        }
    }
}
