package com.example.alexlovr.httpclient.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.alexlovr.httpclient.R;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Start" ;
    Button signInBtn;
    Button dilogsBtn;
    String token = "";// вставить токен
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        signInBtn = (Button) findViewById(R.id.signInBtn);
        signInBtn.setOnClickListener(this);

        dilogsBtn = (Button) findViewById(R.id.dilogsBtn);
        dilogsBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;

        switch (id){
            case R.id.signInBtn:
                Uri uri = Uri.parse("https://oauth.vk.com/authorize?client_id=5682570&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=friends,messages,photos&response_type=token&v=5.59"); // missing 'http://' will cause crashed
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;

            case R.id.dilogsBtn:
                intent = new Intent(this, Dialogs.class);
                intent.putExtra("tok", token);
                startActivity(intent);
                break;
        }
    }
}
