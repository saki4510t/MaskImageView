package com.serenegiant.widget;
/*
 * MaskImageView
 * library and sample of ImageView to clip image by mask image
 *
 * Copyright (c) 2016 saki t_saki@serenegiant.com
 *
 * File name: MaskImageButton.java
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
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class MaskImageButton extends MaskImageView {

	private final Paint mMaskedPaint = new Paint();
	private final Paint mCopyPaint = new Paint();
	private final Rect mMaskBounds = new Rect();
	private final RectF mViewBoundsF = new RectF();
	private Drawable mMaskDrawable;

	public MaskImageButton(final Context context) {
		this(context, null);
	}

	public MaskImageButton(final Context context, final AttributeSet attrs) {
		this(context, attrs, android.R.attr.imageButtonStyle);
	}

	public MaskImageButton(final Context context, final AttributeSet attrs, final int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setFocusable(true);
		mMaskedPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		mMaskDrawable = null;
	}
}
