package com.example.devicesilencingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.models.UserLocationModel;

import java.util.ArrayList;

public class AdapterLocation extends ArrayAdapter<UserLocationModel> {
    private Context context;
    private ArrayList<UserLocationModel>  listData;



    public AdapterLocation(Context context, ArrayList<UserLocationModel> data) {
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
        UserLocationModel mdl = (UserLocationModel) data;
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
