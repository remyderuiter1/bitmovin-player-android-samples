package com.bitmovin.player.samples.ads.basic;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bitmovin.analytics.api.AnalyticsConfig;
import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerConfig;
import com.bitmovin.player.api.advertising.AdItem;
import com.bitmovin.player.api.advertising.AdSource;
import com.bitmovin.player.api.advertising.AdSourceType;
import com.bitmovin.player.api.advertising.AdvertisingConfig;
import com.bitmovin.player.api.analytics.PlayerFactory;
import com.bitmovin.player.api.source.SourceConfig;

public class MainActivity extends AppCompatActivity {
    // These are IMA Sample Tags from https://developers.google.com/interactive-media-ads/docs/sdks/android/tags
    private static final String AD_SOURCE_1 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dredirecterror&nofb=1&correlator=";
    private static final String AD_SOURCE_2 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=";
    private static final String AD_SOURCE_3 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
    private static final String AD_SOURCE_4 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dredirectlinear&correlator=";

    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create AdSources
        AdSource firstAdSource = new AdSource(AdSourceType.Ima, AD_SOURCE_1);
        AdSource secondAdSource = new AdSource(AdSourceType.Ima, AD_SOURCE_2);
        AdSource thirdAdSource = new AdSource(AdSourceType.Ima, AD_SOURCE_3);
        AdSource fourthAdSource = new AdSource(AdSourceType.Ima, AD_SOURCE_4);

        // Set up a pre-roll ad
        AdItem preRoll = new AdItem("pre", thirdAdSource);

        // Set up a mid-roll waterfalling ad at 10% of the content duration
        // NOTE: AdItems containing more than one AdSource will be executed as waterfalling ad
        AdItem midRoll = new AdItem("10%", firstAdSource, secondAdSource);

        // Set up a post-roll ad
        AdItem postRoll = new AdItem("post", fourthAdSource);

        // Add the AdItems to the AdvertisingConfig
        AdvertisingConfig advertisingConfig = new AdvertisingConfig(preRoll, midRoll, postRoll);

        // Create a new PlayerConfiguration
        PlayerConfig playerConfig = new PlayerConfig();

        // Add the AdvertisingConfig to the PlayerConfig. Ads in the AdvertisingConfig will be scheduled automatically.
        playerConfig.setAdvertisingConfig(advertisingConfig);

        // Create new BitmovinPlayerView with our PlayerConfiguration and AnalyticsConfiguration
        String key = "{ANALYTICS_LICENSE_KEY}";
        Player player = PlayerFactory.create(this, playerConfig, new AnalyticsConfig(key));
        playerView = new PlayerView(this, player);
        playerView.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                )
        );

        playerView.getPlayer().load(SourceConfig.fromUrl("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd"));

        LinearLayout rootView = findViewById(R.id.activity_main);

        // Add PlayerView to the layout
        rootView.addView(playerView, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        playerView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerView.onResume();
    }

    @Override
    protected void onPause() {
        playerView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        playerView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        playerView.onDestroy();
        super.onDestroy();
    }
}
