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
import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devicesilencingapp.App;
import com.example.devicesilencingapp.R;
import com.example.devicesilencingapp.db.DBHelper;
import com.example.devicesilencingapp.libs.LocationUtils;
import com.example.devicesilencingapp.libs.SharedPrefs;
import com.example.devicesilencingapp.location.LocationListViewModel;
import com.example.devicesilencingapp.location.model.UserLocationModel;
import com.example.devicesilencingapp.location.service.GPSTrackerService;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationDetailFragment extends Fragment
		implements View.OnClickListener,
					SharedPreferences.OnSharedPreferenceChangeListener,
					AdapterView.OnItemSelectedListener {
    public static final int ACTION_ADD = 0;
    public static final int ACTION_EDIT = 1;

    private static final String ARG_ACTION = "action";
	private static final int REQUEST_CODE_PERMISSION = 2;
	private final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
	private final TypedArray LOCATION_ICON_ID = App.self().getResources().obtainTypedArray(R.array.location_icon_id);

    private LocationListViewModel mViewModel;
    private UserLocationModel mModelLocation;

	private SharedPreferences sharedPreferences;
	private GPSTrackerService mGpsTrackerService = null;
    // Theo dõi trạng thái ràng buộc của service.
    private boolean mBound = false;
    // The BroadcastReceiver used to listen from broadcasts from the service.
    private Receiver receiver;
    private int action;

    private Button btn_location_action;
    private TextView tv_address, tv_lnglat;
    private ImageView iv_subject;
    private Spinner sp_label;
    private EditText et_name, et_radius, et_expiration;
    private SwitchCompat sw_status;

    // id of drawable base on selection of spinner
    private int resourceId;

    // Theo dõi trạng thái kêt nối của service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GPSTrackerService.LocalBinder binder = (GPSTrackerService.LocalBinder) service;
            mGpsTrackerService = binder.getService();
            mBound = true;
	        mGpsTrackerService.requestLocationUpdates();
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
            Location location = intent.getParcelableExtra(GPSTrackerService.EXTRA_LOCATION);
            if (location != null) {
            	mModelLocation.setLatitude(location.getLatitude());
            	mModelLocation.setLongitude(location.getLongitude());
	            LocationUtils.getAddressFromLocation(
			            location.getLatitude(),
			            location.getLongitude(),
			            getActivity().getApplicationContext(),
			            new GeocoderHandler()
	            );
            }
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
        mViewModel = new ViewModelProvider(requireActivity()).get(LocationListViewModel.class);

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

        Button btn_action = view.findViewById(R.id.btn_action);

	    tv_address = view.findViewById(R.id.tv_address);
	    tv_lnglat = view.findViewById(R.id.tv_lnglat);
	    iv_subject = view.findViewById(R.id.iv_subject);
	    btn_location_action = view.findViewById(R.id.btn_location_action);
	    sp_label = view.findViewById(R.id.sp_label);
	    et_name = view.findViewById(R.id.et_name);
	    et_radius = view.findViewById(R.id.et_radius);
	    et_expiration = view.findViewById(R.id.et_expiration);
	    sw_status = view.findViewById(R.id.sw_status);

	    action = getArguments().getInt(ARG_ACTION);

	    if (action == ACTION_ADD) {
		    mModelLocation = new UserLocationModel();
		    btn_action.setText(R.string.add);
		    btn_location_action.callOnClick();
	    } else {
            mModelLocation = mViewModel.getSelectedItem().getValue();

		    btn_action.setText(R.string.edit);

		    tv_address.setText(mModelLocation.getAddress());
	        tv_lnglat.setText(LocationUtils.toString(mModelLocation.getLatitude(), mModelLocation.getLongitude()));
	        iv_subject.setImageResource(mModelLocation.getLabel());
	        Object label = mModelLocation.getLabel();
            // FIXME: suspicious call list.lastindexof
	        sp_label.setSelection(Arrays.asList(LOCATION_ICON_ID).lastIndexOf(label), true);
	        et_name.setText(mModelLocation.getName());
	        et_radius.setText(String.valueOf(mModelLocation.getRadius()));
	        et_expiration.setText(String.valueOf(mModelLocation.getExpiration()));
	        sw_status.setChecked(mModelLocation.getStatus());
        }

	    btn_location_action.setOnClickListener(this);
	    btn_action.setOnClickListener(this);
	    view.findViewById(R.id.btn_cancel).setOnClickListener(this);

	    setButtonsState(isLocationUpdating());

//	    ArrayAdapter<String> sp_data = ArrayAdapter.createFromResource(this, R.array.location_label, android.R.layout.simple_spinner_item, );
//	    // hiển thi danh sách cho spinner
//	    sp_data.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	    sp_label.setAdapter(sp_data);
	    sp_label.setOnItemSelectedListener(this);
    }

    @Override
    public void onStop() {
        if (mBound) {
            mBound = false;
            mGpsTrackerService.removeLocationUpdates();
            getActivity().unbindService(mServiceConnection);
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
                } else if (isLocationUpdating()) {
	                mGpsTrackerService.requestLocationUpdates();
	                setButtonsState(true);
                } else {
                	mGpsTrackerService.removeLocationUpdates();
	                setButtonsState(false);
                }
                break;
            case R.id.btn_action:
	            DBHelper helper = DBHelper.getInstance();

	            mModelLocation.setName(et_name.getText().toString());
	            mModelLocation.setAddress(tv_address.getText().toString());
	            mModelLocation.setLabel(resourceId);
	            mModelLocation.setRadius(Integer.parseInt(et_radius.getText().toString()));
	            mModelLocation.setExpiration(Integer.parseInt(et_expiration.getText().toString()));
	            mModelLocation.setStatus(sw_status.isChecked());

	            if (action == ACTION_ADD) {
	            	// TODO: check data (long, lat) exist in db

	            	long id = helper.addLocation(mModelLocation);
	            	mModelLocation.setId(id);
	            	mViewModel.setNewItem(mModelLocation);
	            } else {
	            	helper.editLocation(mModelLocation);
	            	mViewModel.setSelectedItem(mModelLocation);
	            }

	            removeThis();
                break;
            case R.id.btn_cancel:
            	removeThis();
                break;
        }
    }

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    	resourceId = LOCATION_ICON_ID.getResourceId(position, 0);
    	iv_subject.setImageResource(resourceId);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

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
		return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(getContext(), LOCATION_PERMISSION);
	}

	private void requestPermissions() {
		// Provide an additional rationale to the user. This would happen if the user denied the
		// request previously, but didn't check the "Don't ask again" checkbox.
		if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), LOCATION_PERMISSION)) {
			ActivityCompat.requestPermissions(getActivity(), new String[] { LOCATION_PERMISSION }, REQUEST_CODE_PERMISSION);
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
		} else {
			btn_location_action.setText(R.string.update_current_location);
		}
	}

    private void removeThis() {
        // Hủy tất cả các fragment cũng đang được gắn cùng vào trong FrameLayout
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
