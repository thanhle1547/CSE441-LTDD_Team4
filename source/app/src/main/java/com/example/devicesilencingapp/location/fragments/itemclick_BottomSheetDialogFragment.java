package com.example.devicesilencingapp.location.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.db.DBHelper;
import com.example.devicesilencingapp.location.LocationListViewModel;
import com.example.devicesilencingapp.location.model.UserLocationModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;


public class itemclick_BottomSheetDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private UserLocationModel model;
    private LocationListViewModel viewModel;

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
        /**
         * A ViewModelStore can be considered as a container that stores the ViewModels in a HashMap.
         * Where the key is string value and value is the ViewModel being saved
         * (ViewModelProvider uses a concatenation of the string_key + ViewModel class canonical name).
         *
         * A ViewModelStoreOwner is merely an interface. Any class that implements the getViewModelStore()
         * defined by this interface becomes the owner of ViewModelStore. This class then maintains the ViewModelStore and
         * should be responsible to appropriately restoring it when needed.
         *
         * We can implement our own version of the owner and the state based on the requirement.
         *
         * is ViewModelStoreOwner==activity/fragment?
         *
         * Yes. Based on the Android source code, both Fragment (from androidx.fragment.app)
         * & ComponentActivity (from androidx.activity) implements ViewModelStoreOwner.
         * These classes maintains a viewModelStore and value is restored appropriately.
         *
         * @see <a href="https://stackoverflow.com/questions/58892411/what-is-viewmodelstore-and-viewmodelstoreowner">
         *          What is ViewModelStore and viewModelStoreOwner?
         *      </a>
         */
        viewModel = new ViewModelProvider(requireActivity()).get(LocationListViewModel.class);
        model = viewModel.getSelectedItem().getValue();

        SwitchCompat sw_status = (SwitchCompat) view.findViewById(R.id.sw_status);
        sw_status.setChecked(model.getStatus());
        sw_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	        @Override
	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        model.setStatus(isChecked);

		        DBHelper.getInstance().editLocationStatus(model);
		        viewModel.setSelectedItem(model);
	        }
        });
        view.findViewById(R.id.btn_edit).setOnClickListener(this);
        view.findViewById(R.id.btn_delete).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit:
                dismiss();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(
                                R.id.fragment_detail,
                                LocationDetailFragment.newInstance(LocationDetailFragment.ACTION_EDIT))
                        .commit();
                break;
            case R.id.btn_delete:
                DBHelper.getInstance().removeLocation(model.getId());
                viewModel.setSelectedItem(null);
                dismiss();
                break;
        }
    }
}
