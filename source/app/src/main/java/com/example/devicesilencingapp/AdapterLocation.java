package com.example.devicesilencingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import java.util.ArrayList;

public class AdapterLocation extends ArrayAdapter<Object> {
    private Context context;
    private ArrayList<?>  listData;



    public AdapterLocation(Context context, ArrayList<Object> data) {
        super(context, R.layout.list_item_location, data);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.listData = data;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Object data = listData.get(position);
        row = inflater.inflate(R.layout.list_item_location, parent, false);
        MDLocation mdl = (MDLocation) data;
        TextView tv_trangthai = (TextView) row.findViewById(R.id.tv_trangthai);
        TextView tv_tdd = (TextView) row.findViewById(R.id.tv_location_label);
        TextView tv_dd = (TextView) row.findViewById(R.id.tv_diadiem);
        ImageView img = (ImageView) row.findViewById(R.id.ic_subject);
        tv_trangthai.setText(mdl.getTrangthai());
        tv_tdd.setText(mdl.getTendiadiem());
        tv_dd.setText(mdl.getDiadiem());
        return row;
    }
}
