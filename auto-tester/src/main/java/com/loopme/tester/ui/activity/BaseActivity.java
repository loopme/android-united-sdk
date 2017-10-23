package com.loopme.tester.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.loopme.tester.PreferencesService;
import com.loopme.tester.R;
import com.loopme.tester.handlers.AdSpotAsyncHandler;
import com.loopme.tester.model.AdSpot;
import com.loopme.tester.model.ScreenStackModel;
import com.loopme.tester.ui.fragment.BaseFragment;
import com.loopme.tester.ui.fragment.screen.AdSpotCardFragment;
import com.loopme.tester.ui.fragment.screen.EditAdSpotFragment;
import com.loopme.tester.ui.fragment.screen.ExportFragment;
import com.loopme.tester.ui.fragment.screen.HomeFragment;
import com.loopme.tester.ui.fragment.screen.ImportFragment;
import com.loopme.tester.ui.fragment.screen.InfoFragment;
import com.loopme.tester.ui.fragment.screen.LogFragment;
import com.loopme.tester.utils.UiUtils;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by katerina on 2/9/17.
 */

public class BaseActivity extends AppCompatActivity implements Observer {

    private Queue<Runnable> mRunnableQueue = new LinkedBlockingQueue<>();
    private AdSpotAsyncHandler mAdSpotAsyncHandler;
    private Handler mHandler = new Handler();
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private boolean mIsReady;
    private PreferencesService mPrefs;
    protected static final int PERMISSION_REQUEST_CODE_SAVE = 500;
    protected static final int PERMISSION_REQUEST_CODE_LOAD = 501;
    public static final int PERMISSION_REQUEST_CODE_SAVE_LOG = 502;
    protected static final int PERMISSION_REQUEST_CODE_SHOW_LOG = 503;
    public static final int SCREEN_UNKNOWN = -1;
    public static final int SCREEN_HOME = 0;
    public static final int SCREEN_EDIT = 1;
    public static final int SCREEN_INFO = 2;
    public static final int SCREEN_ADSPOT_CARD = 3;
    public static final int SCREEN_IMPORT = 4;
    public static final int SCREEN_EXPORT = 5;
    public static final int SCREEN_LOG = 6;

