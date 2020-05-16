package com.example.devicesilencingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

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
}
