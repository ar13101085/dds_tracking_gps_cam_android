package com.aos.dds.trackinggpscam.Service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.aos.dds.trackinggpscam.Database.AppSharePref;
import com.aos.dds.trackinggpscam.DatabaseModel.Event;
import com.aos.dds.trackinggpscam.DatabaseModel.EventDetails;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmResults;

public class Tracking extends Service {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    Context context = this;

    Timer timer = new Timer();


    Event event;

    AppSharePref appSharePref;
    public Tracking() {
    }
    Realm realm;
    long currentTime=0;

     Camera mCamera;
     SurfaceTexture texture;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm realm=Realm.getDefaultInstance();

        appSharePref=new AppSharePref(getApplicationContext());



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Toast.makeText(context, "Location API Connected...", Toast.LENGTH_SHORT).show();
                        startLocationUpdates();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Toast.makeText(context, "Connection suspend...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(context, "Location Connection failed ...", Toast.LENGTH_SHORT).show();
                    }
                }).build();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                saveToDatabase();
            }
        }, 0, appSharePref.getDelaySec()*1000);
        currentTime=System.currentTimeMillis();
        event=new Event(currentTime,currentTime);

        realm.beginTransaction();
        event=realm.copyToRealm(event);
        realm.commitTransaction();

        System.out.println("Total Event "+realm.where(Event.class).count());
    }

    public void saveToDatabase(){
        System.out.println("start to save database");
        long currentTimeNow= System.currentTimeMillis();
        System.out.println("current time now "+currentTimeNow);
        boolean b=takePicture(currentTimeNow);
        if(b){
            final Location location=getCurrentLocation();
            System.out.println("Location start");
            if(location==null)
                return;
            System.out.println("Location end");

            System.out.println("Location get value ");

            realm=Realm.getDefaultInstance();
            System.out.println("status realm "+realm.isClosed());
            realm.beginTransaction();
            EventDetails eventDetails=new EventDetails(currentTimeNow,currentTime,location.getLatitude(),location.getLongitude(),getCurrentFileName(currentTimeNow));
            realm.copyToRealm(eventDetails);
            realm.commitTransaction();
            long c=realm.where(EventDetails.class).count();
            System.out.println("Count is : "+c);

            System.out.println("Finish to save in database "+realm.where(EventDetails.class).count());
            return;


        }
        System.out.println("Photo save not complete");
    }
    public Location getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        System.out.println("Location try to get..");
        Location location=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        return location;
    }
    public boolean takePicture(final long time){
        try {


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
            mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    File pictureFile = getOutputMediaFile(time);
                    if (pictureFile == null) {
                        return;
                    }
                    try {
                        FileOutputStream fos = new FileOutputStream(pictureFile);
                        fos.write(data);
                        fos.close();
                        System.out.println("successfully save photo "+pictureFile.getAbsolutePath());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        //mCamera.release();

                        mCamera.stopPreview();
                        mCamera.release();
                        mCamera = null;
                        texture.release();
                        texture=null;
                    }
                }
            });
            return true;
        } catch (Exception e) {
            System.out.println("photo take error "+e.toString());
            return false;
        }
    }
    private static File getOutputMediaFile(long time) {
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
        String timeStamp = time+"";
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + timeStamp+".jpg");

        return mediaFile;
    }

    public String getCurrentFileName(long currentTime){
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
        String s=mediaStorageDir.getPath() + File.separator
                + currentTime+".jpg";
        System.out.println(s);
        return s;
    }
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {


        mGoogleApiClient.connect();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                System.out.println("Location change..."+location.getLatitude()+"  "+location.getLongitude());
            }
        });
    }
    @Override
    public void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, new LocationCallback(){

        });
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        timer.cancel();

        Realm r=Realm.getDefaultInstance();
        Event e=r.where(Event.class).equalTo("EventId",currentTime).findFirst();
        r.beginTransaction();
        e.EventEndTime=System.currentTimeMillis();
        r.commitTransaction();
        //realm.close();
        super.onDestroy();
    }





    public void TestGPS(){
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }
}
