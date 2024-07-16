package com.bitmovin.player.samples.ads.ima

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.advertising.AdItem
import com.bitmovin.player.api.advertising.AdSource
import com.bitmovin.player.api.advertising.AdSourceType
import com.bitmovin.player.api.advertising.AdvertisingConfig
import com.bitmovin.player.api.analytics.AnalyticsPlayerConfig
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceOptions
import com.bitmovin.player.api.source.TimelineReferencePoint
import com.bitmovin.player.samples.ads.ima.databinding.ActivityMainBinding

// These are IMA Sample Tags from https://developers.google.com/interactive-media-ads/docs/sdks/android/tags
private const val AD_SOURCE_1 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dredirecterror&nofb=1&correlator="
private const val AD_SOURCE_2 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator="
private const val AD_SOURCE_3 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator="
private const val AD_SOURCE_4 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dredirectlinear&correlator="

class MainActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create AdSources
        val firstAdSource = AdSource(AdSourceType.Ima, AD_SOURCE_1)
        val secondAdSource = AdSource(AdSourceType.Ima, AD_SOURCE_2)
        val thirdAdSource = AdSource(AdSourceType.Ima, AD_SOURCE_3)
        val fourthAdSource = AdSource(AdSourceType.Ima, AD_SOURCE_4)

        // Set up a pre-roll ad
        val preRoll = AdItem("pre", thirdAdSource)

        // Set up a mid-roll waterfalling ad at 10% of the content duration
        // NOTE: AdItems containing more than one AdSource, will be executed as waterfalling ad
        val midRoll = AdItem("10%", firstAdSource, secondAdSource)

        // Set up a post-roll ad
        val postRoll = AdItem("post", fourthAdSource)

        val sourceConfig = SourceConfig.fromUrl("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd")
        sourceConfig.options = SourceOptions(600.0, TimelineReferencePoint.Start)

        val vMapUrlNlzietMirko =
            "https://7b936.v.fwmrm.net/ad/g/1?nw=506166&resp=vmap1&prof=506166%3Asanoma_sbs_external_live&csid=nlziet_androidtv&caid=AlEkd473ZiI&vdur=1164.1600341&pvrn=673968&metr=1031&flag=%2Bfbad%2Bemcr%2Bslcb%2Bsltp%2Bamcb%2Bplay;talpa_consent=0&_fw_gdpr=0&app_name=nlziet&app_domain=nlzietnl;"

        val vMapUrlNlziet =
            "https://7b936.v.fwmrm.net/ad/g/1?caid=6kJ8xWlwVXZ&csid=NLZIETAndroidExoPlayer&vdur=2194.3200683&pvrn=432932&vprn=700853&_fw_gdpr=0&metr=1031&resp=vmap1&nw=506166&prof=506166%3Asanoma_sbs_external_live&flag=%2Bfbad%2Bemcr%2Bslcb%2Bsltp%2Bamcb%2Bplay;talpa_consent%3D0"
        val adsNlziet = AdItem(
            AdSource(
                AdSourceType.Ima,
                vMapUrlNlziet
            )
        )

        val vMapUrlTest =
            "https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/vmap_ad_samples&sz=640x480&cust_params=sample_ar%3Dpremidpostlongpod&ciu_szs=300x250&gdfp_req=1&ad_rule=1&output=vmap&unviewed_position_start=1&env=vp&impl=s&cmsid=496&vid=short_onecue&correlator="
        val adsSample = AdItem(AdSource(AdSourceType.Ima, vMapUrlTest))

        // Add the AdItems to the AdvertisingConfig
        val advertisingConfig = AdvertisingConfig(
            schedule = listOf(adsSample),
            adsManagerAvailableCallback = { adsManager ->
                adsManager.addAdEventListener {
                    Log.e("testing", "Event: $it")
                }
            },
            shouldPlayAdBreak = { adBreak ->
                Log.e("testing", "Checking shouldPlayAd")
                true
            },
        )

        // Create a new PlayerConfig containing the advertising config. Ads in the AdvertisingConfig will be scheduled automatically.
        val playerConfig = PlayerConfig(
            advertisingConfig = advertisingConfig
        )

        // Create new Player with our PlayerConfig
        val analyticsKey = "{ANALYTICS_LICENSE_KEY}"
        val player = Player(
            this,
            playerConfig,
            AnalyticsPlayerConfig.Enabled(AnalyticsConfig(analyticsKey)),
        )
        playerView = PlayerView(
            this, player,
        ).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
        playerView.keepScreenOn = true
        player.load(sourceConfig)
        player.play()

        // Add PlayerView to the layout
        binding.root.addView(playerView, 0)
    }

    override fun onStart() {
        super.onStart()
        playerView.onStart()
    }

    override fun onResume() {
        super.onResume()
        playerView.onResume()
    }

    override fun onPause() {
        playerView.onPause()
        super.onPause()
    }

    override fun onStop() {
        playerView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        playerView.onDestroy()
        super.onDestroy()
    }

}
