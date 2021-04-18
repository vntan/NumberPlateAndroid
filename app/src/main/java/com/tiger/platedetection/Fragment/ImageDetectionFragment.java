package com.tiger.platedetection.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.tiger.platedetection.Process.DetectionPlate;
import com.tiger.platedetection.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class ImageDetectionFragment extends Fragment{
    String TAG = "ImageDetectionFragment";

    private static final int PICK_IMAGE = 100;

    Button btnDetectImage, btnLoadImage;
    PhotoView imgView, imgIcon;
    Bitmap bitmap;

    Uri imageUri;

    public ImageDetectionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_image_detection, container, false);
        AnhXa(view);

        imgIcon.setImageResource(R.drawable.ic_add_photo);


        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });
        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });

        btnDetectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgView.invalidate();
                BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable();
                bitmap = drawable.getBitmap();

                if (bitmap == null)
                    Toast.makeText(getContext(), "Please input the image to process !", Toast.LENGTH_LONG).show();
                else{
                    DetectionPlate detectionPlate = new DetectionPlate(getContext(), imgView);
                    detectionPlate.detectPlate(bitmap);
                }

                btnDetectImage.setEnabled(false);
            }
        });
        return view;
    }

    void AnhXa(View view){
        btnDetectImage = view.findViewById(R.id.btnDetectImage);
        btnLoadImage = view.findViewById(R.id.btnLoadImage);
        imgView = view.findViewById(R.id.imgView);
        imgIcon = view.findViewById(R.id.imgIcon);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
                btnDetectImage.setEnabled(true);

                imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imgView.setImageURI(imageUri);
                imgIcon.setVisibility(View.INVISIBLE);
            }

    }

}