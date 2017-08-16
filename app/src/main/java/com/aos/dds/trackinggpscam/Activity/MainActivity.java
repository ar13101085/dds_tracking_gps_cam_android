package com.aos.dds.trackinggpscam.Activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.aos.dds.trackinggpscam.R;
import com.aos.dds.trackinggpscam.Service.Tracking;
import com.bumptech.glide.Glide;

import junit.runner.Version;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageView image_view;
    Button button;
    private Camera mCamera;
    SurfaceTexture texture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image_view= (ImageView) findViewById(R.id.image_view);
        button = (Button) findViewById(R.id.buttonTakePhoto);
        //startService(new Intent(this, GpsTracking.class));
        takePermission();

       // SurfaceView surface = new SurfaceView(MainActivity.this);

        texture=new SurfaceTexture(10);
        mCamera = Camera.open();
        try {
            mCamera.setPreviewTexture(texture);
        } catch (IOException e1) {
            //Log.e(Version.APP_ID, e1.getMessage());
            e1.printStackTrace();
        }

        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewSize(640, 480);
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        params.setPictureFormat(ImageFormat.JPEG);
        mCamera.setParameters(params);
        mCamera.startPreview();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("ready to take ....");
                try {
                    mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            //Toast.makeText(MainActivity.this, "Picture taken", Toast.LENGTH_SHORT).show();
                            File pictureFile = getOutputMediaFile();
                            if (pictureFile == null) {
                                return;
                            }
                            try {
                                FileOutputStream fos = new FileOutputStream(pictureFile);
                                fos.write(data);
                                fos.close();

                                Glide.with(MainActivity.this)
                                        .load(pictureFile)
                                        .into(image_view);

                            } catch (FileNotFoundException e) {

                            } catch (IOException e) {
                            }finally {
                                mCamera.stopPreview();
                                texture.release();
                                texture=new SurfaceTexture(10);
                                try {
                                    mCamera.setPreviewTexture(texture);
                                } catch (IOException e1) {
                                    //Log.e(Version.APP_ID, e1.getMessage());
                                    e1.printStackTrace();
                                }
                                mCamera.startPreview();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /*camera = Camera.open();
        try {
            camera.setPreviewDisplay(surface.getHolder());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        camera.startPreview();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Image snapshot   Started",Toast.LENGTH_SHORT).show();
                // here below "this" is activity context.

                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        File pictureFile = getOutputMediaFile();
                        if (pictureFile == null) {
                            return;
                        }
                        try {
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(data);
                            fos.close();

                            Glide.with(MainActivity.this)
                                    .load(pictureFile)
                                    .into(image_view);

                        } catch (FileNotFoundException e) {

                        } catch (IOException e) {
                        }
                    }
                });

            }
        });*/



    }
    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "AOS");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
    public void takePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }
}
