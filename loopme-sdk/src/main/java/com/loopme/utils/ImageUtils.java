package com.loopme.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class ImageUtils {

    public static void setScaledImage(ImageView imageView, final String filePath) {
        ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(initOnPreDrawListener(imageView, filePath));
    }

    private static ViewTreeObserver.OnPreDrawListener initOnPreDrawListener(final ImageView view, final String filePath) {
        return new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                int imageViewHeight = view.getMeasuredHeight();
                int imageViewWidth = view.getMeasuredWidth();
                view.setImageBitmap(decodeSampledBitmap(filePath, imageViewWidth, imageViewHeight));
                return true;
            }
        };
    }

    private static Bitmap decodeSampledBitmap(String filePath, int requiredWidth, int requiredHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int requiredWidth, int requiredHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > requiredHeight || width > requiredWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > requiredHeight && (halfWidth / inSampleSize) > requiredWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
