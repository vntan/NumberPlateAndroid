package com.tiger.platedetection.Process;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.tiger.platedetection.ml.Model;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DetectionPlate {
    private String TAG = "DetectionPlate";

    private List<PlateInfo> plateList;
    private Context context;
    ImageView imgView;
    CameraSurface cameraSurface;

    public DetectionPlate(Context context, ImageView imgView) {
        this.context = context;
        this.imgView = imgView;
    }

    public DetectionPlate(Context context, CameraSurface cameraSurface) {
        this.context = context;
        this.cameraSurface = cameraSurface;
    }

    public List<PlateInfo> getPlateList() {
        return plateList;
    }

    public void setPlateList(List<PlateInfo> plateList) {
        this.plateList = plateList;
    }

    boolean checkNumber(float num){
        return num >=0 && num<=1;
    }


    List<PlateInfo> getAllPlate(Bitmap bitmap, int accurancy, boolean cameraUsing){
        List<PlateInfo> plateInfoList = new ArrayList<>();
        try {
            Model model = Model.newInstance(context);

            // Creates inputs for reference.
            TensorImage normalizedInputImageTensor = TensorImage.fromBitmap(bitmap);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(normalizedInputImageTensor);
            TensorBuffer locations = outputs.getLocationsAsTensorBuffer();
            TensorBuffer classes = outputs.getClassesAsTensorBuffer();
            TensorBuffer scores = outputs.getScoresAsTensorBuffer();
            TensorBuffer numberOfDetections = outputs.getNumberOfDetectionsAsTensorBuffer();

            float numberDetection = numberOfDetections.getFloatArray()[0];
            int index = 0, count = 0;
            float[] location = locations.getFloatArray();

            for (int i = 0; i < numberDetection; i++) {
                float score = scores.getFloatArray()[i];
                if (score * 100 >= accurancy) {

                    if (checkNumber(location[index]) && checkNumber(location[index+1]) &&checkNumber(location[index+2]) &&checkNumber(location[index+3]) )
                    {
                        count++;
                        Log.d("FINDING", "--------------------------------------------");
                        Log.d("FINDING", String.format("Width Image  %d", bitmap.getWidth()));
                        Log.d("FINDING", String.format("Height Image  %d", bitmap.getHeight()));
                        Log.d("FINDING", String.format("Score: %f", score * 100));
                        Log.d("FINDING", String.format("Location: %f %f %f %f", location[index], location[index + 1], location[index + 2], location[index + 3]));

                        int top = (int) (location[index] * bitmap.getHeight());
                        int left = (int) (location[index + 1] * bitmap.getWidth());
                        int right = (int) (location[index + 3] * bitmap.getWidth());
                        int bottom = (int) (location[index + 2] * bitmap.getHeight());

                        plateInfoList.add(new PlateInfo("",top, left, bottom, right ));



                    }

                }
                index += 4;
            }

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
            Log.e(TAG, "getAllPlate: " + e.toString());
            return null;
        }

        return plateInfoList;
    }

    synchronized public void detectPlate(Bitmap bitmap) {

        if (imgView == null || bitmap == null) return;

        plateList = getAllPlate(bitmap, 40, false);

        if (plateList.isEmpty()) {
            Toast.makeText(context, "Not found", Toast.LENGTH_LONG).show();
            return;
        }


        Bitmap mask = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        for (PlateInfo plate : plateList) {
            Log.d(TAG, "detectPlate: "+plateList.size()+plate.getLeft()+ plate.getTop()+ plate.getRight()+plate.getBottom());
            Canvas canvas = new Canvas(mask);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.YELLOW);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);



            int getWidth = plate.getLeft() + plate.getRight()-plate.getLeft() + 10 >bitmap.getWidth() ? plate.getRight()-plate.getLeft(): plate.getRight()-plate.getLeft()+10;
            int getHeight = plate.getTop() + plate.getBottom()-plate.getTop() + 10 >bitmap.getHeight() ? plate.getBottom()-plate.getTop(): plate.getBottom()-plate.getTop() + 10;


            Bitmap bitmapCopy = Bitmap.createBitmap(bitmap, plate.getLeft(), plate.getTop(), getWidth, getHeight);
            InputImage image = InputImage.fromBitmap(bitmapCopy, 0);

            TextRecognizer recognizer = TextRecognition.getClient();


            recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
                @Override
                public void onSuccess(Text text) {
                    String name = "";

                    if (text.getTextBlocks().size() > 0){
                        int index = 0;
                        Text.TextBlock block = text.getTextBlocks().get(index);

                        while (index < text.getTextBlocks().size() && block.getLines().size() < 2){
                            block = text.getTextBlocks().get(index);
                            index++;
                        }

                        if (block.getLines().size() > 1) {
                            name = block.getLines().get(0).getText() + " " +  block.getLines().get(1).getText();

                        }
                    }

                    if (name != "") {
                        canvas.drawRect(plate.getLeft(), plate.getTop(), plate.getRight(), plate.getBottom(), paint);
                        Log.d("FINDING", "detectPlate: "+name);
                        Paint paintText = new Paint();
                        paintText.setColor(Color.YELLOW);
                        paintText.setStyle(Paint.Style.FILL);

                        Paint paintBackground = new Paint();
                        paintBackground.setColor(Color.RED);
                        paintBackground.setStyle(Paint.Style.FILL);

                        int size = 0;
                        do {
                            paintText.setTextSize(++ size);
                        } while(paintText.measureText(name) < getWidth);

                        paintText.setTextSize(size - 1);


                        Rect rect = new Rect();
                        Paint.FontMetrics fm = paintText.getFontMetrics();
                        float height = fm.descent - fm.ascent;


                        rect.left =  plate.getLeft();
                        rect.top = plate.getTop() - 10 - (int)height;
                        rect.right = plate.getRight();
                        rect.bottom = plate.getTop();


                        if (plate.getTop() - 10 - height < 0)  rect.top = plate.getTop() + (int)height + 10;
                        canvas.drawRect(rect, paintBackground);

                        if (plate.getTop() - 10 - (int)height < 0) canvas.drawText(name, plate.getLeft(), plate.getTop() + (int)height + 10, paintText);
                        else
                            canvas.drawText(name, plate.getLeft(), plate.getTop() - 10 , paintText);
                        imgView.setImageBitmap(mask);

                    }
                }

            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    imgView.setImageBitmap(mask);
                }
            });
        }
        imgView.setImageBitmap(mask);
    }

    synchronized public void detectPlateRealTime(Bitmap bitmap, int orientation, ImageView imgView) {
/*        this.imgView = imgView;
        detectPlate(bitmap);
        plateList = new ArrayList<>();*/
        if (cameraSurface == null) return;
        plateList = getAllPlate(bitmap, 50, true);

        for (PlateInfo plate : plateList) {
            //.d("FINDING", "AHUHU: ");
            int getWidth = plate.getLeft() + plate.getRight()-plate.getLeft() + 10 >bitmap.getWidth() ? plate.getRight()-plate.getLeft(): plate.getRight()-plate.getLeft()+10;
            int getHeight = plate.getTop() + plate.getBottom()-plate.getTop() + 10 >bitmap.getHeight() ? plate.getBottom()-plate.getTop(): plate.getBottom()-plate.getTop() + 10;


            Bitmap bitmapCopy = Bitmap.createBitmap(bitmap, plate.getLeft(), plate.getTop(), getWidth, getHeight);
            InputImage image = InputImage.fromBitmap(bitmapCopy, 0);
            switch (orientation){
                case 0:
                    image = InputImage.fromBitmap(bitmapCopy, 270);
                    break;
                case 90:
                    image = InputImage.fromBitmap(bitmapCopy, 0);
                    break;
                case 180:
                    image = InputImage.fromBitmap(bitmapCopy, 90);
                    break;
                case 270:
                    image = InputImage.fromBitmap(bitmapCopy, 180);
                    break;
            }

            TextRecognizer recognizer = TextRecognition.getClient();

            recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
                @Override
                public void onSuccess(Text text) {
                    Log.d("TEXTFIND2", text.getText());
                    if (text.getTextBlocks().size() > 0){
                        int index = 0;
                        Text.TextBlock block = text.getTextBlocks().get(index);

                        while (index < text.getTextBlocks().size() && block.getLines().size() < 2){
                            block = text.getTextBlocks().get(index);
                            index++;
                        }

                        if (block.getLines().size() >= 2) {
                            plate.setNamePlate(block.getLines().get(0).getText() + " " +  block.getLines().get(1).getText());
                            plate.setShow(true);
                            cameraSurface.reDraw(plateList, orientation);
                        }
                    }else{
                        plate.setShow(false);
                        cameraSurface.reDraw(plateList, orientation);
                    }

                }
            });
        }

        if (plateList.isEmpty()) cameraSurface.reDraw(plateList, orientation);

    }


}





