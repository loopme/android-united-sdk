package com.loopme.tester.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopme.tester.R;
import com.loopme.tester.ui.view.LoopMeEditText;

public class ActiveSearchFragment extends BaseFragment implements
        View.OnClickListener, LoopMeEditText.OnLoopMeEditTextListener {

    private View mRootView;
    private LoopMeEditText mLoopMeEditSearch;
    private TextView mCancelButton;
    private OnActiveSearchFragmentListener mOnActiveSearchFragmentListener;

    public static ActiveSearchFragment newInstance() {
        return new ActiveSearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_active_search, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRootView.findViewById(R.id.fragment_active_search_cancel).setOnClickListener(this);

        mLoopMeEditSearch = (LoopMeEditText) mRootView.findViewById(R.id.fragment_active_search_edit);
        mCancelButton = (TextView) mRootView.findViewById(R.id.fragment_active_search_cancel);
        mCancelButton.setVisibility(View.GONE);
        mLoopMeEditSearch.setOnLoopMeEditTextListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_active_search_cancel: {
                disableSearch();
                onUpdateList();
                break;
            }
        }
    }

    private void onRemoveFocus() {
        if (mOnActiveSearchFragmentListener != null) {
            mOnActiveSearchFragmentListener.onRemoveFocus();
        }
    }

    public void disableSearch() {
        if (mLoopMeEditSearch != null) {
            mLoopMeEditSearch.disableSearch();
            activeSearchVisibility(false);
            onRemoveFocus();
        }
    }

    private void activeSearchVisibility(boolean activeSearch) {
        if (activeSearch) {
            mCancelButton.setVisibility(View.VISIBLE);
        } else {
            mCancelButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTextSearched(String searchString) {
        activeSearchVisibility(true);
        if (TextUtils.isEmpty(searchString)) {
            onUpdateList();
        } else {
            onSearch(searchString);
        }
    }

    @Override
    public void onHideSoftKeyboard() {
        onRemoveFocus();
    }

    private void onUpdateList() {
        if (mOnActiveSearchFragmentListener != null) {
            mOnActiveSearchFragmentListener.onUpdateList();
        }
    }

    private void onSearch(String text) {
        if (mOnActiveSearchFragmentListener != null && !TextUtils.isEmpty(text)) {
            mOnActiveSearchFragmentListener.onSearch(text);
        }
    }

    public interface OnActiveSearchFragmentListener {
        void onSearch(String text);

        void onUpdateList();

        void onRemoveFocus();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnActiveSearchFragmentListener = (OnActiveSearchFragmentListener) context;
    }

    @Override
    public void onDetach() {
        mOnActiveSearchFragmentListener = null;
        disableSearch();
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
