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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.audio.SoundIndicator
import io.getstream.video.android.compose.ui.components.avatar.UserAvatar
import io.getstream.video.android.compose.ui.components.video.VideoRenderer
import io.getstream.video.android.model.Call
import io.getstream.video.android.model.CallParticipantState
import io.getstream.video.android.model.VideoTrack
import io.getstream.video.android.model.toUser

/**
 * Represents a single participant in a call.
 *
 * @param call The active call.
 * @param participant Participant to render.
 * @param modifier Modifier for styling.
 * @param labelPosition The position of the user audio state label.
 * @param isFocused If the participant is focused or not.
 * @param onRender Handler when the Video renders.
 */
@Composable
public fun CallParticipant(
    call: Call,
    participant: CallParticipantState,
    modifier: Modifier = Modifier,
    labelPosition: Alignment = Alignment.TopStart,
    isFocused: Boolean = false,
    onRender: (View) -> Unit = {}
) {
    val track = participant.track

    val containerModifier =
        if (isFocused) modifier.border(
            BorderStroke(
                3.dp,
                VideoTheme.colors.infoAccent
            )
        ) else modifier

    Box(modifier = containerModifier) {
        ParticipantVideo(
            call = call,
            participant = participant,
            track = track,
            onRender = onRender
        )

        ParticipantLabel(participant, labelPosition)
    }
}

@Composable
private fun ParticipantVideo(
    call: Call,
    participant: CallParticipantState,
    track: VideoTrack?,
    onRender: (View) -> Unit
) {
    if (track != null && track.video.enabled()) {
        VideoRenderer(
            call = call,
            videoTrack = track,
            onRender = onRender
        )
    } else {
        UserAvatar(
            shape = RectangleShape,
            user = participant.toUser()
        )
    }
}

@Composable
private fun BoxScope.ParticipantLabel(
    participant: CallParticipantState,
    labelPosition: Alignment
) {
    Row(
        modifier = Modifier
            .align(labelPosition)
            .padding(16.dp)
            .height(36.dp)
            .background(
                Color.DarkGray,
                shape = RoundedCornerShape(8.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val name = participant.name.ifEmpty {
            participant.id
        }
        Text(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            text = name,
            style = VideoTheme.typography.bodyBold,
            color = Color.White
        )

        SoundIndicator(participant.hasAudio, participant.audioLevel)
    }
}
