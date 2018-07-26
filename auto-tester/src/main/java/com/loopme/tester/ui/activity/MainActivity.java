package com.loopme.tester.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.widget.Toast;

import com.loopme.tester.tracker.AppEventTracker;
import com.loopme.tester.AppUpdateChecker;
import com.loopme.tester.BuildConfig;
import com.loopme.tester.Constants;
import com.loopme.tester.Integration;
import com.loopme.tester.R;
import com.loopme.tester.db.contracts.AdContract;
import com.loopme.tester.enums.LoadType;
import com.loopme.tester.enums.ViewMode;
import com.loopme.tester.loaders.AdSpotCursorLoader;
import com.loopme.tester.loaders.FileLoaderManager;
import com.loopme.tester.model.AdSpot;
import com.loopme.tester.model.Response;
import com.loopme.tester.model.ScreenStackModel;
import com.loopme.tester.qr.QRAdActivity;
import com.loopme.tester.ui.dialog.CreateFolderDialogFragment;
import com.loopme.tester.ui.fragment.ActionBarFragment;
import com.loopme.tester.ui.fragment.ActiveSearchFragment;
import com.loopme.tester.ui.fragment.AdSpotListFragment;
import com.loopme.tester.ui.fragment.FilesListFragment;
import com.loopme.tester.ui.fragment.screen.AdSpotCardFragment;
import com.loopme.tester.ui.fragment.screen.EditAdSpotFragment;
import com.loopme.tester.ui.fragment.screen.ExportFragment;
import com.loopme.tester.ui.fragment.screen.HomeFragment;
import com.loopme.tester.ui.fragment.screen.ImportFragment;
import com.loopme.tester.ui.fragment.screen.InfoFragment;
import com.loopme.tester.ui.fragment.screen.LogFragment;
import com.loopme.tester.utils.FileUtils;
import com.loopme.tester.utils.Utils;
import com.testfairy.TestFairy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends BaseActivity implements
        CreateFolderDialogFragment.OnCreateFolderDialogFragmentListener,
        ActionBarFragment.OnActionBarFragmentListener,
        AdSpotListFragment.OnAdSpotListFragmentListener,
        EditAdSpotFragment.OnEditAdFragmentListener,
        InfoFragment.OnInfoFragmentListener,
        ExportFragment.OnExportFragmentListener,
        ImportFragment.OnImportFragmentListener,
        AdSpotCardFragment.OnAdSpotCardFragmentListener,
        FilesListFragment.OnFilesListFragmentListener,
        ActiveSearchFragment.OnActiveSearchFragmentListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        FileLoaderManager.FileLoaderManagerCallback,
        LogFragment.OnLogFragmentListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String SEARCH_TEXT = "SEARCH_TEXT";
    private static final String SEARCH_NAME = "SEARCH_NAME";
    private static final String SEARCH_APPKEY = "SEARCH_APPKEY";

    public static final int ADSPOT_LOADER_ID = 100;
    private static final int SEARCH_LOADER_ID = 101;
    private static final int EXPORT_LOADER_ID = 102;
    private static final int SEARCH_BEFORE_CREATE_LOADER_ID = 103;

    private FileLoaderManager mFileLoaderManager;
    private boolean mIsNeedExport;
    private File mExportedFile;
    private static final int QR_ACTIVITY_REQUEST_CODE = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TestFairy.begin(this, BuildConfig.TESTFAIRY_APP_TOKEN);
        trackFirstLaunch();

        mFileLoaderManager = new FileLoaderManager(this, this);
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.root, HomeFragment.newInstance());
            mScreenStack.push(new ScreenStackModel(SCREEN_HOME, null, ViewMode.INFO));
            fragmentTransaction.commit();
        }
