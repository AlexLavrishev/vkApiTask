package com.example.alexlovr.httpclient.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.example.alexlovr.httpclient.ItemClasses.DialogItem;
import com.example.alexlovr.httpclient.R;

import static android.content.ContentValues.TAG;

/**
 * Created by shadow on 26/10/16.
 */
public class DialogAdapter extends BaseAdapter {
    private List<DialogItem> list;
    private LayoutInflater layoutInflater;
    ImageView ava;
    public DialogAdapter(Context context, List<DialogItem> list) {
        this.list = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            view = layoutInflater.inflate(R.layout.dialog_item, parent, false);
        }

        DialogItem item = getDialogItem(position);


        TextView uName = (TextView) view.findViewById(R.id.uName);
        TextView lastMsg = (TextView) view.findViewById(R.id.lastMsg);
        TextView ownerMsg = (TextView) view.findViewById(R.id.ownerMsg);
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView uid = (TextView) view.findViewById(R.id.uid);

        uName.setText(item.getuName());
        lastMsg.setText(item.getLastMsg());
        if (item.getOwnerMsg() == 1){
            ownerMsg.setText("Вы: ");
        }
//        ownerMsg.setText(item.getOwnerMsg());
        time.setText(item.getTime());
        uid.setText(""+item.getUid());
//        Log.i(TAG, "UID: " + item.getUid());
//        Log.i(TAG, "Time: " + item.getOwnerMsg());

        ava = (ImageView) view.findViewById(R.id.ava);
//        Bitmap img =  BitmapFactory.decodeByteArray(item.getAva(), 0, item.getAva().length);
        ava.setImageBitmap(item.getAva());

//        Log.i(TAG, "getView: "+ position + " " + item.getAva());
        view.findViewById(R.id.uName);
        return view;
    }
    private DialogItem getDialogItem(int position){
        return (DialogItem) getItem(position);
    }

}
