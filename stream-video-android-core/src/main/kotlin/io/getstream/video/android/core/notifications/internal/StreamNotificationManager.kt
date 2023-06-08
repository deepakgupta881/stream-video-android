package io.getstream.video.android.core.notifications.internal

import android.annotation.SuppressLint
import android.content.Context
import io.getstream.android.push.PushDevice
import io.getstream.android.push.PushProvider
import io.getstream.log.TaggedLogger
import io.getstream.log.taggedLogger
import io.getstream.result.Error
import io.getstream.result.Result
import io.getstream.result.flatMapSuspend
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.notifications.NotificationConfig
import io.getstream.video.android.datastore.delegate.StreamUserDataStore
import io.getstream.video.android.model.Device
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.openapitools.client.apis.DevicesApi
import org.openapitools.client.models.CreateDeviceRequest

internal class StreamNotificationManager private constructor(
    private val context: Context,
    private val scope: CoroutineScope,
    private val notificationConfig: NotificationConfig,
    private val devicesApi: DevicesApi,
    private val dataStore: StreamUserDataStore,
){

    suspend fun registerPushDevice() {
        logger.d { "[registerPushDevice] no args" }
        // first get a push device generator that works for this device
        val generator = notificationConfig.pushDeviceGenerators.firstOrNull { it.isValidForThisDevice(context) }

        // if we found one, register it at the server
        if (generator != null) {
            generator.onPushDeviceGeneratorSelected()

            generator.asyncGeneratePushDevice { generatedDevice ->
                logger.d { "[registerPushDevice] pushDevice gnerated: $generatedDevice" }
                scope.launch { createDevice(generatedDevice) }
            }
        }
    }

    suspend fun createDevice(pushDevice: PushDevice): Result<Device> {
        logger.d { "[createDevice] pushDevice: $pushDevice" }
        val newDevice = pushDevice.toDevice()
        return pushDevice
            .takeUnless { newDevice == dataStore.userDevice.value }
            ?.toCreateDeviceRequest()
            ?.flatMapSuspend { createDeviceRequest ->
                try {
                    devicesApi.createDevice(createDeviceRequest)
                    dataStore.updateUserDevice(pushDevice.toDevice())
                    Result.Success(newDevice)
                } catch (e: Exception) {
                    Result.Failure(Error.ThrowableError("Device couldn't be created", e))
                }
            }
            ?: Result.Success(newDevice)
    }

    private fun removeStoredDeivce(device: Device) {
        logger.d { "[storeDevice] device: device" }
        scope.launch {
            dataStore.userDevice.value
                .takeIf { it == device }
                ?.let { dataStore.updateUserDevice(null) }
        }
    }

    /**
     * @see StreamVideo.deleteDevice
     */
    suspend fun deleteDevice(device: Device): Result<Unit> {
        logger.d { "[deleteDevice] device: $device" }
        val userId = dataStore.user.value?.id
        return try {
            devicesApi.deleteDevice(device.id, userId)
            removeStoredDeivce(device)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(Error.ThrowableError("Device couldn't be deleted", e))
        }
    }

    private fun PushDevice.toDevice(): Device =
        Device(
            id = this.token,
            pushProvider = this.pushProvider.key,
            pushProviderName = this.providerName ?: ""
        )
    private fun PushDevice.toCreateDeviceRequest(): Result<CreateDeviceRequest> =
        when (pushProvider) {
            PushProvider.FIREBASE -> Result.Success(CreateDeviceRequest.PushProvider.firebase)
            PushProvider.HUAWEI -> Result.Success(CreateDeviceRequest.PushProvider.huawei)
            PushProvider.XIAOMI -> Result.Success(CreateDeviceRequest.PushProvider.xiaomi)
            PushProvider.UNKNOWN -> Result.Failure(Error.GenericError("Unsupported PushProvider"))
        }.map {
            CreateDeviceRequest(
                id = token,
                pushProvider = it,
                pushProviderName = providerName
            )
        }


    internal companion object {
        private val logger: TaggedLogger by taggedLogger("StreamVideo:Notifications")
        @SuppressLint("StaticFieldLeak")
        private lateinit var internalStreamNotificationManager: StreamNotificationManager
        internal fun install(
            context: Context,
            scope: CoroutineScope,
            notificationConfig: NotificationConfig,
            devicesApi: DevicesApi,
            streamUserDataStore: StreamUserDataStore,
        ): StreamNotificationManager {
            synchronized(this) {
                if (Companion::internalStreamNotificationManager.isInitialized) {
                    logger.e {
                        "The $internalStreamNotificationManager is already installed but you've " +
                                "tried to install a new one."
                    }
                } else {
                    internalStreamNotificationManager = StreamNotificationManager(
                        context.applicationContext,
                        scope,
                        notificationConfig,
                        devicesApi,
                        streamUserDataStore,
                    )
                }
                return internalStreamNotificationManager
            }
        }
    }
}