package com.example.devicesilencingapp.time.fragments;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TimePicker;

import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.db.DBHelper;
import com.example.devicesilencingapp.time.TimeManager;
import com.example.devicesilencingapp.time.TimeViewModal;
import com.example.devicesilencingapp.time.timeModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class AddTimeFragment extends Fragment implements View.OnClickListener , SharedPreferences.OnSharedPreferenceChangeListener,
        AdapterView.OnItemSelectedListener {
    private Button bt_add,bt_cancel;
    private TimePicker timePicker;
//    private SwitchCompat chkWeekly;
    private SwitchCompat chkSunday;
    private SwitchCompat chkMonday;
    private SwitchCompat chkTuesday;
    private SwitchCompat chkWednesday;
    private SwitchCompat chkThursday;
    private SwitchCompat chkFriday;
    private SwitchCompat chkSaturday;

    private TimeViewModal mViewModal;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LocationDetailFragment.
     */
    public static AddTimeFragment newInstance(){
        return new AddTimeFragment();
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_time,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModal = new ViewModelProvider(requireActivity()).get(TimeViewModal.class);

        bt_add = (Button) view.findViewById(R.id.btn_add);
        bt_add.setOnClickListener(this);
        bt_cancel = (Button) view.findViewById(R.id.btn_cancel);
        bt_cancel.setOnClickListener(this);
        timePicker = (TimePicker) view.findViewById(R.id.alarm_details_time_picker);
//        chkWeekly = (SwitchCompat) view.findViewById(R.id.alarm_details_repeat_weekly);
        chkSunday = (SwitchCompat) view.findViewById(R.id.alarm_details_repeat_sunday);
        chkMonday = (SwitchCompat) view.findViewById(R.id.alarm_details_repeat_monday);
        chkTuesday = (SwitchCompat) view.findViewById(R.id.alarm_details_repeat_tuesday);
        chkWednesday = (SwitchCompat) view.findViewById(R.id.alarm_details_repeat_wednesday);
        chkThursday = (SwitchCompat) view.findViewById(R.id.alarm_details_repeat_thursday);
        chkFriday = (SwitchCompat) view.findViewById(R.id.alarm_details_repeat_friday);
        chkSaturday = (SwitchCompat) view.findViewById(R.id.alarm_details_repeat_saturday);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add :
            {
                boolean[] bl = new boolean[7];
                bl[0] = chkSunday.isChecked();
                bl[1] = chkMonday.isChecked();
                bl[2] = chkTuesday.isChecked();
                bl[3] = chkWednesday.isChecked();
                bl[4] = chkThursday.isChecked();
                bl[5] = chkFriday.isChecked();
                bl[6] = chkSaturday.isChecked();
                int gio = timePicker.getCurrentHour();
                int phutp = timePicker.getCurrentMinute();
                timeModel model = new timeModel(gio, phutp, bl, true);

                long id = DBHelper.getInstance().insertTBTime(model);
                model.setId(id);
                mViewModal.setNewItem(model);

                TimeManager.setAlarms(getActivity());
                removeThis();
                break;
            }
            case R.id.btn_cancel :
                removeThis();
                break;
        }

    }
    private void removeThis() {
        // Hủy tất cả các fragment cũng đang được gắn cùng vào trong FrameLayout
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
