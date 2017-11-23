package com.loopme.tester;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Surface;
import android.widget.FrameLayout;

import com.loopme.Constants;
import com.loopme.ResourceInfo;
import com.loopme.ad.AdParams;
import com.loopme.tracker.constants.EventConstants;
import com.loopme.utils.Utils;
import com.loopme.vast.TrackingEvent;
import com.loopme.xml.Tracking;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class UtilsTest {

    @Mock
    private Context mContext;
    @Mock
    private RecyclerView mRecyclerView;
    @Mock
    private LinearLayoutManager mLinearLayoutManager;
    @Mock
    private GridLayoutManager mGridLayoutManager;
    @Mock
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    @Test
    public void getScreenOrientation() {
//        int result = Utils.getScreenOrientation();
//        int expected = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//        Assert.assertEquals(expected, result);
    }

    @Test
    public void getScreenOrientation_ReturnDefaultPortrait() {
        //default value then resource null
        int result = Utils.getScreenOrientation();
        int expected = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        Assert.assertEquals(expected, result);
    }

    @Test
    public void getStringFromStream_ReturnTrue() {
        String expected = "abcde";
        InputStream stream = new ByteArrayInputStream(expected.getBytes());
        String result = Utils.getStringFromStream(stream);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void getOrientationForSquareScreens_ReturnPortrait() {
        int result = Utils.getOrientationForSquareScreens(Surface.ROTATION_90);
        int expected = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        Assert.assertEquals(expected, result);
    }

    @Test
    public void getOrientationForSquareScreens_ReturnLandscape() {
        int result = Utils.getOrientationForSquareScreens(83);
        int expectedLand = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        Assert.assertEquals(expectedLand, result);
    }

    @Test
    public void parseDuration_return() {
        String validDuration = "01:02:30";
        int expectedResult1 = 1 * 3600 + 2 * 60 + 30;
        int result1 = Utils.parseDuration(validDuration);
        Assert.assertEquals(expectedResult1, result1);
    }

    @Test
    public void isPackageInstalledTest_ReturnTrue() {
        ArrayList<String> listToCheck = new ArrayList<>();
        listToCheck.add("com.tester");
        listToCheck.add("com.google");

        ArrayList<String> installedList = new ArrayList<>();
        installedList.add("com.facebook");
        installedList.add("com.tester");
        installedList.add("com.boo");
        installedList.add("com.google");

        boolean result = Utils.isPackageInstalled(listToCheck, installedList);
        boolean expected = true;
        Assert.assertEquals(expected, result);
    }

    @Test
    public void isPackageNotInstalled_ReturnFalse() {
        ArrayList<String> listToCheck = new ArrayList<>();
        listToCheck.add("com.tester");
        listToCheck.add("com.google");

        ArrayList<String> installedList = new ArrayList<>();
        installedList.add("com.facebook");
        installedList.add("com.boo");

        boolean result = Utils.isPackageInstalled(listToCheck, installedList);
        boolean expected = false;
        Assert.assertEquals(expected, result);
    }

    @Test
    public void getResourceInfo_ReturnTrue() {
        String url = "https://www.toptal.com/code/";
        String fileName = "file.txt";
        String testUrlInfo = url + fileName;
        ResourceInfo expected = new ResourceInfo(url, fileName);
        ResourceInfo result = Utils.getResourceInfo(testUrlInfo);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void getInteger_Return123() {
        String number = "123";
        int expected = Integer.valueOf(number);
        int result = Utils.getInteger(number);
        Assert.assertEquals(expected, result);

    }

    @Test
    public void getInteger_ReturnZero() {
        String number = "dasd";
        int expected = 0;
        int result = Utils.getInteger(number);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void isBooleanString_ReturnTrue() {
        String trueString = "true";
        boolean expected = true;
        boolean result = Utils.isBooleanString(trueString);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void isBooleanString_ReturnFalse() {
        String falseString = "adasd";
        boolean expected = false;
        boolean result = Utils.isBooleanString(falseString);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void isUrl_ReturnTrue() {
        String url = "http://google.com";
        boolean expected = true;
        boolean result = Utils.isUrl(url);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void isUrl_ReturnFalse() {
        String url = "htcom";
        boolean expected = false;
        boolean result = Utils.isUrl(url);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void createTimeStamp_Return30() {
        int durationInMillis = 30_000;
        String expected = "00:30";
        String result = Utils.createTimeStamp(durationInMillis);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void createTimeStamp_Return01_21() {
        int durationInMillis = 81_000;
        String expected = "01:21";
        String result = Utils.createTimeStamp(durationInMillis);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void parsePercent_Retun20() {
        String percent20 = "20%";
        int expected = 20;
        int result = Utils.parsePercent(percent20);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void makeChromeShortCut() {
        String chrome = "browser Chrome ver 10.0";
        String expected = "browser Chrm ver 10.0";
        String result = Utils.makeChromeShortCut(chrome);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void createProgressPoints() {
        String url = "http:/12345.com";
        int duration = 30_000;

        AdParams params = createParams(url);
        List<TrackingEvent> expected = createTrackingEventsList(url, duration);
        List<TrackingEvent> result = Utils.createProgressPoints(duration, params);

        for (int i = 0; i < result.size(); i++) {
            TrackingEvent expectedEvent = expected.get(i);
            TrackingEvent resultEvent = expected.get(i);
            Assert.assertEquals(expectedEvent, resultEvent);
        }
    }

    @Test
    public void calculateNewLayoutParams_Stretch_None() {
        FrameLayout.LayoutParams layoutParams = getLayoutParams();
        int videoWidth = 480;
        int videoHeight = 200;
        int resizeWidth = 300;
        int resizeHeight = 250;
        FrameLayout.LayoutParams result = Utils.calculateNewLayoutParams(layoutParams, videoWidth, videoHeight, resizeWidth, resizeHeight, Constants.StretchOption.NONE);
        FrameLayout.LayoutParams expected = getLayoutParams();
        expected.width = 300;
        expected.height = 125;
        Assert.assertEquals(expected.height, result.height);
        Assert.assertEquals(expected.width, result.width);
    }

    @Test
    public void calculateNewLayoutParams_Stretch_Stretch() {
        FrameLayout.LayoutParams layoutParams = getLayoutParams();
        int videoWidth = 480;
        int videoHeight = 200;
        int resizeWidth = 300;
        int resizeHeight = 250;
        FrameLayout.LayoutParams result = Utils.calculateNewLayoutParams(layoutParams, videoWidth, videoHeight, resizeWidth, resizeHeight, Constants.StretchOption.STRETCH);
        FrameLayout.LayoutParams expected = getLayoutParams();
        expected.width = 300;
        expected.height = 250;
        Assert.assertEquals(expected.width, result.width);
        Assert.assertEquals(expected.height, result.height);
    }

    @Test
    public void calculateNewLayoutParams_Stretch_None_Square() {
        FrameLayout.LayoutParams layoutParams = getLayoutParams();
        int videoWidth = 200;
        int videoHeight = 200;
        int resizeWidth = 300;
        int resizeHeight = 250;
        FrameLayout.LayoutParams result = Utils.calculateNewLayoutParams(layoutParams, videoWidth, videoHeight, resizeWidth, resizeHeight, Constants.StretchOption.NONE);
        FrameLayout.LayoutParams expected = getLayoutParams();
        expected.width = 250;
        expected.height = 250;
        Assert.assertEquals(expected.width, result.width);
        Assert.assertEquals(expected.height, result.height);
    }

    @Test
    public void calculateNewLayoutParams_Stretch_None_Invalid_params() {
        FrameLayout.LayoutParams layoutParams = getLayoutParams();
        int videoWidth = 0;
        int videoHeight = 0;
        int resizeWidth = 300;
        int resizeHeight = 250;
        FrameLayout.LayoutParams result = Utils.calculateNewLayoutParams(layoutParams, videoWidth, videoHeight, resizeWidth, resizeHeight, Constants.StretchOption.NONE);
        FrameLayout.LayoutParams expected = getLayoutParams();
        expected.width = 300;
        expected.height = 250;
        Assert.assertEquals(expected.width, result.width);
        Assert.assertEquals(expected.height, result.height);
    }

    @Test
    public void formatTime_Two_Decimal() {
        double time = 2.9946346;
        String result = Utils.formatTime(time);
        String expected = "2.99";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void isUsualFormat_mp4_ReturnTrue() {
        boolean result = Utils.isUsualFormat("folder/video.mp4");
        Assert.assertTrue(result);
    }

    @Test
    public void isUsualFormat_webm_ReturnTrue() {
        boolean result = Utils.isUsualFormat("folder/video.webm");
        Assert.assertTrue(result);
    }

    @Test
    public void isUsualFormat_m3u8_ReturnFalse() {
        boolean result = Utils.isUsualFormat("folder/video.m3u8");
        Assert.assertFalse(result);
    }

    @Test
    public void isUsualFormat_invalidSource_ReturnFalse() {
        boolean result = Utils.isUsualFormat("video.mp4");
        Assert.assertFalse(result);
    }

    @Test
    public void getSourceUrl_validSource_equals() {
        String consoleMessage = "message:https://fb.com/res/log.txt";
        String result = Utils.getSourceUrl(consoleMessage);
        String expected = "//fb.com/res/log.txt";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void getSourceUrl_inValidSource_equalsEmpty() {
        String consoleMessage = ".com/res/log.txt";
        String result = Utils.getSourceUrl(consoleMessage);
        String expected = "";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void getSourceUrl_nullSource_equalsEmpty() {
        String consoleMessage = null;
        String result = Utils.getSourceUrl(consoleMessage);
        String expected = "";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void getPositionsOnScreen_recyclerViewNull() {
        int[] result = Utils.getPositionsOnScreen(null);
        int[] expected = {-1, -1};
        Assert.assertEquals(expected[0], result[0]);
        Assert.assertEquals(expected[1], result[1]);
    }

    @Test
    public void getPositionsOnScreen_linearLayoutManager_returnEquals() {
        int firstPosition = 0;
        int lastPosition = 5;
        when(mLinearLayoutManager.findFirstVisibleItemPosition()).thenReturn(firstPosition);
        when(mLinearLayoutManager.findLastVisibleItemPosition()).thenReturn(lastPosition);
        when(mRecyclerView.getLayoutManager()).thenReturn(mLinearLayoutManager);

        int[] result = Utils.getPositionsOnScreen(mRecyclerView);
        int[] expected = {firstPosition, lastPosition};
        Assert.assertEquals(expected[0], result[0]);
        Assert.assertEquals(expected[1], result[1]);
    }

    @Test
    public void getPositionsOnScreen_staggeredGridLayoutManager_returnEquals() {
        int firstPosition = 1;
        int lastPosition = 8;
        int[] firstArray = {firstPosition, 2, 3, 4};
        int[] lastArray = {5, 6, 7, lastPosition};
        when(mStaggeredGridLayoutManager.findFirstVisibleItemPositions(null)).thenReturn(firstArray);
        when(mStaggeredGridLayoutManager.findLastVisibleItemPositions(null)).thenReturn(lastArray);
        when(mRecyclerView.getLayoutManager()).thenReturn(mStaggeredGridLayoutManager);

        int[] result = Utils.getPositionsOnScreen(mRecyclerView);
        int[] expected = {firstPosition, lastPosition};
        Assert.assertEquals(expected[0], result[0]);
        Assert.assertEquals(expected[1], result[1]);
    }

    @Test
    public void getPositionsOnScreen_gridLayoutManager_returnEquals() {
        int firstPosition = 1;
        int lastPosition = 8;
        when(mGridLayoutManager.findFirstVisibleItemPosition()).thenReturn(firstPosition);
        when(mGridLayoutManager.findLastVisibleItemPosition()).thenReturn(lastPosition);
        when(mRecyclerView.getLayoutManager()).thenReturn(mGridLayoutManager);

        int[] result = Utils.getPositionsOnScreen(mRecyclerView);
        int[] expected = {firstPosition, lastPosition};
        Assert.assertEquals(expected[0], result[0]);
        Assert.assertEquals(expected[1], result[1]);
    }


    private FrameLayout.LayoutParams getLayoutParams() {
        return new FrameLayout.LayoutParams(mContext, null);
    }

    private List<TrackingEvent> createTrackingEventsList(String url, int duration) {
        List<TrackingEvent> expected = new ArrayList<>();
        expected.add(new TrackingEvent(url));
        expected.add(new TrackingEvent(url, duration / 4));
        expected.add(new TrackingEvent(url, duration / 2));
        expected.add(new TrackingEvent(url, duration * 3 / 4));
        expected.add(new TrackingEvent(url, ((int) (duration * 0.1))));
        expected.add(new TrackingEvent(url, ((int) (duration * 0.15))));
        expected.add(new TrackingEvent(url, 3_000));
        return expected;
    }

    private AdParams createParams(String url) {
        List<Tracking> eventsList = new ArrayList<>();
        eventsList.add(new Tracking(EventConstants.CREATIVE_VIEW, "0", url));
        eventsList.add(new Tracking(EventConstants.FIRST_QUARTILE, "0", url));
        eventsList.add(new Tracking(EventConstants.MIDPOINT, "0", url));
        eventsList.add(new Tracking(EventConstants.THIRD_QUARTILE, "0", url));
        eventsList.add(new Tracking(EventConstants.PROGRESS, "10%", url));
        eventsList.add(new Tracking(EventConstants.PROGRESS, "15%", url));
        eventsList.add(new Tracking(EventConstants.PROGRESS, "00:00:03", url));
        AdParams params = new AdParams();
        params.setTrackingEventsList(eventsList);
        return params;
    }
}
