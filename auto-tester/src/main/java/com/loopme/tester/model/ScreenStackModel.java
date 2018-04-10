package com.loopme.tester.model;

import android.os.Bundle;

import com.loopme.tester.enums.ViewMode;

/**
 * Created by katerina on 2/9/17.
 */

public class ScreenStackModel {
    private int mScreenId;
    private Bundle mStateToSave;
    private ViewMode mViewMode = ViewMode.INFO;

    public ScreenStackModel() {

    }

    public ScreenStackModel(int screenId, Bundle dataForAnotherFragment) {
        mScreenId = screenId;
    }

    public ScreenStackModel(int screenId, Bundle dataForAnotherFragment, ViewMode viewMode) {
        mScreenId = screenId;
        mViewMode = viewMode;
    }

    public int getScreenId() {
        return mScreenId;
    }

    public void setScreenId(int screenId) {
        mScreenId = screenId;
    }

    public Bundle getStateToSave() {
        return mStateToSave;
    }

    public ViewMode getViewMode() {
        return mViewMode;
    }

    public void setViewMode(ViewMode viewMode) {
        this.mViewMode = viewMode;
    }

    public void setStateToSave(Bundle stateToSave) {
        mStateToSave = stateToSave;
    }
}
