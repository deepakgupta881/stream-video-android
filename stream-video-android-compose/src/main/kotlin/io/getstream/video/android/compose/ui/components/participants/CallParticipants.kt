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

package io.getstream.video.android.compose.ui.components.participants

import android.view.View
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.getstream.video.android.call.state.CallAction
import io.getstream.video.android.model.Call

/**
 * Renders all the CallParticipants, based on the number of people in a call and the call state.
 * Also takes into account if there are any screen sharing sessions active and adjusts the UI
 * accordingly.
 *
 * @param call The call that contains all the participants state and tracks.
 * @param modifier Modifier for styling.
 * @param paddingValues Padding within the parent.
 * @param isFullscreen If we're rendering a full screen activity.
 * @param onRender Handler when each of the Video views render their first frame.
 * @param onCallAction Handler when the user triggers a Call Control Action.
 */
@Composable
public fun CallParticipants(
    call: Call,
    onCallAction: (CallAction) -> Unit,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    isFullscreen: Boolean = false,
    onRender: (View) -> Unit = {}
) {
    val screenSharingSessions by call.screenSharingSessions.collectAsState(initial = emptyList())

    val screenSharing = screenSharingSessions.firstOrNull()

    if (screenSharing == null) {
        RegularCallParticipantsContent(call, modifier, paddingValues, onRender)
    } else {
        val participants by call.callParticipants.collectAsState()

        ScreenSharingCallParticipantsContent(
            call = call,
            session = screenSharing,
            participants = participants,
            modifier = modifier,
            paddingValues = paddingValues,
            onRender = onRender,
            isFullscreen = isFullscreen,
            onCallAction = onCallAction
        )
    }
}
