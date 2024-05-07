package com.loopme.video360;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.loopme.controllers.view.View360Controller;
import com.loopme.video360.common.GLUtil;
import com.loopme.video360.strategy.display.DisplayModeManager;
import com.loopme.video360.strategy.interactive.InteractiveModeManager;
import com.loopme.video360.texture.MD360Texture;
import com.loopme.video360.texture.MD360VideoTexture;

import java.util.LinkedList;
import java.util.List;

import static com.loopme.video360.common.VRUtil.notNull;

public class MDVRLibrary {

    private static final String LOG_TAG = MDVRLibrary.class.getSimpleName();

    private final InteractiveModeManager mInteractiveModeManager;
    private final DisplayModeManager mDisplayModeManager;

    private final List<MD360Director> mDirectorList;
    private final List<GLSurfaceView> mGLSurfaceViewList;
    private final MD360Texture mSurface;
    private final MDStatusManager mMDStatusManager;
    private final int mContentType;
    private boolean mIsPaused = true;

    private MDVRLibrary(Builder builder) {
        mContentType = builder.contentType;
        mSurface = builder.texture;

        mDirectorList = new LinkedList<>();
        mGLSurfaceViewList = new LinkedList<>();
        mMDStatusManager = new MDStatusManager();

        // start glSurfaceViews
        initWithGLSurfaceView(builder.context, builder.glSurfaceView);

        // start mode manager
        mDisplayModeManager = new DisplayModeManager(mGLSurfaceViewList);
        mInteractiveModeManager = new InteractiveModeManager(mDirectorList);

        mDisplayModeManager.prepare(builder.context);
        mInteractiveModeManager.prepare(builder.context);

        mMDStatusManager.reset(mDisplayModeManager.getVisibleSize());
    }

    private void initWithGLSurfaceView(Context context, GLSurfaceView glSurfaceView) {
        initOpenGL(context, glSurfaceView, mSurface);
    }

    public void setEventCallback(View360Controller.Callback callback) {
        for (MD360Director director : mDirectorList) {
            director.setEventCallback(callback);
        }
    }

    private void initOpenGL(Context context, GLSurfaceView glSurfaceView, MD360Texture texture) {
        if (GLUtil.supportsEs2(context)) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);
            MD360Director director = MD360DirectorFactory.createDirector();
            MD360Renderer renderer = MD360Renderer.with(context)
                    .setTexture(texture)
                    .setDirector(director)
                    .setContentType(mContentType)
                    .build();
            renderer.setStatus(mMDStatusManager.newChild());

            glSurfaceView.setRenderer(renderer);

            mDirectorList.add(director);
            mGLSurfaceViewList.add(glSurfaceView);
        } else {
            glSurfaceView.setVisibility(View.GONE);
            Toast.makeText(context, "OpenGLES2 not supported.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onResume(Context context){
        mIsPaused = false;
        mInteractiveModeManager.onResume(context);

        for (GLSurfaceView glSurfaceView:mGLSurfaceViewList){
            glSurfaceView.onResume();
        }
    }

    public void onPause(Context context){
        mIsPaused = true;
        mInteractiveModeManager.onPause(context);

        for (GLSurfaceView glSurfaceView:mGLSurfaceViewList){
            glSurfaceView.onPause();
        }
    }

    public void setGyroSupported(boolean b) {
        for (MD360Director director : mDirectorList) {
            director.setGyroSupported(b);
        }
    }

    public void setAccelSupported(boolean b) {
        for (MD360Director director : mDirectorList) {
            director.setAccelSupported(b);
        }
    }

    public boolean isPaused() {
        return mIsPaused;
    }

    public void onDestroy(){
        if (mSurface != null) {
            mSurface.release();
        }
    }

    public boolean handleTouchEvent(MotionEvent event) {
        return mInteractiveModeManager.handleTouchEvent(event);
    }

    public interface IOnSurfaceReadyCallback {
        void onSurfaceReady(Surface surface);
    }

    public static Builder with(Context context){
        return new Builder(context);
    }

    public static class Builder {
        private GLSurfaceView glSurfaceView;
        private final Context context;
        private int contentType = ContentType.VIDEO;
        private MD360Texture texture;

        private Builder(Context context) {
            this.context = context;
        }

        public Builder video(IOnSurfaceReadyCallback callback){
            texture = new MD360VideoTexture(callback);
            contentType = ContentType.VIDEO;
            return this;
        }

        public MDVRLibrary build(GLSurfaceView glSurfaceView){
            notNull(texture,"You must call video/bitmap function in before build");
            this.glSurfaceView = glSurfaceView;
            return new MDVRLibrary(this);
        }
    }

    interface ContentType{
        int VIDEO = 0;
    }
}
