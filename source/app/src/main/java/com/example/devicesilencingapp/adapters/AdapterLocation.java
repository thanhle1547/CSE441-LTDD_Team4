package com.example.devicesilencingapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.models.UserLocationModel;

import java.util.ArrayList;

public class AdapterLocation extends ArrayAdapter<UserLocationModel> {
    private final String TAG = AdapterLocation.class.getSimpleName();
    private Context context;
    private ArrayList<UserLocationModel>  listData;



    public AdapterLocation(Context context, ArrayList<UserLocationModel> data) {
        super(context, R.layout.list_item_location);
        this.context = context;
        this.listData = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        Log.i(TAG, "run getView()"); not called ???

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item_location, parent, false);
        UserLocationModel mdl = listData.get(position);
        TextView tv_trangthai = (TextView) convertView.findViewById(R.id.tv_status);
        TextView tv_tdd = (TextView) convertView.findViewById(R.id.tv_location_label);
        TextView tv_dd = (TextView) convertView.findViewById(R.id.tv_address);
        ImageView img = (ImageView) convertView.findViewById(R.id.iv_subject);
        tv_trangthai.setText(context.getString(mdl.getStatus() ? R.string.is_on : R.string.is_off));
        tv_tdd.setText(mdl.getName());
        tv_dd.setText(mdl.getAddress());
        img.setImageResource(mdl.getLabel());
        return convertView;
    }
}
