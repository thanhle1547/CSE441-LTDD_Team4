package com.example.devicesilencingapp.location.fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.libs.LocationUtils;
import com.example.devicesilencingapp.location.LocationListViewModel;
import com.example.devicesilencingapp.models.LocationModel;
import com.example.devicesilencingapp.services.GPSTrackerService;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationDetailFragment extends Fragment implements View.OnClickListener {
    public static final int ACTION_ADD = 0;
    public static final int ACTION_EDIT = 1;

    private static final String ARG_ACTION = "action";

    private LocationListViewModel mViewModel;
    private LocationModel mLlocationModel;

    private GPSTrackerService mGpsTrackerService = null;
    // Theo dõi trạng thái ràng buộc của service.
    private boolean mBound = false;
    // The BroadcastReceiver used to listen from broadcasts from the service.
    private Receiver receiver;

    // Theo dõi trạng thái kêt nối của service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GPSTrackerService.LocalBinder binder = (GPSTrackerService.LocalBinder) service;
            mGpsTrackerService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mGpsTrackerService = null;
            mBound = false;
        }
    };

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            tv_address.setText(locationAddress);
        }
    }

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null)
                LocationUtils.getAddressFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    getActivity().getApplicationContext(),
                    new GeocoderHandler()
                );
            else
                tv_address.setText("Can't get location");
            tv_lnglat.setText(LocationUtils.toString(location.getLatitude(), location.getLongitude()));
        }
    }

    public LocationDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LocationDetailFragment.
     */
    public static LocationDetailFragment newInstance(int action) {
        LocationDetailFragment fragment = new LocationDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ACTION, action);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new Receiver();
        mViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(LocationListViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(receiver, new IntentFilter(GPSTrackerService.ACTION_BROADCAST));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO: anh xa

        if (getArguments().getInt(ARG_ACTION) == ACTION_ADD)
            mLlocationModel = new LocationModel();
        else {
            mLlocationModel = mViewModel.getSelectedItem().getValue();
        }

        // TODO: set OnclickListener event for buttons
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            mGpsTrackerService.removeLocationUpdates();
            getActivity().unbindService(mServiceConnection);
            mBound = false;
        }
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_location:
                if (mGpsTrackerService == null) {
                    // Bind to the service. If the service is in foreground mode, this signals to the service
                    // that since this activity is in the foreground, the service can exit foreground mode.
                    getActivity().bindService(
                            new Intent(getContext(), GPSTrackerService.class),
                            mServiceConnection,
                            Context.BIND_AUTO_CREATE
                    );
                    mGpsTrackerService.requestLocationUpdates();
                }
                break;
            case R.id.btn_action:
                break;
            case R.id.btn_cancel:
                break;
        }
    }

    public void removeThis() {
        // Hủy tất cả các fragment cũng đang được gắn cùng vào trong FrameLayout
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
