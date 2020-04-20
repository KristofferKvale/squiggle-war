package com.mygdx.game;

import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class AndroidLauncher extends AndroidApplication {

	public static InterstitialAd mInterstitialAd;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		MobileAds.initialize(this, new OnInitializationCompleteListener() {
			@Override
			public void onInitializationComplete(InitializationStatus initializationStatus) {
				Log.d("AdMob", "Done initializing");
			}
		});

		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

		initialize(new Game(), config);
	}
}
