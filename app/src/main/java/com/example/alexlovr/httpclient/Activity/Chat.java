package com.example.alexlovr.httpclient.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.alexlovr.httpclient.Adapter.MsgAdapter;
import com.example.alexlovr.httpclient.ItemClasses.MessageItem;
import com.example.alexlovr.httpclient.R;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Chat extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{
    private static final String TAG = "Char Activity";
    private byte[] ava;
    private String uid;
    private String name;
    private Bitmap avaBitmap;
    private String token = "";

    int start_from;
    ListView listView;
    ImageButton attach;
    ImageButton send;
    ImageView photo;
    SwipeRefreshLayout mSwipeRefreshLayout;
    EditText textMsg;
    File f;
    MsgAdapter msgAdapter;
    List<MessageItem> item =null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        attach = (ImageButton) findViewById(R.id.attach);
        attach.setOnClickListener(this);

        send = (ImageButton) findViewById(R.id.send);
        send.setOnClickListener(this);

        textMsg = (EditText) findViewById(R.id.textMsg);

        photo = (ImageView) findViewById(R.id.photo);

        if (getIntent().getExtras().getString("uid") != null){
            uid = getIntent().getExtras().getString("uid");
            name = getIntent().getExtras().getString("name");
            ava = getIntent().getExtras().getByteArray("ava");
            token = getIntent().getExtras().getString("tok");
            avaBitmap = BitmapFactory.decodeByteArray(ava, 0, ava.length);
        }
        listView = (ListView) findViewById(R.id.listView);
        Log.i(TAG, "onCreate: " + uid);
        start_from = 0;
        getMsg(start_from);
//        listView.setSelection(listView.getCount() - 1);
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "onRefresh: ");
        start_from += 15;
        getMsg(start_from);
        listView.refreshDrawableState();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.attach:
                Log.i(TAG, "onClick: Attach Activity Start");
                new MaterialFilePicker()
                        .withActivity(Chat.this)
                        .withRequestCode(10)
                        .start();
                break;
            case R.id.send:
                String text = textMsg.getText().toString();
                int fileFlag;
                int strFlag;

                if (f != null){
                    sendFileMsg(text);
                }else{
                    if (text.length() != 0 ){
                        textMsg.setText("");
                        sendMsg(text);
                    }else{
                        Toast.makeText(this, "Невозможно отправить пустое сообщение",
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 10){
            f  = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
            Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            photo.setImageBitmap(myBitmap);
            Log.i(TAG, "onActivityResult: " + f.toString());
        }
    }

    private List<MessageItem> initListMessages(JSONArray items, List<MessageItem> l) {
        List<MessageItem> list = new ArrayList<>();
        for (int i = items.length() - 1; i >= 0; i--) {
            try {
                JSONObject oneItem = (JSONObject) ((JSONObject) items.get(i));
                int owner = oneItem.getInt("out");
                String text = oneItem.getString("body");
                String time = getTime(oneItem.getLong("date"));
                String msgOwner;
                if (owner == 0){
                    msgOwner = name;
                }else{
                    msgOwner = "You :";
                }
                Log.i(TAG, "111owner: " + owner);
                Log.i(TAG, "111msgOwner: " + msgOwner);
                Log.i(TAG, "111text: " + text);
                Log.i(TAG, "111time: " + time);
                MessageItem tmpItem = new MessageItem(avaBitmap, msgOwner,  text,  time, owner);
                list.add(tmpItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (l != null){
            list.addAll(l);
        }
        return list;
    }

    private void getMsg(int s){
        String u = "https://api.vk.com/method/messages.getHistory?count=15&peer_id="+uid+"&offset="+s+"&access_token="+token+"&v=5.59";
        new LoadMsg(s).execute(u);
        Log.i(TAG, "getMsg: " + u);
    }

    private void sendMsg(String text){
        String url = "https://api.vk.com/method/messages.send?peer_id="+uid+"&message="+text+"&access_token="+token+"&v=5.59";
        new SendMsg().execute(url);
    }

    private void sendFileMsg(String text){
//        String url = "https://api.vk.com/method/messages.send?peer_id="+uid+"&message="+text+"&access_token="+token+"&v=5.59";
        String url = "https://api.vk.com/method/photos.getMessagesUploadServer?access_token="+token+"&v=5.59";
        new GetUploadServer(text).execute(url);
    }

    private String getTime(long t){
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

    private String getMimeType(String path) {

        String extension = MimeTypeMap.getFileExtensionFromUrl(path);

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    private JSONObject readStream(InputStream in) {
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

    private class LoadMsg extends AsyncTask<String,Void,Boolean> {
        private int s;
        public LoadMsg(int s ) {
            this.s = s;
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
                JSONObject res = null;
                JSONArray items = null;
                try {
                    res = response.getJSONObject("response");
                    items = res.getJSONArray("items");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if  (s == 1){
                    JSONObject oneItem = (JSONObject) ((JSONObject) items.get(0));
                    int owner = oneItem.getInt("out");
                    String text = oneItem.getString("body");
                    String time = getTime(oneItem.getLong("date"));
                    String msgOwner;
                    if (owner == 0){
                        msgOwner = name;
                    }else{
                        msgOwner = "You :";
                    }
                    MessageItem tmpItem = new MessageItem(avaBitmap, msgOwner,  text,  time, owner);
                    item.add(item.size(), tmpItem);
                }else{
                    item = initListMessages(items, item);
                }
                urlConnection.disconnect();
            }
            catch(Exception e){
                Log.i(TAG, "doInBackground: false " + e.toString());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            msgAdapter = new MsgAdapter(getBaseContext(), item);
            mSwipeRefreshLayout.setRefreshing(false);
            listView.setAdapter(msgAdapter);
            if ( s == 0 || s == 1 ){
                listView.setSelection(listView.getCount() - 1);
            }
        }
    }

    private class SendMsg extends AsyncTask<String,Void,Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try
            {
                String u = params[0];
                URL url = new URL(u);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject response = readStream(in);
                Log.i(TAG, "doInBackground: " + response.toString());
                urlConnection.disconnect();
            }
            catch(Exception e){
                Log.i(TAG, "doInBackground: false " + e.toString());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {

            String u = "https://api.vk.com/method/messages.getHistory?count=1&peer_id="+uid+"&&access_token="+token+"&v=5.59";
            new LoadMsg(1).execute(u);
        }
    }

    private class GetUploadServer extends AsyncTask<String,Void,Boolean> {
        String upload_url;
        String text;

        public GetUploadServer(String text) {
            this.text = text;
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
                JSONObject res = response.getJSONObject("response");
                upload_url = res.optString("upload_url");
                Log.i(TAG, "doInBackground: " + upload_url);
                urlConnection.disconnect();
            }
            catch(Exception e){
                Log.i(TAG, "doInBackground: false " + e.toString());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    String content_type  = getMimeType(f.getPath());
                    Log.i(TAG, "run: " + content_type);
                    String file_path = f.getAbsolutePath();
                    Log.i(TAG, "run: " + file_path);
                    OkHttpClient client = new OkHttpClient();
                    RequestBody file_body = RequestBody.create(MediaType.parse(content_type),f);
                    RequestBody request_body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("type",content_type)
                            .addFormDataPart("photo",file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                            .build();
                    Request request = new Request.Builder()
                            .url(upload_url)
                            .post(request_body)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        String jsonData = response.body().string();
                        JSONObject r = new JSONObject(jsonData);
                        String server = r.optString("server", "");
                        String hash = r.optString("hash", "");
                        String photo = r.optString("photo", "");
                        Log.i(TAG, "server: " + server);
                        Log.i(TAG, "hash: " + hash);
                        Log.i(TAG, "photo: " + photo);
                        String saveUrl = "https://api.vk.com/method/photos.saveMessagesPhoto?server="+server+"&hash="+hash+"&photo="+ URLEncoder.encode(photo, "UTF-8")+"&access_token="+token+"&v=5.59";
                        Log.i(TAG, "run: " + saveUrl);
                        new SaveImage(text).execute(saveUrl);
                        if(!response.isSuccessful()){
                            throw new IOException("Error : "+response);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    }

    private class SaveImage extends AsyncTask<String,Void,Boolean> {
        String text;
        String id;

        public SaveImage(String text) {
            this.text = text;
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

                JSONArray res = response.getJSONArray("response");

                JSONObject oneItem = (JSONObject) ((JSONObject) res.get(0));
                Log.i(TAG, "doInBackground: " +oneItem.optString("id"));
                id = oneItem.optString("id");


                urlConnection.disconnect();
            }
            catch(Exception e){
                Log.i(TAG, "doInBackground: false " + e.toString());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            String attach = "photo24427632_"+ id; //  вставить свой идент в место 24427632
            String url = "https://api.vk.com/method/messages.send?peer_id="+uid+"&message="+text+"&attachment="+ attach +"&access_token="+token+"&v=5.59";
            new SendMsg().execute(url);
        }
    }

}
