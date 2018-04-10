package com.loopme.tester.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopme.tester.R;
import com.loopme.tester.model.AdSpot;
import com.loopme.tester.ui.fragment.screen.EditAdSpotFragment;

/**
 * Created by katerina on 1/28/17.
 */

public class RemoveAdSpotDialogFragment extends DialogFragment implements
        View.OnClickListener {

    private RemoveAdSpotDialogFragmentListener mListener;
    private AdSpot mAdSpot;


    public RemoveAdSpotDialogFragment() {
    }

    public static RemoveAdSpotDialogFragment newInstance(Bundle bundle) {
        RemoveAdSpotDialogFragment fragment = new RemoveAdSpotDialogFragment();
        fragment.setArguments(bundle);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_remove_adspot, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            mAdSpot = getArguments().getParcelable(EditAdSpotFragment.ARG_AD_SPOT);
        }
        setMessage(view);
        view.findViewById(R.id.remove_adspot_button).setOnClickListener(this);
        view.findViewById(R.id.cancel_remove_adspot_button).setOnClickListener(this);
    }

    private void setMessage(View view) {
        if (mAdSpot != null) {
            String message = getString(R.string.remove_pattern, mAdSpot.getName());
            ((TextView) view.findViewById(R.id.fragment_adspot_to_remove_textview)).setText(message);
        }
    }

    private void onRemove(AdSpot adSpot) {
        if (mListener != null) {
            mListener.onRemoveAdSpot(adSpot);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.remove_adspot_button: {
                onRemove(mAdSpot);
                break;
            }
        }
        dismiss();
    }


    public interface RemoveAdSpotDialogFragmentListener {
        void onRemoveAdSpot(AdSpot adSpot);
    }

    public void setListener(RemoveAdSpotDialogFragmentListener listener) {
        if (listener != null) {
            mListener = listener;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }
}
