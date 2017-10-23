package com.loopme.tester.ui.fragment.screen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopme.tester.R;
import com.loopme.tester.ui.fragment.BaseFragment;
import com.loopme.tester.utils.FileUtils;

/**
 * Created by vynnykiakiv on 4/21/17.
 */

public class LogFragment extends BaseFragment implements View.OnClickListener {

    private OnLogFragmentListener mOnLogFragmentListener;

    public interface OnLogFragmentListener {
        void onLogFragmentDismiss();
    }

    public static LogFragment newInstance() {
        return new LogFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnLogFragmentListener = (OnLogFragmentListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.fragment_log_container).setOnClickListener(this);
        TextView mLogTextView = (TextView) view.findViewById(R.id.fragment_log_textview);
        mLogTextView.setText(FileUtils.getLogContent());
    }

    @Override
    public void onDetach() {
        mOnLogFragmentListener = null;
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        onLogFragmentDismiss();
    }

    private void onLogFragmentDismiss() {
        if (mOnLogFragmentListener != null) {
            mOnLogFragmentListener.onLogFragmentDismiss();
        }
    }
}
