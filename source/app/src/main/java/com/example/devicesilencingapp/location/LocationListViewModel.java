package com.example.devicesilencingapp.location;

import com.example.devicesilencingapp.models.LocationModel;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocationListViewModel extends ViewModel {
    private MutableLiveData<ArrayList<LocationModel>> locationList;
    private MutableLiveData<LocationModel> selected;

    private MutableLiveData<LocationModel> newItem;

    public LocationListViewModel() {
        locationList = new MutableLiveData<>();
        selected = new MutableLiveData<>();
        newItem = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<LocationModel>> getLocationList() {
        return locationList;
    }

    public void setLocationList(ArrayList<LocationModel> location_list) {
        locationList.setValue(location_list);
    }

    public MutableLiveData<LocationModel> getSelectedItem() {
        return selected;
    }

    public void setSelectedItem(@Nullable LocationModel contact) {
        selected.setValue(contact);
    }

    public MutableLiveData<LocationModel> getNewItem() {
        return newItem;
    }

    public void setNewItem(LocationModel newItem) {
        this.newItem.setValue(newItem);
    }
}
