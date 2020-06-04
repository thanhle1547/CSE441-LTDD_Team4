package com.example.devicesilencingapp.time.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class EditTimeFragment extends Fragment implements View.OnClickListener {
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
    private timeModel mModel;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LocationDetailFragment.
     */
    public static EditTimeFragment newInstance(){
        return new EditTimeFragment();
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
        mModel = mViewModal.getSelected().getValue();

        bt_add = (Button) view.findViewById(R.id.btn_add);
        bt_add.setText("Sửa");
        bt_add.setOnClickListener(this);
        bt_cancel = (Button) view.findViewById(R.id.btn_cancel);
        bt_cancel.setOnClickListener(this);
        timePicker = (TimePicker) view.findViewById(R.id.alarm_details_time_picker);
        chkSunday = (SwitchCompat) view.findViewById(R.id.alarm_details_repeat_sunday);
        chkMonday = (SwitchCompat) view.findViewById(R.id.alarm_details_repeat_monday);
        chkTuesday = (SwitchCompat) view.findViewById(R.id.alarm_details_repeat_tuesday);
        chkWednesday = (SwitchCompat) view.findViewById(R.id.alarm_details_repeat_wednesday);
        chkThursday = (SwitchCompat) view.findViewById(R.id.alarm_details_repeat_thursday);
        chkFriday = (SwitchCompat) view.findViewById(R.id.alarm_details_repeat_friday);
        chkSaturday = (SwitchCompat) view.findViewById(R.id.alarm_details_repeat_saturday);

        timePicker.setCurrentHour(mModel.getTimeHour());
        timePicker.setCurrentMinute(mModel.getTimeMinute());
        chkSunday.setChecked(mModel.getRepeatingDay(timeModel.SUNDAY));
        chkMonday.setChecked(mModel.getRepeatingDay(timeModel.MONDAY));
        chkTuesday.setChecked(mModel.getRepeatingDay(timeModel.TUESDAY));
        chkWednesday.setChecked(mModel.getRepeatingDay(timeModel.WEDNESDAY));
        chkThursday.setChecked(mModel.getRepeatingDay(timeModel.THURSDAY));
        chkFriday.setChecked(mModel.getRepeatingDay(timeModel.FRDIAY));

        // TODO: hiển thị dữ liệu từ mModel
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
                timeModel model = new timeModel(mModel.getId(),gio, phutp, bl, true);
                DBHelper.getInstance().updateTime(model);
                mViewModal.setSelected(model);
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
}
