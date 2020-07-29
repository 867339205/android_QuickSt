package com.quickst.tool;

import android.content.Context;
import android.content.SharedPreferences;

public class sharedPreferences {
    private Context mContext;

    public sharedPreferences() {
    }

    public sharedPreferences(Context mContext) {
        this.mContext = mContext;
    }

    public void savePass(String str){
        SharedPreferences sp = mContext.getSharedPreferences("admin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("pass", str);
        editor.commit();
    }

    public String getPass(){
        SharedPreferences sp = mContext.getSharedPreferences("admin", Context.MODE_PRIVATE);
        return sp.getString("pass", "QuickSt.2020.07.29");
    }
}
