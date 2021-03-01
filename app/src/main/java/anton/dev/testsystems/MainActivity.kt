package anton.dev.testsystems

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import anton.dev.testsystems.databinding.ActivityMainBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout


class MainActivity : AppCompatActivity() {

    private val url =
        "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/master.m3u8"
    private lateinit var binding: ActivityMainBinding
    private lateinit var player: SimpleExoPlayer
    private lateinit var bFullScreen: ImageView
    private var isFullScreen = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        bFullScreen = binding.playerView.findViewById(R.id.exo_fullscreen_icon)

        bFullScreen.setOnClickListener {
            if (isFullScreen) {
                setOrientationPortrait()
            } else {
                setOrientationFullScreen()
            }
        }
        initPlayer()
    }

    private fun setOrientationPortrait() {
        bFullScreen.setImageResource(R.drawable.ic_fullscreen_open)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

        if (supportActionBar != null) {
            supportActionBar!!.show()
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val params = binding.playerView.layoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = (200 * applicationContext.resources.displayMetrics.density).toInt()
        binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
        binding.playerView.layoutParams = params
        isFullScreen = false
    }

    private fun setOrientationFullScreen() {
        bFullScreen.setImageResource(R.drawable.ic_fullscreen_close)

        Handler().postDelayed({
            hideUiMode()
        }, 2_000)


        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val params = binding.playerView.layoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        binding.playerView.layoutParams = params
        binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

        isFullScreen = true
    }

    private fun hideUiMode() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE)

        binding.playerView.hideController()

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
    }

    private fun initPlayer() {
        player = SimpleExoPlayer.Builder(applicationContext).build()
        binding.playerView.player = player
        binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun onPause() {
        super.onPause()
        player.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> setOrientationPortrait()
            Configuration.ORIENTATION_LANDSCAPE -> setOrientationFullScreen()
            else -> throw RuntimeException()
        }
    }

}

