package com.loopme.video360.texture;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Build;
import android.view.Surface;

import com.loopme.Logging;
import com.loopme.video360.MDVRLibrary;

import javax.microedition.khronos.opengles.GL10;

import static com.loopme.video360.common.GLUtil.glCheck;

public class MD360VideoTexture extends MD360Texture {

    private static final String LOG_TAG = MD360VideoTexture.class.getSimpleName();

    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private MDVRLibrary.IOnSurfaceReadyCallback mOnSurfaceReadyListener;

    public MD360VideoTexture(MDVRLibrary.IOnSurfaceReadyCallback onSurfaceReadyListener) {
        mOnSurfaceReadyListener = onSurfaceReadyListener;
    }

    @Override
    public void release() {
        super.release();

        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
        }
        mSurfaceTexture = null;

        if (mSurface != null) {
            mSurface.release();
        }
        mSurface = null;

    }

    @Override
    public void create() {
        super.create();
        int glSurfaceTexture = getCurrentTextureId();
        if (isEmpty(glSurfaceTexture)) return;

        onCreateSurface(glSurfaceTexture);
    }

    private void onCreateSurface(int glSurfaceTextureId) {
        if ( mSurfaceTexture == null ) {
            //attach the texture to a surface.
            //It's a clue class for rendering an android view to gl level
            mSurfaceTexture = new SurfaceTexture(glSurfaceTextureId);
            mSurfaceTexture.detachFromGLContext();
            mSurfaceTexture.setDefaultBufferSize(getWidth(), getHeight());
            mSurface = new Surface(mSurfaceTexture);
            if (mOnSurfaceReadyListener != null)
                mOnSurfaceReadyListener.onSurfaceReady(mSurface);
        }
    }

    @Override
    protected void onResize(int width, int height) {
        if (mSurfaceTexture != null)
            mSurfaceTexture.setDefaultBufferSize(width,height);
    }

    @Override
    public synchronized void syncDrawInContext(ISyncDrawCallback callback){
        int glSurfaceTexture = getCurrentTextureId();
        if (isEmpty(glSurfaceTexture)) return;

        if (mSurfaceTexture == null) {
            Logging.out(LOG_TAG, "mSurfaceTexture == null");
            create();
            return;
        }
            mSurfaceTexture.attachToGLContext(glSurfaceTexture);
            mSurfaceTexture.updateTexImage();
            callback.onDrawOpenGL();
            mSurfaceTexture.detachFromGLContext();
    }

    @Override
    protected int createTextureId() {
        int[] textures = new int[1];

        // Generate the texture to where android view will be rendered
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glGenTextures(1, textures, 0);
        glCheck("Texture generate");

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        glCheck("Texture bind");

        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return textures[0];
    }
}
