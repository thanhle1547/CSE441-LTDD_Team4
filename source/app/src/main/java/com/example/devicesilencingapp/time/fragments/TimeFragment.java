package com.example.devicesilencingapp.time.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.devicesilencingapp.AdapterLocation;
import com.example.devicesilencingapp.AdapterTime;
import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.time.TimeViewModal;
import com.example.devicesilencingapp.time.modal.ScheduleModal;

import java.util.ArrayList;
import java.util.Objects;

//hien thi list view va xu li click vao item
public class TimeFragment extends Fragment {
    Context context;
    private ScheduleModal oldSelected; //ktra du lieu co thay doi k
    private AdapterTime adapter;// quan li item trong list
    private ArrayList<ScheduleModal> data; //du lieu tu csdl
    private TimeViewModal viewModal; //quan sat du lieu dc chon
    private ListView listView;

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
        viewModal = new ViewModelProvider(this).get(TimeViewModal.class);
        listView = (ListView) view.findViewById(R.id.lv_time);
        DBHelper helper = new DBHelper(getActivity());
        try{
            data = helper.getAllSchedule();
        }
        catch (NoSuchFieldException e){
            e.printStackTrace();
        }
        context = getActivity();
        adapter = new AdapterTime(context, data);
        viewModal.setScheduleList(data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScheduleModal schedule = (ScheduleModal) data.get(position);
                oldSelected = schedule;
                viewModal.setSelected(schedule);//luu du lieu dc chon

            }
        });

    }

    @Override
    //quan sat du lieu
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModal.getScheduleList().observe(getViewLifecycleOwner(), new Observer<ArrayList<ScheduleModal>>() {
            @Override
            public void onChanged(ArrayList<ScheduleModal> scheduleModals) {
                data = scheduleModals;
                adapter.clear();
                adapter.addAll(scheduleModals);
                adapter.notifyDataSetChanged();
            }
        });

        //quan sat cai dc chon
        viewModal.getSelected().observe(getViewLifecycleOwner(), new Observer<ScheduleModal>() {
            @Override
            public void onChanged(ScheduleModal scheduleModal) {
                if(Objects.deepEquals(scheduleModal, oldSelected)){
                    return;
                }
                int index = data.indexOf(oldSelected);
                if(scheduleModal == null){
                    data.remove(index);
                    adapter.remove(oldSelected);
                }
                else {
                    data.set(index, scheduleModal);
                }
                adapter.notifyDataSetChanged();
            }
        });

        //quan sat cai dc them

        viewModal.getNewItem().observe(getViewLifecycleOwner(), new Observer<ScheduleModal>() {
            @Override
            public void onChanged(ScheduleModal scheduleModal) {
                data.add(scheduleModal);
                adapter.notifyDataSetChanged();
            }
        });
    }



}
