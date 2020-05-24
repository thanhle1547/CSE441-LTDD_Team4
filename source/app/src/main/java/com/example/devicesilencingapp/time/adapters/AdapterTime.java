package com.example.devicesilencingapp.time.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.time.timeModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//file quan li items
public class AdapterTime extends ArrayAdapter<timeModel> {
    ArrayList<timeModel> dataSet;
    Context mContext;

    public Context getContext() {
        return mContext;
    }
    private static  class ViewHolder{
        TextView tv_time;
        TextView tv_date;
        TextView tv_status;
    }

    public AdapterTime(@NonNull Context context, ArrayList<timeModel> dataSet ) {
        super(context, R.layout.item_time);
        this.mContext = context;
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        timeModel timeModel = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());//
            convertView = inflater.inflate(R.layout.item_time, parent, false); //chuyen xml thanh java
            viewHolder.tv_time = (TextView)  convertView.findViewById(R.id.tv_time);
            viewHolder.tv_date = (TextView)  convertView.findViewById(R.id.tv_date);
            viewHolder.tv_status = (TextView)  convertView.findViewById(R.id.tv_status);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();

        }

        //gan noi dung
        viewHolder.tv_time.setText(timeModel.getTimeHour() + " - " +timeModel.getTimeMinute());


        StringBuilder repeatingDays = new StringBuilder();
        for (int i = 0; i<7; ++i) {
            repeatingDays.append(timeModel.getRepeatingDay(i)).append(",");
        }
//        boolean[] array = timeModel.getRepeatingDays();
//        String dates = TextUtils.join(",",array);
        viewHolder.tv_date.setText((repeatingDays));
        viewHolder.tv_status.setText(getContext().getString( timeModel.isEnabled()? R.string.is_on : R.string.is_off));
        return convertView;
    }
}
