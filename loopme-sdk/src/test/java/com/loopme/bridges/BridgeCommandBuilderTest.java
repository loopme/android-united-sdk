package com.loopme.bridges;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.loopme.Constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class BridgeCommandBuilderTest {

    @Test
    void isNativeCallFinished() {
        assertEquals(
                "javascript:window.L.bridge.set('webview', {isNativeCallFinished: 'true'});",
                BridgeCommandBuilder.isNativeCallFinished(true)
        );
        assertEquals(
                "javascript:window.L.bridge.set('webview', {isNativeCallFinished: 'false'});",
                BridgeCommandBuilder.isNativeCallFinished(false)
        );
    }

    @Test
    void shake() {
        assertEquals(
                "javascript:window.L.bridge.set('webview', {shake: 'true'});",
                BridgeCommandBuilder.shake(true)
        );
        assertEquals(
                "javascript:window.L.bridge.set('webview', {shake: 'false'});",
                BridgeCommandBuilder.shake(false)
        );
    }

    @Test
    void videoMute() {
        assertEquals(
                "javascript:window.L.bridge.set('video', {mute: 'true'});",
                BridgeCommandBuilder.videoMute(true)
        );
        assertEquals(
                "javascript:window.L.bridge.set('video', {mute: 'false'});",
                BridgeCommandBuilder.videoMute(false)
        );
    }

    @Test
    void videoCurrentTime() {
        assertEquals(
                "javascript:window.L.bridge.set('video', {currentTime: '123'});",
                BridgeCommandBuilder.videoCurrentTime(123)
        );
        assertEquals(
                "javascript:window.L.bridge.set('video', {currentTime: '-123'});",
                BridgeCommandBuilder.videoCurrentTime(-123)
        );
    }

    @Test
    void videoDuration() {
        assertEquals(
                "javascript:window.L.bridge.set('video', {duration: '123'});",
                BridgeCommandBuilder.videoDuration(123)
        );
        assertEquals(
                "javascript:window.L.bridge.set('video', {duration: '-123'});",
                BridgeCommandBuilder.videoDuration(-123)
        );
    }

    @ParameterizedTest
    @EnumSource(value = Constants.VideoState.class)
    void videoState(Constants.VideoState state) {
        assertEquals(
                "javascript:window.L.bridge.set('video', {state: '" + state + "'});",
                BridgeCommandBuilder.videoState(state)
        );
    }

    @ParameterizedTest
    @EnumSource(value = Constants.WebviewState.class)
    void webviewState(Constants.WebviewState state) {
        assertEquals(
                "javascript:window.L.bridge.set('webview', {state: '" + state + "'});",
                BridgeCommandBuilder.webviewState(BridgeCommandBuilder.LOOPME_PREFIX, state)
        );
    }

    @Test
    void fullscreenMode() {
        assertEquals(
                "javascript:window.L.bridge.set('webview', {fullscreenMode: true});",
                BridgeCommandBuilder.fullscreenMode(true)
        );
        assertEquals(
                "javascript:window.L.bridge.set('webview', {fullscreenMode: false});",
                BridgeCommandBuilder.fullscreenMode(false)
        );
    }

    @Test
    void mraidSetIsViewable() {
        assertEquals(
                "javascript:mraidbridge.setIsViewable(true);",
                BridgeCommandBuilder.mraidSetIsViewable(true)
        );
        assertEquals(
                "javascript:mraidbridge.setIsViewable(false);",
                BridgeCommandBuilder.mraidSetIsViewable(false)
        );
    }

    @Test
    void mraidNotifyReady() {
        assertEquals(
                "javascript:mraidbridge.notifyReadyEvent();",
                BridgeCommandBuilder.mraidNotifyReady()
        );
    }

    @Test
    void mraidNotifyError() {
        assertEquals(
                "javascript:mraidbridge.notifyErrorEvent();",
                BridgeCommandBuilder.mraidNotifyError()
        );
    }

    @Test
    void mraidNotifyStateChange() {
        assertEquals(
                "javascript:mraidbridge.notifyStateChangeEvent();",
                BridgeCommandBuilder.mraidNotifyStateChange()
        );
    }

    // TODO: replace Constants.MraidState with enum source and write parameterized test
    @Test
    void mraidSetState() {
        assertEquals(
                "javascript:mraidbridge.setState('" + Constants.MraidState.DEFAULT + "');",
                BridgeCommandBuilder.mraidSetState(Constants.MraidState.DEFAULT)
        );
        assertEquals(
                "javascript:mraidbridge.setState('" + Constants.MraidState.EXPANDED + "');",
                BridgeCommandBuilder.mraidSetState(Constants.MraidState.EXPANDED)
        );
        assertEquals(
                "javascript:mraidbridge.setState('" + Constants.MraidState.RESIZED + "');",
                BridgeCommandBuilder.mraidSetState(Constants.MraidState.RESIZED)
        );
        assertEquals(
                "javascript:mraidbridge.setState('" + Constants.MraidState.HIDDEN + "');",
                BridgeCommandBuilder.mraidSetState(Constants.MraidState.HIDDEN)
        );
        assertEquals(
                "javascript:mraidbridge.setState('" + Constants.MraidState.LOADING + "');",
                BridgeCommandBuilder.mraidSetState(Constants.MraidState.LOADING)
        );
    }

    @Test
    void mraidNotifySizeChangeEvent() {
        assertEquals(
                "javascript:mraidbridge.notifySizeChangeEvent(123, 456);",
                BridgeCommandBuilder.mraidNotifySizeChangeEvent(123, 456)
        );
    }

    @Test
    void mraidResize() {
        assertEquals(
                "javascript:mraidbridge.resize();",
                BridgeCommandBuilder.mraidResize()
        );
    }

    @Test
    void mraidNativeCallComplete() {
        assertEquals(
                "javascript:mraidbridge.nativeCallComplete();",
                BridgeCommandBuilder.mraidNativeCallComplete()
        );
    }
}