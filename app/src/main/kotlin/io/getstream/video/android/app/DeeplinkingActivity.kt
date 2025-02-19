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

package io.getstream.video.android.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.getstream.log.taggedLogger
import io.getstream.video.android.app.ui.MainActivity
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.model.StreamCallId

class DeeplinkingActivity : ComponentActivity() {

    private val logger by taggedLogger("Call:DeeplinkView")

    override fun onCreate(savedInstanceState: Bundle?) {
        logger.d { "[onCreate] savedInstanceState: $savedInstanceState" }
        super.onCreate(savedInstanceState)

        setContent {
            VideoTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(VideoTheme.colors.appBackground),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = VideoTheme.colors.primaryAccent,
                    )
                }
            }
        }

        val data: Uri = intent?.data ?: return
        val callId = data.toString().split("id=").lastOrNull() ?: return

        logger.d { "Action: ${intent?.action}" }
        logger.d { "Data: ${intent?.data}" }

        joinCall(callId)
    }

    private fun joinCall(cid: String) {
        val callId = StreamCallId(type = "default", id = cid)
        val intent = MainActivity.createIntent(
            context = this@DeeplinkingActivity,
            callId = callId,
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
