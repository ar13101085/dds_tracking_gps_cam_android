package com.aos.dds.trackinggpscam.Interface;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Arif on 8/3/2017.
 */

public interface GpsInformation {
    public void UpdateGPS(LatLng latLng);
    public void NoGPS();
}
