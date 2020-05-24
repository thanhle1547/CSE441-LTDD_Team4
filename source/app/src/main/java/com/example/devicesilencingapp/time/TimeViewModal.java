package com.example.devicesilencingapp.time;

import java.util.ArrayList;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//luu du lieu chung cho cac fragment
public class TimeViewModal extends ViewModel {
    private MutableLiveData<ArrayList<timeModel>> timeModelList;//danh sach lich trinh
    private MutableLiveData<timeModel> selected; //item dc chon
    private MutableLiveData<timeModel> newItem; //luu du lieu ms dc them

    public TimeViewModal() {
        timeModelList = new MutableLiveData<>();
        selected = new MutableLiveData<>();
        newItem = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<timeModel>> gettimeModelList() {
        return timeModelList;
    }

    public void settimeModelList(ArrayList<timeModel> timeModelList) {
        this.timeModelList.setValue(timeModelList);
    }

    public MutableLiveData<timeModel> getSelected() {
        return selected;
    }

    public void setSelected(timeModel selected) {
        this.selected.setValue(selected);
    }

    public MutableLiveData<timeModel> getNewItem() {
        return newItem;
    }

    public void setNewItem(timeModel newItem) {
        this.newItem.setValue(newItem);
    }
}
