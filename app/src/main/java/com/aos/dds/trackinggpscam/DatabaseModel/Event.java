package com.aos.dds.trackinggpscam.DatabaseModel;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by Arif on 8/5/2017.
 */

public class Event extends RealmObject {
    public long EventId;
    public long EventStartTime;
    public long EventEndTime;

    public Event() {
    }

    public Event(long eventId, long eventStartTime) {
        EventId = eventId;
        EventStartTime = eventStartTime;
    }

}
