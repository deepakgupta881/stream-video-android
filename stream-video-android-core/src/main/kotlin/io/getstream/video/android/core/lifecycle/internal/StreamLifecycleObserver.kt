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

package io.getstream.video.android.core.lifecycle.internal

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.getstream.video.android.core.dispatchers.DispatcherProvider
import io.getstream.video.android.core.lifecycle.LifecycleHandler
import kotlinx.coroutines.withContext

internal class StreamLifecycleObserver(
    private val lifecycle: Lifecycle,
    private val handler: LifecycleHandler,
) : DefaultLifecycleObserver {
    private var recurringStartedEvent = false

    @Volatile
    private var isObserving = false

    suspend fun observe() {
        if (isObserving.not()) {
            isObserving = true
            withContext(DispatcherProvider.Main) {
                lifecycle.addObserver(this@StreamLifecycleObserver)
            }
        }
    }

    suspend fun dispose() {
        if (isObserving) {
            withContext(DispatcherProvider.Main) {
                lifecycle.removeObserver(this@StreamLifecycleObserver)
            }
        }
        isObserving = false
        recurringStartedEvent = false
    }

    override fun onStart(owner: LifecycleOwner) {
        // ignore event when we just started observing the lifecycle
        if (recurringStartedEvent) {
            handler.started()
        }
        recurringStartedEvent = true
    }

    override fun onStop(owner: LifecycleOwner) {
        handler.stopped()
    }
}
