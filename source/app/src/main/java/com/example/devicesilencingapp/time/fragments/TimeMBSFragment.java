package com.example.devicesilencingapp.time.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.example.devicesilencingapp.db.DBHelper;
import com.example.devicesilencingapp.time.TimeViewModal;
import com.example.devicesilencingapp.time.timeModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.devicesilencingapp.R;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     TimeMBSFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class TimeMBSFragment extends BottomSheetDialogFragment implements View.OnClickListener{
    private timeModel modal;
    private TimeViewModal viewModal;
    // TODO: Customize parameters
    public static TimeMBSFragment newInstance() {

        return new TimeMBSFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_mbs_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModal = ViewModelProviders.of(requireActivity()).get(TimeViewModal.class);
        modal = viewModal.getSelected().getValue();
        SwitchCompat switchCompat = ((SwitchCompat) view.findViewById(R.id.btn_status));
        switchCompat.setChecked(modal.isEnabled);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                modal.setEnabled(isChecked);
                DBHelper.getInstance().editTimeStatus(modal);
                viewModal.setSelected(modal);
            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            // TODO: Customize the item layout
            super(inflater.inflate(R.layout.fragment_time_mbs_item, parent, false));
            text = itemView.findViewById(R.id.text);
        }
    }

    private class itemAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final int mItemCount;

        itemAdapter(int itemCount) {
            mItemCount = itemCount;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }

    }

}
