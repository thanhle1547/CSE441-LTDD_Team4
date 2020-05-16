package com.example.devicesilencingapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

        itemclick_BottomSheetDialogFragment clickitem = itemclick_BottomSheetDialogFragment.newInstance();
        clickitem.show(getFragmentManager(),"clickitemlocation");
    }

}
