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

package io.getstream.video.android.ui.xml

import android.Manifest
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Rational
import android.view.Menu
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import io.getstream.video.android.CallViewModelFactoryProvider
import io.getstream.video.android.PermissionManagerProvider
import io.getstream.video.android.StreamVideoProvider
import io.getstream.video.android.call.state.CancelCall
import io.getstream.video.android.call.state.ToggleCamera
import io.getstream.video.android.call.state.ToggleMicrophone
import io.getstream.video.android.model.state.StreamCallState
import io.getstream.video.android.permission.PermissionManager
import io.getstream.video.android.permission.StreamPermissionManagerImpl
import io.getstream.video.android.ui.xml.binding.bindView
import io.getstream.video.android.ui.xml.databinding.ActivityCallBinding
import io.getstream.video.android.ui.xml.widget.active.ActiveCallView
import io.getstream.video.android.ui.xml.widget.incoming.IncomingCallView
import io.getstream.video.android.ui.xml.widget.outgoing.OutgoingCallView
import io.getstream.video.android.viewmodel.CallViewModel
import io.getstream.video.android.viewmodel.CallViewModelFactory

public abstract class AbstractXmlCallActivity :
    AppCompatActivity(),
    StreamVideoProvider,
    CallViewModelFactoryProvider,
    PermissionManagerProvider {

    private lateinit var callPermissionManager: PermissionManager

    private val binding by lazy { ActivityCallBinding.inflate(layoutInflater) }

    private val streamVideo by lazy { getStreamVideo(this) }

    private val factory by lazy {
        getCallViewModelFactory() ?: defaultViewModelFactory()
    }

    private val callViewModel by viewModels<CallViewModel>(factoryProducer = { factory })

    /**
     * Provides the default ViewModel factory.
     */
    public fun defaultViewModelFactory(): CallViewModelFactory {
        return CallViewModelFactory(
            streamVideo = streamVideo,
            permissionManager = getPermissionManager(),
        )
    }

    /**
     * Provides the default [PermissionManager] implementation.
     */
    override fun initPermissionManager(): PermissionManager {
        return StreamPermissionManagerImpl(
            fragmentActivity = this,
            onPermissionResult = { permission, isGranted ->
                when (permission) {
                    Manifest.permission.CAMERA -> callViewModel.onCallAction(ToggleCamera(isGranted))
                    Manifest.permission.RECORD_AUDIO -> callViewModel.onCallAction(ToggleMicrophone(isGranted))
                }
            }, onShowSettings = {
                showPermissionsDialog()
            }
        )
    }

    /**
     * Returns the [PermissionManager] initialized in [initPermissionManager].
     */
    override fun getPermissionManager(): PermissionManager = callPermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        callPermissionManager = initPermissionManager()
        showWhenLockedAndTurnScreenOn()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        observeStreamCallState()

        lifecycleScope.launchWhenCreated {
            callViewModel.streamCallState.collect { state ->
                when {
                    state is StreamCallState.Incoming && !state.acceptedByMe -> {
                        showIncomingScreen()
                    }

                    state is StreamCallState.Outgoing && !state.acceptedByCallee -> {
                        showOutgoingScreen()
                    }

                    state is StreamCallState.Idle -> {
                        finish()
                    }

                    else -> {
                        showActiveCallScreen()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startVideoFlow()
    }

    /**
     * Observes the current call state and sets the toolbar title accordingly.
     */
    private fun observeStreamCallState() {
        lifecycleScope.launchWhenCreated {
            callViewModel.streamCallState.collect {
                val callId = when (val state = it) {
                    is StreamCallState.Active -> state.callGuid.id
                    else -> ""
                }
                val status = it.formatAsTitle()

                val title = when (callId.isBlank()) {
                    true -> status
                    else -> "$status: $callId"
                }
                binding.callToolbar.title = title
            }
        }
    }

    /**
     * Sets up the toolbar.
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.callToolbar)
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }
        binding.callToolbar.setNavigationOnClickListener { handleBackPressed() }
    }

    /**
     * Shows the outgoing call screen and initialises the state observers required to populate the screen.
     */
    private fun showOutgoingScreen() {
        binding.contentHolder.removeAllViews()
        val outgoingScreen = OutgoingCallView(context = this)
        binding.contentHolder.addView(
            outgoingScreen,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        outgoingScreen.bindView(callViewModel, this)
    }

    /**
     * Shows the incoming call screen and initialises the state observers required to populate the screen.
     */
    private fun showIncomingScreen() {
        binding.contentHolder.removeAllViews()
        val incomingScreen = IncomingCallView(context = this)
        binding.contentHolder.addView(
            incomingScreen,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        incomingScreen.bindView(callViewModel, this)
    }

    /**
     * Shows the active call screen and initialises the state observers required to populate the screen.
     */
    private fun showActiveCallScreen() {
        binding.contentHolder.removeAllViews()
        val activeCallView = ActiveCallView(context = this)
        binding.contentHolder.addView(
            activeCallView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        activeCallView.bindView(callViewModel, this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.call_menu, menu)
        return true
    }

    /**
     * If the user denied the permission and clicked don't ask again, will open settings so the user can enable the
     * permissions.
     */
    private fun startSettings() {
        startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                val uri = Uri.fromParts("package", packageName, null)
                data = uri
            }
        )
    }

    /**
     * Starts the flow to connect to a call.
     */
    private fun startVideoFlow() {
        val isInitialized = callViewModel.isVideoInitialized.value
        if (isInitialized) return
        callViewModel.connectToCall()
    }

    /**
     * Shows a dialog explaining why the permissions are needed.
     */
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

    /**
     * Keeps the app visible if the device enters the locked state.
     */
    private fun showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    /**
     * Triggers when the user taps on the system or header back button.
     *
     * Attempts to show Picture in Picture mode, if the user allows it and your Application supports
     * the feature.
     */
    protected open fun handleBackPressed() {
        val callState = callViewModel.streamCallState.value

        if (callState !is StreamCallState.Connected) {
            closeCall()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                callViewModel.dismissOptions()

                enterPictureInPictureMode(
                    PictureInPictureParams.Builder().setAspectRatio(Rational(9, 16)).apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            this.setAutoEnterEnabled(true)
                        }
                    }.build()
                )
            } else {
                enterPictureInPictureMode()
            }
        } else {
            closeCall()
        }
    }

    /**
     * Clears state when the user closes the call.
     */
    private fun closeCall() {
        callViewModel.onCallAction(CancelCall)
        callViewModel.clearState()
        finish()
    }

    override fun onStop() {
        super.onStop()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val isInPiP = isInPictureInPictureMode

            if (isInPiP) {
                callViewModel.onCallAction(CancelCall)
                callViewModel.clearState()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            callViewModel.onPictureInPictureModeChanged(isInPictureInPictureMode)
        }
    }

    /**
     * Formats the current call state so that we can show it in the toolbar.
     */
    private fun StreamCallState.formatAsTitle() = when (this) {
        is StreamCallState.Drop -> "Drop"
        is StreamCallState.Joined -> "Joined"
        is StreamCallState.Connecting -> "Connecting"
        is StreamCallState.Connected -> "Connected"
        is StreamCallState.Incoming -> "Incoming"
        is StreamCallState.Joining -> "Joining"
        is StreamCallState.Outgoing -> "Outgoing"
        StreamCallState.Idle -> "Idle"
    }
}
