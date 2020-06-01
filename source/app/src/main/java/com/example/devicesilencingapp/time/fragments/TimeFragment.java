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
    private int selected_index;
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
        viewModal = new ViewModelProvider(this).get(TimeViewModal.class);
        ListView listView = (ListView) view.findViewById(R.id.lv_time);

        DBHelper helper = new DBHelper(getActivity());
//        boolean[] bl = new boolean[7];
//        for (int i = 0; i<7 ; i++){
//            bl[i]=true;
//        }
//        timeModel modestine = new timeModel(18 ,36,bl,true);
//        helper.insertTBTime(modestine);
//        TimeBroadcastReceiver.setAlarms(getActivity());
        data = helper.getAlarms();

        context = getActivity().getApplicationContext();
        adapter = new AdapterTime(context, data);
//        viewModal.settimeModelList(data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                timeModel timeModel = (timeModel) parent.getItemAtPosition(position);
                selected_index = position;
                oldSelected = timeModel;
                viewModal.setSelected(timeModel);//luu du lieu dc chon
                dialogclickitemtime();

            }
        });
//        timeModel time1 = helper.getTime(1);
//        int a =  time1.getTimeHour();
//        Toast.makeText(context,"abc" + a, Toast.LENGTH_SHORT).show();


    }

    @Override
    //quan sat du lieu
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModal.gettimeModelList().observe(getViewLifecycleOwner(), new Observer<ArrayList<timeModel>>() {
            @Override
            public void onChanged(ArrayList<timeModel> timeModel) {
                data = timeModel;
                adapter.clear();
                adapter.addAll(timeModel);
                adapter.notifyDataSetChanged();
            }
        });

        //quan sat cai dc chon
        viewModal.getSelected().observe(getViewLifecycleOwner(), new Observer<timeModel>() {
            @Override
            public void onChanged(timeModel timeModel) {
                if(Objects.deepEquals(timeModel, oldSelected)){
                    return;
                }
                int index = data.indexOf(oldSelected);
                if(timeModel == null){
                    data.remove(index);
                    adapter.remove(oldSelected);
                }
                else {
                    data.set(index, timeModel);
                }
                adapter.notifyDataSetChanged();
            }
        });

        //quan sat cai dc them

        viewModal.getNewItem().observe(getViewLifecycleOwner(), new Observer<timeModel>() {
            @Override
            public void onChanged(timeModel timeModel) {
                data.add(timeModel);
                adapter.notifyDataSetChanged();
            }
        });

    }


    private void dialogclickitemtime(){

        itemclick_bottomSheet clickitem = itemclick_bottomSheet.newInstance();
        clickitem.show(getChildFragmentManager(), clickitem.getTag());
    }
}
