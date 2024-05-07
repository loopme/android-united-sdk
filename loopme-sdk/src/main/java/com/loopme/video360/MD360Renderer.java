package com.loopme.video360;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.loopme.video360.objects.MDSphere3D;
import com.loopme.video360.texture.MD360Texture;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.loopme.video360.common.GLUtil.glCheck;

public class MD360Renderer implements GLSurfaceView.Renderer, MD360Texture.ISyncDrawCallback {

    private static final String TAG = "MD360Renderer";

    private final MDSphere3D mObject3D;
    private final MD360Program mProgram;
    private final MD360Texture mTexture;

    // final
    private final Context mContext;
    private final MD360Director mDirector;
    private MDStatusManager.Status mStatus;

    private MD360Renderer(Builder params) {
        mContext = params.context;
        mTexture = params.texture;
        mDirector = params.director;
        mObject3D = new MDSphere3D();
        mProgram = new MD360Program(params.contentType);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // set the background clear color to black.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        // enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // start
        initProgram();
        initTexture();
        initObject3D();
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Update surface
        mTexture.resize(width, height);

        // Update Projection
        mDirector.updateProjection(width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if (mStatus == null) return;
        if (mStatus.isAllReady()) {
            mTexture.syncDrawInContext(this);
        } else {
            mStatus.ready();
        }
    }

    @Override
    public void onDrawOpenGL() {
        // Set our per-vertex lighting program.
        mProgram.use();
        glCheck("mProgram use");

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mProgram.getTextureUniformHandle(), 0);
        glCheck("glUniform1i");

        // Pass in the combined matrix.
        mDirector.shot(mProgram);

        // Draw
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mObject3D.getNumIndices());
    }

    private void initProgram() {
        mProgram.build(mContext);
    }

    private void initTexture() {
        mTexture.create();
    }

    private void initObject3D() {
        // load
        mObject3D.loadObj(mContext);

        // upload
        mObject3D.uploadDataToProgram(mProgram);
    }

    public static Builder with(Context context) {
        Builder builder = new Builder();
        builder.context = context;
        return builder;
    }

    public void setStatus(MDStatusManager.Status mStatus) {
        this.mStatus = mStatus;
    }

    public static class Builder {
        private Context context;
        private MD360Texture texture;
        private MD360Director director;
        private int contentType = MDVRLibrary.ContentType.VIDEO;

        private Builder() {
        }

        public MD360Renderer build() {
            if (director == null) director = MD360Director.builder().build();
            return new MD360Renderer(this);
        }

        public Builder setContentType(int contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * set surface{@link MD360Texture} to this render
         *
         * @param texture {@link MD360Texture} surface may used by multiple render{@link MD360Renderer}
         * @return builder
         */
        public Builder setTexture(MD360Texture texture) {
            this.texture = texture;
            return this;
        }

        public Builder setDirector(MD360Director director) {
            this.director = director;
            return this;
        }
    }
}
