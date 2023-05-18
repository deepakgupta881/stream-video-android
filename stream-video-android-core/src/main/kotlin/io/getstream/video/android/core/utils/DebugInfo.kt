/*
 * Copyright (c) 2014-2023 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.video.android.core.utils

import io.getstream.log.taggedLogger
import io.getstream.video.android.core.StreamVideoImpl
import io.getstream.video.android.core.dispatchers.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal data class Timer(val name: String, val start: Long) {
    var end: Long = 0
    var duration: Long = 0
    var splits: List<Pair<String, Long>> = mutableListOf<Pair<String, Long>>()
    var durations: List<Pair<String, Long>> = mutableListOf<Pair<String, Long>>()

    fun split(s: String) {
        val now = System.currentTimeMillis()
        val last = splits.lastOrNull()?.second ?: start
        splits += s to now
        durations += s to (now - last)
    }

    fun finish(s: String? = null): Long {
        s?.let {
            split(s)
        }
        end = System.currentTimeMillis()
        duration = end - start
        return duration
    }
}

/**
 * Handy helper gathering all relevant debug information
 */
internal class DebugInfo(val client: StreamVideoImpl) {
    val scope = CoroutineScope(DispatcherProvider.IO)

    private val logger by taggedLogger("DebugInfo")

    // timers to help track performance issues in prod
    val timers = mutableListOf<Timer>()
    // last 20 events

    // phone type
    val phoneModel = android.os.Build.MODEL

    // android version
    val version = android.os.Build.VERSION.SDK_INT



    // how many times the network dropped

    // how often the sockets reconnected

    // supported codecs
    // resolution
    val resolution by lazy {  }
    val availableResolutions by lazy {  }

    init {
        if (client.developmentMode) {
            scope.launch {
                while (true) {
                    delay(20000)
                    log()
                }
            }
        }
    }

    fun log() {
        val call = client.state.activeCall.value
        val sessionId = call?.sessionId
        val subscriber = call?.session?.subscriber
        val publisher = call?.session?.publisher
        val resolution = call?.camera?.resolution?.value
        val availableResolutions = call?.camera?.availableResolutions?.value
        val maxResolution = availableResolutions?.maxBy { it.width * it.height }

        val localSdp = publisher?.localSdp
        val remoteSdp = publisher?.remoteSdp
        val publisherIce = publisher?.connection?.iceConnectionState()
        val subIce = subscriber?.connection?.iceConnectionState()

        val videoTrackState = call?.mediaManager?.videoTrack?.state()
        val coordinatorSocket = client.socketImpl.connectionState.value.javaClass.name
        val sfuSocket =
            call?.session?.sfuConnectionModule?.sfuSocket?.connectionState?.value?.javaClass?.name

        // good spot to attach your debugger

        logger.i { "Debug info $phoneModel running android $version" }
        logger.i { "Active call is ${call?.cid}, session id $sessionId" }
        logger.i { "video quality: current resolution $resolution max resolution for camera is $maxResolution" }
        logger.i { "Coordinator socket: $coordinatorSocket, SFU socket: $sfuSocket Subscriber: $publisherIce Publisher: $subIce" }
        logger.i { "Performance details" }
        timers.forEach {
            logger.i { "${it.name} took ${it.duration}" }
            it.durations.forEach { (s, t) ->
                logger.i { " - ${it.name}:$s took $t" }
            }
        }
        publisher?.let {
            val stats = it.getStats().value
            logger.i { "Publisher stats. video quality: $stats" }
        }
    }

    fun listCodecs() {
        // see https://developer.android.com/reference/kotlin/android/media/MediaCodecInfo
    }

    fun trackTime(s: String): Timer {
        val timer = Timer(s, System.currentTimeMillis())
        timers += timer
        return timer
    }
}
