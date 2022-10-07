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

package io.getstream.video.android.app.ui.call

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.getstream.video.android.R
import io.getstream.video.android.app.VideoApp
import io.getstream.video.android.app.ui.call.content.VideoCallContent
import io.getstream.video.android.model.CallSettings
import io.getstream.video.android.model.IceServer
import io.getstream.video.android.model.IceServerConfig
import io.getstream.video.android.ui.ParticipantContentView
import io.getstream.video.android.ui.ParticipantItemView
import io.getstream.video.android.viewmodel.CallViewModel
import io.getstream.video.android.viewmodel.CallViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class CallActivity : AppCompatActivity() {

    private val factory by lazy {
        CallViewModelFactory(VideoApp.streamCalls, VideoApp.credentialsProvider)
    }

    private val callViewModel by viewModels<CallViewModel>(factoryProducer = { factory })

    @RequiresApi(M)
    private val permissionsContract = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val missing = getMissingPermissions()
        val deniedCamera = !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
        val deniedMicrophone =
            !shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)

        when {
            missing.isNotEmpty() && !deniedCamera && !deniedMicrophone -> requestPermissions(missing)
            isGranted -> startVideoFlow()
            deniedCamera || deniedMicrophone -> showPermissionsDialog()
            else -> {
                checkPermissions()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setupViews() // XML variant

        setContent {
            VideoCallContent(callViewModel, onLeaveCall = ::leaveCall)
        }
    }

    private fun setupViews() {
        setContentView(R.layout.activity_call)

        lifecycleScope.launch {
            val view = findViewById<ParticipantContentView>(R.id.participantContent)

            callViewModel.callState.filterNotNull().collectLatest { room ->
                Log.d("RoomState", room.toString())
                room.callParticipants.collectLatest { participants ->
                    Log.d("RoomState", participants.toString())
                    view.renderParticipants(room, participants)
                }
            }
        }

        lifecycleScope.launch {
            val view = findViewById<ParticipantItemView>(R.id.floatingParticipantView)

            callViewModel.callState.filterNotNull().collectLatest { room ->
                room.localParticipant.collectLatest { participant ->
                    val track = participant.track
                    val video = track?.video

                    if (track != null && video != null) {
                        view.visibility = View.VISIBLE
                        view.initialize(room, track.streamId) {
                            lifecycleScope.launch {
                                view.elevation = 10f
                                view.bringToFront()
                            }
                        }
                        view.bindTrack(video)
                    }
                }
            }
        }
    }

    private fun leaveCall() {
        callViewModel.leaveCall()
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= M) {
            checkPermissions()
        } else {
            startVideoFlow()
        }
    }

    private fun startSettings() {
        startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                val uri = Uri.fromParts("package", packageName, null)
                data = uri
            }
        )
    }

    private fun startVideoFlow() {
        val isInitialized = callViewModel.isVideoInitialized.value
        if (isInitialized) return

        val userToken = requireNotNull(intent.getStringExtra(KEY_USER_TOKEN))
        val sfuUrl = requireNotNull(intent.getStringExtra(KEY_SFU_URL))
        val callId = requireNotNull(intent.getStringExtra(KEY_CALL_ID))
        val iceServers =
            requireNotNull(intent.getSerializableExtra(KEY_ICE_SERVERS)) as? IceServerConfig

        callViewModel.init(
            callId = callId,
            sfuUrl = sfuUrl,
            userToken = userToken,
            iceServers = iceServers?.iceServers ?: emptyList(),
            callSettings = CallSettings(
                audioOn = false,
                videoOn = false
            )
        )
    }

    @RequiresApi(M)
    private fun checkPermissions() {
        val missing = getMissingPermissions()

        if (missing.isNotEmpty()) {
            requestPermissions(missing)
        } else {
            startVideoFlow()
        }
    }

    @RequiresApi(M)
    private fun requestPermissions(permissions: Array<out String>) {
        val deniedCamera = !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
        val deniedMicrophone =
            !shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)

        if (!deniedCamera && !deniedMicrophone) {
            permissionsContract.launch(permissions.first())
        } else {
            showPermissionsDialog()
        }
    }

    @RequiresApi(M)
    private fun getMissingPermissions(): Array<out String> {
        val permissionsToCheck = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        val missing = permissionsToCheck
            .map { it to (checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED) }
            .filter { (_, isGranted) -> !isGranted }
            .map { it.first }

        return missing.toTypedArray()
    }

    private fun showPermissionsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permissions required to launch the app")
            .setMessage("Open settings to allow camera and microphone permissions.")
            .setPositiveButton("Launch settings") { dialog, _ ->
                startSettings()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                finish()
                dialog.dismiss()
            }
            .create()
            .show()
    }

    companion object {
        private const val KEY_CALL_ID = "call_id"
        private const val KEY_SFU_URL = "signal_url"
        private const val KEY_USER_TOKEN = "user_token"
        private const val KEY_ICE_SERVERS = "ice_servers"

        internal fun getIntent(
            context: Context,
            callCid: String,
            signalUrl: String,
            userToken: String,
            iceServers: List<IceServer>,
        ): Intent {
            return Intent(context, CallActivity::class.java).apply {
                putExtra(KEY_CALL_ID, callCid)
                putExtra(KEY_SFU_URL, signalUrl)
                putExtra(KEY_USER_TOKEN, userToken)
                putExtra(KEY_ICE_SERVERS, IceServerConfig(iceServers))
            }
        }
    }
}
