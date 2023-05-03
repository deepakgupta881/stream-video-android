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

package io.getstream.video.android.dogfooding.ui.join

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.avatar.UserAvatar
import io.getstream.video.android.core.user.UserPreferencesManager
import io.getstream.video.android.dogfooding.R
import io.getstream.video.android.dogfooding.ui.theme.Colors
import io.getstream.video.android.dogfooding.ui.theme.StreamButton

@Composable
fun CallJoinScreen(
    callJoinViewModel: CallJoinViewModel = hiltViewModel(),
    navigateToCallPreview: () -> Unit,
    navigateUpToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CallJoinHeader(navigateUpToLogin = navigateUpToLogin)

        CallJoinBody(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
private fun CallJoinHeader(
    callJoinViewModel: CallJoinViewModel = hiltViewModel(),
    navigateUpToLogin: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            color = Color.White,
            text = callJoinViewModel.user?.id.orEmpty(),
            maxLines = 1,
            fontSize = 16.sp
        )

        val context = LocalContext.current
        StreamButton(
            modifier = Modifier.width(125.dp),
            text = stringResource(id = R.string.sign_out),
            onClick = {
                callJoinViewModel.signOut(context)
                navigateUpToLogin.invoke()
            }
        )
    }
}

@Composable
private fun CallJoinBody(
    modifier: Modifier,
    callJoinViewModel: CallJoinViewModel = hiltViewModel(),
) {
    val user = callJoinViewModel.user

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user != null) {
            UserAvatar(
                modifier = Modifier.size(100.dp),
                user = user,
            )

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                modifier = Modifier.padding(horizontal = 30.dp),
                text = "Welcome, ${user.name}",
                color = Color.White,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.join_description),
            color = Colors.description,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
        )

        Spacer(modifier = Modifier.height(42.dp))

        StreamButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 35.dp),
            text = stringResource(id = R.string.start_a_new_call),
            onClick = { callJoinViewModel.startNewCall() }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 35.dp),
            text = stringResource(id = R.string.call_id_number),
            color = Colors.description,
            fontSize = 13.sp,
        )

        Spacer(modifier = Modifier.height(4.dp))

        var callId by remember { mutableStateOf("default:NnXAIvBKE4Hy") }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 35.dp),
        ) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .border(
                        BorderStroke(1.dp, Color(0xFF4C525C)),
                        RoundedCornerShape(6.dp)
                    ),
                shape = RoundedCornerShape(6.dp),
                value = callId,
                onValueChange = { callId = it },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = VideoTheme.colors.textHighEmphasis,
                    focusedLabelColor = VideoTheme.colors.primaryAccent,
                    unfocusedIndicatorColor = Colors.secondBackground,
                    focusedIndicatorColor = Colors.secondBackground,
                    backgroundColor = Colors.secondBackground
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email
                )
            )

            StreamButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxHeight(),
                onClick = { callJoinViewModel.joinCall(callId) },
                text = stringResource(id = R.string.join_call)
            )
        }
    }
}

@Preview
@Composable
private fun CallJoinScreenPreview() {
    VideoTheme {
        val context = LocalContext.current
        val preference = UserPreferencesManager.initialize(context)
        CallJoinScreen(
            callJoinViewModel = CallJoinViewModel(preference),
            navigateToCallPreview = {},
            navigateUpToLogin = {}
        )
    }
}
