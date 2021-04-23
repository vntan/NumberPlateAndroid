package com.tiger.platedetection.Process;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
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

        Paint paintBackground = new Paint();
        paintBackground.setColor(Color.RED);
        paintBackground.setStyle(Paint.Style.FILL);

        int size = 0;

        for (PlateInfo plate: plateInfoList) {
            if (!plate.isShow()) continue;
            canvas.drawRect(plate.getLeft(), plate.getTop(), plate.getRight(), plate.getBottom(), paint);
            if (plate.getNamePlate() != ""){
                do {
                    paintText.setTextSize(++ size);
                } while(paintText.measureText(plate.getNamePlate()) < ( plate.getRight() - plate.getLeft()));
                paintText.setTextSize(size - 1);

                Rect rect = new Rect();
                Paint.FontMetrics fm = paintText.getFontMetrics();
                float height = fm.descent - fm.ascent;


                rect.left =  plate.getLeft();
                rect.top = plate.getTop() - (int)height;
                rect.right = plate.getRight();
                rect.bottom = plate.getTop();


                switch (rotation){
                    case 0:
                        rect.top = plate.getTop() - 20 - (int)height;
                        canvas.drawRect(rect, paintBackground);
                        canvas.drawText(plate.getNamePlate(), plate.getLeft(), plate.getTop() - 20, paintText);
                        //canvas.drawRect(rect, paintBackground);
                        break;
                    case 90:
                        rect.top = plate.getTop() - 10 - (int)height;
                        canvas.drawRect(rect, paintBackground);
                        canvas.drawText(plate.getNamePlate(), plate.getLeft(), plate.getTop() - 10 , paintText);
                        break;
                    case 180:
                        rect.bottom = plate.getBottom() - 10;
                        canvas.drawRect(rect, paintBackground);
                        canvas.drawText(plate.getNamePlate(), plate.getLeft() , plate.getBottom() - 10, paintText);
                        break;
                    case 270:
                        //rect.top = plate.getBottom() - 10;
                        rect.bottom = plate.getTop() + (int)height;
                        canvas.drawRect(rect, paintBackground);
                        canvas.drawText(plate.getNamePlate(), plate.getLeft(), plate.getBottom() - 10 , paintText);
                        break;
                }

            }

        }
    }
}
