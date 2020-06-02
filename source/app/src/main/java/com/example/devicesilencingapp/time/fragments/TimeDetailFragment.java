package com.example.devicesilencingapp.time.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.location.fragments.LocationDetailFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeDetailFragment extends Fragment {
    public static final int ACTION_ADD = 0;
    public static final int ACTION_EDIT = 1;
    private int action;
    private static final String Action = "action";

    public TimeDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimeDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimeDetailFragment newInstance(int action) {
        TimeDetailFragment fragment = new TimeDetailFragment();
        Bundle args = new Bundle();
        args.putInt(Action, action);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn_action = view.findViewById(R.id.btn_action);
        Button bt_start_time = view.findViewById(R.id.bt_start_time);
        Button bt_end_time = view.findViewById(R.id.bt_end_time);
        action = getArguments().getInt(Action);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_detail, container, false);
    }
}
