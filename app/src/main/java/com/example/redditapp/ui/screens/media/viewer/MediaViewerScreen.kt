package com.example.redditapp.ui.screens.media.viewer

import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem.fromUri
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.example.redditapp.Constants.Companion.REDDIT_API
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun MediaViewerScreen(
    onDismissRequest: () -> Unit,
    mediaUrl: String?,
    audioUrl: String?,
    mediaType: MediaTypes?,
    gallery: List<String>?,
    height: Int?,
    width: Int?,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {

            if (mediaUrl != null) {
                if (mediaType == MediaTypes.IMAGE) {
                    val imageLoader = ImageLoader.Builder(LocalContext.current)
                        .components { add(GifDecoder.Factory()) }.build()
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = mediaUrl)
                                .build(), imageLoader = imageLoader
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .background(color = Color.Black)
                            .clickable {
                                onDismissRequest()
                            }
                    )
                }
                if (mediaType == MediaTypes.VIDEO) {
                    val context = LocalContext.current
                    val dataSourceFactory = DefaultHttpDataSource.Factory()
                    val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(fromUri(mediaUrl))
                    var mediaSrc: MediaSource = videoSource
                    if (audioUrl != null) {
                        val audioSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(fromUri(audioUrl))
                        mediaSrc = MergingMediaSource(videoSource, audioSource)
                    }
                    val viewPlayer = remember {
                        ExoPlayer.Builder(context).build().apply {
                            setMediaSource(mediaSrc)
                            prepare()
                            playWhenReady = false
                        }
                    }

                    val videoIsPlaying = remember{mutableStateOf(false)}

                    if (mediaSrc != null) {
                        DisposableEffect(key1 = Unit) {
                            val listener = object : Player.Listener {
                                //                                override fun onIsPlayingChanged(isPlaying: Boolean) {
//                                    super.onIsPlayingChanged(isPlaying)
//                                    if(isPlaying) {
//                                        viewPlayer.pause()
//                                    }
//                                    Log.d(REDDIT_API, "isplaying changed")
//                                }
                                override fun onPlaybackStateChanged(playbackState: Int) {
//                                    super.onPlaybackStateChanged(playbackState)
                                    if (playbackState == Player.STATE_READY && !videoIsPlaying.value) {
                                        Log.d(REDDIT_API, "isplaying changed $playbackState")
                                        viewPlayer.pause()
                                        viewPlayer.seekTo(0)
                                        viewPlayer.play()
                                        videoIsPlaying.value = true
                                    }
                                }
                            }
                            viewPlayer.addListener(listener)
                            onDispose {
                                viewPlayer.removeListener(listener)
                                viewPlayer.release()
                            }
                        }
                        AndroidView(
                            factory = {
                                PlayerView(context).apply {
                                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                                    player = viewPlayer
                                    layoutParams =
                                        FrameLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT
                                        )
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            if (gallery != null) {
                val coroutineScope = rememberCoroutineScope()
                val imageLoader = ImageLoader.Builder(LocalContext.current)
                    .components { add(GifDecoder.Factory()) }.build()
                val numItems = gallery.size
                var pagerState = rememberPagerState(
                    pageCount = { numItems }
                )
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronLeft,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(pagerState.currentPage - 1)
                                }
                            })
                        Text(text = "${pagerState.currentPage + 1}/${numItems}")
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(pagerState.currentPage + 1)
                                }
                            })
                    }
                    HorizontalPager(state = pagerState) { page ->
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(data = gallery[page])
                                    .build(), imageLoader = imageLoader
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.Black)
                                .clickable { onDismissRequest() }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MediaViewerScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray)
    ) {
        MediaViewerScreen(
            {},
            "https://v.redd.it/jk90e6z0yb1/DASH_1080.mp4?source=fallback",
            "https://v.redd.it/jo2fyf21dpxb1/DASH_AUDIO_128.mp4",
            MediaTypes.VIDEO,
            emptyList<String>(),
            1920,
            1080,
        )
    }
}