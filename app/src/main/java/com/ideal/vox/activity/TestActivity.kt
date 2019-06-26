package com.ideal.vox.activity

import android.os.Bundle
import android.widget.Toast
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.ideal.vox.R


class TestActivity : BaseActivity(), YouTubePlayer.OnInitializedListener {
    override fun onInitializationSuccess(provider: YouTubePlayer.Provider?, player: YouTubePlayer?, wasRestored: Boolean) {
        player?.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS)
        player?.setFullscreen(true)
        if (!wasRestored)
            player?.loadVideo("NzpkclSyDNs")
    }

    private val RECOVERY_DIALOG_REQUEST: Int = 123

    override fun onInitializationFailure(provider: YouTubePlayer.Provider?, errorReason: YouTubeInitializationResult) {
        if (errorReason.isUserRecoverableError) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
        } else {
            val errorMessage = String.format("Error Player", errorReason.toString())
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_test)
        val youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .replace(R.id.youtube_fragment, youTubePlayerFragment)
                .commit()
        youTubePlayerFragment.initialize("AIzaSyAY1cezRA0mbLqSR2g6tVR7xdDKkJme3Kk", this)
    }
}