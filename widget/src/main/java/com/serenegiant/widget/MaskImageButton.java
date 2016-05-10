package com.serenegiant.widget;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class MaskImageButton extends ImageButton {

	private final Paint mMaskedPaint = new Paint();
	private final Paint mCopyPaint = new Paint();
	private final Rect mMaskBounds = new Rect();
	private final RectF mViewBoundsF = new RectF();
	private Drawable mMaskDrawable;

	public MaskImageButton(final Context context) {
		this(context, null, 0);
	}

	public MaskImageButton(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MaskImageButton(final Context context, final AttributeSet attrs, final int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mMaskedPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		mMaskDrawable = null;
	}

	/**
	 * set drawable for alpha mask, if set null, mask feature is disabled and all source image is displayed.
	 * only alpha value is valid and other color attribute(R, B, and B) are ignored.
	 * If the alpha value is smaller than 1.0, half transparent image is displayed
	 * @param mask_drawable
	 */
	public synchronized void setMaskDrawable(final Drawable mask_drawable) {
		if (mMaskDrawable != mask_drawable) {
			mMaskDrawable = mask_drawable;
			if (mMaskDrawable != null) {
				mMaskDrawable.setBounds(mMaskBounds);
			}
			postInvalidate();
		}
	}

    protected synchronized void onSizeChanged(int width, int height, int old_width, int old_height) {
    	// calculate drawable size for mask applying padding
    	final int padding_left = getPaddingLeft();
    	final int padding_top = getPaddingTop();
    	final int sz = Math.min(width - padding_left - getPaddingRight(), height - padding_top - getPaddingBottom());
    	final int left =  (width - sz) / 2 + padding_left;
    	final int top = (height - sz) / 2 + padding_top;
        mMaskBounds.set(left, top, left + sz, top + sz);
		mMaskedPaint.setMaskFilter(new BlurMaskFilter(sz * 2 / 3.0f, BlurMaskFilter.Blur.NORMAL));

        // keep view size(keep drawing rectangle)
		mViewBoundsF.set(0, 0, width, height);
        if (mMaskDrawable != null) {
			mMaskDrawable.setBounds(mMaskBounds);
		}
    }

	@Override
	protected synchronized void onDraw(final Canvas canvas) {
		final int saveCount = canvas.saveLayer(mViewBoundsF, mCopyPaint,
			Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG);
		try {
			if (mMaskDrawable != null) {
				mMaskDrawable.draw(canvas);
				canvas.saveLayer(mViewBoundsF, mMaskedPaint, 0);
			}
			super.onDraw(canvas);
		} finally {
			canvas.restoreToCount(saveCount);
		}
	}
}
