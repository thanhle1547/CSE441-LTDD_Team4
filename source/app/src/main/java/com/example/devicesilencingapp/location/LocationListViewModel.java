package com.example.devicesilencingapp.location;

import android.util.Log;

import com.example.devicesilencingapp.models.UserLocationModel;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocationListViewModel extends ViewModel {
    private static final String TAG = LocationListViewModel.class.getSimpleName();

    private MutableLiveData<ArrayList<UserLocationModel>> locationList;
    private MutableLiveData<UserLocationModel> selected;
    private MutableLiveData<UserLocationModel> newItem;

    public LocationListViewModel() {
        Log.i(TAG, "constructor LocationListViewModel() called");

        locationList = new MutableLiveData<>();
        selected = new MutableLiveData<>();
        newItem = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<UserLocationModel>> getLocationList() {
        return locationList;
    }

    public void setLocationList(ArrayList<UserLocationModel> location_list) {
        locationList.setValue(location_list);
    }

    public MutableLiveData<UserLocationModel> getSelectedItem() {
        return selected;
    }

    public void setSelectedItem(@Nullable UserLocationModel locationModel) {
        selected.setValue(locationModel);
    }

    public MutableLiveData<UserLocationModel> getNewItem() {
        return newItem;
    }

    public void setNewItem(UserLocationModel newItem) {
        this.newItem.setValue(newItem);
    }
}
