package com.example.alexlovr.httpclient.ItemClasses;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shadow on 26/10/16.
 */
public class DialogItem {

    private int uid;
    private String uName;
    private String lastMsg;
    private String time;
    private int ownerMsg;
    private Bitmap ava;


    public DialogItem(int uid, String uName, String lastMsg, int ownerMsg, String time){
        this.uid = uid;
        this.uName = uName;
        this.lastMsg = lastMsg;
        this.ownerMsg = ownerMsg;
        this.time = time;
//        this.ava = ava;
    }

    public Bitmap getAva() {
        return ava;
    }

    public int getUid() {
        return uid;
    }

    public String getuName() {
        return uName;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public String getTime() {
        return time;
    }

    public int getOwnerMsg() {
        return ownerMsg;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setOwnerMsg(int ownerMsg) {
        this.ownerMsg = ownerMsg;
    }

    public void setAva(Bitmap ava) {
        this.ava = ava;
    }
}
