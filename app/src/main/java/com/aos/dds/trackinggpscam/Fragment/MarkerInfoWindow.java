package com.aos.dds.trackinggpscam.Fragment;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aos.dds.trackinggpscam.R;
import com.bumptech.glide.Glide;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class MarkerInfoWindow extends Fragment {


    public MarkerInfoWindow() {
        // Required empty public constructor
    }
    ImageView imageView;
    Context context;
    public File fileImage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_marker_info_window, container, false);
        imageView= (ImageView) view.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullscreenDialog();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        this.context=context;
        super.onAttach(context);

    }

    public void setData(String fileLocation){
        File file=new File(fileLocation);
        System.out.println("file name is "+fileLocation);
        System.out.println("file location is : "+file.length());
        fileImage=new File(fileLocation);
        Glide.with(context)
                .load(fileImage)
                //.load("http://www.hindustantimes.com/rf/image_size_640x362/HT/p2/2016/05/20/Pictures/cafe_55e55b80-1e83-11e6-a451-36c3a3fdf989.JPG")
                .into(imageView);
    }
    public void fullscreenDialog(){
        final RelativeLayout root = new RelativeLayout(context);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setContentView(R.layout.fullscreen_image_view);
        ImageView fullImage= (ImageView) dialog.findViewById(R.id.imageView);
        Button clgImage= (Button) dialog.findViewById(R.id.buttonClose);
        Glide.with(context)
                .load(fileImage)
                //.load("http://www.hindustantimes.com/rf/image_size_640x362/HT/p2/2016/05/20/Pictures/cafe_55e55b80-1e83-11e6-a451-36c3a3fdf989.JPG")
                .into(fullImage);
        dialog.show();
        clgImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}
