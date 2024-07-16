package com.bitmovin.samples.tv.playback.basic

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.bitmovin.analytics.api.AnalyticsConfig
import com.bitmovin.player.PlayerView
import com.bitmovin.player.api.PlaybackConfig
import com.bitmovin.player.api.Player
import com.bitmovin.player.api.PlayerConfig
import com.bitmovin.player.api.advertising.AdItem
import com.bitmovin.player.api.advertising.AdSource
import com.bitmovin.player.api.advertising.AdSourceType
import com.bitmovin.player.api.advertising.AdvertisingConfig
import com.bitmovin.player.api.analytics.AnalyticsPlayerConfig
import com.bitmovin.player.api.deficiency.ErrorEvent
import com.bitmovin.player.api.event.PlayerEvent
import com.bitmovin.player.api.event.SourceEvent
import com.bitmovin.player.api.event.on
import com.bitmovin.player.api.source.SourceConfig
import com.bitmovin.player.api.source.SourceOptions
import com.bitmovin.player.api.source.SourceType
import com.bitmovin.player.api.source.TimelineReferencePoint
import com.bitmovin.player.api.ui.PlayerViewConfig
import com.bitmovin.player.api.ui.UiConfig
import com.bitmovin.player.samples.tv.playback.basic.R
import com.bitmovin.player.samples.tv.playback.basic.databinding.ActivityMainBinding

private const val SEEKING_OFFSET = 10
private val TAG = MainActivity::class.java.simpleName


// These are IMA Sample Tags from https://developers.google.com/interactive-media-ads/docs/sdks/android/tags
private const val AD_SOURCE_1 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dredirecterror&nofb=1&correlator="
private const val AD_SOURCE_2 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator="
private const val AD_SOURCE_3 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator="
private const val AD_SOURCE_4 = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dredirectlinear&correlator="


class MainActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private lateinit var player: Player
    private lateinit var binding: ActivityMainBinding
    private var pendingSeekTarget: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch from splash screen to main theme when we are done loading
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializePlayer()
    }

    private fun initializePlayer() {
        // Initialize PlayerView from layout and attach a new Player instance
        val analyticsKey = "{ANALYTICS_LICENSE_KEY}"

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

//        val sourceConfig = SourceConfig("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd", SourceType.Dash)
        val sourceConfig = SourceConfig("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd", SourceType.Dash)
        sourceConfig.options = SourceOptions(300.0, TimelineReferencePoint.Start)


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
//            schedule = listOf(preRoll, midRoll, postRoll),
            adsManagerAvailableCallback = { adsManager ->
//                adsManager.addAdEventListener {
//                    Log.e("testing", "Event: $it")
//                }
            },
            shouldPlayAdBreak = { adBreak ->
                Log.e("testing", "Checking shouldPlayAd")
                false
            },
        )

        val playerConfig = PlayerConfig(
            advertisingConfig = advertisingConfig,
                playbackConfig = PlaybackConfig(isAutoplayEnabled = true)
        )

        player = Player(
            this,
            playerConfig,
            AnalyticsPlayerConfig.Enabled(AnalyticsConfig(analyticsKey)),
        )

        playerView = PlayerView(
            this,
            player,
            // Here a custom bitmovinplayer-ui.js is loaded which utilizes the Cast-UI as this
            // matches our needs here perfectly.
            // I.e. UI controls get shown / hidden whenever the Player API is called.
            // This is needed due to the fact that on Android TV no touch events are received
            PlayerViewConfig(
                uiConfig = UiConfig.WebUi(
                    jsLocation = "file:///android_asset/bitmovinplayer-ui.js",
                )
            )
        )
        playerView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        playerView.keepScreenOn = true
        binding.playerRootLayout.addView(playerView, 0)

        // Create a new SourceConfig. In this case we are loading a DASH source.
//        val sourceURL = "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd"

        player.load(sourceConfig)
    }

    override fun onResume() {
        super.onResume()

        playerView.onResume()
        addEventListener()
        player.play()
    }

    override fun onStart() {
        super.onStart()
        playerView.onStart()
    }

    override fun onPause() {
        removeEventListener()
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

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // This method is called on key down and key up, so avoid being called twice
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (handleUserInput(event.keyCode)) {
                return true
            }
        }

        // Make sure to return super.dispatchKeyEvent(event) so that any key not handled yet will work as expected
        return super.dispatchKeyEvent(event)
    }

    private fun handleUserInput(keycode: Int): Boolean {
        Log.d(TAG, "Keycode $keycode")
        return when (keycode) {
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER,
            KeyEvent.KEYCODE_SPACE,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                player.togglePlay()
                true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                player.play()
                true
            }
            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                player.pause()
                true
            }
            KeyEvent.KEYCODE_MEDIA_STOP -> {
                player.stopPlayback()
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                player.seekForward()
                true
            }
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_MEDIA_REWIND -> {
                player.seekBackward()
                true
            }
            else -> return false
        }
    }

    private fun addEventListener() {
        player.on<PlayerEvent.Error>(::onErrorEvent)
        player.on<SourceEvent.Error>(::onErrorEvent)
        player.on(::onSeeked)
    }

    private fun removeEventListener() {
        player.off(::onErrorEvent)
        player.off(::onSeeked)
    }

    private fun onErrorEvent(errorEvent: ErrorEvent) {
        Log.e(TAG, "An Error occurred (${errorEvent.code}): ${errorEvent.message}")
    }

    private fun onSeeked(event: PlayerEvent.Seeked) {
        pendingSeekTarget = null
    }

    private fun Player.seekForward() {
        val seekTarget = (pendingSeekTarget ?: currentTime) + SEEKING_OFFSET
        pendingSeekTarget = seekTarget
        seek(seekTarget)
    }

    private fun Player.seekBackward() {
        val seekTarget = (pendingSeekTarget ?: currentTime) - SEEKING_OFFSET
        pendingSeekTarget = seekTarget
        seek(seekTarget)
    }
}

private fun Player.togglePlay() = if (isPlaying) pause() else play()

private fun Player.stopPlayback() {
    pause()
    seek(0.0)
}
