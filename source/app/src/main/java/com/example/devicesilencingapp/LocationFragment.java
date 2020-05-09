package com.example.devicesilencingapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LocationFragment extends Fragment {
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
        return view;
    }
}