    protected Deque<ScreenStackModel> mScreenStack = new LinkedBlockingDeque<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsReady = true;
        mAdSpotAsyncHandler = new AdSpotAsyncHandler(getContentResolver());
        startBackgroundThread();
        mPrefs = new PreferencesService(this);
    }

    public void setAutoLoadingState(boolean autoLoadingState) {
        if (mPrefs != null) {
            mPrefs.setAutoLoadingState(autoLoadingState);
        }
    }

    public boolean getAutoLoadingState() {
        return mPrefs != null && mPrefs.getAutoLoadingState();
    }

    public void insertOrUpdateAdSpot(AdSpot adSpot) {
        mAdSpotAsyncHandler.insertOrUpdateAdSpot(adSpot);
    }

    public void insertAdSpot(AdSpot adSpot) {
        mAdSpotAsyncHandler.insertAsync(adSpot);
    }

    public void insertAllAdSpot(ArrayList<AdSpot> newKeysList) {
        mAdSpotAsyncHandler.insertAllAsync(newKeysList);
    }

    public void updateAdSpot(AdSpot adSpot) {
        mAdSpotAsyncHandler.updateAsync(adSpot.getAdSpotId(), adSpot);
    }

    public void deleteAdSpot(AdSpot adSpot) {
        mAdSpotAsyncHandler.deleteAsync(adSpot.getAdSpotId(), adSpot);
    }

    public void reloadDatabaseData(ArrayList<AdSpot> adSpotList) {
        deleteAllAdSpot();
        insertAllAdSpot(adSpotList);
    }

    public void deleteAllAdSpot() {
        mAdSpotAsyncHandler.deleteAllAsync();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsReady = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIsReady = true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mIsReady = true;
        checkQueue();
    }

    @Override
    protected void onDestroy() {
        stopBackgroundThread();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mIsReady = false;
    }

    private void startBackgroundThread() {
        if (mBackgroundThread == null) {
            mBackgroundThread = new HandlerThread("BaseActivity");
            mBackgroundThread.start();
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        }
    }

    private void stopBackgroundThread() {
        if (mBackgroundThread != null) {
            mBackgroundThread.quit();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        }
    }

    public void postRunnable(Runnable runnable) {
        mHandler.post(runnable);
    }

    public void postRunnableDelayed(Runnable runnable, long delayed) {
        mHandler.postDelayed(runnable, delayed);
    }

    public void postBackgroundRunnable(Runnable runnable) {
        mBackgroundHandler.post(runnable);
    }

    public void removeRunnable(Runnable runnable) {
        if (mBackgroundHandler != null) {
            mBackgroundHandler.removeCallbacks(runnable);
        }
    }

    public void makeTransaction(Runnable runnable) {
        if (mIsReady) {
            runnable.run();
        } else {
            mRunnableQueue.add(runnable);
        }
    }

    public void makeTransaction(final FragmentTransaction fragmentTransaction) {
        if (mIsReady) {
            fragmentTransaction.commit();
        } else {
            mRunnableQueue.add(new Runnable() {
                @Override
                public void run() {
                    fragmentTransaction.commit();
                }
            });
        }
    }

    private void checkQueue() {
        int size = mRunnableQueue.size();
        if (size > 0) {
            Runnable runnable = mRunnableQueue.poll();
            runnable.run();
        }
    }


    public int getCurrentScreenId() {
        if (mScreenStack.size() > 0) {
            ScreenStackModel screenStackModel = mScreenStack.peek();
            return screenStackModel.getScreenId();
        }
        return SCREEN_UNKNOWN;
    }

    public void openScreen(int screenId) {
        openScreen(screenId, false);
    }

    public void clearStack() {
        mScreenStack.clear();
    }

    public void openScreen(int screenId, Bundle args) {
        openScreen(screenId, args, false);
    }

    public void openScreen(int screenId, boolean replaceCurrent) {
        openScreen(screenId, null, replaceCurrent);
    }

    public void openScreen(int screenId, Bundle args, boolean replaceCurrent) {
        makeTransaction(new OpenScreenRunnable(screenId, replaceCurrent, args));
        hideSoftKeyBoard();
    }

    public void closeCurrentScreen() {
        makeTransaction(new CloseCurrentScreenRunnable());
        hideSoftKeyBoard();
    }

    public class CloseCurrentScreenRunnable implements Runnable {

        @Override
        public void run() {
            if (mScreenStack.isEmpty()) {
                return;
            }

            if (mScreenStack.size() == 1) {
                BaseActivity.this.onBackPressed();
                return;
            }

            if (closeScreenIfOnTop(SCREEN_INFO)
                    || closeScreenIfOnTop(SCREEN_LOG)
                    || closeScreenIfOnTop(SCREEN_IMPORT)
                    || closeScreenIfOnTop(SCREEN_EXPORT)) {
                return;
            }

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();

            mScreenStack.pop();
            ScreenStackModel restoringScreenModel = mScreenStack.peek();
            Bundle savedState = restoringScreenModel.getStateToSave();

            Fragment curFragment = fm.findFragmentById(R.id.root);
            if (curFragment != null) {
                ((BaseFragment) curFragment).onBeforeClose();
            }

            switch (restoringScreenModel.getScreenId()) {
                case SCREEN_HOME: {
                    fragmentTransaction.replace(R.id.root, HomeFragment.newInstance());
                    break;
                }
                case SCREEN_EDIT: {
                    fragmentTransaction.replace(R.id.root, EditAdSpotFragment.newInstance(savedState));
                    break;
                }
                case SCREEN_ADSPOT_CARD: {
                    fragmentTransaction.replace(R.id.root, AdSpotCardFragment.newInstance(savedState));
                    break;
                }
            }

            fragmentTransaction.commit();
        }
    }

    public class OpenScreenRunnable implements Runnable {
        private final int mScreenId;
        private boolean mReplace;
        private Bundle mArguments;

        public OpenScreenRunnable(int screenId) {
            this(screenId, false, null);
        }

        public OpenScreenRunnable(int screenId, boolean replace, Bundle args) {
            mScreenId = screenId;
            mReplace = replace;
            mArguments = args;
        }

        @Override
        public void run() {
            closeScreenIfOnTop(SCREEN_INFO);

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            Fragment curFragment = fm.findFragmentById(R.id.root);

            Bundle dataForAnotherScreen = mArguments;
            Bundle stateToSave = null;
            if (curFragment != null && curFragment instanceof BaseFragment) {
                stateToSave = ((BaseFragment) curFragment).getStateBundle();
                ((BaseFragment) curFragment).onBeforeClose();
            }
            switch (mScreenId) {
                case SCREEN_HOME: {
                    fragmentTransaction.add(R.id.root, HomeFragment.newInstance());
                    break;
                }
                case SCREEN_EDIT: {
                    fragmentTransaction.replace(R.id.root, EditAdSpotFragment.newInstance(dataForAnotherScreen));
                    break;
                }
                case SCREEN_INFO: {
                    fragmentTransaction.add(R.id.info_root, InfoFragment.newInstance());
                    break;
                }
                case SCREEN_ADSPOT_CARD: {
                    fragmentTransaction.replace(R.id.root, AdSpotCardFragment.newInstance(dataForAnotherScreen));
                    break;
                }
                case SCREEN_IMPORT: {
                    fragmentTransaction.add(R.id.import_export_root_container, ImportFragment.newInstance());
                    break;
                }
                case SCREEN_EXPORT: {
                    fragmentTransaction.add(R.id.import_export_root_container, ExportFragment.newInstance());
                    break;
                }
                case SCREEN_LOG: {
                    fragmentTransaction.add(R.id.log_root, LogFragment.newInstance());
                    break;
                }
                default: {
                    throw new RuntimeException("Unknown screen");
                }
            }

            fragmentTransaction.commit();
            if (mScreenStack.size() > 0) {
                if (mReplace) {
                    mScreenStack.pop();
                } else {
                    ScreenStackModel currentModel = mScreenStack.peek();
                    currentModel.setStateToSave(stateToSave);
                }
            }

            ScreenStackModel newScreenModel = new ScreenStackModel();
            newScreenModel.setScreenId(mScreenId);
            mScreenStack.push(newScreenModel);
        }

        public void setReplace(boolean replace) {
            mReplace = replace;
        }

        public void setArguments(Bundle arguments) {
            mArguments = arguments;
        }

    }

    private boolean closeScreenIfOnTop(int screenOnTop) {
        if (mScreenStack.peek() == null) {
            return false;
        }
        int screenId = mScreenStack.peek().getScreenId();

        if (screenId == screenOnTop) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment;

            switch (screenOnTop) {
                case SCREEN_INFO: {
                    fragment = fragmentManager.findFragmentById(R.id.info_root);
                    break;
                }
                case SCREEN_LOG: {
                    fragment = fragmentManager.findFragmentById(R.id.log_root);
                    break;
                }
                case SCREEN_IMPORT: {
                    fragment = fragmentManager.findFragmentById(R.id.import_export_root_container);
                    break;
                }
                case SCREEN_EXPORT: {
                    fragment = fragmentManager.findFragmentById(R.id.import_export_root_container);
                    break;
                }
                default: {
                    return false;
                }
            }
            if (fragment != null) {
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
                mScreenStack.pop();
                return true;
            }
        }
        return false;
    }

    private void removeScreen(int screenId) {
        ScreenStackModel removingModel = null;
        for (ScreenStackModel screenStackModel : mScreenStack) {
            if (screenStackModel.getScreenId() == screenId) {
                removingModel = screenStackModel;
                break;
            }
        }

        mScreenStack.removeLastOccurrence(removingModel);
    }

    private void removeAllToScreen(int screenId) {
        ScreenStackModel screenStackModel;

        boolean remove = true;
        while (remove) {
            screenStackModel = mScreenStack.peek();
            if (screenStackModel.getScreenId() == screenId) {
                break;
            }
            if (mScreenStack.size() == 1) {
                break;
            }
            mScreenStack.pop();
        }
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    public void findAdSpotById(long adSpotId, AdSpotCardFragment.OnAdSpotUpdateCallback callback) {
        mAdSpotAsyncHandler.findAdSpotById(adSpotId, callback);
    }

    public void hideSoftKeyBoard() {
        UiUtils.hideSoftKeyboard(getWindow().getDecorView().getRootView(), this);
    }
}
