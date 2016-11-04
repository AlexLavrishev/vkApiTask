package com.example.alexlovr.httpclient.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexlovr.httpclient.Adapter.DialogAdapter;
import com.example.alexlovr.httpclient.ItemClasses.DialogItem;
import com.example.alexlovr.httpclient.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by alexlovr on 01.11.16.
 */

public class Dialogs extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private String TAG = "DialogList";
    String url;
    ListView listView;
    String token = "";
    DialogAdapter dialogAdapter;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_list);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener((AdapterView.OnItemClickListener) this);
        token = getIntent().getExtras().getString("tok");
        url = "https://api.vk.com/method/messages.getDialogs?count=8&access_token="+token+"&v=5.59";
        new GetDialogs().execute(url);

    }

    private List<DialogItem> initListDialogs(JSONArray items){
        List<DialogItem> list = new ArrayList<DialogItem>();
        for (int i = 0; i < items.length(); i++) {
            try {
                JSONObject oneItem = (JSONObject) ((JSONObject) items.get(i)).get("message");
                final int uid = oneItem.optInt("user_id");
                int time = oneItem.optInt("date") ;
                String timeString = getTime(time);
                int ownerMsg = oneItem.optInt("out");
                String msg = oneItem.optString("body");
//                Log.i(TAG, "initListDialogs: " + uid);
                final DialogItem tmpItem = new DialogItem(uid, ""+uid, msg, ownerMsg, timeString);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new GetUserInfo(uid, tmpItem).execute("");
                    }
                });
                t.start();
                list.add(tmpItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

//  Парсинг ответа в JSON
    public JSONObject readStream(InputStream in) {
        BufferedReader streamReader = null;
        JSONObject res = null;
        try {
            streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        try {
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            res = new JSONObject(responseStrBuilder.toString());
//                Log.i(TAG, "readStream: " + res.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String getTime(long t){
        String timeString="";
        t = t * 1000L;
        long nowTimeMillis = System.currentTimeMillis() ;
        Calendar now = Calendar.getInstance();
        int h = now.get(Calendar.HOUR);
        int m = now.get(Calendar.MINUTE);
        int s = now.get(Calendar.SECOND);
        int ms = now.get(Calendar.MILLISECOND);
        long nowTime = TimeUnit.MILLISECONDS.convert(h, TimeUnit.HOURS) + TimeUnit.MILLISECONDS.convert(m, TimeUnit.MINUTES) + TimeUnit.MILLISECONDS.convert(s, TimeUnit.SECONDS) + TimeUnit.MILLISECONDS.convert(ms, TimeUnit.MILLISECONDS);
        long startDay = nowTimeMillis - nowTime;
        long r = startDay - t;
        if (r < 0){
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            timeString = formatter.format(new Date(t ));
        }else{
            if (r < 86400000){
                timeString = "Вчера";
            }else{
                SimpleDateFormat formatter = new SimpleDateFormat("d MMMM");
                timeString = formatter.format(new Date(t ));
            }
        }
        return timeString;
    }

    public boolean getDialogs(JSONObject r) {
        JSONArray items = null;
        Log.i(TAG, "onResponse: " + r.toString());
        try {
            JSONObject s = r.getJSONObject("response");
            items = s.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        final List<DialogItem> item = initListDialogs(items);
        dialogAdapter = new DialogAdapter(getBaseContext(), item);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(dialogAdapter);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        return true;
    }

    private class GetUserInfo extends AsyncTask<String,Void,Boolean> {
        int id;
        DialogItem tmpItem;
        String u;
        public GetUserInfo(int id, DialogItem tmpItem) {
            this.id = id;
            this.tmpItem = tmpItem;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(String... params) {
            u = "https://api.vk.com/method/users.get?user_ids="+id+"&fields=photo_100&access_token="+token+"&v=5.59 ";
            try
            {
                URL url = new URL(u);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject response = readStream(in);

                JSONArray s = response.getJSONArray("response");
                Log.i(TAG, "" + s.getJSONObject(0).optString("first_name") + " " + s.getJSONObject(0).optString("last_name"));
                tmpItem.setuName("" + s.getJSONObject(0).optString("first_name") + " " + s.getJSONObject(0).optString("last_name"));
                u = s.getJSONObject(0).optString("photo_100");
                Log.i(TAG, " 234 " + url);
                urlConnection.disconnect();
            }
            catch(Exception e){
                Log.i(TAG, "doInBackground: false " + e.toString());
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean == false){
                Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();

            }else{
                new PhotoLoader(tmpItem).execute(u);
            }
        }
    }

    private class GetDialogs extends AsyncTask<String,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(String... params) {
            try
            {
                String u = params[0];
                URL url = new URL(u);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject response = readStream(in);
                String method = u.substring(26, u.indexOf("?"));
                urlConnection.disconnect();
                return getDialogs(response);
            }
            catch(Exception e){
                Log.i(TAG, "doInBackground: false " + e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean == false){
                Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();
            }
        }



    }

    private class PhotoLoader extends AsyncTask<String, Void, Void> {

        DialogItem tmpItem;
        String urls;
        public PhotoLoader( DialogItem tmpItem ) {
            this.tmpItem = tmpItem;
        }
        @Override
        protected Void doInBackground(String... params) {
            try
            {
                String u = params[0];
                urls = u;
                URL url = new URL(u);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                Bitmap bmp = BitmapFactory.decodeStream(in);
                tmpItem.setAva(bmp);
                urlConnection.disconnect();
            }
            catch(Exception e){
                Log.i(TAG, "doInBackground: false " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.i(TAG, "326 " + tmpItem.getUid());
            dialogAdapter.notifyDataSetChanged();
        }

    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(300);
        view.startAnimation(animation1);
        String uid =(String) ((TextView) view.findViewById(R.id.uid)).getText();
        String name =(String) ((TextView) view.findViewById(R.id.uName)).getText();
        ImageView image = (ImageView) view.findViewById(R.id.ava);

        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();



        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Log.i(TAG, "onItemClick: " + byteArray);
        Log.i(TAG, "onItemClick: " + uid);
        Log.i(TAG, "onItemClick: " + name);

        Intent intent = new Intent(getApplicationContext(), Chat.class);
        intent.putExtra("uid", uid);
        intent.putExtra("name", name);
        intent.putExtra("ava", byteArray);
        intent.putExtra("tok", token);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

}

