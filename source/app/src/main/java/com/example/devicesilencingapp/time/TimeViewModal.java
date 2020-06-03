package com.example.devicesilencingapp.time;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//luu du lieu chung cho cac fragment
public class TimeViewModal extends ViewModel {
    private timeModel oldAddItem;
//    private MutableLiveData<ArrayList<timeModel>> timeModelList;//danh sach lich trinh
    private MutableLiveData<timeModel> selected; //item dc chon
    private MutableLiveData<timeModel> newItem; //luu du lieu ms dc them

    public TimeViewModal() {
        selected = new MutableLiveData<>();
        newItem = new MutableLiveData<>();
    }
    public boolean isNewItem(timeModel newItem) {
        return newItem.compareTo(oldAddItem) == 0;
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
        this.oldAddItem = newItem;
    }
}
