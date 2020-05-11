package com.example.devicesilencingapp;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class itemclick_BottomSheetDialogFragment extends BottomSheetDialogFragment{

    public static itemclick_BottomSheetDialogFragment newInstance() {
        return new itemclick_BottomSheetDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.clickitemlocation,container,false);
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        view.findViewById(R.id.battatLocation).setOnClickListener(this);
//        view.findViewById(R.id.sualocation).setOnClickListener(this);
//        view.findViewById(R.id.xoalocation).setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View view) {
//
//    }
}
