package com.example.devicesilencingapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LocationFragment extends Fragment{
    Context context;
    private ListView lv_Location;
    private AdapterLocation adapter;
    private ArrayList<Object> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_location,container,false);
        lv_Location = (ListView) view.findViewById(R.id.lv_location);
        list = new ArrayList<Object>();
        for(int i= 0; i <10; i++)
        {
            MDLocation mdlc = new MDLocation("trang thai " + i,"ten dia diem : " + i, "dia diem "+ i,"img");
            list.add(mdlc);
        }
        context = getActivity();
        adapter = new AdapterLocation(context, list);
        lv_Location.setAdapter(adapter);




        lv_Location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MDLocation location =(MDLocation)list.get(i);
                Toast.makeText(context, "getDiadiem"+ location.getDiadiem() + "getImg"+ location.getImg(), Toast.LENGTH_SHORT).show();
                dialogclickitemlocation(location.getDiadiem());

                //                Intent intent = new Intent(context, class.class);
//                intent.putExtra("getDiadiem", location.getDiadiem());
//                intent.putExtra("getImg", location.getImg());
//                intent.putExtra("getTendiadiem", location.getTendiadiem());
//                intent.putExtra("getTrangthai", location.getTrangthai());
//                startActivity(intent);
            }
        });
        return view;
    }
    private void dialogclickitemlocation(String a){
        Dialog dialogclickitem= new Dialog(getActivity());
        dialogclickitem.setContentView(R.layout.clickitemlocation);
        final ImageView battatLocation = (ImageView)dialogclickitem.findViewById(R.id.battatLocation);
        final LinearLayout sualocation = (LinearLayout)dialogclickitem.findViewById(R.id.sualocation);
        final LinearLayout xoalocation = (LinearLayout)dialogclickitem.findViewById(R.id.xoalocation);
        battatLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "bat tat", Toast.LENGTH_SHORT).show();
            }
        });
        sualocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "sua", Toast.LENGTH_SHORT).show();
            }
        });
        xoalocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "xoa", Toast.LENGTH_SHORT).show();
            }
        });
        Window window = dialogclickitem.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        dialogclickitem.show();
    }

}
