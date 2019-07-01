package com.ideal.vox.activity

import android.os.Bundle


import android.widget.Toast
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.gson.JsonObject
import com.ideal.vox.R
import com.ideal.vox.retrofitManager.ResponseListener
import com.ideal.vox.utils.Const
import kotlinx.android.synthetic.main.activity_test.*
import retrofit2.Call

class YtDemoActivity : BaseActivity(), YouTubePlayer.OnInitializedListener, YouTubePlayer.PlayerStateChangeListener {

    private val RECOVERY_DIALOG_REQUEST: Int = 123
    private var linkCall: Call<JsonObject>? = null
    private var ytLink: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        skipTV.setOnClickListener { finish() }

        linkCall = apiInterface.ytDemoLink()
        apiManager.makeApiCall(linkCall!!, this)

    }

    override fun onSuccess(call: Call<*>, payload: Any) {
        super.onSuccess(call, payload)
        val jsonObj = payload as JsonObject
        ytLink = jsonObj["youtube_link"].asString

//        ytLink = "TGuOL5aAPKA"

        val youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .replace(R.id.youtube_fragment, youTubePlayerFragment)
                .commit()
        youTubePlayerFragment.initialize(Const.API_KEY, this)
    }


    override fun onError(call: Call<*>, statusCode: Int, errorMessage: String, responseListener: ResponseListener) {
        finish()
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider?, player: YouTubePlayer?, wasRestored: Boolean) {
        player?.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS)
//        player?.setFullscreen(true)
        player?.setPlayerStateChangeListener(this)
        if (!wasRestored)
            player?.loadVideo(ytLink)
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider?, errorReason: YouTubeInitializationResult) {
        if (errorReason.isUserRecoverableError) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
        } else {
            val errorMessage = String.format("Error Player", errorReason.toString())
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onError(p0: YouTubePlayer.ErrorReason?) {
    }

    override fun onAdStarted() {
    }

    override fun onLoading() {
    }

    override fun onVideoStarted() {
    }

    override fun onLoaded(p0: String?) {
    }

    override fun onVideoEnded() {
        finish()
    }
}