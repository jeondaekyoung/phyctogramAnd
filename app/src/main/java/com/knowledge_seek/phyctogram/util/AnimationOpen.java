/*  Created by Edward Akoto on 12/31/12.
 *  Email akotoe@aua.ac.ke
 * 	Free for modification and distribution
 */

package com.knowledge_seek.phyctogram.util;

import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

public class AnimationOpen extends TranslateAnimation implements
		Animation.AnimationListener {

	private LinearLayout mainLayout;
	int panelWidth, displayWidth;

	public AnimationOpen(LinearLayout layout, int width, int dWidth, int fromXType,
						 float fromXValue, int toXType, float toXValue, int fromYType,
						 float fromYValue, int toYType, float toYValue) {

		super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue,
				toYType, toYValue);

		// init
		mainLayout = layout;
		panelWidth = width;
		displayWidth = dWidth;
		setDuration(250);
		setFillAfter(true);
		setInterpolator(new AccelerateDecelerateInterpolator());
		setAnimationListener(this);
		mainLayout.startAnimation(this);
	}

	public void onAnimationEnd(Animation arg0) {

		LayoutParams params = (LayoutParams) mainLayout.getLayoutParams();
		params.leftMargin = panelWidth;
		params.width = displayWidth;
		params.gravity = Gravity.LEFT;
		mainLayout.clearAnimation();
		mainLayout.setLayoutParams(params);
		mainLayout.requestLayout();

	}

	public void onAnimationRepeat(Animation arg0) {

	}

	public void onAnimationStart(Animation arg0) {

	}

}
