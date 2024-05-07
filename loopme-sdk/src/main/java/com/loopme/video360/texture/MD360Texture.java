package com.loopme.video360.texture;

public abstract class MD360Texture {
    private static final int TEXTURE_EMPTY = 0;
    private static final String TAG = "MD360Texture";
    private int mWidth;
    private int mHeight;
    private final ThreadLocal<Integer> mLocalGLTexture = new ThreadLocal<>();

    public MD360Texture() {
    }

    public void resize(int width,int height){
        boolean changed = mWidth == width && mHeight == height;
        mWidth = width;
        mHeight = height;

        // resize the texture
        if (changed) onResize(mWidth,mHeight);
    }

    public void create() {
        int glTexture = createTextureId();
        if (glTexture != TEXTURE_EMPTY)
            mLocalGLTexture.set(glTexture);
    }

    public void release() {}

    protected int getCurrentTextureId(){
        Integer value = mLocalGLTexture.get();
        return value != null ? value : TEXTURE_EMPTY;
    }

    final protected boolean isEmpty(int textureId){
        return textureId == TEXTURE_EMPTY;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    abstract protected void onResize(int width, int height);

    abstract protected int createTextureId();

    abstract public void syncDrawInContext(ISyncDrawCallback callback);

    public interface ISyncDrawCallback {
        void onDrawOpenGL();
    }
}
