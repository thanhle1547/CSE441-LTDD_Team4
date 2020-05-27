package com.example.devicesilencingapp.location;

import com.example.devicesilencingapp.models.UserLocationModel;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocationListViewModel extends ViewModel {
    private MutableLiveData<ArrayList<UserLocationModel>> locationList;
    private MutableLiveData<UserLocationModel> selected;

    private MutableLiveData<UserLocationModel> newItem;

    public LocationListViewModel() {
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

    public void setSelectedItem(@Nullable UserLocationModel contact) {
        selected.setValue(contact);
    }

    public MutableLiveData<UserLocationModel> getNewItem() {
        return newItem;
    }

    public void setNewItem(UserLocationModel newItem) {
        this.newItem.setValue(newItem);
    }
}
