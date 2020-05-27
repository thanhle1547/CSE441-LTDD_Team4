package com.example.devicesilencingapp.location.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.libs.LocationUtils;
import com.example.devicesilencingapp.libs.SharedPrefs;
import com.example.devicesilencingapp.location.LocationListViewModel;
import com.example.devicesilencingapp.models.UserLocationModel;
import com.example.devicesilencingapp.location.service.GPSTrackerService;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationDetailFragment extends Fragment
		implements View.OnClickListener,
					SharedPreferences.OnSharedPreferenceChangeListener {
    public static final int ACTION_ADD = 0;
    public static final int ACTION_EDIT = 1;

    private static final String ARG_ACTION = "action";
	private static final int REQUEST_CODE_PERMISSION = 2;
	private final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;

    private LocationListViewModel mViewModel;
    private UserLocationModel mLlocation;

	private SharedPreferences sharedPreferences;
	private GPSTrackerService mGpsTrackerService = null;
    // Theo dõi trạng thái ràng buộc của service.
    private boolean mBound = false;
    // The BroadcastReceiver used to listen from broadcasts from the service.
    private Receiver receiver;
    private int action;

    private Button btn_location_action;
    private TextView tv_addresss;

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

    // TODO: Resolve problem: Inner class may be 'static'
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
	        if (message.what == 1) {
		        Bundle bundle = message.getData();
		        locationAddress = bundle.getString("address");
	        } else {
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

        // Kiểm tra xem người dùng đã thu hồi lại quyền chưa
	    if (isLocationUpdating() && !checkPermissions())
		    requestPermissions();
    }

	@Override
	public void onStart() {
		super.onStart();

		sharedPreferences = SharedPrefs.getInstance().getSharedPrefs();
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(receiver, new IntentFilter(GPSTrackerService.ACTION_BROADCAST));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
        super.onPause();
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

	    action = getArguments().getInt(ARG_ACTION);

        if (action == ACTION_ADD) {
	        mLlocation = new UserLocationModel();
	        mGpsTrackerService.requestLocationUpdates();
        }
        else {
            mLlocation = mViewModel.getSelectedItem().getValue();
            // TODO: set values for views
        }

        // TODO: set OnclickListener event for buttons
	    btn_location_action = (Button) view.findViewById(R.id.btn_location_action);

	    btn_location_action.setOnClickListener(this);
	    ((Button) view.findViewById(R.id.btn_action)).setOnClickListener(this);
	    ((Button) view.findViewById(R.id.btn_cancel)).setOnClickListener(this);

	    setButtonsState(isLocationUpdating());
    }

    @Override
    public void onStop() {
        if (mBound) {
            mBound = false;
            mGpsTrackerService.removeLocationUpdates();
        }
	    sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
	    super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_location_action:
                if (mGpsTrackerService == null) {
                    // Bind to the service. If the service is in foreground mode, this signals to the service
                    // that since this activity is in the foreground, the service can exit foreground mode.
                    getActivity().bindService(
                            new Intent(getContext(), GPSTrackerService.class),
                            mServiceConnection,
                            Context.BIND_AUTO_CREATE
                    );
                }
                if (isLocationUpdating()) {
	                mGpsTrackerService.requestLocationUpdates();
                } else {
                	mGpsTrackerService.removeLocationUpdates();
                }
                break;
            case R.id.btn_action:
            	// TODO: insert/update data to db
	            DBHelper helper = new DBHelper(getActivity().getApplicationContext());

	            mLlocation.setDiadiem(tv_addresss.getText().toString());

	            if (action == ACTION_ADD) {
	            	long id = helper.addLocation(mLlocation);
	            	mLlocation.setId(id);
	            	mViewModel.setNewItem(mLlocation);
	            } else {
	            	helper.editLocation(mLlocation);
	            	mViewModel.setSelectedItem(mLlocation);
	            }

	            removeThis();
                break;
            case R.id.btn_cancel:
            	removeThis();
                break;
        }
    }

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQUEST_CODE_PERMISSION)
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				Toast.makeText(
						getContext(),
						"Please grant location permission",
						Toast.LENGTH_SHORT)
						.show();
			else {
				setButtonsState(false);
				mGpsTrackerService.removeLocationUpdates();
			}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(GPSTrackerService.KEY_REQUESTING_LOCATION_UPDATES))
			setButtonsState(SharedPrefs.getInstance().get(GPSTrackerService.KEY_REQUESTING_LOCATION_UPDATES, Boolean.class));
	}

	private boolean checkPermissions() {
		return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, LOCATION_PERMISSION);
	}

	private void requestPermissions() {
		// Provide an additional rationale to the user. This would happen if the user denied the
		// request previously, but didn't check the "Don't ask again" checkbox.
		if (ActivityCompat.shouldShowRequestPermissionRationale(this, LOCATION_PERMISSION)) {
			ActivityCompat.requestPermissions(this, new String[] { LOCATION_PERMISSION }, REQUEST_CODE_PERMISSION);
		} else {
			// Request permission. It's possible this can be auto answered if device policy
			// sets the permission in a given state or the user denied the permission
			// previously and checked "Never ask again".
			ActivityCompat.requestPermissions(
					getActivity(),
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					REQUEST_CODE_PERMISSION
			);
		}
	}

	/**
	 * Returns true if requesting location updates, otherwise returns false.
	 */
	private boolean isLocationUpdating() {
		return SharedPrefs.getInstance().get(GPSTrackerService.KEY_REQUESTING_LOCATION_UPDATES, Boolean.class);
	}

	private void setButtonsState(boolean requestingLocationUpdates) {
		if (requestingLocationUpdates) {
			btn_location_action.setText(R.string.stop_location_update);
			btn_location_action.setTextColor(R.color.white);
			btn_location_action.setBackgroundColor(R.color.red_600);
		} else {
			// TODO: set inactive button style
			btn_location_action.setText(R.string.update_current_location);
			btn_location_action.setBackgroundColor(R.color.red_600);
		}
	}

    private void removeThis() {
        // Hủy tất cả các fragment cũng đang được gắn cùng vào trong FrameLayout
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
