package com.tiger.platedetection.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.overlay.OverlayLayout;
import com.otaliastudios.cameraview.size.Size;
import com.tiger.platedetection.Process.CameraSurface;
import com.tiger.platedetection.Process.DetectionPlate;
import com.tiger.platedetection.R;
import com.tiger.platedetection.ml.Model;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CameraDetectionFragment extends Fragment {

    CameraView cameraView;
    FrameLayout frameLayout;
    ImageView imgView;


    public CameraDetectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera_detection, container, false);

        AnhXa(view);

        CameraSurface cameraSurface = new CameraSurface(getContext(), cameraView.getWidth(), cameraView.getHeight());

        frameLayout.addView(cameraSurface);


        cameraView.setLifecycleOwner(getViewLifecycleOwner());
        cameraView.setPreviewFrameRate(10F);

        cameraView.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull Frame frame) {
                long time = frame.getTime();
                Size size = frame.getSize();
                int format = frame.getFormat();
                int userRotation = frame.getRotationToUser();
                int viewRotation = frame.getRotationToView();
                Log.d("GET", "USER: "+userRotation);
                Log.d("GET", "VIEW: "+viewRotation);
                if (frame.getDataClass() == byte[].class) {
                    byte[] data = frame.getData();
                    // Process byte array...
                    YuvImage yuvImage = new YuvImage(data,
                            ImageFormat.NV21, size.getWidth(), size.getHeight(), null);

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    yuvImage.compressToJpeg(new Rect(0, 0, size.getWidth(), size.getHeight()), 50, out);
                    byte[] imageBytes = out.toByteArray();
                    Bitmap bm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    Bitmap bitmap = bm;

                    int orientation = userRotation ;
                    // On android the camera rotation and the screen rotation
                    // are off by 90 degrees, so if you are capturing an image
                    // in "portrait" orientation, you'll need to rotate the image.
                    if (bitmap.getWidth() > bitmap.getHeight() ) {
                        orientation = userRotation;
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm,
                                bm.getWidth(), bm.getHeight(), true);
                        bitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                                scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                    }

                    if (bitmap != null){
                        DetectionPlate detectionPlate = new DetectionPlate(getContext(), cameraSurface);
                        detectionPlate.detectPlateRealTime(bitmap, orientation, imgView);
                    }
                } else if (frame.getDataClass() == Image.class) {
                    Image data = frame.getData();
                    // Process android.media.Image...
                }
            }
        });

        return view;
    }

    private void AnhXa(View view) {
        cameraView = view.findViewById(R.id.cameraView);
        frameLayout = view.findViewById(R.id.frameCamera);
        imgView = view.findViewById(R.id.imgView);
    }
}