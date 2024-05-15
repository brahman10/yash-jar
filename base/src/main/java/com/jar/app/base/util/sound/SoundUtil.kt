package com.jar.app.base.util.sound

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.jar.app.core_base.util.BaseConstants.CDN_BASE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.net.URL

class SoundUtil(
    private val context: WeakReference<Context>,
    var isInDisableMode: Boolean
) : DefaultLifecycleObserver {

    private var currentPlaying: SoundType? = null
    private var wasPlayingAlwaysOnSound = false
    private var isAlreadyPrepared = false


    private val alwaysOnSoundMusicPlayer by lazy {
        MediaPlayer()
    }

    private var mediaPlayer: MediaPlayer = MediaPlayer()

    private suspend fun cacheAudioFile(soundType: SoundType): File {
        val fileName = getKeyName(soundType)
        val localFile = File(context.get()?.cacheDir, fileName)
        withContext(Dispatchers.IO) {
            if (!localFile.exists()) {
                try {
                    val url = URL(getSoundUrl(soundType))
                    val connection = url.openConnection()
                    val inputStream = connection.getInputStream()
                    val outputStream = FileOutputStream(localFile)
                    inputStream.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (ex: Exception) {
                    Timber.d(ex)
                }
            }
        }
        return localFile
    }
    private fun getKeyName(soundType: SoundType): String {
        return when (soundType) {
            is SoundType.CustomSound -> {
                return soundType.soundUrl.substringAfterLast("/")
            }
            else -> soundType.javaClass.name + ".mp3"
        }
    }

    private suspend fun resetMediaPlayer(soundType: SoundType, looping: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                mediaPlayer.reset()
                val localFile = cacheAudioFile(soundType)
                val context = context.get() ?: return@withContext
                mediaPlayer.apply {
                    setDataSource(context, localFile.toUri())
                    isLooping = looping
                    setVolume(1f, 1f)
                    prepare()
                    setOnErrorListener { _, _, _ ->
                        try {
                            mediaPlayer.stop()
                            mediaPlayer.reset()

                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                        true
                    }
                }
            } catch (e: Exception) {
                Timber.d(e)
            }
        }
    }

    suspend fun playSound(soundType: SoundType, looping: Boolean = false) {
        withContext(Dispatchers.IO) {
            try {
                if (currentPlaying == soundType) {
                    return@withContext
                }
                currentPlaying = soundType
                resetMediaPlayer(soundType, looping)
                mediaPlayer.start()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun stopCurrentTrack() {
        try {
            mediaPlayer.stop()
            currentPlaying = null
        } catch (e: Exception) {
            Timber.d(e)
        }
    }
    fun pauseTrack(){
        if(mediaPlayer.isPlaying){
            mediaPlayer.pause()
        }
    }
    fun resumeTrack(){
        if(mediaPlayer.isPlaying.not()){
            mediaPlayer.start()
        }
    }

    fun pauseAlwaysOnSound() {
        if (alwaysOnSoundMusicPlayer.isPlaying) {
            alwaysOnSoundMusicPlayer.pause()
        }
    }

    private suspend fun playAlwaysOnAudio() {
        withContext(Dispatchers.IO) {
            try {
                if (alwaysOnSoundMusicPlayer.isPlaying.not()) {
                    val localFile = cacheAudioFile(SoundType.ALWAYS_ON)
                    val context = context.get() ?: return@withContext
                    alwaysOnSoundMusicPlayer.apply {
                        setDataSource(context, localFile.toUri())
                        isLooping = true
                        setVolume(1f, 1f)
                        prepare()
                        start()
                    }
                }
            } catch (e: Exception) {
                Timber.d(e)
            }
        }
    }


    override fun onResume(owner: LifecycleOwner) {
        owner.lifecycleScope.launch {
            if (isInDisableMode.not() && isAlreadyPrepared.not()) {
                playAlwaysOnAudio()
                isAlreadyPrepared = true
            } else {
                alwaysOnSoundMusicPlayer.pause()
            }
        }
        super.onResume(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        try {
            if (alwaysOnSoundMusicPlayer.isPlaying) {
                wasPlayingAlwaysOnSound = true
            }
            pauseAll()
        } catch (e: Exception) {
            Timber.e(e)
        }
        super.onStop(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        releasePlayers()
        super.onDestroy(owner)
    }

    private fun pauseAll() {
        try {
            alwaysOnSoundMusicPlayer.pause()
            stopCurrentTrack()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun releasePlayers() {
        try {
            alwaysOnSoundMusicPlayer.release()
            mediaPlayer.release()
        } catch (ex: java.lang.Exception) {
            Timber.d(ex)
        }
    }

    private fun getSoundUrl(soundType: SoundType): String {
        return when (soundType) {
            SoundType.ALWAYS_ON -> "${CDN_BASE_URL}/spinsMusic/always_on_bg_music.mp3"
            SoundType.PULL_DOWN -> "${CDN_BASE_URL}/spinsMusic/pull_down_music.mp3"
            SoundType.BUTTON_RESET -> "${CDN_BASE_URL}/spinsMusic/Button_Reset.mp3"
            SoundType.SPIN_WHEEL -> "${CDN_BASE_URL}/spinsMusic/wheel_spin_music.mp3"
            SoundType.OH_NO -> "${CDN_BASE_URL}/spinsMusic/ohno_music.mp3"
            SoundType.JACKPOT_CELEBRATION -> "${CDN_BASE_URL}/spinsMusic/jackpot_celebration_sound.mp3"
            SoundType.CELEBRATION -> "${CDN_BASE_URL}/spinsMusic/spin_celebration.mp3"
            is SoundType.CustomSound -> soundType.soundUrl
        }
    }
}