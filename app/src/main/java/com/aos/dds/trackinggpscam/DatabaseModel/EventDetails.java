package com.aos.dds.trackinggpscam.DatabaseModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Arif on 8/5/2017.
 */

public class EventDetails extends RealmObject {
    @PrimaryKey
    public long eventDetailsId;
    public long eventId;
    public double lat;
    public double lng;
    public String photoPath;

    public EventDetails() {
    }

    public EventDetails(long eventDetailsId, long eventId, double lat, double lng, String photoPath) {
        this.eventDetailsId = eventDetailsId;
        this.eventId = eventId;
        this.lat = lat;
        this.lng = lng;
        this.photoPath = photoPath;
    }

    @Override
    public String toString() {
        return "EventDetails{" +
                "eventDetailsId=" + eventDetailsId +
                ", eventId=" + eventId +
                ", lat=" + lat +
                ", lng=" + lng +
                ", photoPath='" + photoPath + '\'' +
                '}';
    }
}
