package com.loopme.views;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.loopme.Constants;
import com.loopme.receiver.MraidAdCloseButtonReceiver;
import com.loopme.utils.Utils;

public class CloseButton extends View {
    private static final int THICKNESS_OUTER = Utils.convertDpToPixel(6);
    private static final int THICKNESS_INNER = Utils.convertDpToPixel(4);
    private static final int OFFSET = Utils.convertDpToPixel(14);
    private static final int VIEW_SIZE = Utils.convertDpToPixel(30);
    private static final int CLICKABLE_VIEW_SIZE = Utils.convertDpToPixel(40);

    private MraidAdCloseButtonReceiver mMraidCloseButtonReceiver;
    private final Paint mPaint = new Paint();

    public CloseButton(Context context) { super(context); }

    public void registerReceiver() {
        if (mMraidCloseButtonReceiver != null)
            return;
        mMraidCloseButtonReceiver = new MraidAdCloseButtonReceiver(customCloseButton -> {
            this.setVisibility(!customCloseButton ? View.VISIBLE : View.GONE);
        });
        ContextCompat.registerReceiver(
            this.getContext(),
            mMraidCloseButtonReceiver,
            new IntentFilter(Constants.MRAID_NEED_CLOSE_BUTTON),
            ContextCompat.RECEIVER_NOT_EXPORTED
        );
    }

    public void unregisterReceiver() {
        if (mMraidCloseButtonReceiver == null)
            return;
        this.getContext().unregisterReceiver(mMraidCloseButtonReceiver);
        mMraidCloseButtonReceiver = null;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        drawCross(canvas, Color.BLACK, THICKNESS_OUTER);
        drawCross(canvas, Color.WHITE, THICKNESS_INNER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(CLICKABLE_VIEW_SIZE, CLICKABLE_VIEW_SIZE);
    }

    private void drawCross(Canvas canvas, @ColorInt int color, float thickness) {
        mPaint.setColor(color);
        mPaint.setStrokeWidth(thickness);

        canvas.drawLine(OFFSET, OFFSET, VIEW_SIZE, VIEW_SIZE, mPaint);
        canvas.drawLine(OFFSET, VIEW_SIZE, VIEW_SIZE, OFFSET, mPaint);
        canvas.drawCircle(OFFSET, OFFSET, thickness / 2, mPaint);
        canvas.drawCircle(VIEW_SIZE, VIEW_SIZE, thickness / 2, mPaint);
        canvas.drawCircle(OFFSET, VIEW_SIZE, thickness / 2, mPaint);
        canvas.drawCircle(VIEW_SIZE, OFFSET, thickness / 2, mPaint);
    }
}
