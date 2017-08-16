package com.aos.dds.trackinggpscam.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.aos.dds.trackinggpscam.DatabaseModel.EventDetails;
import com.aos.dds.trackinggpscam.Fragment.MarkerInfoWindow;
import com.aos.dds.trackinggpscam.Model.InfoWindowModel;
import com.aos.dds.trackinggpscam.R;
import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.appolica.interactiveinfowindow.fragment.MapInfoWindowFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class MapsActivity extends FragmentActivity{

    private GoogleMap mMap;
    private InfoWindow recyclerWindow;
    private InfoWindow formWindow;
    private InfoWindowManager infoWindowManager;
    Map<Marker,InfoWindowModel> markerMap=new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        System.out.println("Hello Maping");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final MapInfoWindowFragment mapInfoWindowFragment =
                (MapInfoWindowFragment) getSupportFragmentManager().findFragmentById(R.id.infoWindowMap);

        infoWindowManager = mapInfoWindowFragment.infoWindowManager();

        Realm realm=Realm.getDefaultInstance();
        long eventId=getIntent().getLongExtra("eventId",0);
        RealmResults<EventDetails> events=realm.where(EventDetails.class).equalTo("eventId",eventId).findAll();
        System.out.println("count "+events.size());
        for (EventDetails eventDetails:events
                ) {
            System.out.println("event details : "+eventDetails);
        }
        mapInfoWindowFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap=googleMap;

                Realm realm=Realm.getDefaultInstance();
                long eventId=getIntent().getLongExtra("eventId",0);
                RealmResults<EventDetails> events=realm.where(EventDetails.class).equalTo("eventId",eventId).findAll();
                EventDetails ed=null;//last access
                for (EventDetails eventDetails:events
                        ) {

                    final Marker marker1 = googleMap.addMarker(new MarkerOptions().position(new LatLng(eventDetails.lat,eventDetails.lng)).snippet("AOS"));


                    final int offsetX = (int) getResources().getDimension(R.dimen.marker_offset_x);
                    final int offsetY = (int) getResources().getDimension(R.dimen.marker_offset_y);

                    final InfoWindow.MarkerSpecification markerSpec =
                            new InfoWindow.MarkerSpecification(offsetX, offsetY);

                    System.out.println("all details "+eventDetails);
                    MarkerInfoWindow markerInfoWindow=new MarkerInfoWindow();
                    InfoWindow infoWindow=new InfoWindow(marker1,markerSpec,markerInfoWindow);
                    markerMap.put(marker1,new InfoWindowModel(infoWindow,eventDetails,markerInfoWindow));

                    if(ed==null){
                        ed=eventDetails;
                        continue;
                    }

                    Polyline line = mMap.addPolyline(
                            new PolylineOptions().add(
                                    new LatLng(ed.lat, ed.lng),
                                    new LatLng(eventDetails.lat,eventDetails.lng)
                            ).width(10).color(Color.BLUE).geodesic(true)
                    );
                    ed=eventDetails;
                }

                try {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(events.get(events.size()-1).lat,events.get(events.size()-1).lng)));
                } catch (Exception e) {

                }
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(14.0f));
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        InfoWindow infoWindow = markerMap.get(marker).infoWindow;
                        //markerMap.get(marker).markerInfoWindow.setData(markerMap.get(marker).eventDetails.photoPath,MapsActivity.this);

                        if (infoWindow != null) {
                            infoWindowManager.toggle(infoWindow, true);
                        }else{
                            System.out.println("Info window is null....");
                        }
                        ((MarkerInfoWindow)infoWindow.getWindowFragment()).setData(markerMap.get(marker).eventDetails.photoPath);
                        return true;
                    }
                });

            }
        });



    }

}
