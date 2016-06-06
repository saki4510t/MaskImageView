package com.serenegiant.widget;
/*
 * MaskImageView
 * library and sample of ImageView to clip image by mask image
 *
 * Copyright (c) 2016 saki t_saki@serenegiant.com
 *
 * File name: MaskImageView.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
*/

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * ImageView that display source image applying alpha mask
 */
public class MaskImageView extends ImageView {

	private final Paint mMaskedPaint = new Paint();
	private final Paint mCopyPaint = new Paint();
	private final Rect mMaskBounds = new Rect();
	private final RectF mViewBoundsF = new RectF();
	private Drawable mMaskDrawable;

	public MaskImageView(final Context context) {
		this(context, null, 0);
	}

	public MaskImageView(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MaskImageView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mMaskedPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		TypedArray attribs = context.obtainStyledAttributes(attrs, R.styleable.MaskImageView, defStyleAttr, 0);
		mMaskDrawable = attribs.getDrawable(R.styleable.MaskImageView_mask);
		attribs.recycle();
		attribs = null;
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
    	int sz = (int)(Math.min(width - padding_left - getPaddingRight(), height - padding_top - getPaddingBottom()) * 2 / 3.0f);
    	final Drawable dr = getDrawable();
    	if (dr != null) {
    		sz = Math.min(sz, dr.getIntrinsicWidth());
			sz = Math.min(sz, dr.getIntrinsicHeight());
		}
    	final int left =  (width - sz) / 2 + padding_left;
    	final int top = (height - sz) / 2 + padding_top;
        mMaskBounds.set(left, top, left + sz, top + sz);
		mMaskedPaint.setMaskFilter(new BlurMaskFilter(sz, BlurMaskFilter.Blur.NORMAL));

        // keep view size(keep drawing rectangle)
		mViewBoundsF.set(0, 0, width, height);
        if (mMaskDrawable != null) {
			mMaskDrawable.setBounds(mMaskBounds);
		}
    }

	@Override
	protected synchronized void onDraw(final Canvas canvas) {
		if ((mViewBoundsF.width() == 0) || (mViewBoundsF.height() == 0)) {
			super.onDraw(canvas);
			return;
		}
		final int saveCount = canvas.saveLayer(mViewBoundsF, mCopyPaint,
			Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG);
		try {
			canvas.translate(-getPaddingLeft(), -getPaddingTop());
			if (mMaskDrawable != null) {
				mMaskDrawable.draw(canvas);
				canvas.saveLayer(mViewBoundsF, mMaskedPaint, 0);
			}
			canvas.translate(getPaddingLeft(), getPaddingTop());
			super.onDraw(canvas);
		} finally {
			canvas.restoreToCount(saveCount);
		}
	}
}