//        Integration.insertIasKeys(this);
        Integration.insertDefaultKeys(this);

        new AppUpdateChecker(this, AppUpdateChecker.LaunchMode.START_UP).checkUpdate();
    }

    private void trackFirstLaunch() {
        AppEventTracker.getInstance().track(AppEventTracker.Event.FIRST_LAUNCH, isFirstLaunch());
        setSetFirstLaunchDone();
    }

    @Override
    public void onExportFile(String path, String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            mExportedFile = new File(path, fileName);
            mIsNeedExport = true;
            restartLoader(EXPORT_LOADER_ID);
            closeCurrentScreen();
        } else {
            Toast.makeText(this, R.string.files_name_is_empty, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCloseExportFragment() {
        closeCurrentScreen();
    }

    @Override
    public void onImportFile(File file) {
        if (mFileLoaderManager != null) {
            mFileLoaderManager.startLoad(LoadType.FILE_READ, file);
        }
    }

    @Override
    public void onCloseImportFragment() {
        closeCurrentScreen();
    }

    @Override
    public void onUpdateFilePath(File file) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.import_export_root_container);
        if (fragment != null) {
            if (fragment instanceof ExportFragment) {
                ((ExportFragment) fragment).onUpdateFilePath(file);
            } else if (fragment instanceof ImportFragment) {
                ((ImportFragment) fragment).onUpdateFilePath(file);
            }
        }
    }

    @Override
    public void setFileSelected(File file) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.import_export_root_container);
        if (fragment != null) {
            if (fragment instanceof ExportFragment) {
                ((ExportFragment) fragment).setFileSelected(file);
            } else if (fragment instanceof ImportFragment) {
                ((ImportFragment) fragment).setFileSelected(file);
            }
        }
    }

    @Override
    public void onNewAdSpot() {
        Bundle args = EditAdSpotFragment.createArguments(ViewMode.CREATE, null);
        openScreen(SCREEN_EDIT, args);
    }

    @Override
    public void onEditAdSpot(AdSpot adSpot) {
        Bundle args = EditAdSpotFragment.createArguments(ViewMode.EDIT, adSpot);
        openScreen(SCREEN_EDIT, args);
    }

    @Override
    public void onItemSelected(AdSpot adSpot) {
        Bundle args = AdSpotCardFragment.createArguments(adSpot);
        openScreen(SCREEN_ADSPOT_CARD, args);
    }

    @Override
    public void onOpenInfo() {
        openScreen(SCREEN_INFO);
    }

    @Override
    public void onSave() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root);
        if (fragment != null && fragment instanceof EditAdSpotFragment) {
            ((EditAdSpotFragment) fragment).onSave();
        }
    }

    @Override
    public void editAdSpot() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root);
        if (fragment != null && fragment instanceof AdSpotCardFragment) {
            ((AdSpotCardFragment) fragment).onEditAdSpot();
        }
    }

    @Override
    public void onCreate(final AdSpot adSpot) {
        insertAdSpot(adSpot);
        restartLoader(ADSPOT_LOADER_ID);
        closeCurrentScreen();
    }

    @Override
    public void onEdit(AdSpot adSpot) {
        updateAdSpot(adSpot);
        restartLoader(ADSPOT_LOADER_ID);
        closeCurrentScreen();
    }

    public void restartLoader() {
        restartLoader(ADSPOT_LOADER_ID);
    }

    private void restartLoader(int loaderId) {
        getSupportLoaderManager().restartLoader(loaderId, null, this);
    }

    private void restartLoader(int loaderId, Bundle args) {
        getSupportLoaderManager().restartLoader(loaderId, args, this);
    }

    @Override
    public void onDelete(AdSpot adSpot) {
        deleteAdSpot(adSpot);
        restartLoader(ADSPOT_LOADER_ID);
        clearSearchText();
        onRemoveFocus();
    }

    @Override
    public void onClose() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root);
        if (fragment != null && fragment instanceof AdSpotCardFragment) {
            ((AdSpotCardFragment) fragment).cancelTasks();
        }
        closeCurrentScreen();
    }

    @Override
    public void onCheckAdSpot(AdSpot adSpot) {
        Bundle args = new Bundle();
        args.putString(SEARCH_NAME, adSpot.getName());
        args.putString(SEARCH_APPKEY, adSpot.getAppKey());
        restartLoader(SEARCH_BEFORE_CREATE_LOADER_ID, args);
    }

    @Override
    public void showImportFragment() {
        if (Utils.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            openScreen(SCREEN_IMPORT);
        } else {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSION_REQUEST_CODE_LOAD);
        }
    }

    @Override
    public void showExportFragment() {
        if (Utils.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            openScreen(SCREEN_EXPORT);
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQUEST_CODE_SAVE);
        }
    }

    @Override
    public void showLogFragment() {
        if (!FileUtils.isLogFileValid()) {
            Toast.makeText(this, R.string.log_file_is_empty, Toast.LENGTH_LONG).show();
            return;
        }

        if (Utils.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            openScreen(SCREEN_LOG, false);
        } else {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSION_REQUEST_CODE_SHOW_LOG);
        }
    }

    @Override
    public void dismissInfoFragment() {
        closeCurrentScreen();
    }


    @Override
    public void onSuccess(File folder) {
        Toast.makeText(this, getString(R.string.folder_successfully_created), Toast.LENGTH_LONG).show();
        goIntoFolder(folder);
    }

    public void goIntoFolder(File folder) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_export_files_list_container);
        if (fragment != null && fragment instanceof FilesListFragment) {
            ((FilesListFragment) fragment).goIntoFolder(folder);
        }
    }

    @Override
    public void onSearch(String text) {
        Bundle args = new Bundle();
        args.putString(SEARCH_TEXT, text);
        restartLoader(SEARCH_LOADER_ID, args);
    }

    @Override
    public void onUpdateList() {
        restartLoader(ADSPOT_LOADER_ID);
    }

    @Override
    public void onRemoveFocus() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root);
        if (fragment != null && fragment instanceof HomeFragment) {
            ((HomeFragment) fragment).requestFocus();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SEARCH_LOADER_ID: {
                return new AdSpotCursorLoader(this,
                        AdContract.AdEntry.CONTENT_URI,
                        null,
                        "( " + AdContract.AdEntry.COLUMN_NAME + " like ? OR " + AdContract.AdEntry.COLUMN_APPKEY + " like ? )",
                        new String[]{"%" + args.getString(SEARCH_TEXT) + "%", "%" + args.getString(SEARCH_TEXT) + "%"},
                        null);
            }
            case ADSPOT_LOADER_ID: {
                return new AdSpotCursorLoader(this, AdContract.AdEntry.CONTENT_URI, null, null, null,
                        AdContract.AdEntry.COLUMN_TIME + " DESC");
            }
            case EXPORT_LOADER_ID: {
                return new AdSpotCursorLoader(this, AdContract.AdEntry.CONTENT_URI, null, null, null,
                        AdContract.AdEntry.COLUMN_TIME + " DESC");
            }
            case SEARCH_BEFORE_CREATE_LOADER_ID: {
                return new AdSpotCursorLoader(this,
                        AdContract.AdEntry.CONTENT_URI,
                        null,
                        "( " + AdContract.AdEntry.COLUMN_NAME + " = ? OR " + AdContract.AdEntry.COLUMN_APPKEY + " = ? )",
                        new String[]{args.getString(SEARCH_NAME), args.getString(SEARCH_APPKEY)},
                        null);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        switch (loader.getId()) {
            case ADSPOT_LOADER_ID: {
                if (cursor != null && !cursor.isClosed() && cursor.getCount() >= 0) {
                    if (loader instanceof AdSpotCursorLoader) {
                        List<AdSpot> adSpotList = ((AdSpotCursorLoader) loader).getAdSpotModelList();
                        updateAdSpotList(adSpotList);
                    }
                }
                break;
            }
            case SEARCH_LOADER_ID: {
                if (cursor != null && !cursor.isClosed() && cursor.getCount() >= 0 && loader instanceof AdSpotCursorLoader) {
                    List<AdSpot> adSpotList = ((AdSpotCursorLoader) loader).getAdSpotModelList();
                    updateAdSpotList(adSpotList);
                }
                break;
            }
            case EXPORT_LOADER_ID: {
                if (mIsNeedExport) {
                    if (cursor != null && !cursor.isClosed() && cursor.getCount() >= 0 && cursor.moveToFirst()) {
                        if (loader instanceof AdSpotCursorLoader) {
                            ArrayList<AdSpot> adSpotList = ((AdSpotCursorLoader) loader).getAdSpotModelList();
                            mFileLoaderManager.startLoad(LoadType.FILE_SAVE, mExportedFile, adSpotList);
                        }
                    }
                    mIsNeedExport = false;
                }
                break;
            }
            case SEARCH_BEFORE_CREATE_LOADER_ID: {
                if (cursor != null && !cursor.isClosed() && cursor.getCount() >= 0 && cursor.moveToFirst()) {
                    onCheckAdSpotResult(true, Utils.getAdSpotId(loader));
                } else {
                    onCheckAdSpotResult(false, Constants.AD_SPOT_DOES_NOT_EXIST_ID);
                }
                break;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void clearSearchText() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root);
        if (fragment != null && fragment instanceof HomeFragment) {
            ((HomeFragment) fragment).clearSearchText();
        }
    }

    private void updateAdSpotList(List<AdSpot> adSpotList) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root);
        if (fragment != null && fragment instanceof HomeFragment) {
            ((HomeFragment) fragment).updateAdSpotList(adSpotList);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        getSupportLoaderManager().destroyLoader(EXPORT_LOADER_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        if (requestCode == PERMISSION_REQUEST_CODE_SAVE) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                openScreen(SCREEN_EXPORT);
            } else {
                Toast.makeText(this, R.string.change_permissions_import, Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PERMISSION_REQUEST_CODE_LOAD) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                openScreen(SCREEN_IMPORT);
            } else {
                Toast.makeText(this, R.string.change_permissions_export, Toast.LENGTH_LONG).show();
            }
        } else if ((requestCode == PERMISSION_REQUEST_CODE_SHOW_LOG)) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                openScreen(SCREEN_LOG);
            } else {
                Toast.makeText(this, R.string.to_see_log_change_permission, Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PERMISSION_CAMERA_CODE) {
            if (checkCameraPermission()) {
                runQrAdActivity();
            } else {
                Snackbar.make(findViewById(R.id.main_layout_root), "To use this feature need camera permission", Snackbar.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root);
        if (fragment != null)
            if (fragment instanceof HomeFragment) {
                if (((HomeFragment) fragment).processBackPress()) {
                    finish();
                    return;
                }
            } else if (fragment instanceof ActiveSearchFragment) {
                if (((ActiveSearchFragment) fragment).processBackPress()) {
                    ((ActiveSearchFragment) fragment).disableSearch();
                    return;
                }
            }
        if (mScreenStack.size() > 1) {
            closeCurrentScreen();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onSaveFileSuccess() {
        Toast.makeText(this, getString(R.string.exported_successfully), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReadFileSuccess(ArrayList<AdSpot> adSpotsList) {
        closeCurrentScreen();
        reloadDatabaseData(adSpotsList);
        postRunnableDelayed(new Runnable() {
            @Override
            public void run() {
                restartLoader(ADSPOT_LOADER_ID);
                Toast.makeText(MainActivity.this, getString(R.string.imported_successfully), Toast.LENGTH_LONG).show();
            }
        }, Utils.getTimeDelay());
    }

    @Override
    public void onError(Response response) {
        Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLogFragmentDismiss() {
        closeCurrentScreen();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT && Utils.isLg()) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root);
            if (fragment != null && fragment instanceof HomeFragment) {
                ((HomeFragment) fragment).refreshAdSpotListView();
            }
        }
    }

    public void requestPermission(String permissionType, int requestCode) {
        Utils.requestPermission(this, permissionType, requestCode);
    }

    private void onCheckAdSpotResult(final boolean isAdSpotAlreadyExist, final long existedAdSpotId) {
        postRunnable(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root);
                if (fragment instanceof EditAdSpotFragment) {
                    ((EditAdSpotFragment) fragment).onCheckAdSpotResult(isAdSpotAlreadyExist, existedAdSpotId);
                }
            }
        });
    }

    @Override
    public void reedQrCode() {
        if (checkCameraPermission()) {
            runQrAdActivity();
        } else {
            askCameraPermission();
        }
    }

    private void runQrAdActivity() {
        Intent intent = new Intent(this, QRAdActivity.class);
        startActivityForResult(intent, MainActivity.QR_ACTIVITY_REQUEST_CODE);
    }
}
