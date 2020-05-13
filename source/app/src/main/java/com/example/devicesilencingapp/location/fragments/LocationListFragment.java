package com.example.devicesilencingapp.location.fragments;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.location.LocationListViewModel;

public class LocationListFragment extends Fragment {

	private LocationListViewModel mViewModel;

	public static LocationListFragment newInstance() {
		return new LocationListFragment();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.location_list_fragment, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mViewModel = ViewModelProviders.of(this).get(LocationListViewModel.class);
		// TODO: Use the ViewModel
	}

}
