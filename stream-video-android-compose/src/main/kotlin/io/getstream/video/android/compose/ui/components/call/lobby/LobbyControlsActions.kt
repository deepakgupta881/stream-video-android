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

package io.getstream.video.android.compose.ui.components.call.lobby

import android.content.res.Configuration
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.getstream.video.android.common.viewmodel.CallViewModel
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleMicrophoneAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleSpeakerphoneAction
import io.getstream.video.android.core.call.state.CallAction
import io.getstream.video.android.core.call.state.CallDeviceState

/**
 * Builds the default set of Lobby Control actions based on the [CallDeviceState].
 *
 * @return [List] of call control actions that the user can trigger.
 */
@Composable
public fun buildDefaultLobbyControlActions(
    callViewModel: CallViewModel,
    onCallAction: (CallAction) -> Unit
): List<@Composable () -> Unit> {

    val callDeviceState by callViewModel.callDeviceState.collectAsStateWithLifecycle()

    return buildDefaultLobbyControlActions(
        callDeviceState = callDeviceState,
        onCallAction = onCallAction
    )
}

/**
 * Builds the default set of Lobby Control actions based on the [callDeviceState].
 *
 * @param callDeviceState Information of whether microphone, speaker and camera are on or off.
 * @return [List] of call control actions that the user can trigger.
 */
@Composable
public fun buildDefaultLobbyControlActions(
    callDeviceState: CallDeviceState,
    onCallAction: (CallAction) -> Unit
): List<@Composable () -> Unit> {

    val orientation = LocalConfiguration.current.orientation

    val modifier = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        Modifier.size(VideoTheme.dimens.callControlButtonSize)
    } else {
        Modifier.size(VideoTheme.dimens.landscapeCallControlButtonSize)
    }

    return listOf(
        {
            ToggleMicrophoneAction(
                modifier = Modifier.size(48.dp),
                isMicrophoneEnabled = callDeviceState.isMicrophoneEnabled,
                onCallAction = onCallAction
            )
        },
        {
            ToggleCameraAction(
                modifier = Modifier.size(48.dp),
                isCameraEnabled = callDeviceState.isCameraEnabled,
                onCallAction = onCallAction
            )
        },
        {
            ToggleSpeakerphoneAction(
                modifier = modifier,
                isSpeakerphoneEnabled = callDeviceState.isSpeakerphoneEnabled,
                onCallAction = onCallAction
            )
        }
    )
}
