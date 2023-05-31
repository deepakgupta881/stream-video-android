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

package io.getstream.video.android.compose.ui.components.call.renderer.internal

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.getstream.video.android.compose.ui.components.call.renderer.CallSingleVideoRenderer
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.ParticipantState

/**
 * Renders call participants based on the number of people in a call.
 *
 * @param call The state of the call.
 * @param modifier Modifier for styling.
 * @param parentSize The size of the parent.
 */
@Composable
internal fun BoxScope.OrientationVideoRenderer(
    modifier: Modifier = Modifier,
    call: Call,
    parentSize: IntSize = IntSize(0, 0),
    videoRenderer: @Composable (
        modifier: Modifier,
        call: Call,
        participant: ParticipantState,
        isFocused: Boolean
    ) -> Unit = { videoModifier, videoCall, videoParticipant, videoIsFocused ->
        CallSingleVideoRenderer(
            modifier = videoModifier,
            call = videoCall,
            participant = videoParticipant,
            isFocused = videoIsFocused,
        )
    },
) {
    val primarySpeaker by call.state.dominantSpeaker.collectAsStateWithLifecycle()
    val participants by call.state.participants.collectAsStateWithLifecycle()
    val sortedParticipants by call.state.sortedParticipants.collectAsStateWithLifecycle()
    val callParticipants by remember(participants) {
        derivedStateOf {
            if (sortedParticipants.size > 6) {
                sortedParticipants
            } else {
                participants
            }
        }
    }

    val orientation = LocalConfiguration.current.orientation

    if (orientation == ORIENTATION_LANDSCAPE) {
        LandscapeVideoRenderer(
            call = call,
            primarySpeaker = primarySpeaker,
            callParticipants = callParticipants,
            modifier = modifier,
            parentSize = parentSize,
            videoRenderer = videoRenderer,
        )
    } else {
        PortraitVideoRenderer(
            call = call,
            primarySpeaker = primarySpeaker,
            callParticipants = callParticipants,
            modifier = modifier,
            parentSize = parentSize,
            videoRenderer = videoRenderer,
        )
    }
}
