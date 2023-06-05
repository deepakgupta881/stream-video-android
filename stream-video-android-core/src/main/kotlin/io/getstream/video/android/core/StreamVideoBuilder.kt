/*
 * Copyright (c) 2014-2022 Stream.io Inc. All rights reserved.
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

package io.getstream.video.android.core

import android.app.Notification
import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import com.jakewharton.threetenabp.AndroidThreeTen
import io.getstream.android.push.PushDeviceGenerator
import io.getstream.log.StreamLog
import io.getstream.log.android.AndroidStreamLogger
import io.getstream.video.android.core.dispatchers.DispatcherProvider
import io.getstream.video.android.core.filter.AudioFilter
import io.getstream.video.android.core.filter.VideoFilter
import io.getstream.video.android.core.internal.module.ConnectionModule
import io.getstream.video.android.core.logging.LoggingLevel
import io.getstream.video.android.datastore.delegate.StreamUserDataStore
import io.getstream.video.android.model.ApiKey
import io.getstream.video.android.model.User
import io.getstream.video.android.model.UserToken
import io.getstream.video.android.model.UserType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * The StreamVideoBuilder is used to create a new instance of the StreamVideoClient.
 *
 * @sample
 * val client = StreamVideoBuilder(
 *      context = context,
 *      apiKey = apiKey,
 *      geo = GEO.GlobalEdgeNetwork,
 *      user,
 *      token,
 *      loggingLevel = LoggingLevel.BODY
 *  )
 *
 */
public class StreamVideoBuilder @JvmOverloads constructor(
    context: Context,
    /** Your Stream API Key, you can find it in the dashboard */
    private val apiKey: ApiKey,
    /** Your GEO routing policy, supports geofencing for privacy concerns */
    private val geo: GEO = GEO.GlobalEdgeNetwork,
    /** The user object, can be a regular user, guest user or anonymous */
    private var user: User,
    /** The token for this user generated using your API secret on your server */
    private val token: UserToken = "",
    /** If a token is expired, the token provider makes a request to your backend for a new token */
    private val tokenProvider: (suspend (error: Throwable?) -> String)? = null,
    /** Logging level */
    private val loggingLevel: LoggingLevel = LoggingLevel(),
    /** Enable push notifications if you want to receive calls etc */
    private val enablePush: Boolean = false,
    /** Support for different push providers */
    private val pushDeviceGenerators: List<PushDeviceGenerator> = emptyList(),
    /** Overwrite the default notification logic for incoming calls */
    private val ringNotification: ((call: Call) -> Notification?)? = null,
    /** Audio filters enable you to add custom effects to your audio before its send to the server */
    private val audioFilters: List<AudioFilter> = emptyList(),
    /** Video filters enable you to change the video before it's send. */
    private val videoFilters: List<VideoFilter> = emptyList(),
    /** Connection timeout in seconds */
    private val connectionTimeoutInMs: Long = 10000,
) {
    private val context: Context = context.applicationContext

    /** URL overwrite to allow for testing against a local instance of video */
    private var videoDomain: String = "video.stream-io-api.com"

    val scope = CoroutineScope(DispatcherProvider.IO)

    public fun build(): StreamVideo {
        val lifecycle = ProcessLifecycleOwner.get().lifecycle

        if (apiKey.isBlank()) {
            throw IllegalArgumentException("The API key can not be empty")
        }

        if (token.isBlank() && tokenProvider == null && user.type == UserType.Authenticated) {
            throw IllegalArgumentException(
                "Either a user token or a token provider must be provided"
            )
        }

        if (user.type == UserType.Authenticated && user.id.isEmpty()) {
            throw IllegalArgumentException(
                "Please specify the user id for authenticated users"
            )
        } else if (user.type == UserType.Anonymous && user.id.isEmpty()) {
            val randomId = UUID.randomUUID().toString()
            user = user.copy(id = "anon-$randomId")
        }

        // initializes
        StreamLog.install(AndroidStreamLogger())
        StreamLog.setValidator { priority, _ -> priority.level >= loggingLevel.priority.level }
        AndroidThreeTen.init(context)

        val dataStore = if (!StreamUserDataStore.isInstalled) {
            StreamUserDataStore.install(context, scope=scope)
        } else {
            StreamUserDataStore.instance()
        }

        scope.launch {
            dataStore.updateUser(user)
            dataStore.updateApiKey(apiKey)
            dataStore.updateUserToken(token)
        }

        // This connection module class exposes the connections to the various retrofit APIs
        val connectionModule = ConnectionModule(
            context = context,
            scope = scope,
            videoDomain = videoDomain,
            connectionTimeoutInMs = connectionTimeoutInMs,
            loggingLevel = loggingLevel,
            user = user,
            apiKey = apiKey,
            userToken = token
        )

        // create the client

        val client = StreamVideoImpl(
            context = context,
            _scope = scope,
            user = user,
            dataStore = dataStore,
            tokenProvider = tokenProvider,
            loggingLevel = loggingLevel,
            lifecycle = lifecycle,
            connectionModule = connectionModule,
            pushDeviceGenerators = pushDeviceGenerators
        )

        scope.launch {
            // addDevice for push
            if (enablePush && user.type == UserType.Authenticated) {
                client.registerPushDevice()
            }
        }
        if (user.type == UserType.Guest) {
            connectionModule.updateAuthType("anonymous")
            client.setupGuestUser(user)
        } else if (user.type == UserType.Anonymous) {
            connectionModule.updateAuthType("anonymous")
        }

        // installs Stream Video instance
        StreamVideo.install(client)

        return client
    }
}

sealed class GEO {
    /** Run calls over our global edge network, this is the default and right for most applications */
    object GlobalEdgeNetwork : GEO()
}

sealed class TokenType {
    /** A user token */
    object User : TokenType()

    /** A call specific token */
    object Call : TokenType()
}
