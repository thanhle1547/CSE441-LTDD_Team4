package com.example.devicesilencingapp.location.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.devicesilencingapp.adapters.AdapterLocation;
import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.location.LocationListViewModel;
import com.example.devicesilencingapp.models.LocationModel;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class LocationListFragment extends Fragment {

	Context context;
	private LocationModel oldSelected;
	private AdapterLocation adapter;
	private ArrayList<LocationModel> data;
	private LocationListViewModel mViewModel;

	private ListView lv_Location;

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
		mViewModel.getLocationList().observe(getViewLifecycleOwner(), new Observer<ArrayList<LocationModel>>() {
			@Override
			public void onChanged(ArrayList<LocationModel> locationModelArrayList) {
				data = locationModelArrayList;
				adapter.clear();
				adapter.addAll(locationModelArrayList);
				adapter.notifyDataSetChanged();
			}
		});

		mViewModel.getSelectedItem().observe(getViewLifecycleOwner(), new Observer<LocationModel>() {
			@Override
			public void onChanged(LocationModel locationModel) {
				if (Objects.deepEquals(locationModel, oldSelected))
					return;

				int index = data.indexOf(oldSelected);

				if (locationModel == null) {
					// TH xóa dl
					data.remove(index);
					adapter.remove(oldSelected);
				} else {
					// TH sửa dl
					data.set(index, locationModel);
				}

				// Update the data
				adapter.notifyDataSetChanged();
			}
		});

		mViewModel.getNewItem().observe(getViewLifecycleOwner(), new Observer<LocationModel>() {
			@Override
			public void onChanged(LocationModel locationModel) {
				data.add(locationModel);
				adapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mViewModel = ViewModelProviders.of(this).get(LocationListViewModel.class);

		lv_Location = (ListView) view.findViewById(R.id.lv_location);
		data = new ArrayList<>();
		for(int i= 0; i <10; i++)
		{
			LocationModel mdlc = new LocationModel("trang thai " + i,"ten dia diem : " + i, "dia diem "+ i,"img");
			data.add(mdlc);
		}
		context = getActivity();
		adapter = new AdapterLocation(context, data);
		mViewModel.setLocationList(data);
		lv_Location.setAdapter(adapter);

		lv_Location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				LocationModel location = (LocationModel) data.get(i);
				oldSelected = location;
				mViewModel.setSelectedItem(location);

				Toast.makeText(context, "getDiadiem"+ location.getDiadiem() + "getImg"+ location.getImg(), Toast.LENGTH_SHORT).show();
				dialogclickitemlocation();
			}
		});
	}

	private void dialogclickitemlocation(){

		itemclick_BottomSheetDialogFragment clickitem = itemclick_BottomSheetDialogFragment.newInstance();
		clickitem.show(getChildFragmentManager(), clickitem.getTag());
	}

}
