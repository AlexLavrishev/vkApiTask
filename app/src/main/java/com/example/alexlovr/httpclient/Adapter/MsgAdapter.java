package com.example.alexlovr.httpclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.example.alexlovr.httpclient.ItemClasses.MessageItem;
import com.example.alexlovr.httpclient.R;

/**
 * Created by shadow on 28/10/16.
 */
public class MsgAdapter extends BaseAdapter {
    List<MessageItem> list;
    private LayoutInflater layoutInflater;
    Context ctx;
    public MsgAdapter(Context context, List<MessageItem> list) {
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
        MessageItem item = getMsgItem(position);
        if (view == null){
            view = layoutInflater.inflate(R.layout.friend_msg_item, parent, false);
        }
        TextView  name =  (TextView) view.findViewById(R.id.name);
        TextView  time =  (TextView) view.findViewById(R.id.time);
        TextView  text =  (TextView) view.findViewById(R.id.textMsg);
        ImageView  ava = (ImageView) view.findViewById(R.id.ava);

        time.setText(item.getTime());
        text.setText(item.getText());
        ava.setImageBitmap(item.getAva());

        if (item.getOwner() == 0){
            name.setText(item.getName());
            ava.setImageBitmap(item.getAva());
        }else{
            name.setText("Вы");
            ava.setImageResource(R.drawable.my_ava);
        }
        return view;
    }

    private MessageItem getMsgItem(int position){
        return (MessageItem) getItem(position);
    }
}
