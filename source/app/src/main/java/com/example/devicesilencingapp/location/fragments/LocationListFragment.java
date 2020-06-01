package com.example.devicesilencingapp.location.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.devicesilencingapp.adapters.AdapterLocation;
import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.db.DBHelper;
import com.example.devicesilencingapp.location.LocationListViewModel;
import com.example.devicesilencingapp.models.UserLocationModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class LocationListFragment extends Fragment {
	private static final String TAG = LocationListFragment.class.getSimpleName();

	private UserLocationModel oldSelected;
	private AdapterLocation adapter;
	private LocationListViewModel mViewModel;

	public static LocationListFragment newInstance() {
		return new LocationListFragment();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_location_list, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerLiveDataListener();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		/**
		 * use requireActivity() instead of getActivity().
		 * This way you will ensure that the activity is attached an not getting a NullPointerException.
		 *
		 * @see <a href="https://stackoverflow.com/questions/60070233/cannot-resolve-viewmodelprovider-construction-in-a-fragment">
		 *          Cannot resolve ViewModelProvider construction in a fragment?
		 *      </a>
		 */
		mViewModel = new ViewModelProvider(requireActivity()).get(LocationListViewModel.class);

		ListView lv_Location = view.findViewById(R.id.lv_location);
		Context context = getActivity().getApplicationContext();
		adapter = new AdapterLocation(context, DBHelper.getInstance().getAllLocations());
		lv_Location.setAdapter(adapter);

		lv_Location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				UserLocationModel location = (UserLocationModel) adapterView.getItemAtPosition(i);
				oldSelected = location;
				mViewModel.setSelectedItem(location);

				dialogclickitemlocation();
			}
		});
	}

	private void registerLiveDataListener() {
		mViewModel.getSelectedItem().observe(getViewLifecycleOwner(), new Observer<UserLocationModel>() {
			@Override
			public void onChanged(UserLocationModel locationModel) {
//				Log.i(TAG, "mViewModel.getSelectedItem()");

//				if (oldSelected == null || oldSelected.compareTo(locationModel) == 0)
//					return;

				if (locationModel == null) {
					// TH xóa dl
					adapter.remove(oldSelected);
				}

				/* no need
				int index = data.indexOf(oldSelected);

				else {
					// TH sửa dl
					data.set(index, locationModel);
				}*/

				// Update the data
				adapter.notifyDataSetChanged();
			}
		});

		mViewModel.getNewItem().observe(getViewLifecycleOwner(), new Observer<UserLocationModel>() {
			@Override
			public void onChanged(UserLocationModel locationModel) {
				if (mViewModel.isNewItem(locationModel))
					return;
				adapter.add(locationModel);
				adapter.notifyDataSetChanged();
			}
		});
	}

	private void dialogclickitemlocation(){

		itemclick_BottomSheetDialogFragment clickitem = itemclick_BottomSheetDialogFragment.newInstance();
		clickitem.show(getChildFragmentManager(), clickitem.getTag());
	}

}
