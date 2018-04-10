package com.loopme.tester.ui.view;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.loopme.tester.utils.UiUtils;

public class LoopMeEditText extends AppCompatEditText implements TextWatcher {

    private OnLoopMeEditTextListener mListener;

    public interface OnLoopMeEditTextListener {
        void onHideSoftKeyboard();

        void onTextSearched(String expression);
    }

    public LoopMeEditText(Context context) {
        super(context);
        addTextChangedListener(this);
    }

    public LoopMeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(this);

    }

    public LoopMeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTextChangedListener(this);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        onHideSoftKeyboard();
        return super.onKeyPreIme(keyCode, event);
    }

    private void onHideSoftKeyboard() {
        if (mListener != null) {
            mListener.onHideSoftKeyboard();
        }
    }

    public void setOnLoopMeEditTextListener(OnLoopMeEditTextListener listener) {
        mListener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
        onTextSearched(text.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public void disableSearch() {
        setText("");
        UiUtils.hideSoftKeyboard(this, getContext());
    }

    public void onTextSearched(String text) {
        if (mListener != null) {
            mListener.onTextSearched(text);
        }
    }
}
