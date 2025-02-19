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

package io.getstream.video.android.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.log.streamLog
import io.getstream.video.android.API_KEY
import io.getstream.video.android.BuildConfig
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.datastore.delegate.StreamUserDataStore
import io.getstream.video.android.model.User
import io.getstream.video.android.token.StreamVideoNetwork
import io.getstream.video.android.token.TokenResponse
import io.getstream.video.android.util.StreamVideoInitHelper
import io.getstream.video.android.util.UserIdGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataStore: StreamUserDataStore,
) : ViewModel() {

    private val event: MutableSharedFlow<LoginEvent> = MutableSharedFlow()
    internal val uiState: SharedFlow<LoginUiState> = event
        .flatMapLatest { event ->
            when (event) {
                is LoginEvent.Loading -> flowOf(LoginUiState.Loading)
                is LoginEvent.GoogleSignIn -> flowOf(LoginUiState.GoogleSignIn)
                is LoginEvent.SignInInSuccess -> signInInSuccess(event.userId)
                else -> flowOf(LoginUiState.Nothing)
            }
        }.shareIn(viewModelScope, SharingStarted.Lazily, 0)

    fun handleUiEvent(event: LoginEvent) {
        viewModelScope.launch { this@LoginViewModel.event.emit(event) }
    }

    private fun signInInSuccess(email: String) = flow {
        // skip login if we are already logged in (use has navigated back)
        if (StreamVideo.isInstalled) {
            emit(LoginUiState.AlreadyLoggedIn)
        } else {
            try {
                val response = StreamVideoNetwork.tokenService.fetchToken(
                    userId = email,
                    apiKey = API_KEY,
                )

                // if we are logged in with Google account then read the data (demo app doesn't have
                // firebase login)
                val authFirebaseUser = FirebaseAuth.getInstance().currentUser
                val user = User(
                    id = response.userId,
                    name = authFirebaseUser?.displayName ?: "",
                    image = authFirebaseUser?.photoUrl?.toString() ?: "",
                    role = "admin",
                    custom = mapOf("email" to response.userId),
                )

                // Store the data in the demo app
                dataStore.updateUser(user)
                dataStore.updateUserToken(response.token)

                // Init the Video SDK with the data
                StreamVideoInitHelper.loadSdk(dataStore)

                emit(LoginUiState.SignInComplete(response))
            } catch (exception: Throwable) {
                emit(LoginUiState.SignInFailure(exception.message ?: "General error"))
                streamLog { "Failed to fetch token - cause: $exception" }
            }
        }
    }.flowOn(Dispatchers.IO)

    init {
        sigInInIfValidUserExist()
    }

    fun sigInInIfValidUserExist() {
        viewModelScope.launch {
            val user = dataStore.user.firstOrNull()
            if (user != null) {
                handleUiEvent(LoginEvent.Loading)
                if (!BuildConfig.BENCHMARK.toBoolean()) {
                    delay(10)
                    handleUiEvent(LoginEvent.SignInInSuccess(userId = user.id))
                }
            } else {
                // Production apps have an automatic guest login. Logging the user out
                // will just re-login automatically with a new random user ID.
                if (BuildConfig.FLAVOR == "production") {
                    handleUiEvent(LoginEvent.Loading)
                    handleUiEvent(
                        LoginEvent.SignInInSuccess(
                            UserIdGenerator.generateRandomString(upperCaseOnly = true),
                        ),
                    )
                }
            }
        }
    }
}

sealed interface LoginUiState {
    object Nothing : LoginUiState

    object Loading : LoginUiState

    object GoogleSignIn : LoginUiState

    object AlreadyLoggedIn : LoginUiState

    data class SignInComplete(val tokenResponse: TokenResponse) : LoginUiState

    data class SignInFailure(val errorMessage: String) : LoginUiState
}

sealed interface LoginEvent {
    object Nothing : LoginEvent

    object Loading : LoginEvent

    data class GoogleSignIn(val id: String = UUID.randomUUID().toString()) : LoginEvent

    data class SignInInSuccess(val userId: String) : LoginEvent
}
