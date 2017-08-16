package com.aos.dds.trackinggpscam.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aos.dds.trackinggpscam.Database.AppSharePref;
import com.aos.dds.trackinggpscam.R;
import com.aos.dds.trackinggpscam.Service.Tracking;

import mehdi.sakout.fancybuttons.FancyButton;

public class HomeActivity extends AppCompatActivity {

    FancyButton fancyButtonStart;
    FancyButton fancyButtonListAll;
    FancyButton fancy_button_setting;
    Intent intent;
    AppSharePref appSharePref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        intent=new Intent(this, Tracking.class);
        fancyButtonStart= (FancyButton) findViewById(R.id.fancyButtonStart);
        fancyButtonListAll= (FancyButton) findViewById(R.id.fancyButtonListAll);
        fancy_button_setting= (FancyButton) findViewById(R.id.fancy_button_setting);

        takePermission();
        appSharePref=new AppSharePref(getApplicationContext());
        fancyButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fancyButtonStart.getText().toString().equalsIgnoreCase("Start Event")){
                    fancyButtonStart.setText("End Event");
                    fancyButtonStart.setIconResource("\uf04d");
                    startEvent();
                }else{
                    fancyButtonStart.setText("Start Event");
                    fancyButtonStart.setIconResource("\uf04b");
                    stopEvent();
                }
                System.out.println(fancyButtonStart.getText().toString());
            }
        });

        fancy_button_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog=new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.take_input);

                FancyButton btn_done= (FancyButton) dialog.findViewById(R.id.btn_done);
                final EditText editText= (EditText) dialog.findViewById(R.id.editTextTime);
                editText.setText(appSharePref.getDelaySec()+"");
                btn_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        try {
                            appSharePref.setDelaySec(Integer.parseInt(editText.getText().toString()));
                            dialog.dismiss();
                        } catch (NumberFormatException e) {
                            Toast.makeText(getApplicationContext(),"Please input valid second..",Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });
        fancyButtonListAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,AllEventActivity.class);
                startActivity(intent);
            }
        });
    }


    public void startEvent(){
        startService(intent);
    }
    public void stopEvent(){
        stopService(intent);
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
