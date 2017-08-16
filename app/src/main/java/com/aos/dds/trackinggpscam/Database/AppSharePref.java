package com.aos.dds.trackinggpscam.Database;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Arif on 8/5/2017.
 */

public class AppSharePref {
    SharedPreferences sharedPreferences;
    Context context;
    public AppSharePref(Context context){
        this.context=context;
        sharedPreferences=context.getSharedPreferences("data",Context.MODE_PRIVATE);

    }
    public void setDelaySec(int delaySec){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("sec",delaySec);
        editor.commit();
    }
    public int getDelaySec(){
        return sharedPreferences.getInt("sec",5);
    }
}
