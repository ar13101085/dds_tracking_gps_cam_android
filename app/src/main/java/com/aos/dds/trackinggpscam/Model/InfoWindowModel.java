package com.aos.dds.trackinggpscam.Model;

import com.aos.dds.trackinggpscam.DatabaseModel.EventDetails;
import com.aos.dds.trackinggpscam.Fragment.MarkerInfoWindow;
import com.appolica.interactiveinfowindow.InfoWindow;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Arif on 8/7/2017.
 */

public class InfoWindowModel {
    public InfoWindow infoWindow;
    public EventDetails eventDetails;
    public MarkerInfoWindow markerInfoWindow;

    public InfoWindowModel(InfoWindow infoWindow, EventDetails eventDetails, MarkerInfoWindow markerInfoWindow) {
        this.infoWindow = infoWindow;
        this.eventDetails = eventDetails;
        this.markerInfoWindow = markerInfoWindow;
    }
}
