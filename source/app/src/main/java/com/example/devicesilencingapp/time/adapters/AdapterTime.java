package com.example.devicesilencingapp.time.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.time.modal.ScheduleModal;

import java.util.ArrayList;
//file quan li items
public class AdapterTime extends ArrayAdapter<ScheduleModal> {
    ArrayList<ScheduleModal> dataSet;
    Context mContext;

    public Context getContext() {
        return mContext;
    }
    private static  class ViewHolder{
        TextView tv_time;
        TextView tv_date;
        TextView tv_status;
    }

    public AdapterTime(@NonNull Context context,   ArrayList<ScheduleModal> dataSet ) {
        super(context, resource);
        this.mContext = context;
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ScheduleModal scheduleModal = getItem(position);
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
        viewHolder.tv_time.setText(scheduleModal.getStart_time() + " - " +scheduleModal.getEnd_time());
        String[] array = scheduleModal.getDates().split("");
        String dates = TextUtils.join(",",array);
        viewHolder.tv_date.setText((dates));
        viewHolder.tv_status.setText(getContext().getString( scheduleModal.getStatus()? R.string.is_on : R.string.is_off));
        return convertView;
    }
}
