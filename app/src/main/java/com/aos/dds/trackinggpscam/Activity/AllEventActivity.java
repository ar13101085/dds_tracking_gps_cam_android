package com.aos.dds.trackinggpscam.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aos.dds.trackinggpscam.DatabaseModel.Event;
import com.aos.dds.trackinggpscam.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class AllEventActivity extends AppCompatActivity {

    ArrayList<String> listData=new ArrayList<String>();
    ArrayList<Event> listAllEvent=new ArrayList<Event>();
    ArrayAdapter<String> adapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_all_event);
        listView= (ListView) findViewById(R.id.list_view);

        final LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.eventlist_header, listView, false);
        listView.addHeaderView(header);


        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listData);
        listView.setAdapter(adapter);

        Realm realm=Realm.getDefaultInstance();
        RealmResults<Event> events=realm.where(Event.class).findAll();
        for (Event event:events
                ) {
            String dateStringStart="";
            try {
                dateStringStart = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(event.EventStartTime));
            } catch (Exception e) {

            }
            String dateStringEnd="";
            try {
                dateStringEnd = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(event.EventEndTime));
            } catch (Exception e) {

            }
            listAllEvent.add(event);
            listData.add(dateStringStart+" -- "+dateStringEnd);
        }
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(AllEventActivity.this,MapsActivity.class);
                intent.putExtra("eventId",listAllEvent.get(position-1).EventId);
                startActivity(intent);
            }
        });

    }
}
