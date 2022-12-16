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

package io.getstream.video.android.compose.ui.components.participants.internal

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.getstream.video.android.R
import io.getstream.video.android.call.state.CallAction
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.model.Call
import io.getstream.video.android.model.CallParticipantState
import io.getstream.video.android.model.ScreenSharingSession

/**
 * Represents the portrait screen sharing content.
 *
 * @param call The call containing state.
 * @param session Screen sharing session to render.
 * @param participants List of participants to render under the screen share track.
 * @param paddingValues Padding values from the parent.
 * @param modifier Modifier for styling.
 * @param onRender Handler when the video renders.
 */
@Composable
internal fun PortraitScreenSharingContent(
    call: Call,
    session: ScreenSharingSession,
    participants: List<CallParticipantState>,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    onRender: (View) -> Unit,
    onCallAction: (CallAction) -> Unit,
) {
    val sharingParticipant = session.participant

    Column(
        modifier = modifier
            .background(VideoTheme.colors.screenSharingBackground)
            .padding(paddingValues)
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(
                id = R.string.stream_screen_sharing_title,
                sharingParticipant.name.ifEmpty { sharingParticipant.id }
            ),
            color = VideoTheme.colors.textHighEmphasis,
            style = VideoTheme.typography.title3Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(16.dp))

        ScreenShareContent(
            call = call,
            session = session,
            onRender = onRender,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            isFullscreen = false,
            onCallAction = onCallAction
        )

        Spacer(modifier = Modifier.height(16.dp))

        ParticipantsRow(
            modifier = Modifier.height(125.dp),
            call = call,
            participants = participants
        )
    }
}
