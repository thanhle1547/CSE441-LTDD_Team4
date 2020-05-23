package com.example.devicesilencingapp.time;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.devicesilencingapp.time.modal.ScheduleModal;

import java.util.ArrayList;

//luu du lieu chung cho cac fragment
public class TimeViewModal extends ViewModel {
    private MutableLiveData<ArrayList<ScheduleModal>> scheduleList;//danh sach lich trinh
    private MutableLiveData<ScheduleModal> selected; //item dc chon
    private MutableLiveData<ScheduleModal> newItem; //luu du lieu ms dc them

    public TimeViewModal() {
        scheduleList = new MutableLiveData<>();
        selected = new MutableLiveData<>();
        newItem = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<ScheduleModal>> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(ArrayList<ScheduleModal> scheduleList) {
        this.scheduleList.setValue(scheduleList);
    }

    public MutableLiveData<ScheduleModal> getSelected() {
        return selected;
    }

    public void setSelected(ScheduleModal selected) {
        this.selected.setValue(selected);
    }

    public MutableLiveData<ScheduleModal> getNewItem() {
        return newItem;
    }

    public void setNewItem(ScheduleModal newItem) {
        this.newItem.setValue(newItem);
    }
}
