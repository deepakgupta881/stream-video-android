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

package io.getstream.video.android.xml.widget.renderer

import io.getstream.video.android.xml.widget.participant.RendererInitializer
import io.getstream.webrtc.android.ui.VideoTextureViewRenderer

/**
 * Interface used to identify any container that had a video renderer inside of it. Used so we can easily initialise
 * the renderer for [VideoTextureViewRenderer].
 */
internal fun interface VideoRenderer {

    /**
     * Used to set the renderer initializer for a view containing [VideoTextureViewRenderer].
     *
     * @param rendererInitializer The initializer to be used for the [VideoTextureViewRenderer].
     */
    public fun setRendererInitializer(rendererInitializer: RendererInitializer)
}
