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

package io.getstream.video.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.result.Result
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.compose.ui.components.call.ringing.RingingCallContent
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.call.state.AcceptCall
import io.getstream.video.android.core.call.state.CallAction
import io.getstream.video.android.core.call.state.CancelCall
import io.getstream.video.android.core.call.state.DeclineCall
import io.getstream.video.android.core.call.state.LeaveCall
import io.getstream.video.android.core.call.state.ToggleCamera
import io.getstream.video.android.core.call.state.ToggleMicrophone
import io.getstream.video.android.core.call.state.ToggleSpeakerphone
import io.getstream.video.android.datastore.delegate.StreamUserDataStore
import io.getstream.video.android.model.mapper.isValidCallId
import io.getstream.video.android.model.mapper.toTypeAndId
import io.getstream.video.android.util.StreamVideoInitHelper
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class RingCallActivity : ComponentActivity() {

    @Inject
    lateinit var dataStore: StreamUserDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            // Not necessary if you initialise the SDK in Application.onCreate()
            StreamVideoInitHelper.loadSdk(dataStore = dataStore)

            // Create Call ID if it wasn't supplied by Intent
            val callId: String = intent.getStringExtra(EXTRA_CID)
                ?: "default:${UUID.randomUUID()}"

            val (type, id) = if (callId.isValidCallId()) {
                callId.toTypeAndId()
            } else {
                "default" to callId
            }

            // Create call object
            val call = StreamVideo.instance().call(type, id)

            // Get list of members
            val members: List<String> = intent.getStringArrayExtra(EXTRA_MEMBERS_ARRAY)?.asList() ?: emptyList()

            // You must add yourself as member too
            val membersWithMe = members.toMutableList().apply { add(call.user.id) }

            // Ring the members
            val result = call.create(ring = true, memberIds = membersWithMe)

            if (result is Result.Failure) {
                // Failed to recover the current state of the call
                // TODO: Automaticly call this in the SDK?
                Log.e("RingCallActivity", "Call.create failed ${result.value}")
                Toast.makeText(
                    this@RingCallActivity,
                    "Failed get call status (${result.value.message})",
                    Toast.LENGTH_SHORT,
                ).show()
                finish()
            }

            setContent {
                VideoTheme {
                    val onCallAction: (CallAction) -> Unit = { callAction ->
                        when (callAction) {
                            is ToggleCamera -> call.camera.setEnabled(callAction.isEnabled)
                            is ToggleMicrophone -> call.microphone.setEnabled(callAction.isEnabled)
                            is ToggleSpeakerphone -> call.speaker.setEnabled(callAction.isEnabled)
                            is LeaveCall -> {
                                call.leave()
                                finish()
                            }
                            is DeclineCall -> {
                                // Not needed. this activity is only used for outgoing calls.
                            }
                            is CancelCall -> {
                                lifecycleScope.launch {
                                    val test = call.reject()
                                    call.leave()
                                    finish()
                                }
                            }
                            is AcceptCall -> {
                                lifecycleScope.launch {
                                    call.accept()
                                    call.join()
                                }
                            }

                            else -> Unit
                        }
                    }

                    RingingCallContent(
                        modifier = Modifier.background(color = VideoTheme.colors.appBackground),
                        call = call,
                        onBackPressed = {
                            call.leave()
                            finish()
                        },
                        onAcceptedContent = {
                            CallContent(
                                modifier = Modifier.fillMaxSize(),
                                call = call,
                                onCallAction = onCallAction,
                            )
                        },
                        onRejectedContent = {
                            call.leave()
                            finish()
                        },
                        onCallAction = onCallAction,
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_CID: String = "EXTRA_CID"
        const val EXTRA_MEMBERS_ARRAY: String = "EXTRA_MEMBERS_ARRAY"

        @JvmStatic
        fun createIntent(
            context: Context,
            callId: String? = null,
            members: List<String>,
        ): Intent {
            return Intent(context, RingCallActivity::class.java).apply {
                putExtra(EXTRA_CID, callId)
                putExtra(EXTRA_MEMBERS_ARRAY, members.toTypedArray())
            }
        }
    }
}
