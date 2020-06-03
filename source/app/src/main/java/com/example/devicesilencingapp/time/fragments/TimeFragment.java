package com.example.devicesilencingapp.time.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.db.DBHelper;
import com.example.devicesilencingapp.time.TimeViewModal;
import com.example.devicesilencingapp.time.adapters.AdapterTime;
import com.example.devicesilencingapp.time.timeModel;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

//hien thi list view va xu li click vao item
public class TimeFragment extends Fragment {
    Context context;
    private timeModel oldSelected; //ktra du lieu co thay doi k
    private AdapterTime adapter;// quan li item trong list
    private ArrayList<timeModel> data;
    private TimeViewModal viewModal; //quan sat du lieu dc chon//du lieu tu csdl

    //tra ve 1 the hien moi cua lop
    public static TimeFragment newInstance(){
        return new TimeFragment();
    }


    @Nullable
    @Override
    //tra ve 1 view dc chuyen tu xml sang java
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time,container,false);
    }

    @Override
    //ddc goi sau oncreatview, nhan vao view chuyen tu xml sang java
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModal = new ViewModelProvider(requireActivity()).get(TimeViewModal.class);
        ListView listView = (ListView) view.findViewById(R.id.lv_time);

        data = DBHelper.getInstance().getAlarms();

        context = getActivity().getApplicationContext();
        adapter = new AdapterTime(context, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                timeModel timeModel = (timeModel) parent.getItemAtPosition(position);
                oldSelected = timeModel;
                viewModal.setSelected(timeModel);//luu du lieu dc chon
                dialogclickitemtime();

            }
        });


    }

    @Override
    //quan sat du lieu
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //quan sat cai dc chon
        viewModal.getSelected().observe(getViewLifecycleOwner(), new Observer<timeModel>() {
            @Override
            public void onChanged(timeModel timeModel) {
                if(timeModel == null){
                    adapter.remove(oldSelected);
                }
                adapter.notifyDataSetChanged();
            }
        });

        //quan sat cai dc them

        viewModal.getNewItem().observe(getViewLifecycleOwner(), new Observer<timeModel>() {
            @Override
            public void onChanged(timeModel timeModel) {
                if (!viewModal.isNewItem(timeModel))
                    return;
                adapter.add(timeModel);
                adapter.notifyDataSetChanged();
            }
        });

    }


    private void dialogclickitemtime(){

        itemclick_bottomSheet clickitem = itemclick_bottomSheet.newInstance();
        clickitem.show(getChildFragmentManager(), clickitem.getTag());
    }
}
