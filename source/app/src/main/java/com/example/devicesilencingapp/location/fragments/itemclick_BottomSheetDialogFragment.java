package com.example.devicesilencingapp.location.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.db.DBHelper;
import com.example.devicesilencingapp.location.LocationListViewModel;
import com.example.devicesilencingapp.models.UserLocationModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

public class itemclick_BottomSheetDialogFragment extends BottomSheetDialogFragment
        implements View.OnClickListener {

    public itemclick_BottomSheetDialogFragment() {
        // Required empty public constructor
    }

    public static itemclick_BottomSheetDialogFragment newInstance() {
        return new itemclick_BottomSheetDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.clickitemlocation,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_action).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_action:
                UserLocationModel model = getSelected();
                SwitchCompat switchCompat = (SwitchCompat) v;

                model.setStatus(switchCompat.isChecked());

                DBHelper.getInstance().editLocationStatus(model);
                break;
            case R.id.btn_edit:
                dismiss();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(
                                R.id.fragment_detail,
                                LocationDetailFragment.newInstance(LocationDetailFragment.ACTION_EDIT))
                        .commit();
                break;
            case R.id.btn_delete:
                DBHelper.getInstance().removeLocation(getSelected().getId());
                dismiss();
                break;
        }
    }

    private UserLocationModel getSelected() {
        LocationListViewModel viewModel = new ViewModelProvider(this).get(LocationListViewModel.class);
        return viewModel.getSelectedItem().getValue();
    }
}
