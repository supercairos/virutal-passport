package io.romain.passport.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.OnClick;
import io.romain.passport.R;
import io.romain.passport.logic.helpers.AccountHelper;
import io.romain.passport.logic.helpers.UserHelper;
import io.romain.passport.ui.views.SvgLogoView;
import io.romain.passport.utils.Dog;
import io.romain.passport.utils.SimpleAnimatorListener;

public class LandingActivity extends BaseActivity {

	private static final long ICON_DISOLVE_DURATION = 500;
	private static final long FADE_IN_DURATION = 300;

	private final Handler mHandler = new Handler();

	@Bind(R.id.content_root_view)
	FrameLayout mRootView;

	@Bind(R.id.landing_splash_screen)
	ViewGroup mSplashScreen;
	@Bind(R.id.landing_button_screen)
	ViewGroup mButtonScreen;

	@Bind(R.id.landing_icon)
	ImageView mLandingIcon;

	@Bind(R.id.landing_button_layout)
	ViewGroup mLandingButtonLayout;

	@Bind(R.id.background)
	ImageView mBackground;
	@Bind(R.id.background_blur)
	ImageView mBackgroundBlur;

	@Bind(R.id.svg_animated_logo)
	SvgLogoView mLogoView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String token = AccountHelper.peekToken(mAccountManager);
		if (!TextUtils.isEmpty(token)) {
			Dog.d("Auto login : %s", token.substring(0, token.length() < 10 ? token.length() : 10));
			UserHelper.next(this);
		}

		setContentView(R.layout.activity_landing);

		ObjectAnimator a1 = ObjectAnimator.ofFloat(mLandingIcon, View.SCALE_X, 0.0f).setDuration(ICON_DISOLVE_DURATION);
		ObjectAnimator a2 = ObjectAnimator.ofFloat(mLandingIcon, View.SCALE_Y, 0.0f).setDuration(ICON_DISOLVE_DURATION);

		ObjectAnimator a3 = ObjectAnimator.ofFloat(mButtonScreen, View.ALPHA, 1.0f).setDuration(FADE_IN_DURATION);
		ObjectAnimator a4 = ObjectAnimator.ofFloat(mSplashScreen, View.ALPHA, 0.0f).setDuration(FADE_IN_DURATION);

		a3.setStartDelay(ICON_DISOLVE_DURATION - FADE_IN_DURATION);
		a4.setStartDelay(ICON_DISOLVE_DURATION - FADE_IN_DURATION);
		final AnimatorSet set = new AnimatorSet();
		set.playTogether(a1, a2, a3, a4);
		set.addListener(new SimpleAnimatorListener() {

			@Override
			public void onAnimationEnd(Animator animation) {
				mSplashScreen.setVisibility(View.GONE);
				mLogoView.start();
			}
		});
		mHandler.postDelayed(set::start, 1500);

		mLogoView.setOnStateChangeListenerListener(state -> {
			switch (state) {
				case SvgLogoView.STATE_FILL_STARTED:
					ObjectAnimator a6 = ObjectAnimator.ofFloat(mLandingButtonLayout, View.TRANSLATION_Y, 0).setDuration(500);
					ObjectAnimator a7 = ObjectAnimator.ofFloat(mLandingButtonLayout, View.ALPHA, 1).setDuration(500);

					ObjectAnimator a8 = ObjectAnimator.ofFloat(mBackgroundBlur, View.ALPHA, 1).setDuration(500);
					ObjectAnimator a9 = ObjectAnimator.ofFloat(mBackground, View.ALPHA, 0).setDuration(500);

					AnimatorSet animator = new AnimatorSet();
					animator.playTogether(a6, a7, a8, a9);
					animator.addListener(new SimpleAnimatorListener() {

						@Override
						public void onAnimationEnd(Animator animation) {
							mBackground.setVisibility(View.GONE);
						}
					});
					animator.start();
					break;
			}
		});
	}

	@OnClick(R.id.button_register)
	void onButtonRegisterClicked() {
		startActivity(new Intent(this, RegisterActivity.class));
	}

	@OnClick(R.id.button_login)
	void onButtonLoginClicked() {
		startActivity(new Intent(this, LoginActivity.class));
	}
}
